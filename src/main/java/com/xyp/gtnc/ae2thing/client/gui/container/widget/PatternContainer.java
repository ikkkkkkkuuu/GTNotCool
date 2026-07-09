package com.xyp.gtnc.ae2thing.client.gui.container.widget;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.glodblock.github.common.item.ItemFluidDrop;
import com.glodblock.github.common.item.ItemFluidEncodedPattern;
import com.glodblock.github.common.item.ItemFluidPacket;
import com.glodblock.github.loader.ItemAndBlockHolder;
import com.glodblock.github.util.FluidPatternDetails;
import com.glodblock.github.util.Util;
import com.xyp.gtnc.ae2thing.api.Constants;
import com.xyp.gtnc.ae2thing.client.gui.container.ContainerWirelessDualInterfaceTerminal;
import com.xyp.gtnc.ae2thing.client.gui.container.IPatternContainer;
import com.xyp.gtnc.ae2thing.client.gui.container.slot.SlotPattern;
import com.xyp.gtnc.ae2thing.client.gui.container.slot.SlotPatternFake;
import com.xyp.gtnc.ae2thing.inventory.IPatternTerminal;
import com.xyp.gtnc.ae2thing.inventory.item.WirelessTerminal;

import appeng.api.AEApi;
import appeng.api.definitions.IDefinitions;
import appeng.api.storage.ITerminalHost;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.container.slot.AppEngSlot;
import appeng.container.slot.IOptionalSlotHost;
import appeng.container.slot.SlotFake;
import appeng.container.slot.SlotFakeCraftingMatrix;
import appeng.container.slot.SlotPatternTerm;
import appeng.container.slot.SlotRestrictedInput;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.util.Platform;
import appeng.util.item.AEFluidStack;
import appeng.util.item.AEItemStack;
import gregtech.common.items.ItemFluidDisplay;

public class PatternContainer implements IPatternContainer, IOptionalSlotHost, IWidgetSlot {

    protected final IInventory crafting;
    protected final IInventory craftingEx;
    protected final IInventory outputEx;
    protected final IInventory patternInv;
    protected final SlotPattern patternSlotIN;
    protected final SlotPattern patternSlotOUT;
    protected SlotPattern patternRefiller;
    protected SlotPatternFake[] craftingExSlots;
    protected SlotPatternFake[] outputExSlots;
    protected SlotFake[] craftingSlots;
    protected SlotPatternTerm craftSlot;
    private static final int CRAFTING_GRID_PAGES = 2;
    private static final int CRAFTING_GRID_WIDTH = 4;
    private static final int CRAFTING_GRID_HEIGHT = 4;
    private static final int CRAFTING_GRID_SLOTS = CRAFTING_GRID_WIDTH * CRAFTING_GRID_HEIGHT;
    protected final AppEngInternalInventory cOut = new AppEngInternalInventory(null, 1);
    private final IPatternTerminal it;
    private final ContainerWirelessDualInterfaceTerminal container;
    private final List<Slot> slots = new ArrayList<>();
    private final ITerminalHost host;

