package com.xyp.gtnc.mixins.late.Thaumcraft;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.xyp.gtnc.Client.research.ResearchAutomation;
import com.xyp.gtnc.Client.research.ResearchSolver;
import com.xyp.gtnc.Client.research.ResearchTexts;
import com.xyp.gtnc.Config.Config;

import cpw.mods.fml.client.config.GuiButtonExt;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.client.gui.GuiResearchTable;
import thaumcraft.client.lib.PlayerNotifications;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.research.ResearchNoteData;
import thaumcraft.common.tiles.TileResearchTable;

/** Client-only GUI extension. Registration is guarded by the physical-side check in LateMixinsLoader. */
@Mixin(value = GuiResearchTable.class, remap = false)
public abstract class MixinGuiResearchTableAutoResearch extends GuiContainer {

    @Unique
    private static final int GTNC_UNLOCK_ALL = 2101;
    @Unique
    private static final int GTNC_AUTO = 2102;
    @Unique
    private static final int GTNC_SOLVE = 2103;
    @Unique
    private static final int GTNC_RETRY = 2104;
    @Unique
    private static final int GTNC_CONFIRM = 2105;

    @Unique
    private static ResearchSolver.Plan gtnc$lastPlan;

    @Shadow
    public ResearchNoteData note;
    @Shadow
    EntityPlayer player;
    @Shadow
    private TileResearchTable tileEntity;

    @Unique
    private ResearchAutomation gtnc$automation;
    @Unique
    private GuiButtonExt gtnc$unlockAllButton;
    @Unique
    private GuiButtonExt gtnc$autoButton;
    @Unique
    private GuiButtonExt gtnc$solveButton;
    @Unique
    private GuiButtonExt gtnc$retryButton;
    @Unique
    private GuiTextField gtnc$amountField;
    @Unique
    private GuiButtonExt gtnc$confirmButton;
    @Unique
    private Aspect gtnc$selectedAspect;
    @Unique
    private String gtnc$lastAutoFingerprint = "";
    @Unique
    private boolean gtnc$shownHint;

    protected MixinGuiResearchTableAutoResearch(net.minecraft.inventory.Container container) {
        super(container);
    }

