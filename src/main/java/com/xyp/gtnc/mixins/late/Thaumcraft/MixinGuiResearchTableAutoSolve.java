package com.xyp.gtnc.mixins.late.Thaumcraft;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.xyp.gtnc.Config.Config;
import com.xyp.gtnc.utils.AutoResearchSolver;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.client.gui.GuiResearchTable;
import thaumcraft.client.lib.PlayerNotifications;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketAspectPlaceToServer;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.research.ResearchNoteData;
import thaumcraft.common.lib.utils.HexUtils;
import thaumcraft.common.tiles.TileResearchTable;

/**
 * 研究台「一键研究」按钮：在六边形连连看网格上自动求解并落子，等效手动把小游戏解开。
 * <p>
 * 连连看的完成条件（见 {@code ResearchManager.checkResearchCompletion}）：所有 {@code type==1} 的主源质节点
 * 必须连成一整块，相邻两格的源质之间要有「组件关系」（一个是另一个的合成组成源质），且两个源质玩家都已发现。
 * 空格（{@code type==0}）填入源质后变 {@code type==2}，未用到的空格在完成时自动清除。
 * <p>
 * 求解思路：以第一个主源质为种子求连通域，对每个尚未接入的主源质用 BFS（状态 = 格 + 源质，只走空格、
 * 只用已发现源质、每步满足组件关系）寻找接入连通域的最短链，落子后并入连通域，重复至全部连通。
 * 求出方案后逐格发送 {@code PacketAspectPlaceToServer}（复用原版放置通道，无需新网络包），服务端逐包放置，
 * 最后一子触发原生完成判定——与玩家手动解开走完全相同的路径。
 * <p>
 * 前置校验：所有主源质须已发现、书写工具墨水耐久足够，否则弹出提示并中止（绝不半途落子）。
 * 由 {@link Config#tcResearchAutoSolve} 控制，默认开启。此为客户端专属类，在 {@code LateMixinsLoader} 中按物理端 gate。
 */
@SideOnly(Side.CLIENT)
@Mixin(value = GuiResearchTable.class, remap = false)
public abstract class MixinGuiResearchTableAutoSolve extends GuiContainer {

    // 占位构造器，仅为让 mixin 继承 GuiContainer 编译通过，运行时不会被调用。
    private MixinGuiResearchTableAutoSolve() {
        super(null);
    }

    @Shadow
    private TileResearchTable tileEntity;
    @Shadow
    private String username;
    @Shadow
    EntityPlayer player;
    @Shadow
    public ResearchNoteData note;

    // 按钮相对 GUI 左上角(guiLeft/guiTop)的位置与尺寸；画在羊皮纸网格上方的空白标题区。
    private static final int BTN_X = 169;
    private static final int BTN_Y = 10;
    private static final int BTN_W = 60;
    private static final int BTN_H = 14;

    /** 当前是否有可求解的未完成研究笔记（决定按钮高亮/可点，还是灰显）。 */
    private boolean gtnc$hasSolvableNote() {
        return this.note != null && this.note.key != null && this.note.key.length() != 0 && !this.note.isComplete();
    }

    /** 一次性日志标记，确认绘制方法确实每帧执行。 */
    private boolean gtnc$loggedDraw = false;

