package com.xyp.gtnc.Client.research;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.client.lib.PlayerNotifications;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketAspectCombinationToServer;
import thaumcraft.common.lib.network.playerdata.PacketAspectPlaceToServer;
import thaumcraft.common.tiles.TileResearchTable;

/** Executes research operations on the client tick, using only Thaumcraft's normal server packets. */
public final class ResearchAutomation {

    private static final int PACKET_DELAY_TICKS = 8;

    private final EntityPlayer player;
    private final TileResearchTable table;
    private final Deque<Step> steps = new ArrayDeque<>();
    private int cooldown;
    private boolean running;
    private Runnable completion;

    public ResearchAutomation(EntityPlayer player, TileResearchTable table) {
        this.player = player;
        this.table = table;
    }

    public boolean isRunning() {
        return running;
    }

    public void startPlan(ResearchSolver.Plan plan, Runnable completion) {
        if (running || plan == null) return;
        for (ResearchSolver.Placement erase : plan.erases) steps.addLast(new EraseStep(erase.q, erase.r));
        for (Aspect fixed : plan.fixedAspects) steps.addLast(new DiscoverStep(fixed));
        for (ResearchSolver.Placement placement : plan.placements) {
            steps.addLast(new PlaceStep(placement.q, placement.r, placement.aspect));
        }
        start(completion);
    }

    public void startUnlockAll(Runnable completion) {
        if (running) return;
        List<Aspect> aspects = new ArrayList<>(Aspect.aspects.values());
        aspects.sort(Comparator.comparingInt(ResearchAutomation::depth));
        for (Aspect aspect : aspects) steps.addLast(new DiscoverStep(aspect));
        start(completion);
    }

    public void startSynthesis(Aspect aspect, int amount, Runnable completion) {
        if (running || aspect == null || amount <= 0) return;
        steps.addLast(new ProduceStep(aspect, amount));
        start(completion);
    }

    private void start(Runnable completion) {
        this.completion = completion;
        running = !steps.isEmpty();
        cooldown = 0;
        if (running) PlayerNotifications.addNotification(ResearchTexts.started());
    }

    public void tick() {
        if (!running) return;
        if (cooldown-- > 0) return;
        Step step = steps.peekFirst();
        if (step == null) {
            finish();
            return;
        }

        StepResult result = step.run();
        if (result == StepResult.DONE) {
            steps.removeFirst();
        } else if (result == StepResult.SENT) {
            cooldown = PACKET_DELAY_TICKS;
        } else if (result == StepResult.FAILED) {
            steps.clear();
            running = false;
        }
    }

    public void cancel(boolean notify) {
        if (running && notify) PlayerNotifications.addNotification(ResearchTexts.cancelled());
        steps.clear();
        running = false;
        completion = null;
    }

    private void finish() {
        running = false;
        PlayerNotifications.addNotification(ResearchTexts.complete());
        Runnable callback = completion;
        completion = null;
        if (callback != null) callback.run();
    }

    private StepResult produceOne(Aspect target, Set<Aspect> visiting) {
        Aspect[] components = target == null ? null : target.getComponents();
        if (components == null || !visiting.add(target)) {
            if (target != null) PlayerNotifications.addNotification(ResearchTexts.missingPrimal(target.getName()));
            return StepResult.FAILED;
        }

        int firstNeeded = components[0] == components[1] ? 2 : 1;
        if (available(components[0]) < firstNeeded) {
            StepResult result = produceOne(components[0], visiting);
            visiting.remove(target);
            return result;
        }
        if (components[0] != components[1] && available(components[1]) < 1) {
            StepResult result = produceOne(components[1], visiting);
            visiting.remove(target);
            return result;
        }

        sendCombination(components[0], components[1]);
        visiting.remove(target);
        return StepResult.SENT;
    }

    private int available(Aspect aspect) {
        return pool().getAmount(aspect) + table.bonusAspects.getAmount(aspect);
    }

    private boolean known(Aspect aspect) {
        AspectList known = pool();
        return known.aspects.containsKey(aspect);
    }

    private AspectList pool() {
        return Thaumcraft.proxy.getPlayerKnowledge()
            .getAspectsDiscovered(player.getCommandSenderName());
    }

    private void sendCombination(Aspect first, Aspect second) {
        boolean firstBonus = pool().getAmount(first) <= 0 && table.bonusAspects.getAmount(first) > 0;
        boolean secondBonus = pool().getAmount(second) <= 0 && table.bonusAspects.getAmount(second) > 0;
        PacketHandler.INSTANCE.sendToServer(
            new PacketAspectCombinationToServer(
                player,
                table.xCoord,
                table.yCoord,
                table.zCoord,
                first,
                second,
                firstBonus,
                secondBonus,
                true));
    }

    private void sendPlacement(int q, int r, Aspect aspect) {
        PacketHandler.INSTANCE.sendToServer(
            new PacketAspectPlaceToServer(
                player,
                (byte) q,
                (byte) r,
                table.xCoord,
                table.yCoord,
                table.zCoord,
                aspect));
    }

    private static int depth(Aspect aspect) {
        return depth(aspect, new HashSet<Aspect>());
    }

    private static int depth(Aspect aspect, Set<Aspect> visiting) {
        Aspect[] components = aspect == null ? null : aspect.getComponents();
        if (components == null || !visiting.add(aspect)) return 0;
        int value = 1 + Math.max(depth(components[0], visiting), depth(components[1], visiting));
        visiting.remove(aspect);
        return value;
    }

    private interface Step {

        StepResult run();
    }

    private enum StepResult {
        DONE,
        SENT,
        FAILED
    }

    private final class DiscoverStep implements Step {

        private final Aspect aspect;

        private DiscoverStep(Aspect aspect) {
            this.aspect = aspect;
        }

        @Override
        public StepResult run() {
            if (known(aspect)) return StepResult.DONE;
            return produceOne(aspect, new HashSet<Aspect>());
        }
    }

    private final class EraseStep implements Step {

        private final int q;
        private final int r;
        private boolean sent;

        private EraseStep(int q, int r) {
            this.q = q;
            this.r = r;
        }

        @Override
        public StepResult run() {
            if (sent) return StepResult.DONE;
            sendPlacement(q, r, null);
            sent = true;
            return StepResult.SENT;
        }
    }

    private final class PlaceStep implements Step {

        private final int q;
        private final int r;
        private final Aspect aspect;
        private boolean sent;

        private PlaceStep(int q, int r, Aspect aspect) {
            this.q = q;
            this.r = r;
            this.aspect = aspect;
        }

        @Override
        public StepResult run() {
            if (sent) return StepResult.DONE;
            if (available(aspect) <= 0) return produceOne(aspect, new HashSet<Aspect>());
            sendPlacement(q, r, aspect);
            sent = true;
            return StepResult.SENT;
        }
    }

    private final class ProduceStep implements Step {

        private final Aspect aspect;
        private int remaining;

        private ProduceStep(Aspect aspect, int remaining) {
            this.aspect = aspect;
            this.remaining = remaining;
        }

        @Override
        public StepResult run() {
            if (remaining <= 0) return StepResult.DONE;
            StepResult result = produceOne(aspect, new HashSet<Aspect>());
            if (result == StepResult.SENT) remaining--;
            return result;
        }
    }
}
