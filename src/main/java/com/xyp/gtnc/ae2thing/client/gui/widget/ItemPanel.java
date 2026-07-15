package com.xyp.gtnc.ae2thing.client.gui.widget;

import static net.minecraft.client.gui.GuiScreen.isShiftKeyDown;

import java.io.IOException;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.xyp.gtnc.Common.compat.FluidDropCompat;
import com.xyp.gtnc.ae2thing.AE2Thing;
import com.xyp.gtnc.ae2thing.client.gui.BaseMEGui;
import com.xyp.gtnc.ae2thing.client.gui.IGuiMonitorTerminal;
import com.xyp.gtnc.ae2thing.client.gui.IWidgetGui;
import com.xyp.gtnc.ae2thing.client.gui.container.ContainerWirelessDualInterfaceTerminal;
import com.xyp.gtnc.ae2thing.client.me.AdvItemRepo;
import com.xyp.gtnc.ae2thing.integration.Mods;
import com.xyp.gtnc.ae2thing.network.CPacketInventoryAction;
import com.xyp.gtnc.ae2thing.util.Ae2ReflectClient;

import appeng.api.config.SearchBoxMode;
import appeng.api.config.Settings;
import appeng.api.config.TerminalStyle;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IAEStackType;
import appeng.api.util.IConfigManager;
import appeng.client.gui.implementations.GuiMEMonitorable;
import appeng.client.gui.slots.VirtualMEMonitorableSlot;
import appeng.client.gui.slots.VirtualMESlot;
import appeng.client.gui.widgets.GuiImgButton;
import appeng.client.gui.widgets.GuiScrollbar;
import appeng.client.gui.widgets.IDropToFillTextField;
import appeng.client.gui.widgets.ISortSource;
import appeng.container.AEBaseContainer;
import appeng.core.AEConfig;
import appeng.core.AELog;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketMonitorableAction;
import appeng.core.sync.packets.PacketValueConfig;
import appeng.helpers.InventoryAction;
import appeng.helpers.MonitorableAction;
import appeng.util.IConfigManagerHost;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import codechicken.nei.LayoutManager;
import codechicken.nei.util.TextHistory;
import it.unimi.dsi.fastutil.objects.Reference2BooleanMap;

public class ItemPanel implements IAEBasePanel, IGuiMonitorTerminal, IConfigManagerHost, IDropToFillTextField {

    private final BaseMEGui parent;
    private final IWidgetGui gui;
    private final ContainerWirelessDualInterfaceTerminal container;
    private final int perRow;
    private final int rows;
    protected THGuiTextField searchField;
    private final AEBaseContainer inventorySlots;
    private final AdvItemRepo repo;
    private final IConfigManager configSrc;
    private final ISortSource source;
    private final GuiScrollbar scrollbar;
    private int absX;
    private int absY;
    private final int w;
    private final int h;
    private int offsetY;
    private final boolean showViewBtn = true;
    private GuiImgButton SortByBox;
    private GuiImgButton ViewBox;
    private GuiImgButton SortDirBox;
    private GuiImgButton searchBoxSettings;
    private static String memoryText = "";
    private final TextHistory history;
    private int lastClickTime = 0;

    public ItemPanel(IWidgetGui gui, ContainerWirelessDualInterfaceTerminal container, IConfigManager configSrc,
        ISortSource source) {
        this.gui = gui;
        this.container = container;
        this.parent = gui.getGui();
        this.inventorySlots = this.container;
        this.configSrc = configSrc;
        this.source = source;
        this.scrollbar = new GuiScrollbar();
        this.repo = new AdvItemRepo(scrollbar, source);
        // NOTE: deliberately NOT calling repo.setCache(this). The cache enables AdvItemRepo's background
        // "repo sort thread", which rebuilds the reflected ItemRepo.view via clear()+addAll() while the render
        // thread reads it (getReferenceStack/size/setScrollBar) from paths the drawScreen lock doesn't cover.
        // That race showed up only in real (busy) networks as the left ME list mis-sorting or showing only
        // fluids, and got worse on Shift release (setPaused(false) schedules an immediate re-sort). Without a
        // cache, every AdvItemRepo override falls through to stock ItemRepo, so sorting/filtering runs
        // synchronously on the client thread — identical to vanilla AE2's ME terminal, race eliminated.
        this.repo.setPowered(true);
        this.w = 101;
        this.h = 96;
        this.repo.setRowSize(4);
        this.rows = 4;
        this.perRow = 4;
        this.history = Ae2ReflectClient.getHistory(LayoutManager.searchField);
    }