    /**
     * 在 {@code drawScreen} 末尾（整个 GUI 都画完、处于最顶层的标准 2D UI 状态）绘制「一键研究」按钮，
     * 绝不会被物品槽/网格遮挡或被深度剔除。用<b>绝对坐标</b>（{@code guiLeft/guiTop + 相对偏移}）。
     * 只要研究台 GUI 打开且开关开启就始终绘制：有可求解笔记时高亮可点，否则灰显。
     */
    @Inject(method = "drawScreen", at = @At("TAIL"), require = 1, remap = true)
    private void gtnc$drawAutoSolveButton(int mx, int my, float partialTicks, CallbackInfo ci) {
        if (!Config.tcResearchAutoSolve) {
            return;
        }
        if (!gtnc$loggedDraw) {
            gtnc$loggedDraw = true;
            org.apache.logging.log4j.LogManager.getLogger("sciencenotcool")
                .info(
                    "[AutoSolve] draw button: guiLeft={} guiTop={} at abs=({},{}) note={}",
                    this.guiLeft,
                    this.guiTop,
                    this.guiLeft + BTN_X,
                    this.guiTop + BTN_Y,
                    this.note != null ? this.note.key : "null");
        }
        int x0 = this.guiLeft + BTN_X;
        int y0 = this.guiTop + BTN_Y;
        boolean solvable = gtnc$hasSolvableNote();
        boolean hover = solvable && mx >= x0 && mx < x0 + BTN_W && my >= y0 && my < y0 + BTN_H;
        int bg;
        int border;
        int textColor;
        if (!solvable) {
            bg = 0xF0202020;
            border = 0xFF555555;
            textColor = 0xFFAAAAAA;
        } else if (hover) {
            bg = 0xF0503078;
            border = 0xFFB080FF;
            textColor = 0xFFFFFFFF;
        } else {
            bg = 0xF0303030;
            border = 0xFF808080;
            textColor = 0xFFFFFFFF;
        }
        // 切到标准 2D UI 状态：关光照/深度，开混合，重置颜色，避免被前面的 3D/物品渲染状态污染。
        GL11.glDisable(GL11.GL_LIGHTING);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Gui.drawRect(x0, y0, x0 + BTN_W, y0 + BTN_H, bg);
        Gui.drawRect(x0, y0, x0 + BTN_W, y0 + 1, border);
        Gui.drawRect(x0, y0 + BTN_H - 1, x0 + BTN_W, y0 + BTN_H, border);
        Gui.drawRect(x0, y0, x0 + 1, y0 + BTN_H, border);
        Gui.drawRect(x0 + BTN_W - 1, y0, x0 + BTN_W, y0 + BTN_H, border);
        // #tr gui.researchtable.autosolve
        // # Auto Solve
        // # zh_CN 一键研究
        String label = StatCollector.translateToLocal("gui.researchtable.autosolve");
        FontRenderer fr = this.fontRendererObj;
        fr.drawString(label, x0 + (BTN_W - fr.getStringWidth(label)) / 2, y0 + 3, textColor);
    }