    @Override
    public void initGui() {
        super.initGui();
        gtnc$automation = new ResearchAutomation(player, tileEntity);

        int x = guiLeft - 90;
        int y = guiTop + 52;
        gtnc$unlockAllButton = new GuiButtonExt(GTNC_UNLOCK_ALL, x, y, 88, 20, ResearchTexts.unlockAll());
        gtnc$autoButton = new GuiButtonExt(GTNC_AUTO, x, y + 21, 88, 20, ResearchTexts.auto(Config.tcAutoResearch));
        gtnc$solveButton = new GuiButtonExt(GTNC_SOLVE, x, y + 42, 88, 20, ResearchTexts.solve());
        gtnc$retryButton = new GuiButtonExt(GTNC_RETRY, x, y + 63, 88, 20, ResearchTexts.retry());
        buttonList.add(gtnc$unlockAllButton);
        buttonList.add(gtnc$autoButton);
        buttonList.add(gtnc$solveButton);
        buttonList.add(gtnc$retryButton);

        gtnc$amountField = new GuiTextField(fontRendererObj, 0, 0, 36, 14);
        gtnc$amountField.setMaxStringLength(6);
        gtnc$amountField.setVisible(false);
        gtnc$confirmButton = new GuiButtonExt(GTNC_CONFIRM, 0, 0, 38, 16, ResearchTexts.confirm());
        gtnc$confirmButton.visible = false;
        buttonList.add(gtnc$confirmButton);
        gtnc$refreshButtons();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (gtnc$amountField != null) gtnc$amountField.updateCursorCounter();
        if (gtnc$automation != null) gtnc$automation.tick();
        gtnc$refreshButtons();

        if (!gtnc$shownHint) {
            gtnc$shownHint = true;
            PlayerNotifications.addNotification(ResearchTexts.synthesizeHint());
        }
        if (!Config.tcAutoResearch || note == null
            || note.complete
            || gtnc$automation == null
            || gtnc$automation.isRunning()) return;

        String fingerprint = ResearchSolver.fingerprint(note);
        if (!fingerprint.isEmpty() && !fingerprint.equals(gtnc$lastAutoFingerprint)) {
            gtnc$lastAutoFingerprint = fingerprint;
            gtnc$startSolve(false);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
        if (button == null) return;

        if (button.id == GTNC_AUTO) {
            Config.setTcAutoResearch(!Config.tcAutoResearch);
            gtnc$autoButton.displayString = ResearchTexts.auto(Config.tcAutoResearch);
            if (!Config.tcAutoResearch) gtnc$lastAutoFingerprint = "";
            return;
        }
        if (gtnc$automation != null && gtnc$automation.isRunning()) {
            PlayerNotifications.addNotification(ResearchTexts.busy());
            return;
        }

        if (button.id == GTNC_UNLOCK_ALL) {
            gtnc$automation.startUnlockAll(this::gtnc$refreshButtons);
        } else if (button.id == GTNC_SOLVE) {
            gtnc$startSolve(true);
        } else if (button.id == GTNC_RETRY) {
            if (note == null || gtnc$lastPlan == null
                || !ResearchSolver.fingerprint(note)
                    .equals(gtnc$lastPlan.fingerprint)) {
                PlayerNotifications.addNotification(ResearchTexts.noRetry());
            } else {
                gtnc$automation.startPlan(gtnc$lastPlan, this::gtnc$refreshButtons);
            }
        } else if (button.id == GTNC_CONFIRM) {
            gtnc$confirmSynthesis();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (gtnc$amountField != null && gtnc$amountField.getVisible()
            && gtnc$amountField.textboxKeyTyped(typedChar, keyCode)) return;
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void onGuiClosed() {
        if (gtnc$automation != null) gtnc$automation.cancel(false);
        super.onGuiClosed();
    }

    @Inject(method = "mouseClicked", at = @At("TAIL"), remap = true)
    private void gtnc$afterMouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo callback) {
        if (mouseButton != 0 || !isCtrlKeyDown()) {
            if (gtnc$amountField != null && gtnc$amountField.getVisible()) gtnc$hideSynthesisInput();
            return;
        }
        int gx = (width - xSize) / 2;
        int gy = (height - ySize) / 2;
        Aspect aspect = gtnc$invokeGetClickedAspect(mouseX, mouseY, gx, gy, true);
        if (aspect == null) return;

        gtnc$selectedAspect = aspect;
        gtnc$amountField.xPosition = mouseX - 18;
        gtnc$amountField.yPosition = mouseY + 9;
        gtnc$amountField.setText("1");
        gtnc$amountField.setFocused(true);
        gtnc$amountField.setVisible(true);
        gtnc$confirmButton.xPosition = mouseX + 20;
        gtnc$confirmButton.yPosition = mouseY + 8;
        gtnc$confirmButton.visible = true;
    }

    @Inject(method = "drawScreen", at = @At("TAIL"), remap = true)
    private void gtnc$afterDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        if (gtnc$amountField != null && gtnc$amountField.getVisible()) gtnc$amountField.drawTextBox();
    }

    @Invoker("getClickedAspect")
    protected abstract Aspect gtnc$invokeGetClickedAspect(int mouseX, int mouseY, int guiX, int guiY,
        boolean ignoreZero);

    @Unique
    private void gtnc$startSolve(boolean notifyOnMissingNote) {
        if (note == null || note.complete) {
            if (notifyOnMissingNote) PlayerNotifications.addNotification(ResearchTexts.noNote());
            return;
        }
        AspectList pool = Thaumcraft.proxy.getPlayerKnowledge()
            .getAspectsDiscovered(player.getCommandSenderName());
        ResearchSolver.Plan plan = ResearchSolver.solve(note, pool);
        if (plan == null) {
            PlayerNotifications.addNotification(ResearchTexts.solveFailed());
            return;
        }
        gtnc$lastPlan = plan;
        gtnc$automation.startPlan(plan, this::gtnc$refreshButtons);
    }

    @Unique
    private void gtnc$confirmSynthesis() {
        int amount;
        try {
            amount = Integer.parseInt(gtnc$amountField.getText());
        } catch (NumberFormatException ignored) {
            amount = 0;
        }
        if (amount <= 0 || gtnc$selectedAspect == null) {
            PlayerNotifications.addNotification(ResearchTexts.invalidAmount());
        } else {
            gtnc$automation.startSynthesis(gtnc$selectedAspect, amount, this::gtnc$refreshButtons);
        }
        gtnc$hideSynthesisInput();
    }

    @Unique
    private void gtnc$hideSynthesisInput() {
        gtnc$amountField.setFocused(false);
        gtnc$amountField.setVisible(false);
        gtnc$confirmButton.visible = false;
    }

    @Unique
    private void gtnc$refreshButtons() {
        if (gtnc$unlockAllButton == null) return;
        AspectList pool = Thaumcraft.proxy.getPlayerKnowledge()
            .getAspectsDiscovered(player.getCommandSenderName());
        gtnc$unlockAllButton.visible = pool != null && pool.aspects.size() < Aspect.aspects.size();
        boolean busy = gtnc$automation != null && gtnc$automation.isRunning();
        gtnc$solveButton.enabled = !busy;
        gtnc$retryButton.enabled = !busy && gtnc$lastPlan != null;
        gtnc$unlockAllButton.enabled = !busy;
    }
}
