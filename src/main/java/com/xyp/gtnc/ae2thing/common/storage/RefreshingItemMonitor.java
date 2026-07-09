package com.xyp.gtnc.ae2thing.common.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;

import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.storage.IBaseMonitor;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IMEMonitorHandlerReceiver;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;

/**
 * A delegating wrapper around the network item monitor that adds the missing "external change" detection needed by the
 * ME terminal panel.
 * <p>
 * AE2's {@code CraftingGridCache.updatePatterns()} posts only the CURRENT craftable set via
 * {@code postAlterationOfStoredItems}; it never emits a change event for a craftable that dropped out when a pattern is
 * removed. So the terminal learns about new craftables (live) but never learns a craftable vanished, and the client
 * repo (add/update-only) keeps showing it until the GUI is reopened.
 * <p>
 * This wrapper forwards the real change events verbatim (preserving the instant add/update behavior) and, in addition,
 * snapshot-diffs the storage list every few ticks so removed/added craftables produce synthetic change events. The
 * consuming {@code ItemMonitor.processItemList()} already calls
 * {@link RefreshableStorageMonitor#refreshExternalChanges}
 * when the monitor implements it.
 */
public class RefreshingItemMonitor
    implements IMEMonitor<IAEItemStack>, IMEMonitorHandlerReceiver<IAEItemStack>, RefreshableStorageMonitor {

    private static final int EXTERNAL_REFRESH_INTERVAL = 5;

    private final IMEMonitor<IAEItemStack> delegate;
    private final EntityPlayer player;
    private final Map<IMEMonitorHandlerReceiver, Object> listeners = new HashMap<>();

    private IItemList<IAEItemStack> lastSnapshot;
    private int lastRefreshTick = Integer.MIN_VALUE;

    public RefreshingItemMonitor(IMEMonitor<IAEItemStack> delegate, EntityPlayer player) {
        this.delegate = delegate;
        this.player = player;
    }

    // --- listener management: keep our own list, subscribe to the delegate lazily ---------------------------------

    @Override
    public void addListener(IMEMonitorHandlerReceiver l, Object verificationToken) {
        if (this.listeners.isEmpty()) {
            this.delegate.addListener(this, null);
        }
        this.listeners.put(l, verificationToken);
    }

    @Override
    public void removeListener(IMEMonitorHandlerReceiver l) {
        this.listeners.remove(l);
        if (this.listeners.isEmpty()) {
            this.delegate.removeListener(this);
        }
    }

    // --- as a receiver on the delegate: forward real change events verbatim ---------------------------------------

    @Override
    public boolean isValid(Object verificationToken) {
        return true;
    }

    @Override
    public void postChange(IBaseMonitor<IAEItemStack> monitor, Iterable<IAEItemStack> change,
        BaseActionSource actionSource) {
        this.notifyListeners(change, actionSource);
    }

    @Override
    public void onListUpdate() {
        final Iterator<Map.Entry<IMEMonitorHandlerReceiver, Object>> i = this.listeners.entrySet()
            .iterator();
        while (i.hasNext()) {
            final Map.Entry<IMEMonitorHandlerReceiver, Object> e = i.next();
            final IMEMonitorHandlerReceiver receiver = e.getKey();
            if (receiver.isValid(e.getValue())) {
                receiver.onListUpdate();
            } else {
                i.remove();
            }
        }
    }

    private void notifyListeners(Iterable<IAEItemStack> change, BaseActionSource src) {
        final Iterator<Map.Entry<IMEMonitorHandlerReceiver, Object>> i = this.listeners.entrySet()
            .iterator();
        while (i.hasNext()) {
            final Map.Entry<IMEMonitorHandlerReceiver, Object> e = i.next();
            final IMEMonitorHandlerReceiver receiver = e.getKey();
            if (receiver.isValid(e.getValue())) {
                receiver.postChange(this, change, src);
            } else {
                i.remove();
            }
        }
    }

    // --- external-change detection --------------------------------------------------------------------------------

    @Override
    public IItemList<IAEItemStack> refreshExternalChanges(BaseActionSource source, boolean force) {
        // Consumers always want the FULL storage list (ItemMonitor.processItemList looks up absolute counts, incl.
        // non-craftables, against it). The snapshot/diff we keep internally is ONLY the craftable subset, because the
        // sole job of this class is catching craftables that vanished on pattern removal (AE2 never emits an event for
        // that). Quantity changes and new/updated items already arrive live via postChange, so diffing the whole table
        // — tens of thousands of stacks copied every few ticks — was pure waste.
        final IItemList<IAEItemStack> current = this.delegate.getStorageList();

        if (!force && !this.shouldRefresh()) {
            return current;
        }

        final IItemList<IAEItemStack> currentCraftables = this.copyCraftables(current);
        this.lastRefreshTick = this.currentTick();

        if (this.lastSnapshot == null) {
            this.lastSnapshot = currentCraftables;
            return current;
        }

        final List<IAEStack<?>> changes = this.calculateChanges(this.lastSnapshot, currentCraftables);
        this.lastSnapshot = currentCraftables;
        if (!changes.isEmpty()) {
            this.notifyListeners(cast(changes), source);
        }
        return current;
    }

    private boolean shouldRefresh() {
        if (this.lastSnapshot == null) {
            return true;
        }
        final int tick = this.currentTick();
        if (tick == this.lastRefreshTick) {
            return false;
        }
        return tick - this.lastRefreshTick >= EXTERNAL_REFRESH_INTERVAL;
    }

    private int currentTick() {
        return this.player == null ? 0 : this.player.ticksExisted;
    }

    private IItemList<IAEItemStack> copyCraftables(IItemList<IAEItemStack> source) {
        // Only the craftable subset is retained: it's typically a few hundred entries against a storage list of tens of
        // thousands, so this is where the allocation savings come from. calculateChanges below only ever needs to know
        // which craftables appeared/vanished, so non-craftables would be dead weight in the snapshot.
        final IItemList<IAEItemStack> copy = AEApi.instance()
            .storage()
            .createItemList();
        for (IAEItemStack stack : source) {
            if (stack.isCraftable()) {
                copy.addStorage(stack.copy());
            }
        }
        return copy;
    }

    private List<IAEStack<?>> calculateChanges(IItemList<IAEItemStack> previous, IItemList<IAEItemStack> current) {
        final List<IAEStack<?>> changes = new ArrayList<>();
        // Appeared or size-changed entries.
        for (IAEItemStack cur : current) {
            final IAEItemStack prev = previous.findPrecise(cur);
            final long prevSize = prev == null ? 0 : prev.getStackSize();
            final boolean craftableChanged = prev == null ? cur.isCraftable() : prev.isCraftable() != cur.isCraftable();
            if (cur.getStackSize() - prevSize != 0 || craftableChanged) {
                final IAEItemStack change = cur.copy();
                change.setStackSize(cur.getStackSize() - prevSize);
                changes.add(change);
            }
        }
        // Vanished entries: the removed craftable no longer appears in the current storage list. We must post a change
        // that makes ItemMonitor send a size-0 / non-craftable record so the client prunes it. Two filters are in the
        // way:
        // 1. ItemMonitor.processItemList iterates `this.items` with a MeaningfulItemIterator, which DROPS size-0 /
        // non-craftable entries before they reach the "vanished" (else) branch. So the queued change must be
        // MEANINGFUL to survive — hence a non-zero sentinel size.
        // 2. The client ItemRepo.add() OR-s the craftable flag, so the record must carry craftable=false or a removed
        // craftable would linger. reset() clears craftable/requestable.
        // ItemMonitor's else branch calls setStackSize(0) before sending, so the sentinel size is internal only and
        // never reaches the client (a naive setStackSize(0) here, like the reference's generic diff, gets filtered out
        // and is exactly why removed craftables never cleared).
        for (IAEItemStack prev : previous) {
            if (current.findPrecise(prev) == null) {
                final IAEItemStack change = prev.copy();
                change.reset();
                change.setStackSize(-1);
                changes.add(change);
            }
        }
        return changes;
    }

    @SuppressWarnings("unchecked")
    private static Iterable<IAEItemStack> cast(List<IAEStack<?>> list) {
        return (Iterable<IAEItemStack>) (Iterable<?>) list;
    }

    // --- delegated IMEMonitor / IMEInventoryHandler / IMEInventory surface -----------------------------------------

    @Override
    public IItemList<IAEItemStack> getStorageList() {
        return this.delegate.getStorageList();
    }

    @Override
    public IItemList<IAEItemStack> getAvailableItems(IItemList out, int iteration) {
        return this.delegate.getAvailableItems(out, iteration);
    }

    @Override
    public IAEItemStack injectItems(IAEItemStack input, Actionable type, BaseActionSource src) {
        return this.delegate.injectItems(input, type, src);
    }

    @Override
    public IAEItemStack extractItems(IAEItemStack request, Actionable mode, BaseActionSource src) {
        return this.delegate.extractItems(request, mode, src);
    }

    @Override
    public StorageChannel getChannel() {
        return this.delegate.getChannel();
    }

    @Override
    public AccessRestriction getAccess() {
        return this.delegate.getAccess();
    }

    @Override
    public boolean isPrioritized(IAEItemStack input) {
        return this.delegate.isPrioritized(input);
    }

    @Override
    public boolean canAccept(IAEItemStack input) {
        return this.delegate.canAccept(input);
    }

    @Override
    public int getPriority() {
        return this.delegate.getPriority();
    }

    @Override
    public int getSlot() {
        return this.delegate.getSlot();
    }

    @Override
    public boolean validForPass(int i) {
        return this.delegate.validForPass(i);
    }
}
