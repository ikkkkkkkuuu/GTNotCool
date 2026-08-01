package com.xyp.gtnc.Client.research;

import java.lang.reflect.Method;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;

import com.xyp.gtnc.Config.Config;
import com.xyp.gtnc.ScienceNotCool;

import cpw.mods.fml.client.config.GuiButtonExt;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.client.gui.GuiResearchTable;
import thaumcraft.client.lib.PlayerNotifications;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.tiles.TileResearchTable;

/** Vanilla Thaumcraft research table with GTNC's client-side automation controls. */
public final class GuiAutoResearchTable extends GuiResearchTable {

    private static final int GTNC_UNLOCK_ALL = 2101;
    private static final int GTNC_AUTO = 2102;
    private static final int GTNC_SOLVE = 2103;
    private static final int GTNC_RETRY = 2104;
    private static final int GTNC_CONFIRM = 2105;
    private static final Method GET_CLICKED_ASPECT = findGetClickedAspect();

    private static ResearchSolver.Plan lastPlan;
    private static boolean loggedOpen;

    private final EntityPlayer gtncPlayer;
    private final TileResearchTable gtncTable;
    private ResearchAutomation automation;
    private GuiButtonExt unlockAllButton;
    private GuiButtonExt autoButton;
    private GuiButtonExt solveButton;
    private GuiButtonExt retryButton;
    private GuiTextField amountField;
    private GuiButtonExt confirmButton;
    private Aspect selectedAspect;
    private String lastAutoFingerprint = "";
    private boolean shownHint;

    public GuiAutoResearchTable(EntityPlayer player, TileResearchTable table) {
        super(player, table);
        gtncPlayer = player;
        gtncTable = table;
    }