    public PatternContainer(InventoryPlayer ip, ITerminalHost host, ContainerWirelessDualInterfaceTerminal container) {
        this.container = container;
        this.it = (IPatternTerminal) host;
        this.host = host;
        this.crafting = this.it.getInventoryByName(Constants.CRAFTING);
        this.craftingEx = this.it.getInventoryByName(Constants.CRAFTING_EX);
        this.outputEx = this.it.getInventoryByName(Constants.OUTPUT_EX);
        this.patternInv = this.it.getInventoryByName(Constants.PATTERN);
        this.craftingSlots = new SlotFakeCraftingMatrix[9];
        this.craftingExSlots = new SlotPatternFake[CRAFTING_GRID_SLOTS * CRAFTING_GRID_PAGES];
        this.outputExSlots = new SlotPatternFake[CRAFTING_GRID_SLOTS * CRAFTING_GRID_PAGES];
        this.addMESlotToContainer(
            this.patternSlotIN = new SlotPattern(
                SlotRestrictedInput.PlacableItemType.BLANK_PATTERN,
                patternInv,
                0,
                220,
                31,
                ip));
        this.slots.add(this.patternSlotIN);
        this.addMESlotToContainer(
            this.patternSlotOUT = new SlotPattern(
                SlotRestrictedInput.PlacableItemType.ENCODED_PATTERN,
                patternInv,
                1,
                220,
                31 + 43,
                ip));
        this.patternSlotOUT.setStackLimit(1);
        this.slots.add(this.patternSlotOUT);
        if (this.isPatternTerminal()) {
            this.addMESlotToContainer(
                this.patternRefiller = new SlotPattern(
                    SlotRestrictedInput.PlacableItemType.UPGRADES,
                    this.it.getInventoryByName(Constants.UPGRADES),
                    0,
                    217,
                    110,
                    this.container.getInventoryPlayer()));
            this.slots.add(this.patternRefiller);
        }
        for (int page = 0; page < CRAFTING_GRID_PAGES; page++) {
            for (int y = 0; y < CRAFTING_GRID_HEIGHT; y++) {
                for (int x = 0; x < CRAFTING_GRID_WIDTH; x++) {
                    this.addMESlotToContainer(
                        this.craftingExSlots[x + y * CRAFTING_GRID_WIDTH
                            + page * CRAFTING_GRID_SLOTS] = new SlotPatternFake(
                                craftingEx,
                                this,
                                x + y * CRAFTING_GRID_WIDTH + page * CRAFTING_GRID_SLOTS,
                                224,
                                -59,
                                x,
                                y,
                                x + 4));
                    this.slots.add(this.craftingExSlots[x + y * CRAFTING_GRID_WIDTH + page * CRAFTING_GRID_SLOTS]);
                }
            }
            for (int x = 0; x < CRAFTING_GRID_WIDTH; x++) {
                for (int y = 0; y < CRAFTING_GRID_HEIGHT; y++) {
                    this.addMESlotToContainer(
                        this.outputExSlots[x * CRAFTING_GRID_HEIGHT + y
                            + page * CRAFTING_GRID_SLOTS] = new SlotPatternFake(
                                outputEx,
                                this,
                                x * CRAFTING_GRID_HEIGHT + y + page * CRAFTING_GRID_SLOTS,
                                224 + 97,
                                -59,
                                -x,
                                y,
                                x));
                    this.slots.add(this.outputExSlots[x * CRAFTING_GRID_HEIGHT + y + page * CRAFTING_GRID_SLOTS]);
                }
            }
        }
        this.addMESlotToContainer(
            this.craftSlot = new SlotPatternTerm(
                ip.player,
                this.container.getActionSource(),
                this.container.getPowerSource(),
                this.host,
                this.crafting,
                patternInv,
                this.cOut,
                224 + 92,
                -32,
                this,
                0,
                this.container));
        this.craftSlot.setIIcon(-1);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                this.addMESlotToContainer(
                    this.craftingSlots[x
                        + y * 3] = new SlotFakeCraftingMatrix(this.crafting, x + y * 3, 224 + x * 18, -50 + y * 18));
            }
        }

        if (this.hasRefillerUpgrade()) {
            refillBlankPatterns(patternSlotIN);
        }
    }

    public void detectAndSendChanges() {
        if (Platform.isServer()) {
            this.container.substitute = this.it.isSubstitution();
            this.container.combine = this.it.shouldCombine();
            this.container.beSubstitute = this.it.canBeSubstitute();
            this.container.prioritize = this.it.isPrioritize();
            this.container.craftingMode = this.it.isCraftingRecipe();
            if (container.inverted != it.isInverted() || container.activePage != it.getActivePage()) {
                container.inverted = it.isInverted();
                container.activePage = it.getActivePage();
                updateOrderOfOutputSlots();
            }
            if (this.container.isCraftingMode() != this.it.isCraftingRecipe()) {
                this.container.setCraftingMode(this.it.isCraftingRecipe());
                this.updateOrderOfOutputSlots();
            }
        }
    }

    private void updateOrderOfOutputSlots() {
        if (this.container.isCraftingMode()) {
            this.craftSlot.xDisplayPosition = this.craftSlot.getX();
            for (SlotFake slot : this.craftingSlots) {
                slot.xDisplayPosition = slot.getX();
            }
            for (SlotPatternFake slot : this.outputExSlots) {
                slot.setHidden(true);
            }
            for (SlotPatternFake slot : this.craftingExSlots) {
                slot.setHidden(true);
            }
        } else {
            this.craftSlot.xDisplayPosition = -9000;
            for (SlotFake slot : this.craftingSlots) {
                slot.xDisplayPosition = -9000;
            }
            for (SlotPatternFake slot : this.outputExSlots) {
                slot.setHidden(false);
            }
            for (SlotPatternFake slot : this.craftingExSlots) {
                slot.setHidden(false);
            }
            offsetSlots();
        }
    }

    private void offsetSlots() {
        for (int page = 0; page < CRAFTING_GRID_PAGES; page++) {
            for (int y = 0; y < CRAFTING_GRID_HEIGHT; y++) {
                for (int x = 0; x < CRAFTING_GRID_WIDTH; x++) {
                    this.craftingExSlots[x + y * CRAFTING_GRID_WIDTH + page * CRAFTING_GRID_SLOTS]
                        .setHidden(page != container.activePage || x > 0 && container.inverted);
                    this.outputExSlots[x * CRAFTING_GRID_HEIGHT + y + page * CRAFTING_GRID_SLOTS]
                        .setHidden(page != container.activePage || x > 0 && !container.inverted);
                }
            }
        }
    }

    public void onUpdate(String field, Object oldValue, Object newValue) {
        if (field.equals("inverted") || field.equals("activePage")) {
            updateOrderOfOutputSlots();
        }
        if (field.equals("craftingMode")) {
            this.getAndUpdateOutput();
            this.updateOrderOfOutputSlots();
        }
    }

    private final ItemStack[] recipeCache = new ItemStack[10];
    // Marks whether the crafting grid changed since the last getAndUpdateOutput call. Set by onContainerClosed (grid
    // write operations) to skip the expensive 9-slot traversal + NBT comparison when detectAndSendChanges runs every
    // tick with no actual change.
    private boolean recipeDirty = true;

    public ItemStack getAndUpdateOutput() {
        if (!this.container.isCraftingMode()) return null;
        // Fast path: if no write since last call, the cached output is still valid.
        if (!recipeDirty && recipeCache[9] != null) {
            return recipeCache[9];
        }
        boolean sameRecipe = true;
        for (int i = 0; i < this.crafting.getSizeInventory(); i++) {
            if (recipeCache[i] == null && this.crafting.getStackInSlot(i) == null) continue;
            if (!Platform.isSameItemPrecise(recipeCache[i], this.crafting.getStackInSlot(i))) {
                sameRecipe = false;
                break;
            }
        }

        if (!sameRecipe) {
            final InventoryCrafting ic = new InventoryCrafting(this.container, 3, 3);
            for (int x = 0; x < ic.getSizeInventory(); x++) {
                ic.setInventorySlotContents(x, this.crafting.getStackInSlot(x));
            }

            final ItemStack is = CraftingManager.getInstance()
                .findMatchingRecipe(ic, this.container.getPlayerInv().player.worldObj);
            this.cOut.setInventorySlotContents(0, is);
            for (int i = 0; i < this.crafting.getSizeInventory(); i++) {
                recipeCache[i] = this.crafting.getStackInSlot(i);
            }
            recipeCache[9] = is;
            recipeDirty = false;
            return is;
        } else if (recipeCache[9] != null) {
            recipeDirty = false;
            return recipeCache[9];
        }
        recipeDirty = false;
        return null;
    }

    protected void addMESlotToContainer(AppEngSlot newSlot) {
        this.container.addMESlotToContainer(newSlot);
    }

    @Override
    public IPatternTerminal getPatternTerminal() {
        return this.it;
    }

    @Override
    public void clear() {
        for (final Slot s : this.craftingExSlots) {
            s.putStack(null);
        }
        for (final Slot s : this.outputExSlots) {
            s.putStack(null);
        }
        for (final Slot s : this.craftingSlots) {
            s.putStack(null);
        }
        this.getAndUpdateOutput();
        this.detectAndSendChanges();
    }

    @Override
    public void doubleStacks(int val) {
        if (this.container.isCraftingMode()) return;
        boolean isShift = (val & 1) != 0;
        boolean backwards = (val & 2) != 0;
        int multi = isShift ? 8 : 2;
        multi = backwards ? Math.negateExact(multi) : multi;
        if (canDouble(this.craftingExSlots, multi) && canDouble(this.outputExSlots, multi)) {
            doubleStacksInternal(this.craftingExSlots, multi);
            doubleStacksInternal(this.outputExSlots, multi);
        }
        this.detectAndSendChanges();
    }

    @Override
    public Slot getPatternOutputSlot() {
        return this.patternSlotOUT;
    }

    @Override
    public boolean isPatternTerminal() {
        return true;
    }

    @Override
    public boolean hasRefillerUpgrade() {
        return this.getPatternTerminal()
            .hasRefillerUpgrade();
    }

    @Override
    public void refillBlankPatterns(Slot slot) {
        if (Platform.isServer() && this.it instanceof WirelessTerminal wt) {
            ItemStack blanks = slot.getStack();
            int blanksToRefill = 64;
            if (blanks != null) blanksToRefill -= blanks.stackSize;
            if (blanksToRefill <= 0) return;
            final AEItemStack request = AEItemStack.create(
                AEApi.instance()
                    .definitions()
                    .materials()
                    .blankPattern()
                    .maybeStack(blanksToRefill)
                    .get());
            final IAEItemStack extracted = Platform
                .poweredExtraction(wt, wt.getItemInventory(), request, wt.getActionSource());
            if (extracted != null) {
                if (blanks != null) blanks.stackSize += extracted.getStackSize();
                else {
                    blanks = extracted.getItemStack();
                }
                slot.putStack(blanks);
            }
        }
    }

    protected static boolean containsFluid(SlotFake[] slots) {
        for (SlotFake slot : slots) {
            if (slot.isEnabled() && isAnyFluidItem(slot.getStack())) {
                return true;
            }
        }
        return false;
    }

    protected static boolean nonNullSlot(SlotFake[] slots) {
        for (SlotFake slot : slots) {
            if (slot.isEnabled() && slot.getStack() != null) {
                return true;
            }
        }
        return false;
    }

    protected ItemStack[] getInputs() {
        final ArrayList<ItemStack> input = new ArrayList<>();
        boolean hasInput = false;
        if (this.container.isCraftingMode()) {
            for (SlotFake craftingSlot : this.craftingSlots) {
                ItemStack stack = craftingSlot.getStack();
                input.add(stack);
                hasInput |= stack != null;
            }
            if (hasInput) {
                return input.toArray(new ItemStack[0]);
            }
        } else {
            for (SlotFake craftingSlot : this.craftingExSlots) {
                ItemStack stack = craftingSlot.getStack();
                input.add(stack);
                hasInput |= stack != null;
            }
            if (hasInput) {
                return input.toArray(new ItemStack[0]);
            }
        }

        return null;
    }

    protected ItemStack[] getOutputs() {
        final ArrayList<ItemStack> output = new ArrayList<>();
        if (this.container.isCraftingMode()) {
            final ItemStack out = this.getAndUpdateOutput();
            if (out != null && out.stackSize > 0) {
                return new ItemStack[] { out };
            }
        } else {
            boolean hasOutput = false;
            for (final SlotFake outputSlot : this.outputExSlots) {
                ItemStack stack = outputSlot.getStack();
                output.add(stack);
                hasOutput |= stack != null;
            }
            if (hasOutput) {
                return output.toArray(new ItemStack[0]);
            }
        }
        return null;
    }

    protected boolean notPattern(final ItemStack output) {
        if (output == null) {
            return true;
        }
        if (output.getItem() instanceof ItemFluidEncodedPattern) {
            return false;
        }
        final IDefinitions definitions = AEApi.instance()
            .definitions();

        boolean isPattern = definitions.items()
            .encodedPattern()
            .isSameAs(output);
        // New GTNH standard: an already-encoded Ultimate Pattern must also count as a pattern, otherwise
        // re-encoding over an existing output pattern is refused (encodeItemPattern returns early) and the
        // user can never overwrite the placeholder in the output slot with a new pattern.
        isPattern |= definitions.items()
            .encodedUltimatePattern()
            .isSameAs(output);
        isPattern |= definitions.materials()
            .blankPattern()
            .isSameAs(output);

        return !isPattern;
    }

    protected boolean checkHasFluidPattern() {
        if (this.container.craftingMode) {
            return false;
        }
        boolean hasFluid = containsFluid(this.craftingExSlots);
        boolean search = nonNullSlot(this.craftingExSlots);
        if (!search) { // search=false -> inputs were empty
            return false;
        }
        hasFluid |= containsFluid(this.outputExSlots);
        search = nonNullSlot(this.outputExSlots);
        return hasFluid && search; // search=false -> outputs were empty
    }

    public void encodeItemPattern() {
        ItemStack output = this.patternSlotOUT.getStack();
        final ItemStack[] in = this.getInputs();
        final ItemStack[] out = this.getOutputs();

        // if there is no input, this would be silly.
        if (in == null || out == null) {
            return;
        }
        // first check the output slots, should either be null, or a pattern
        if (output != null && this.notPattern(output)) {
            return;
        }
        // Pick the pattern item by mode. GTNH convention: a CRAFTING pattern must use vanilla AE2's encoded pattern
        // (its FluidPatternDetails/PatternHelper reports isCraftable()=true), while a PROCESSING pattern uses the
        // Encoded Ultimate Pattern (AE2FC). Encoding a crafting recipe onto an ultimate pattern makes it read back as a
        // processing pattern, which then forces the terminal back into processing mode.
        final ItemStack ultimatePattern = AEApi.instance()
            .definitions()
            .items()
            .encodedUltimatePattern()
            .maybeStack(1)
            .orNull();
        final ItemStack vanillaPattern = AEApi.instance()
            .definitions()
            .items()
            .encodedPattern()
            .maybeStack(1)
            .orNull();
        // The pattern item this mode should produce.
        final ItemStack targetPattern = this.container.craftingMode
            ? (vanillaPattern != null ? vanillaPattern : ultimatePattern)
            : (ultimatePattern != null ? ultimatePattern : vanillaPattern);
        if (output == null) {
            // Grab a blank pattern from the input slot
            output = this.patternSlotIN.getStack();
            if (this.notPattern(output)) {
                return; // no blanks.
            }
            output.stackSize--;
            if (output.stackSize == 0) {
                this.patternSlotIN.putStack(null);
            }
            output = targetPattern != null ? targetPattern.copy() : null;
        } else {
            // Re-encoding over an existing pattern — reuse the item that matches the current mode.
            output = targetPattern != null ? targetPattern.copy() : output;
        }
        if (output == null) {
            return;
        }

        // encode the slot using IAEStack for proper fluid support
        final NBTTagCompound encodedValue = new NBTTagCompound();

        // Convert to IAEStack<?>[] so fluids are written as IAEFluidStack (not items)
        IAEStack<?>[] encodedIn = itemsToAEStack(in);
        IAEStack<?>[] encodedOut = itemsToAEStack(out);
        encodedValue.setTag("in", FluidPatternDetails.writeStackArray(encodedIn));
        encodedValue.setTag("out", FluidPatternDetails.writeStackArray(encodedOut));
        encodedValue.setBoolean("crafting", this.container.craftingMode);
        encodedValue.setBoolean("substitute", this.container.substitute);
        encodedValue.setBoolean("beSubstitute", this.container.beSubstitute);
        encodedValue.setBoolean("prioritize", this.container.prioritize);
        output.setTagCompound(encodedValue);
        stampAuthor(output);
        this.patternSlotOUT.putStack(output);
    }

    private static IAEStack<?>[] itemsToAEStack(ItemStack[] items) {
        // Keep the array the SAME length as the input grid and leave empty slots as null.
        // AE2FC's FluidPatternDetails.writeStackArray/setInputs encodes by POSITION (null slots write empty tags),
        // and shaped crafting recipes (e.g. for the Molecular Assembler) rely on those positions. Filtering out
        // nulls here compacts the array, so an ingredient in slot 2 shifts to slot 1 after encoding.
        IAEStack<?>[] stacks = new IAEStack<?>[items.length];
        for (int i = 0; i < items.length; i++) {
            ItemStack stack = items[i];
            if (stack == null) continue;
            IAEFluidStack fluidStack = toAEFluidStack(stack);
            if (fluidStack != null) {
                stacks[i] = fluidStack;
                continue;
            }
            stacks[i] = AEItemStack.create(stack);
        }
        return stacks;
    }

    protected ItemStack stampAuthor(ItemStack patternStack) {
        if (patternStack.stackTagCompound == null) {
            patternStack.stackTagCompound = new NBTTagCompound();
        }
        patternStack.stackTagCompound.setString("author", this.container.getPlayerInv().player.getCommandSenderName());
        return patternStack;
    }

    protected NBTBase createItemTag(final ItemStack i) {
        final NBTTagCompound c = new NBTTagCompound();
        if (i != null) {
            Util.writeItemStackToNBT(i, c);
        }
        return c;
    }

    @Override
    public void encode() {
        if (this.hasRefillerUpgrade()) refillBlankPatterns(this.patternSlotIN);
        if (!checkHasFluidPattern()) {
            encodeItemPattern();
            return;
        }
        ItemStack stack = this.patternSlotOUT.getStack();
        if (stack == null) {
            stack = this.patternSlotIN.getStack();
            if (notPattern(stack)) {
                return;
            }
            if (stack.stackSize == 1) {
                this.patternSlotIN.putStack(null);
            } else {
                stack.stackSize--;
            }
            encodeFluidPattern();
        } else if (!notPattern(stack)) {
            encodeFluidPattern();
        }
    }

    @Nullable
    private static IAEFluidStack toAEFluidStack(ItemStack stack) {
        FluidStack fs = null;
        if (stack.getItem() instanceof ItemFluidDrop) {
            fs = ItemFluidDrop.getFluidStack(stack);
        } else if (stack.getItem() instanceof ItemFluidDisplay) {
            if (stack.getTagCompound() != null) {
                Fluid fluid = FluidRegistry.getFluid(stack.getItemDamage());
                int amt = (int) stack.getTagCompound()
                    .getLong("mFluidDisplayAmount");
                if (amt > 0 && fluid != null) fs = new FluidStack(fluid, amt);
            }
        } else if (stack.getItem() instanceof ItemFluidPacket) {
            fs = ItemFluidPacket.getFluidStack(stack);
        }
        return fs != null ? AEFluidStack.create(fs) : null;
    }

    private static boolean isAnyFluidItem(ItemStack stack) {
        if (stack == null) return false;
        return stack.getItem() instanceof ItemFluidDrop || stack.getItem() instanceof ItemFluidDisplay
            || stack.getItem() instanceof ItemFluidPacket;
    }

    protected static IAEStack<?>[] collectInventory(Slot[] slots) {
        List<IAEStack<?>> stacks = new ArrayList<>();
        for (Slot slot : slots) {
            ItemStack stack = slot.getStack();
            if (stack != null) {
                IAEFluidStack fluidStack = toAEFluidStack(stack);
                if (fluidStack != null) {
                    stacks.add(fluidStack);
                    continue;
                }
                IAEItemStack aeStack = AEItemStack.create(stack);
                if (aeStack != null) stacks.add(aeStack);
            }
        }
        return stacks.toArray(new IAEStack<?>[0]);
    }

    protected void encodeFluidPattern() {
        ItemStack patternStack = AEApi.instance()
            .definitions()
            .items()
            .encodedUltimatePattern()
            .maybeStack(1)
            .orNull();
        if (patternStack == null) {
            patternStack = new ItemStack(ItemAndBlockHolder.PATTERN);
        }
        FluidPatternDetails pattern = new FluidPatternDetails(patternStack);
        IAEStack<?>[] inputs = collectInventory(this.craftingExSlots);
        IAEStack<?>[] outputs = collectInventory(this.outputExSlots);
        pattern.setInputs(inputs);
        pattern.setOutputs(outputs);
        pattern.setCanBeSubstitute(this.container.beSubstitute ? 1 : 0);
        ItemStack result = pattern.writeToStack();
        NBTTagCompound tag = result.getTagCompound();
        if (tag == null) tag = new NBTTagCompound();
        // Override in/out with IAEStack<?>[] using writeStackNBT (supports IAEFluidStack)
        tag.setTag("in", FluidPatternDetails.writeStackArray(inputs));
        tag.setTag("out", FluidPatternDetails.writeStackArray(outputs));
        result.setTagCompound(tag);
        patternSlotOUT.putStack(stampAuthor(result));
    }

    @Override
    public void encodeAndMoveToInventory() {
        this.encode();
        ItemStack output = this.patternSlotOUT.getStack();
        if (output != null) {
            if (!this.container.getPlayerInv()
                .addItemStackToInventory(output)) {
                this.container.getPlayerInv().player.entityDropItem(output, 0);
            }
            this.patternSlotOUT.putStack(null);
        }
        if (this.hasRefillerUpgrade()) refillBlankPatterns(patternSlotIN);
    }

    @Override
    public void encodeAllItemAndMoveToInventory() {
        this.encode();
        ItemStack output = this.patternSlotOUT.getStack();
        if (output != null) {
            if (this.patternSlotIN.getStack() != null) output.stackSize += this.patternSlotIN.getStack().stackSize;
            if (!this.container.getPlayerInv()
                .addItemStackToInventory(output)) {
                this.container.getPlayerInv().player.entityDropItem(output, 0);
            }
            this.patternSlotOUT.putStack(null);
            this.patternSlotIN.putStack(null);
        }
        if (this.hasRefillerUpgrade()) refillBlankPatterns(patternSlotIN);
    }

    @Override
    public boolean isSlotEnabled(int idx) {
        if (idx < 4) // outputs
        {
            return this.container.inverted || idx == 0;
        } else {
            return !this.container.inverted || idx == 4;
        }
    }

    public void onSlotChange(Slot s) {
        // Mark recipe dirty whenever any crafting grid slot changes, so getAndUpdateOutput knows to recompute.
        if (s instanceof SlotFakeCraftingMatrix) {
            recipeDirty = true;
        }
        if (s == this.patternSlotOUT && Platform.isServer()) {
            this.container.setInverted(this.it.isInverted());
            for (final Object crafter : this.container.getCrafters()) {
                final ICrafting icrafting = (ICrafting) crafter;

                for (final Object g : this.container.inventorySlots) {
                    if (g instanceof SlotFake sri) {
                        icrafting.sendSlotContents(this.container, sri.slotNumber, sri.getStack());
                    }
                }
                ((EntityPlayerMP) icrafting).isChangingQuantityOnly = false;
            }
            this.detectAndSendChanges();
        }

    }

    @Override
    public List<Slot> getSlot() {
        return this.slots;
    }
}