    public void saveSearchString() {
        if (Mods.NOT_ENOUGH_ITEMS.isModLoaded() && isNEISearch()
            && !this.searchField.getText()
                .isEmpty()) {
            this.history.add(this.searchField.getText());
        }
    }

    protected boolean isNEISearch() {
        final Enum<?> s = AEConfig.instance.settings.getSetting(Settings.SEARCH_MODE);
        return s == SearchBoxMode.NEI_MANUAL_SEARCH || s == SearchBoxMode.NEI_AUTOSEARCH;
    }

    @Override
    public String getBackground() {
        return "gui/widget/items.png";
    }

    @Override
    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY) {
        GL11.glTranslatef(0f, 0f, 100f);
        this.scrollbar.draw(this.parent);
        GL11.glTranslatef(0f, 0f, -100f);
        if (AEConfig.instance.preserveSearchBar && searchField != null && searchField.isMouseIn(mouseX, mouseY))
            this.parent.drawTooltip(this.absX - offsetX, this.absY - 20, 0, searchField.getMessage());
        if (Mods.NOT_ENOUGH_ITEMS.isModLoaded() && searchField != null
            && this.searchField.isMouseIn(mouseX, mouseY)
            && this.isNEISearch()
            && this.parent != null) {
            // draw selection
            List<String> list = Ae2ReflectClient.getHistoryList(this.history);
            this.parent.drawHistorySelection(
                searchField.xPosition - offsetX,
                searchField.yPosition - this.parent.getGuiTop(),
                searchField.getText(),
                searchField.width,
                list);
        }
    }

    @Override
    public void drawBG(int offsetX, int offsetY, int mouseX, int mouseY) {
        this.bindTextureBack(getBackground());
        this.parent.drawTexturedModalRect(absX, absY, 0, 0, 101, 96);
        if (this.searchField != null) {
            this.searchField.drawTextBox();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float btn) {}

    @Override
    public void initGui() {
        this.absX = this.parent.getGuiLeft() - 101;
        this.absY = this.parent.getGuiTop() + this.parent.getYSize() - 96;
        this.searchField = new THGuiTextField(this.parent.getFontRenderer(), absX + 3, absY + 4, 72, 12);
        this.searchField.setEnableBackgroundDrawing(false);
        this.searchField.setMaxStringLength(25);
        this.searchField.setTextColor(0xFFFFFF);
        this.searchField.setVisible(true);
        // this.searchField.setMessage(ButtonToolTips.SearchStringTooltip.getLocal());
        this.gui.getMeSlots()
            .clear();
        for (int y = 0; y < this.rows; y++) {
            for (int x = 0; x < this.perRow; x++) {
                this.gui.registerMESlot(
                    new VirtualMEMonitorableSlot(
                        (this.absX - this.parent.getGuiLeft() + 5) + x * 18,
                        (this.absY + 18 - this.parent.getGuiTop()) + y * 18,
                        this.repo,
                        x + y * this.perRow,
                        type -> true));
            }
        }
        this.offsetY = this.absY;
        this.gui.getButtonList()
            .add(
                this.SortByBox = new GuiImgButton(
                    this.absX - 18,
                    this.offsetY,
                    Settings.SORT_BY,
                    this.configSrc.getSetting(Settings.SORT_BY)));
        this.offsetY += 20;

        if (this.showViewBtn) {
            this.gui.getButtonList()
                .add(
                    this.ViewBox = new GuiImgButton(
                        this.absX - 18,
                        this.offsetY,
                        Settings.VIEW_MODE,
                        this.configSrc.getSetting(Settings.VIEW_MODE)));
            this.offsetY += 20;
        }

        this.gui.getButtonList()
            .add(
                this.SortDirBox = new GuiImgButton(
                    this.absX - 18,
                    this.offsetY,
                    Settings.SORT_DIRECTION,
                    this.configSrc.getSetting(Settings.SORT_DIRECTION)));
        this.offsetY += 20;

        this.gui.getButtonList()
            .add(
                this.searchBoxSettings = new GuiImgButton(
                    this.absX - 18,
                    this.offsetY,
                    Settings.SEARCH_MODE,
                    AEConfig.instance.settings.getSetting(Settings.SEARCH_MODE)));
        this.offsetY += 20;
        final Enum<?> searchMode = AEConfig.instance.settings.getSetting(Settings.SEARCH_MODE);

        if (searchMode == SearchBoxMode.AUTOSEARCH || searchMode == SearchBoxMode.NEI_AUTOSEARCH) {
            this.searchField.setFocused(true);
        }
        if ((AEConfig.instance.preserveSearchBar || this.parent.isSubGui())) {
            setSearchString(memoryText, false);
        }
        if (this.parent.isSubGui()) {
            this.repo.updateView();
        }
        this.setScrollBar();
    }

    /** Left edge of the ME terminal panel frame (absolute screen X). */
    public int getAbsX() {
        return this.absX;
    }

    /** Top edge of the ME terminal panel frame (absolute screen Y). */
    public int getAbsY() {
        return this.absY;
    }

    @Override
    public boolean hideItemPanelSlot(int tx, int ty, int tw, int th) {
        int rw = 101;
        int rh = 96;
        if (tw <= 0 || th <= 0) {
            return false;
        }

        int rx = this.absX;
        int ry = this.absY;

        rw += rx;
        rh += ry;
        tw += tx;
        th += ty;

        // overflow || intersect
        return (rw < rx || rw > tx) && (rh < ry || rh > ty) && (tw < tx || tw > rx) && (th < ty || th > ry);
    }

    @Override
    public void mouseClicked(int xCoord, int yCoord, int btn) {
        this.saveSearchString();
        this.searchField.mouseClicked(xCoord, yCoord, btn);
        if (btn == 1 && this.searchField.isMouseIn(xCoord, yCoord)) {
            setSearchString("", true);
        }
        this.scrollbar.click(this.parent, xCoord - this.parent.getGuiLeft(), yCoord - this.parent.getGuiTop());
    }

    public void setSearchString(String memoryText, boolean updateView) {
        this.searchField.setText(memoryText);
        this.repo.setSearchString(memoryText);
        if (updateView) {
            this.repo.updateView();
            this.setScrollBar();
        }
        updateSuggestion();
    }

    @Override
    public void setScrollBar() {
        this.scrollbar.setTop(this.absY - this.parent.getGuiTop() + 18)
            .setLeft(this.absX - this.parent.getGuiLeft() + this.w - 20)
            .setHeight(this.rows * 18 - 2);
        this.scrollbar
            .setRange(0, (this.repo.size() + this.perRow - 1) / this.perRow - this.rows, Math.max(1, this.rows / 6));
    }

    @Override
    public boolean isOverTextField(int mousex, int mousey) {
        return searchField.isMouseIn(mousex, mousey);
    }

    @Override
    public void setTextFieldValue(String displayName, int mousex, int mousey, ItemStack stack) {
        if (!searchField.isMouseIn(mousex, mousey)) return;
        setSearchString(displayName, true);
        this.saveSearchString();
    }

    private boolean meSlotClick(VirtualMESlot slot, int ctrlDown, int clickMode) {
        // Temporary solution
        if (lastClickTime == Minecraft.getMinecraft().thePlayer.ticksExisted) {
            return false;
        }
        lastClickTime = Minecraft.getMinecraft().thePlayer.ticksExisted;
        saveSearchString();
        final EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (this.parent.updateFluidContainer(slot, ctrlDown, clickMode)) return true;

        IAEStack<?> aeStack = slot.getAEStack();
        IAEItemStack stack = aeStack instanceof IAEItemStack ais ? ais : null;

        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            this.inventorySlots.setTargetStack(stack);
            final PacketMonitorableAction p = new PacketMonitorableAction(MonitorableAction.MOVE_REGION, -1);
            NetworkHandler.instance.sendToServer(p);
            return true;
        }

        MonitorableAction action = null;
        switch (clickMode) {
            case 0: // pickup / set-down.
                action = ctrlDown == 1 ? MonitorableAction.SPLIT_OR_PLACE_SINGLE : MonitorableAction.PICKUP_OR_SET_DOWN;
                // Use the generic aeStack, not the item-cast stack: a craftable FLUID is an IAEFluidStack, so the
                // item-cast is null and autocraft would never trigger for fluids.
                if (aeStack != null && action == MonitorableAction.PICKUP_OR_SET_DOWN
                    && aeStack.getStackSize() == 0
                    && player.inventory.getItemStack() == null) {
                    action = MonitorableAction.AUTO_CRAFT;
                }
                break;
            case 1:
                action = ctrlDown == 1 ? MonitorableAction.PICKUP_SINGLE : MonitorableAction.SHIFT_CLICK;
                break;
            case 3: // creative dupe:
                if (aeStack != null && aeStack.isCraftable()) {
                    action = MonitorableAction.AUTO_CRAFT;
                } else if (player.capabilities.isCreativeMode) {
                    if (aeStack instanceof IAEItemStack) {
                        action = MonitorableAction.CREATIVE_DUPLICATE;
                    }
                } else break;
            default:
        }
        if (action == MonitorableAction.AUTO_CRAFT) {
            // For a fluid the item-cast `stack` is null; carry the fluid as an ItemFluidDrop IAEItemStack so the
            // AUTO_CRAFT packet (which requires IAEItemStack) can transport it. The server handler recognizes
            // ItemFluidDrop and opens the fluid autocraft. Item stacks pass through unchanged.
            IAEItemStack craftStack = stack;
            if (craftStack == null && aeStack instanceof IAEFluidStack afs && afs.getFluidStack() != null) {
                // A craftable fluid reports stackSize/amount 0, and FluidDropCompat.newStack returns null when
                // amount <= 0. Copy the FluidStack and bump the amount to >= 1 so we get a valid drop item to
                // carry as the craft target (the amount here is only a transport placeholder; the real craft
                // quantity is chosen in the craft-amount GUI).
                net.minecraftforge.fluids.FluidStack fs = afs.getFluidStack()
                    .copy();
                if (fs.amount <= 0) fs.amount = 1;
                // [液滴分类] 必须留液滴：AUTO_CRAFT 下单目标,流体须伪装成液滴物品才能被 CraftingJobV2 计算
                craftStack = AEItemStack.create(FluidDropCompat.newStack(fs));
            }
            this.inventorySlots.setTargetStack(craftStack);
            AE2Thing.proxy.netHandler.sendToServer(
                new CPacketInventoryAction(
                    InventoryAction.AUTO_CRAFT,
                    Ae2ReflectClient.getInventorySlots(this.parent)
                        .size(),
                    -2,
                    craftStack));
        } else if (action != null) {
            // [液滴分类] 可迁原生：非合成动作时把液滴置空(避免当普通物品处理),纯交互过滤不参与合成
            if (stack != null && FluidDropCompat.isFluidDrop(stack.getItem())) stack = null;
            this.inventorySlots.setTargetStack(stack);
            final PacketMonitorableAction p = new PacketMonitorableAction(action, -1);
            NetworkHandler.instance.sendToServer(p);
        }
        return true;
    }

    @Override
    public boolean handleVirtualSlotClick(VirtualMESlot slot, int mouseButton) {
        if (!(slot instanceof VirtualMEMonitorableSlot)) return false;
        final boolean pickBlock = mouseButton == GuiMEMonitorable.keyBindPickBlockAction;
        final int ctrlDown = pickBlock ? 0 : mouseButton;
        final int clickMode = pickBlock ? 3 : (isShiftKeyDown() ? 1 : 0);
        return meSlotClick(slot, ctrlDown, clickMode);
    }

    @Override
    public boolean handleMouseClick(Slot slot, int slotIdx, int ctrlDown, int mouseButton) {
        return false;
    }

    @Override
    public boolean actionPerformed(GuiButton btn) {
        if (btn instanceof final GuiImgButton iBtn) {
            final boolean backwards = Mouse.isButtonDown(1);
            if (iBtn.getSetting() != Settings.ACTIONS) {
                final Enum<?> cv = iBtn.getCurrentValue();
                final Enum<?> next = Platform.rotateEnum(cv, backwards, iBtn.getSetting().getPossibleValues());
                if (btn == this.searchBoxSettings) {
                    AEConfig.instance.settings.putSetting(iBtn.getSetting(), next);
                } else if (btn == this.SortByBox || btn == this.SortDirBox || btn == this.ViewBox) {
                    try {
                        NetworkHandler.instance
                            .sendToServer(new PacketValueConfig(iBtn.getSetting().name(), next.name()));
                    } catch (final IOException e) {
                        AELog.debug(e);
                    }
                    iBtn.set(next);
                    if (next.getClass() == SearchBoxMode.class || next.getClass() == TerminalStyle.class) {
                        this.reInitalize();
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private void reInitalize() {
        this.gui.getButtonList()
            .clear();
        this.initGui();
    }

    @Override
    public void mouseClickMove(int x, int y, int c, long d) {
        this.scrollbar.click(this.parent, x - this.parent.getGuiLeft(), y - this.parent.getGuiTop());
    }

    @Override
    public boolean mouseWheelEvent(int mouseX, int mouseY, int wheel) {
        if (Mods.NOT_ENOUGH_ITEMS.isModLoaded() && this.searchField.isMouseIn(mouseX, mouseY) && isNEISearch()) {
            TextHistory.Direction direction;
            switch (wheel) {
                case -1:
                    direction = TextHistory.Direction.PREVIOUS;
                    break;
                case 1:
                    direction = TextHistory.Direction.NEXT;
                    break;
                default:
                    return false;
            }
            this.history.get(direction, this.searchField.getText())
                .ifPresent(t -> setSearchString(t, true));
            return true;
        }
        if (this.scrollbar.contains(mouseX - this.parent.getGuiLeft(), mouseY - this.parent.getGuiTop())
            || (mouseX > this.absX && mouseX < this.absX + this.w
                && mouseY > this.absY + 18
                && mouseY < this.absY + this.h)) {
            this.saveSearchString();
            final int currentScroll = this.scrollbar.getCurrentScroll();
            this.scrollbar.wheel(wheel);
            return currentScroll != this.scrollbar.getCurrentScroll();
        }
        return mouseX > this.absX && mouseX < this.absX + this.w && mouseY > this.absY && mouseY < this.absY + this.h;
    }

    private void updateSuggestion() {
        if (Mods.NOT_ENOUGH_ITEMS.isModLoaded() && this.isNEISearch()) {
            if (this.searchField.getText()
                .isEmpty()) {
                this.setSuggestion("");
                return;
            }
            String history = this.findHistoryPrefix(this.searchField.getText());
            if (history != null) {
                this.setSuggestion(history);
            } else {
                this.setSuggestion("");
            }
        }
    }

    private String findHistoryPrefix(String prefix) {
        for (String value : Ae2ReflectClient.getHistoryList(this.history)) {
            if (value.startsWith(prefix)) {
                return value;
            }
        }
        return null;
    }

    private void setSuggestion(String suggestion) {
        this.searchField.setSuggestion(suggestion);
    }

    @Override
    public boolean keyTyped(char character, int key) {
        if (Mods.NOT_ENOUGH_ITEMS.isModLoaded() && this.isNEISearch()) {
            if (key == Keyboard.KEY_TAB && this.searchField.isFocused()) {
                String history = this.findHistoryPrefix(this.searchField.getText());
                if (history != null) {
                    setSearchString(history, true);
                }
                return true;
            } else if (key == Keyboard.KEY_DELETE) {
                String next = this.history.getNext(this.searchField.getText())
                    .orElse("");
                Ae2ReflectClient.getHistoryList(this.history)
                    .removeIf(s -> s.equals(this.searchField.getText()));
                setSearchString(next, true);
                return true;
            }
        }
        if (this.searchField.isFocused()) {
            if (character == ' ' && this.searchField.getText()
                .isEmpty()) {
                return false;
            }

            if (this.searchField.textboxKeyTyped(character, key)) {
                this.repo.setSearchString(this.searchField.getText());
                this.repo.updateView();
                this.setScrollBar();
                this.updateSuggestion();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean draggable() {
        return false;
    }

    @Override
    public Rectangle getRectangle() {
        return new Rectangle(this.absX, this.absY, w, h);
    }

    @Override
    public void setRectangle(int x, int y) {
        this.absX = x;
        this.absY = y;
    }

    @Override
    public void postStackUpdate(List<? extends IAEStack<?>> list) {
        for (IAEStack<?> stack : list) {
            // [液滴分类] 可迁原生：物品面板视图更新时跳过液滴(流体走独立面板),纯显示分流不参与合成
            if (stack instanceof IAEItemStack item && FluidDropCompat.isFluidDrop(item.getItem())) continue;
            this.repo.postUpdate(stack);
        }
        // 性能：繁忙网络每 tick(~20/s) 都会推来一个增量更新包。若每包都立刻 updateView() 全表重排
        // (O(N log N)，大网络几万条)，会把收包/渲染线程拖垮 → 划鼠标时持续掉帧(删 setCache 后这份重排
        // 落在主线程上，属相对历史的性能回退)。postUpdate 只是廉价地更新后台 IItemList；重排改为置脏，
        // 由 drawScreen 在主线程按帧节流排空(见 flushPendingView)。已显示物品的数量因 postUpdate 原地改
        // 同一引用而仍实时更新，只有新增/移除条目的出现与排序会延迟至多一个节流周期，几乎无感。
        this.viewDirty = true;
    }

    /** 收包置脏、主线程按帧排空的节流标记(见 postStackUpdate)。 */
    private volatile boolean viewDirty = false;
    private long lastViewFlushMs = 0L;
    /** 重排最小间隔：把每 tick(~20/s) 的全表重排降到 ~5/s。 */
    private static final long VIEW_FLUSH_INTERVAL_MS = 200L;

    /**
     * 在主线程(drawScreen)按帧调用：若收到过更新包(viewDirty)且距上次重排超过节流间隔，则执行一次
     * 全表 updateView()+setScrollBar()。挪到主线程还顺带消除了原先收包线程(Netty)写 view、渲染线程读 view
     * 的无锁竞态。用户主动操作(搜索/过滤/排序/滚动)不走这里，仍在各自入口直接 updateView() 保证零延迟。
     */
    public void flushPendingView() {
        if (!this.viewDirty) return;
        final long now = System.currentTimeMillis();
        if (now - this.lastViewFlushMs < VIEW_FLUSH_INTERVAL_MS) return;
        this.viewDirty = false;
        this.lastViewFlushMs = now;
        this.repo.updateView();
        if (!this.repo.hasCache()) {
            this.setScrollBar();
        }
    }

    @Override
    public Enum<?> getSortBy() {
        return this.configSrc.getSetting(Settings.SORT_BY);
    }

    @Override
    public Enum<?> getSortDir() {
        return this.configSrc.getSetting(Settings.SORT_DIRECTION);
    }

    @Override
    public Enum<?> getSortDisplay() {
        return this.configSrc.getSetting(Settings.VIEW_MODE);
    }

    @Override
    public Reference2BooleanMap<IAEStackType<?>> getTypeFilter() {
        // The inner cache repo (AdvItemRepo.setCache) uses this ItemPanel as its ISortSource and does the actual
        // client-side type filtering. Without this delegate we'd inherit ISortSource's default all-enabled map, so
        // toggling the item/fluid/essentia buttons would flip the icon but never hide anything. Route to the GUI's
        // live filter map instead.
        return this.source.getTypeFilter();
    }

    @Override
    public void onGuiClosed() {
        memoryText = this.searchField.getText();
    }

    @Override
    public void updateSetting(IConfigManager manager, Enum settingName, Enum newValue) {
        if (this.SortByBox != null) {
            this.SortByBox.set(this.configSrc.getSetting(Settings.SORT_BY));
        }
        if (this.SortDirBox != null) {
            this.SortDirBox.set(this.configSrc.getSetting(Settings.SORT_DIRECTION));
        }
        if (this.ViewBox != null) {
            this.ViewBox.set(this.configSrc.getSetting(Settings.VIEW_MODE));
        }

        this.repo.updateView();
    }

    public AdvItemRepo getRepo() {
        return repo;
    }

    @Override
    public void handleKeyboardInput() {
        this.getRepo()
            .setPaused(this.parent.hasShiftDown());
    }

    @Override
    public void setPlayerInv(ItemStack is) {
        this.container.getPlayerInv()
            .setItemStack(is);
    }

    @Override
    public THGuiTextField getSearchField() {
        return this.searchField;
    }
}