    /**
     * 拦截点击：命中按钮区域时跑求解器并落子，随后取消默认处理（阻止把点击透传给下面的网格/源质列表）。
     * 在 HEAD 判定命中，未命中则放行原方法。
     */
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true, require = 1, remap = true)
    private void gtnc$onMouseClicked(int mx, int my, int button, CallbackInfo ci) {
        if (!Config.tcResearchAutoSolve || button != 0 || !gtnc$hasSolvableNote()) {
            return;
        }
        int rx = mx - this.guiLeft;
        int ry = my - this.guiTop;
        if (rx < BTN_X || rx >= BTN_X + BTN_W || ry < BTN_Y || ry >= BTN_Y + BTN_H) {
            return;
        }
        ci.cancel();
        gtnc$autoSolve();
    }

    /** 求解并落子。所有校验不通过时弹通知并中止，绝不半途落子。 */
    private void gtnc$autoSolve() {
        AspectList discoveredList = Thaumcraft.proxy.getPlayerKnowledge()
            .getAspectsDiscovered(this.username);
        Set<Aspect> discovered = new HashSet<Aspect>();
        if (discoveredList != null) {
            for (Aspect a : discoveredList.getAspects()) {
                if (a != null) {
                    discovered.add(a);
                }
            }
        }

        AutoResearchSolver.Result result;
        try {
            result = AutoResearchSolver.solve(this.note, discovered);
        } catch (AutoResearchSolver.SolveException ex) {
            gtnc$notifyFailure(ex);
            return;
        }

        // 需要的墨水次数：每落一子耗 1；alreadyConnected 时仍需 1 个触发子引发完成判定。
        int needed = result.placements.isEmpty() ? 1 : result.placements.size();
        if (!gtnc$hasInk(needed)) {
            // #tr gui.researchtable.autosolve.noink
            // # Not enough ink to auto-solve
            // # zh_CN 墨水不足，无法一键研究
            PlayerNotifications.addNotification(StatCollector.translateToLocal("gui.researchtable.autosolve.noink"));
            return;
        }

        if (result.placements.isEmpty()) {
            // 已连通：随便找一个能触发 gatherResults/完成判定的落子点。
            gtnc$placeTrigger(discovered);
            return;
        }

        for (Map.Entry<String, Aspect> e : result.placements.entrySet()) {
            HexUtils.Hex hex = this.note.hexes.get(e.getKey());
            if (hex == null) {
                continue;
            }
            PacketHandler.INSTANCE.sendToServer(
                new PacketAspectPlaceToServer(
                    this.player,
                    (byte) hex.q,
                    (byte) hex.r,
                    this.tileEntity.xCoord,
                    this.tileEntity.yCoord,
                    this.tileEntity.zCoord,
                    e.getValue()));
        }
    }

    /**
     * 无需落子即已连通（0/1 个主源质或本就相连）时，向任一主源质的相邻空格放一个合法源质以触发服务端完成判定。
     * 若找不到这样的空格，则退而对某主源质自身重放（服务端 placeAspect 会重新校验并触发 checkResearchCompletion）。
     */
    private void gtnc$placeTrigger(Set<Aspect> discovered) {
        for (Map.Entry<String, HexUtils.Hex> e : this.note.hexes.entrySet()) {
            ResearchManager.HexEntry he = this.note.hexEntries.get(e.getKey());
            if (he == null || he.type != 1 || he.aspect == null) {
                continue;
            }
            HexUtils.Hex mainHex = e.getValue();
            for (int dir = 0; dir < 6; ++dir) {
                HexUtils.Hex nb = mainHex.getNeighbour(dir);
                String nk = nb.toString();
                ResearchManager.HexEntry ne = this.note.hexEntries.get(nk);
                if (ne == null || ne.type != 0) {
                    continue;
                }
                for (Aspect cand : discovered) {
                    if (gtnc$link(he.aspect, cand)) {
                        PacketHandler.INSTANCE.sendToServer(
                            new PacketAspectPlaceToServer(
                                this.player,
                                (byte) nb.q,
                                (byte) nb.r,
                                this.tileEntity.xCoord,
                                this.tileEntity.yCoord,
                                this.tileEntity.zCoord,
                                cand));
                        return;
                    }
                }
            }
        }
    }

    private boolean gtnc$link(Aspect a1, Aspect a2) {
        if (a1 == null || a2 == null) {
            return false;
        }
        if (!a1.isPrimal()) {
            Aspect[] c = a1.getComponents();
            if (c != null && (c[0] == a2 || c[1] == a2)) {
                return true;
            }
        }
        if (!a2.isPrimal()) {
            Aspect[] c = a2.getComponents();
            if (c != null && (c[0] == a1 || c[1] == a1)) {
                return true;
            }
        }
        return false;
    }

    /** 书写工具（槽 0）当前剩余耐久是否够 need 次。 */
    private boolean gtnc$hasInk(int need) {
        net.minecraft.item.ItemStack ink = this.tileEntity.getStackInSlot(0);
        if (ink == null) {
            return false;
        }
        return ink.getMaxDamage() - ink.getItemDamage() >= need;
    }

    private void gtnc$notifyFailure(AutoResearchSolver.SolveException ex) {
        String msg;
        if (ex.failure == AutoResearchSolver.Failure.MAIN_ASPECT_UNDISCOVERED) {
            String aspectName = ex.aspect != null ? ex.aspect.getName() : "?";
            // #tr gui.researchtable.autosolve.undiscovered
            // # You must discover %s first
            // # zh_CN 你需要先发现 %s
            msg = StatCollector.translateToLocalFormatted("gui.researchtable.autosolve.undiscovered", aspectName);
        } else {
            // #tr gui.researchtable.autosolve.nopath
            // # Cannot find a solution
            // # zh_CN 无法求解此研究
            msg = StatCollector.translateToLocal("gui.researchtable.autosolve.nopath");
        }
        PlayerNotifications.addNotification(msg);
    }
}