    @Override
    public void initGui() {
        super.initGui();
        automation = new ResearchAutomation(gtncPlayer, gtncTable);

        int x = guiLeft >= 92 ? guiLeft - 90 : Math.min(width - 90, guiLeft + xSize + 2);
        int y = Math.max(4, Math.min(guiTop + 52, height - 84));
        unlockAllButton = new GuiButtonExt(GTNC_UNLOCK_ALL, x, y, 88, 20, ResearchTexts.unlockAll());
        autoButton = new GuiButtonExt(GTNC_AUTO, x, y + 21, 88, 20, ResearchTexts.auto(Config.tcAutoResearch));
        solveButton = new GuiButtonExt(GTNC_SOLVE, x, y + 42, 88, 20, ResearchTexts.solve());
        retryButton = new GuiButtonExt(GTNC_RETRY, x, y + 63, 88, 20, ResearchTexts.retry());
        buttonList.add(unlockAllButton);
        buttonList.add(autoButton);
        buttonList.add(solveButton);
        buttonList.add(retryButton);

        amountField = new GuiTextField(fontRendererObj, 0, 0, 36, 14);
        amountField.setMaxStringLength(6);
        amountField.setVisible(false);
        confirmButton = new GuiButtonExt(GTNC_CONFIRM, 0, 0, 38, 16, ResearchTexts.confirm());
        confirmButton.visible = false;
        buttonList.add(confirmButton);
        refreshButtons();

        if (!loggedOpen) {
            loggedOpen = true;
            ScienceNotCool.LOG.info("Opened GTNC automatic research table GUI");
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (amountField != null) amountField.updateCursorCounter();
        if (automation != null) automation.tick();
        refreshButtons();

        if (!shownHint) {
            shownHint = true;
            PlayerNotifications.addNotification(ResearchTexts.synthesizeHint());
        }
        if (!Config.tcAutoResearch || note == null || note.complete || automation == null || automation.isRunning()) {
            return;
        }

        String fingerprint = ResearchSolver.fingerprint(note);
        if (!fingerprint.isEmpty() && !fingerprint.equals(lastAutoFingerprint)) {
            lastAutoFingerprint = fingerprint;
            startSolve(false);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
        if (button == null) return;

        if (button.id == GTNC_AUTO) {
            Config.setTcAutoResearch(!Config.tcAutoResearch);
            autoButton.displayString = ResearchTexts.auto(Config.tcAutoResearch);
            if (!Config.tcAutoResearch) lastAutoFingerprint = "";
            return;
        }
        if (automation != null && automation.isRunning()) {
            PlayerNotifications.addNotification(ResearchTexts.busy());
            return;
        }

        if (button.id == GTNC_UNLOCK_ALL) {
            automation.startUnlockAll(this::refreshButtons);
        } else if (button.id == GTNC_SOLVE) {
            startSolve(true);
        } else if (button.id == GTNC_RETRY) {
            if (note == null || lastPlan == null
                || !ResearchSolver.fingerprint(note)
                    .equals(lastPlan.fingerprint)) {
                PlayerNotifications.addNotification(ResearchTexts.noRetry());
            } else {
                automation.startPlan(lastPlan, this::refreshButtons);
            }
        } else if (button.id == GTNC_CONFIRM) {
            confirmSynthesis();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (amountField != null && amountField.getVisible() && amountField.textboxKeyTyped(typedChar, keyCode)) return;
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void onGuiClosed() {
        if (automation != null) automation.cancel(false);
        super.onGuiClosed();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton != 0 || !isCtrlKeyDown()) {
            if (amountField != null && amountField.getVisible()) hideSynthesisInput();
            return;
        }

        Aspect aspect = getClickedAspect(mouseX, mouseY);
        if (aspect == null) return;
        selectedAspect = aspect;
        amountField.xPosition = mouseX - 18;
        amountField.yPosition = mouseY + 9;
        amountField.setText("1");
        amountField.setFocused(true);
        amountField.setVisible(true);
        confirmButton.xPosition = mouseX + 20;
        confirmButton.yPosition = mouseY + 8;
        confirmButton.visible = true;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (amountField != null && amountField.getVisible()) amountField.drawTextBox();
    }

    private void startSolve(boolean notifyOnMissingNote) {
        if (note == null || note.complete) {
            if (notifyOnMissingNote) PlayerNotifications.addNotification(ResearchTexts.noNote());
            return;
        }
        AspectList pool = Thaumcraft.proxy.getPlayerKnowledge()
            .getAspectsDiscovered(gtncPlayer.getCommandSenderName());
        ResearchSolver.Plan plan = ResearchSolver.solve(note, pool);
        if (plan == null) {
            PlayerNotifications.addNotification(ResearchTexts.solveFailed());
            return;
        }
        lastPlan = plan;
        automation.startPlan(plan, this::refreshButtons);
    }

    private void confirmSynthesis() {
        int amount;
        try {
            amount = Integer.parseInt(amountField.getText());
        } catch (NumberFormatException ignored) {
            amount = 0;
        }
        if (amount <= 0 || selectedAspect == null) {
            PlayerNotifications.addNotification(ResearchTexts.invalidAmount());
        } else {
            automation.startSynthesis(selectedAspect, amount, this::refreshButtons);
        }
        hideSynthesisInput();
    }

    private void hideSynthesisInput() {
        amountField.setFocused(false);
        amountField.setVisible(false);
        confirmButton.visible = false;
    }

    private void refreshButtons() {
        if (unlockAllButton == null) return;
        AspectList pool = Thaumcraft.proxy.getPlayerKnowledge()
            .getAspectsDiscovered(gtncPlayer.getCommandSenderName());
        unlockAllButton.visible = pool != null && pool.aspects.size() < Aspect.aspects.size();
        boolean busy = automation != null && automation.isRunning();
        solveButton.enabled = !busy;
        retryButton.enabled = !busy && lastPlan != null;
        unlockAllButton.enabled = !busy;
    }

    private Aspect getClickedAspect(int mouseX, int mouseY) {
        if (GET_CLICKED_ASPECT == null) return null;
        try {
            int guiX = (width - xSize) / 2;
            int guiY = (height - ySize) / 2;
            return (Aspect) GET_CLICKED_ASPECT.invoke(this, mouseX, mouseY, guiX, guiY, true);
        } catch (ReflectiveOperationException | RuntimeException exception) {
            ScienceNotCool.LOG.warn("Could not read the clicked Thaumcraft aspect", exception);
            return null;
        }
    }

    private static Method findGetClickedAspect() {
        try {
            Method method = GuiResearchTable.class
                .getDeclaredMethod("getClickedAspect", int.class, int.class, int.class, int.class, boolean.class);
            method.setAccessible(true);
            return method;
        } catch (ReflectiveOperationException | RuntimeException exception) {
            ScienceNotCool.LOG.warn("Could not access GuiResearchTable#getClickedAspect", exception);
            return null;
        }
    }
}
