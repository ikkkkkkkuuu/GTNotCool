package com.xyp.gtnc.Common.machines.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlockAnyMeta;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static com.xyp.gtnc.ScienceNotCool.RESOURCE_ROOT_ID;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.dreammaster.block.BlockList;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.xyp.gtnc.Common.gui.modularui.multiblock.GTNCMultiBlockBaseGui;
import com.xyp.gtnc.Common.machines.multiblock.multiMachineBase.GTNCMultiBlockBase;
import com.xyp.gtnc.Loader.GTNCRecipeMaps;
import com.xyp.gtnc.utils.StructureUtils;
import com.xyp.gtnc.utils.lang.TextLocalization;

import WayofTime.alchemicalWizardry.ModBlocks;
import WayofTime.alchemicalWizardry.api.items.interfaces.IBindable;
import WayofTime.alchemicalWizardry.api.soulNetwork.SoulNetworkHandler;
import WayofTime.alchemicalWizardry.common.entity.projectile.EntityMeteor;
import cpw.mods.fml.common.Optional;
import goodgenerator.loader.Loaders;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.api.util.ParallelHelper;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

@IMetaTileEntity.SkipGenerateDescription
public class BloodSoulSacrificialArray extends GTNCMultiBlockBase<BloodSoulSacrificialArray>
    implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String STRUCTURE_FILE = RESOURCE_ROOT_ID + ":multiblock/blood_soul_sacrificial_array";
    private static final String[][] SHAPE = StructureUtils.readStructureFromFile(STRUCTURE_FILE);
    private static final int HORIZONTAL_OFFSET = 16;
    private static final int VERTICAL_OFFSET = 10;
    private static final int DEPTH_OFFSET = 9;
    private static final int BASE_PARALLEL = 8;
    private static final int MODE_BLOOD_DEMON = 0;
    private static final int MODE_FALLING_TOWER = 1;
    private static final int MODE_ALCHEMIC = 2;
    private static final double LP_DISCOUNT_PER_UPGRADE_TIER = 0.02;

    private static final Block BLOODY_ICHORIUM = Mods.NewHorizonsCoreMod.isModLoaded() ? getBloodyIchorium()
        : Blocks.diamond_block;
    private static final Block BLOODY_THAUMIUM = Mods.NewHorizonsCoreMod.isModLoaded() ? getBloodyThaumium()
        : Blocks.gold_block;
    private static final Block BLOODY_VOID = Mods.NewHorizonsCoreMod.isModLoaded() ? getBloodyVoid()
        : Blocks.iron_block;
    private static final Block BLOOD_LAMP = Mods.BloodArsenal.isModLoaded() ? getBloodLamp() : Blocks.glowstone;
    private static final Block LP_MATERIALIZER = Mods.BloodArsenal.isModLoaded() ? getLpMaterializer() : Blocks.hopper;

    private boolean creativeOrb;
    private boolean enableRender = true;
    private int currentEssence;

    public BloodSoulSacrificialArray(int id, String name, String regionalName) {
        super(id, name, regionalName);
    }

    public BloodSoulSacrificialArray(String name) {
        super(name);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new BloodSoulSacrificialArray(mName);
    }

    @Override
    public IStructureDefinition<BloodSoulSacrificialArray> getStructureDefinition() {
        return StructureDefinition.<BloodSoulSacrificialArray>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(SHAPE))
            .addElement('A', ofBlock(Loaders.FRF_Casings, 0))
            .addElement(
                'B',
                buildHatchAdder(BloodSoulSacrificialArray.class).hint(1)
                    .atLeast(Maintenance, InputBus, OutputBus)
                    .casingIndex(getCasingTextureID())
                    .buildAndChain(GregTechAPI.sBlockCasings8, 10))
            .addElement('C', ofBlock(gtPlusPlus.core.block.ModBlocks.blockSpecialMultiCasings, 13))
            .addElement('D', ofBlock(gtPlusPlus.core.block.ModBlocks.blockCasingsMisc, 9))
            .addElement('E', ofBlockAnyMeta(BLOODY_ICHORIUM))
            .addElement('F', ofBlockAnyMeta(BLOODY_THAUMIUM))
            .addElement('G', ofBlockAnyMeta(BLOODY_VOID))
            .addElement('H', ofBlock(Blocks.diamond_block, 0))
            .addElement('I', ofBlock(ModBlocks.bloodRune, 0))
            .addElement('J', ofBlock(ModBlocks.bloodRune, 3))
            .addElement('K', ofBlock(ModBlocks.bloodRune, 4))
            .addElement('L', ofBlock(ModBlocks.bloodRune, 5))
            .addElement('M', ofBlock(ModBlocks.bloodRune, 6))
            .addElement('N', ofBlockAnyMeta(BLOOD_LAMP))
            .addElement('O', ofBlockAnyMeta(ModBlocks.blockCrystal))
            .addElement('P', ofBlockAnyMeta(ModBlocks.largeBloodStoneBrick))
            .addElement('Q', ofBlockAnyMeta(Blocks.glowstone))
            .addElement('R', ofBlockAnyMeta(ModBlocks.ritualStone))
            .addElement('S', ofBlockAnyMeta(ModBlocks.runeOfSacrifice))
            .addElement('T', ofBlockAnyMeta(ModBlocks.runeOfSelfSacrifice))
            .addElement('U', ofBlockAnyMeta(ModBlocks.speedRune))
            .addElement('V', ofBlock(Blocks.beacon, 0))
            .addElement('W', ofBlockAnyMeta(LP_MATERIALIZER))
            .addElement('X', ofFrame(Materials.NaquadahAlloy))
            .addElement('Y', ofBlockAnyMeta(ModBlocks.ritualStone))
            .addElement(
                'Z',
                buildHatchAdder(BloodSoulSacrificialArray.class).hint(1)
                    .atLeast(Maintenance, InputBus, OutputBus)
                    .casingIndex(getCasingTextureID())
                    .buildAndChain(GregTechAPI.sBlockCasings8, 3))
            .addElement('0', ofBlockAnyMeta(ModBlocks.blockAltar))
            .addElement('1', ofBlockAnyMeta(Blocks.hopper))
            .addElement('2', ofFrame(Materials.Plutonium))
            .addElement('3', ofBlockAnyMeta(ModBlocks.bloodStoneBrick))
            .build();
    }

    @Override
    public void checkMachine(IGregTechTileEntity tileEntity, ItemStack stack, List<StructureError> errors) {
        checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFFSET, VERTICAL_OFFSET, DEPTH_OFFSET, errors);
    }

    @Override
    public void construct(ItemStack stack, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stack, hintsOnly, HORIZONTAL_OFFSET, VERTICAL_OFFSET, DEPTH_OFFSET);
    }

    @Override
    public int survivalConstruct(ItemStack stack, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivalBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stack,
            HORIZONTAL_OFFSET,
            VERTICAL_OFFSET,
            DEPTH_OFFSET,
            elementBudget,
            env,
            false,
            true);
    }

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        creativeOrb = false;
        ItemStack armokOrb = GTModHandler.getModItem(Mods.Avaritia.ID, "Orb_Armok", 1);
        if (armokOrb != null) {
            for (ItemStack item : getAllStoredInputs()) {
                if (item != null && item.isItemEqual(armokOrb)) {
                    creativeOrb = true;
                    break;
                }
            }
        }
        return super.checkProcessing();
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @Nonnull
            @Override
            protected CheckRecipeResult validateRecipe(@Nonnull GTRecipe recipe) {
                int lpPerRecipe = getDiscountedLpCost(recipe);
                if (creativeOrb || lpPerRecipe <= 0) return CheckRecipeResultRegistry.SUCCESSFUL;
                refreshCurrentEssence();
                return currentEssence >= lpPerRecipe ? CheckRecipeResultRegistry.SUCCESSFUL
                    : SimpleCheckRecipeResult.ofFailure("metadata.blood");
            }

            @Nonnull
            @Override
            protected ParallelHelper createParallelHelper(@Nonnull GTRecipe recipe) {
                int allowedParallel = getMaxParallelRecipes();
                int lpPerRecipe = getDiscountedLpCost(recipe);
                if (!creativeOrb && lpPerRecipe > 0) {
                    refreshCurrentEssence();
                    allowedParallel = Math.min(allowedParallel, currentEssence / lpPerRecipe);
                }
                return new ParallelHelper().setRecipe(recipe)
                    .setItemInputs(inputItems)
                    .setFluidInputs(inputFluids)
                    .setAvailableEUt(availableVoltage * availableAmperage)
                    .setMachine(machine, protectItems, protectFluids)
                    .setRecipeLocked(recipeLockableMachine, isRecipeLocked)
                    .setMaxParallel(allowedParallel)
                    .setEUtModifier(euModifier)
                    .setConsumption(true)
                    .setOutputCalculation(true);
            }

            @Nonnull
            @Override
            protected CheckRecipeResult onRecipeStart(@Nonnull GTRecipe recipe) {
                if (creativeOrb) return CheckRecipeResultRegistry.SUCCESSFUL;
                int lpPerRecipe = getDiscountedLpCost(recipe);
                if (lpPerRecipe <= 0) return CheckRecipeResultRegistry.SUCCESSFUL;

                refreshCurrentEssence();
                long totalCost = (long) lpPerRecipe * calculatedParallels;
                if (totalCost > currentEssence) return SimpleCheckRecipeResult.ofFailure("metadata.blood");
                currentEssence -= (int) totalCost;
                SoulNetworkHandler.setCurrentEssence(getLpOwner(), currentEssence);
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @Nonnull
            @Override
            protected OverclockCalculator createOverclockCalculator(@Nonnull GTRecipe recipe) {
                return OverclockCalculator.ofNoOverclock(recipe)
                    .setDurationModifier(getUpgradeSpeedBonus());
            }
        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    private int getDiscountedLpCost(GTRecipe recipe) {
        if (recipe.mSpecialValue <= 0) return 0;
        double multiplier = Math.max(0.0, 1.0 - mUpgradeTier * LP_DISCOUNT_PER_UPGRADE_TIER);
        return Math.max(1, (int) Math.ceil(recipe.mSpecialValue * multiplier));
    }

    private void refreshCurrentEssence() {
        currentEssence = SoulNetworkHandler.getCurrentEssence(getLpOwner());
    }

    private String getLpOwner() {
        ItemStack stack = getControllerSlot();
        if (stack != null) {
            Item item = stack.getItem();
            if (item instanceof IBindable) {
                String owner = IBindable.getOwnerName(stack);
                if (owner != null && !owner.isEmpty()) return owner;
            }
        }
        return getBaseMetaTileEntity().getOwnerName();
    }

    @Override
    public int getMaxParallelRecipes() {
        return BASE_PARALLEL + getUpgradeParallelBonus();
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return switch (machineMode) {
            case MODE_FALLING_TOWER -> GTNCRecipeMaps.FallingTowerRecipes;
            case MODE_ALCHEMIC -> GTNCRecipeMaps.AlchemicChemistrySetRecipes;
            default -> GTNCRecipeMaps.BloodDemonInjectionRecipes;
        };
    }

    @Nonnull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(
            GTNCRecipeMaps.BloodDemonInjectionRecipes,
            GTNCRecipeMaps.FallingTowerRecipes,
            GTNCRecipeMaps.AlchemicChemistrySetRecipes);
    }

    @Override
    public boolean onRunningTick(ItemStack stack) {
        if ((mProgresstime + 1) % 20 == 0 && mProgresstime > 0
            && machineMode == MODE_FALLING_TOWER
            && enableRender
            && mMaxProgresstime - mProgresstime < 250) {
            IGregTechTileEntity base = getBaseMetaTileEntity();
            World world = base.getWorld();
            ForgeDirection back = base.getFrontFacing()
                .getOpposite();
            if (!world.isRemote) {
                EntityMeteor meteor = new EntityMeteor(
                    world,
                    base.getXCoord() + back.offsetX * 4 + 0.5,
                    257,
                    base.getZCoord() + back.offsetZ * 4 + 0.5,
                    114514);
                meteor.motionY = -1.0f;
                world.spawnEntityInWorld(meteor);
            }
        }
        return super.onRunningTick(stack);
    }

    @Override
    public void onPostTick(IGregTechTileEntity tileEntity, long tick) {
        super.onPostTick(tileEntity, tick);
        if (tileEntity.isServerSide() && tick % 20 == 0) refreshCurrentEssence();
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new GTNCMultiBlockBaseGui<>(this).withMachineModeIcons(
            GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_LPF_FLUID,
            GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_LPF_METAL,
            GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_PACKAGER);
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return true;
    }

    @Override
    public boolean supportsBatchMode() {
        return false;
    }

    @Override
    public int nextMachineMode() {
        return (machineMode + 1) % 3;
    }

    @Override
    public String getMachineModeName() {
        return switch (machineMode) {
            case MODE_FALLING_TOWER -> TextLocalization.BloodSoulSacrificialArray_Mode_1;
            case MODE_ALCHEMIC -> TextLocalization.BloodSoulSacrificialArray_Mode_2;
            default -> TextLocalization.BloodSoulSacrificialArray_Mode_0;
        };
    }

    @Override
    @Deprecated
    public void setMachineModeIcons() {
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_FLUID);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_METAL);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_PACKAGER);
    }

    @Override
    public boolean onWireCutterRightClick(ForgeDirection side, ForgeDirection wrenchingSide, EntityPlayer player,
        float x, float y, float z, ItemStack tool) {
        if (getBaseMetaTileEntity().isServerSide()) {
            enableRender = !enableRender;
            GTUtility.sendChatTrans(
                player,
                enableRender ? "BloodSoulSacrificialArray_Render_Enabled"
                    : "BloodSoulSacrificialArray_Render_Disabled");
            getBaseMetaTileEntity().markDirty();
        }
        return true;
    }

    @Override
    public String[] getInfoData() {
        List<String> info = new ArrayList<>(Arrays.asList(super.getInfoData()));
        info.add(
            TextLocalization.BloodSoulSacrificialArray_LPNetwork + " "
                + EnumChatFormatting.RED
                + currentEssence
                + EnumChatFormatting.RESET
                + " LP");
        return info.toArray(new String[0]);
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, net.minecraft.tileentity.TileEntity tile, NBTTagCompound tag,
        World world, int x, int y, int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        tag.setInteger("bloodSoulLp", currentEssence);
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        currentTip.add(
            TextLocalization.BloodSoulSacrificialArray_LPNetwork + " "
                + EnumChatFormatting.WHITE
                + accessor.getNBTData()
                    .getInteger("bloodSoulLp")
                + EnumChatFormatting.RESET
                + " LP");
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        return new MultiblockTooltipBuilder().addMachineType(TextLocalization.BloodSoulSacrificialArrayRecipeType)
            .addInfo(TextLocalization.Tooltip_BloodSoulSacrificialArray_00)
            .addInfo(TextLocalization.Tooltip_BloodSoulSacrificialArray_01)
            .addInfo(TextLocalization.Tooltip_BloodSoulSacrificialArray_02)
            .addInfo(TextLocalization.Tooltip_BloodSoulSacrificialArray_03)
            .addInfo(TextLocalization.Tooltip_BloodSoulSacrificialArray_04)
            .addInfo(TextLocalization.Tooltip_BloodSoulSacrificialArray_05)
            .addInfo(TextLocalization.Tooltip_GTNC_Upgrade_00)
            .addInfo(TextLocalization.Tooltip_GTNC_Upgrade_01)
            .addInfo(TextLocalization.Tooltip_GTNC_Upgrade_02)
            .beginStructureBlock(33, 14, 30, false)
            .addInputBus(TextLocalization.Tooltip_BloodSoulSacrificialArray_Casing)
            .addOutputBus(TextLocalization.Tooltip_BloodSoulSacrificialArray_Casing)
            .addMaintenanceHatch(TextLocalization.Tooltip_BloodSoulSacrificialArray_Casing)
            .toolTipFinisher();
    }

    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings8, 10);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity tileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean active, boolean redstone) {
        ITexture casing = Textures.BlockIcons.getCasingTextureForId(getCasingTextureID());
        if (side == facing) {
            return new ITexture[] { casing, TextureFactory.builder()
                .addIcon(active ? Textures.BlockIcons.OVERLAY_DTPF_ON : Textures.BlockIcons.OVERLAY_DTPF_OFF)
                .extFacing()
                .build() };
        }
        return new ITexture[] { casing };
    }

    @Override
    public void saveNBTData(NBTTagCompound tag) {
        super.saveNBTData(tag);
        writeMachineState(tag);
    }

    @Override
    public void setItemNBT(NBTTagCompound tag) {
        super.setItemNBT(tag);
        writeMachineState(tag);
    }

    private void writeMachineState(NBTTagCompound tag) {
        tag.setBoolean("bloodSoulRender", enableRender);
        tag.setInteger("bloodSoulMode", machineMode);
    }

    @Override
    public void loadNBTData(NBTTagCompound tag) {
        super.loadNBTData(tag);
        enableRender = !tag.hasKey("bloodSoulRender") || tag.getBoolean("bloodSoulRender");
        machineMode = Math.max(0, Math.min(2, tag.getInteger("bloodSoulMode")));
    }

    @Override
    public void checkMaintenance() {}

    @Override
    public boolean getDefaultHasMaintenanceChecks() {
        return false;
    }

    @Override
    public boolean shouldCheckMaintenance() {
        return false;
    }

    @Optional.Method(modid = "dreamcraft")
    private static Block getBloodyIchorium() {
        return BlockList.BloodyIchorium.block;
    }

    @Optional.Method(modid = "dreamcraft")
    private static Block getBloodyThaumium() {
        return BlockList.BloodyThaumium.block;
    }

    @Optional.Method(modid = "dreamcraft")
    private static Block getBloodyVoid() {
        return BlockList.BloodyVoid.block;
    }

    @Optional.Method(modid = "BloodArsenal")
    private static Block getBloodLamp() {
        return com.arc.bloodarsenal.common.block.ModBlocks.blood_lamp;
    }

    @Optional.Method(modid = "BloodArsenal")
    private static Block getLpMaterializer() {
        return com.arc.bloodarsenal.common.block.ModBlocks.lp_materializer;
    }
}
