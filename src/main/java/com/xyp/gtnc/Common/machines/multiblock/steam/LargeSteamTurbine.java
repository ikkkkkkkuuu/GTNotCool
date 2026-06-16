package com.xyp.gtnc.Common.machines.multiblock.steam;

import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.common.misc.WirelessNetworkManager.addEUToGlobalEnergyMap;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;

import gregtech.GTMod;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEEnhancedMultiBlockBase;
import gregtech.api.metatileentity.implementations.MTEHatchDynamo;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;

// #tr NameLargeSteamTurbineBronze
// # Large Bronze Steam Turbine
// # zh_CN 大型青铜蒸汽涡轮

// #tr NameLargeSteamTurbineSteel
// # Large Steel Steam Turbine
// # zh_CN 大型钢蒸汽涡轮

// #tr NameLargeSteamTurbineTitanium
// # Large Titanium Steam Turbine
// # zh_CN 大型钛蒸汽涡轮

// #tr NameLargeSteamTurbineTungstenSteel
// # Large Tungstensteel Steam Turbine
// # zh_CN 大型钨钢蒸汽涡轮

// #tr LargeSteamTurbineRecipeType
// # Steam Turbine
// # zh_CN 蒸汽涡轮

// #tr Tooltip_LargeSteamTurbineBronze_00
// # Consumes 800L/s Steam, produces 400EU/t at 85%% efficiency.
// # zh_CN 消耗800升/秒蒸汽，以85%%效率产出400EU/t

// #tr Tooltip_LargeSteamTurbineSteel_00
// # Consumes 1600L/s Steam, produces 800EU/t at 90%% efficiency.
// # zh_CN 消耗1600升/秒蒸汽，以90%%效率产出800EU/t

// #tr Tooltip_LargeSteamTurbineTitanium_00
// # Consumes 3200L/s Steam, produces 1600EU/t at 95%% efficiency.
// # zh_CN 消耗3200升/秒蒸汽，以95%%效率产出1600EU/t

// #tr Tooltip_LargeSteamTurbineTungstenSteel_00
// # Consumes 6400L/s Steam, produces 3200EU/t at 100%% efficiency.
// # zh_CN 消耗6400升/秒蒸汽，以100%%效率产出3200EU/t

// #tr Tooltip_LargeSteamTurbine_00
// # Outputs Distilled Water as byproduct.
// # zh_CN 副产蒸馏水

// #tr Tooltip_LargeSteamTurbine_01
// # Use Screwdriver to toggle Wireless Mode.
// # zh_CN 使用螺丝刀切换无线模式

// #tr Tooltip_LargeSteamTurbine_Casing_00
// # Any Machine Block
// # zh_CN 任意机械方块

// #tr Tooltip_LargeSteamTurbine_Casing_01
// # Any Pipe Machine Block
// # zh_CN 任意管道机械方块

public abstract class LargeSteamTurbine extends MTEEnhancedMultiBlockBase<LargeSteamTurbine>
    implements ISurvivalConstructable {

    public boolean firstRun = true;
    public boolean wirelessMode = false;
    public UUID ownerUUID;
    public int mCountCasing;
    public int mPipeCasing;
    public int steamConsumed = 0;
    private static final String STRUCTURE_PIECE_MAIN = "main";

    public LargeSteamTurbine(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public LargeSteamTurbine(String aName) {
        super(aName);
    }

    public abstract Block getCasingBlock();

    public abstract byte getCasingMeta();

    public abstract byte getCasingTextureIndex();

    public abstract Block getPipeBlock();

    public abstract byte getPipeMeta();

    public abstract int getEUt();

    public abstract int getOptimalSteamFlow();

    public abstract int getEfficiencyPercent();

    @Override
    public boolean supportsVoidProtection() {
        return true;
    }

    @Override
    public boolean getDefaultHasMaintenanceChecks() {
        return false;
    }

    @Override
    public boolean shouldCheckMaintenance() {
        return false;
    }

    public void onCasingAdded() {
        mCountCasing++;
    }

    public void onPipeAdded() {
        mPipeCasing++;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureIndex()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_STEAM_HAMMER_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_STEAM_HAMMER_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureIndex()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_STEAM_HAMMER)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_STEAM_HAMMER_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureIndex()) };
    }

    @Override
    public boolean isCorrectMachinePart(ItemStack aStack) {
        return true;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.largeBoilerFakeFuels;
    }

    @Override
    public boolean filtersFluid() {
        return false;
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        if (ownerUUID == null) {
            this.ownerUUID = aBaseMetaTileEntity.getOwnerUuid();
        }
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        ArrayList<FluidStack> tFluids = getStoredFluids();
        int totalSteamConsumed = 0;
        int maxSteam = getOptimalSteamFlow();

        for (FluidStack tFluid : tFluids) {
            if (GTModHandler.isAnySteam(tFluid)) {
                int consume = Math.min(tFluid.amount, maxSteam - totalSteamConsumed);
                if (consume > 0) {
                    depleteInput(new FluidStack(tFluid, consume));
                    totalSteamConsumed += consume;
                }
                if (totalSteamConsumed >= maxSteam) break;
            }
        }

        if (totalSteamConsumed <= 0) {
            this.steamConsumed = 0;
            this.mEUt = 0;
            this.mMaxProgresstime = 0;
            return CheckRecipeResultRegistry.NO_FUEL_FOUND;
        }

        this.steamConsumed = totalSteamConsumed;

        // Efficiency: scales down if we don't reach optimal flow
        float efficiency = getEfficiencyPercent() / 100.0f;
        if (totalSteamConsumed < maxSteam) {
            efficiency *= (float) totalSteamConsumed / maxSteam;
        }

        // EU/t = steam * efficiency * 0.5 (2L steam = 1 EU at 100% efficiency)
        this.mEUt = Math.max(1, (int) (totalSteamConsumed * efficiency * 0.5f));
        this.mMaxProgresstime = 1;
        this.mEfficiencyIncrease = 10000;
        this.mEfficiency = (int) (efficiency * 10000);

        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        if (this.mEUt > 0 && this.steamConsumed > 0) {
            // Output distilled water (condensed steam)
            addOutput(GTModHandler.getDistilledWater(this.steamConsumed));

            if (wirelessMode) {
                // Wireless mode: send EU to global energy map
                if (ownerUUID != null) {
                    addEUToGlobalEnergyMap(ownerUUID, this.mEUt);
                }
            } else {
                // Normal mode: send EU to dynamo hatches
                addEnergyOutput(this.mEUt);
            }

            this.steamConsumed = 0;
            return true;
        }
        return true;
    }

    // #tr GT5U.chat.wireless_mode.enabled
    // # Wireless Mode: Enabled
    // # zh_CN §d无线模式：已启用

    // #tr GT5U.chat.wireless_mode.disabled
    // # Wireless Mode: Disabled
    // # zh_CN §7无线模式：已禁用

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        if (side == getBaseMetaTileEntity().getFrontFacing()) {
            wirelessMode = !wirelessMode;
            GTUtility.sendChatToPlayer(
                aPlayer,
                wirelessMode ? StatCollector.translateToLocal("GT5U.chat.wireless_mode.enabled")
                    : StatCollector.translateToLocal("GT5U.chat.wireless_mode.disabled"));
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setBoolean("wirelessMode", wirelessMode);
        aNBT.setInteger("steamConsumed", steamConsumed);
        aNBT.setBoolean("firstRun", firstRun);
        if (ownerUUID != null) {
            aNBT.setString("ownerUUID", ownerUUID.toString());
        }
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        wirelessMode = aNBT.getBoolean("wirelessMode");
        steamConsumed = aNBT.getInteger("steamConsumed");
        firstRun = aNBT.getBoolean("firstRun");
        if (aNBT.hasKey("ownerUUID")) {
            String uuidStr = aNBT.getString("ownerUUID");
            if (!uuidStr.isEmpty()) {
                ownerUUID = UUID.fromString(uuidStr);
            }
        }
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (mProgresstime > 0 && firstRun) {
            firstRun = false;
            GTMod.achievements.issueAchievement(
                aBaseMetaTileEntity.getWorld()
                    .getPlayerEntityByName(aBaseMetaTileEntity.getOwnerName()),
                "muchsteam");
        }
        super.onPostTick(aBaseMetaTileEntity, aTick);
    }

    @Override
    public String[] getInfoData() {
        String tRunning = mMaxProgresstime > 0
            ? EnumChatFormatting.GREEN + StatCollector.translateToLocal("GT5U.turbine.running.true")
                + EnumChatFormatting.RESET
            : EnumChatFormatting.RED + StatCollector.translateToLocal("GT5U.turbine.running.false")
                + EnumChatFormatting.RESET;

        long storedEnergy = 0;
        long maxEnergy = 0;
        for (MTEHatchDynamo tHatch : GTUtility.validMTEList(mDynamoHatches)) {
            storedEnergy += tHatch.getBaseMetaTileEntity()
                .getStoredEU();
            maxEnergy += tHatch.getBaseMetaTileEntity()
                .getEUCapacity();
        }

        return new String[] { tRunning + ": " + EnumChatFormatting.RED + this.mEUt + EnumChatFormatting.RESET + " EU/t",
            StatCollector.translateToLocal("GT5U.turbine.efficiency") + ": "
                + EnumChatFormatting.YELLOW
                + (mEfficiency / 100F)
                + EnumChatFormatting.RESET
                + "%",
            StatCollector.translateToLocal("GT5U.multiblock.energy") + ": "
                + EnumChatFormatting.GREEN
                + storedEnergy
                + EnumChatFormatting.RESET
                + " EU / "
                + EnumChatFormatting.YELLOW
                + maxEnergy
                + EnumChatFormatting.RESET
                + " EU",
            StatCollector.translateToLocal("GT5U.turbine.flow") + ": "
                + EnumChatFormatting.YELLOW
                + getOptimalSteamFlow()
                + EnumChatFormatting.RESET
                + " L/s",
            StatCollector.translateToLocal("GT5U.turbine.wireless_mode") + ": "
                + (wirelessMode ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF")
                + EnumChatFormatting.RESET,
            StatCollector.translateToLocal("GT5U.turbine.fuel") + ": "
                + EnumChatFormatting.GOLD
                + steamConsumed
                + EnumChatFormatting.RESET
                + "L" };
    }

    @Override
    public IStructureDefinition<LargeSteamTurbine> getStructureDefinition() {
        return StructureDefinition.<LargeSteamTurbine>builder()
            .addShape(
                STRUCTURE_PIECE_MAIN,
                StructureUtility.transpose(
                    new String[][] { { "ccc", "ccc", "ccc" }, { "ccc", "cPc", "ccc" }, { "ccc", "cPc", "ccc" },
                        { "f~f", "fff", "fff" }, }))
            .addElement('P', StructureUtility.lazy(t -> StructureUtility.ofBlock(t.getPipeBlock(), t.getPipeMeta())))
            .addElement(
                'c',
                StructureUtility.lazy(
                    t -> buildHatchAdder(LargeSteamTurbine.class).atLeast(HatchElement.OutputHatch)
                        .casingIndex(t.getCasingTextureIndex())
                        .hint(2)
                        .buildAndChain(
                            StructureUtility.onElementPass(
                                LargeSteamTurbine::onCasingAdded,
                                StructureUtility.ofBlock(t.getCasingBlock(), t.getCasingMeta())))))
            .addElement(
                'f',
                StructureUtility.lazy(
                    t -> buildHatchAdder(LargeSteamTurbine.class)
                        .atLeast(HatchElement.Maintenance, HatchElement.InputHatch, HatchElement.Dynamo)
                        .casingIndex(t.getCasingTextureIndex())
                        .hint(1)
                        .buildAndChain(
                            StructureUtility.onElementPass(
                                LargeSteamTurbine::onPipeAdded,
                                StructureUtility.ofBlock(t.getCasingBlock(), t.getCasingMeta())))))
            .build();
    }

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        mCountCasing = 0;
        mPipeCasing = 0;

        if (!checkPiece(STRUCTURE_PIECE_MAIN, 1, 3, 0, errors)) return;
        checkCasingMin(errors, mCountCasing, 16);
        checkCasingMin(errors, mPipeCasing, 3);
    }

    @Override
    public int getMaxEfficiency(ItemStack aStack) {
        return 10000;
    }

    @Override
    public int getDamageToComponent(ItemStack aStack) {
        return 0;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, 1, 3, 0);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivalBuildPiece(STRUCTURE_PIECE_MAIN, stackSize, 1, 3, 0, elementBudget, env, false, true);
    }

    // ==================== Material Variants ====================

    public static class LargeSteamTurbineBronze extends LargeSteamTurbine {

        public LargeSteamTurbineBronze(int aID, String aName, String aNameRegional) {
            super(aID, aName, aNameRegional);
        }

        public LargeSteamTurbineBronze(String aName) {
            super(aName);
        }

        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
            return new LargeSteamTurbineBronze(this.mName);
        }

        @Override
        public MultiblockTooltipBuilder createTooltip() {
            MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
            tt.addMachineType(StatCollector.translateToLocal("LargeSteamTurbineRecipeType"))
                .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamTurbineBronze_00"))
                .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamTurbine_00"))
                .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamTurbine_01"))
                .beginStructureBlock(3, 4, 3, false)
                .addDynamoHatch(StatCollector.translateToLocal("Tooltip_LargeSteamTurbine_Casing_00"), 1)
                .addInputHatch(StatCollector.translateToLocal("Tooltip_LargeSteamTurbine_Casing_00"), 1)
                .addOutputHatch(StatCollector.translateToLocal("Tooltip_LargeSteamTurbine_Casing_01"), 2)
                .toolTipFinisher();
            return tt;
        }

        @Override
        public Block getCasingBlock() {
            return GregTechAPI.sBlockCasings1;
        }

        @Override
        public byte getCasingMeta() {
            return 10;
        }

        @Override
        public byte getCasingTextureIndex() {
            return 10;
        }

        @Override
        public Block getPipeBlock() {
            return GregTechAPI.sBlockCasings2;
        }

        @Override
        public byte getPipeMeta() {
            return 12;
        }

        @Override
        public int getEUt() {
            return 400;
        }

        @Override
        public int getOptimalSteamFlow() {
            return 800;
        }

        @Override
        public int getEfficiencyPercent() {
            return 85;
        }
    }

    public static class LargeSteamTurbineSteel extends LargeSteamTurbine {

        public LargeSteamTurbineSteel(int aID, String aName, String aNameRegional) {
            super(aID, aName, aNameRegional);
        }

        public LargeSteamTurbineSteel(String aName) {
            super(aName);
        }

        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
            return new LargeSteamTurbineSteel(this.mName);
        }

        @Override
        public MultiblockTooltipBuilder createTooltip() {
            MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
            tt.addMachineType(StatCollector.translateToLocal("LargeSteamTurbineRecipeType"))
                .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamTurbineSteel_00"))
                .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamTurbine_00"))
                .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamTurbine_01"))
                .beginStructureBlock(3, 4, 3, false)
                .addDynamoHatch(StatCollector.translateToLocal("Tooltip_LargeSteamTurbine_Casing_00"), 1)
                .addInputHatch(StatCollector.translateToLocal("Tooltip_LargeSteamTurbine_Casing_00"), 1)
                .addOutputHatch(StatCollector.translateToLocal("Tooltip_LargeSteamTurbine_Casing_01"), 2)
                .toolTipFinisher();
            return tt;
        }

        @Override
        public Block getCasingBlock() {
            return GregTechAPI.sBlockCasings2;
        }

        @Override
        public byte getCasingMeta() {
            return 0;
        }

        @Override
        public byte getCasingTextureIndex() {
            return 16;
        }

        @Override
        public Block getPipeBlock() {
            return GregTechAPI.sBlockCasings2;
        }

        @Override
        public byte getPipeMeta() {
            return 13;
        }

        @Override
        public int getEUt() {
            return 800;
        }

        @Override
        public int getOptimalSteamFlow() {
            return 1600;
        }

        @Override
        public int getEfficiencyPercent() {
            return 90;
        }
    }

    public static class LargeSteamTurbineTitanium extends LargeSteamTurbine {

        public LargeSteamTurbineTitanium(int aID, String aName, String aNameRegional) {
            super(aID, aName, aNameRegional);
        }

        public LargeSteamTurbineTitanium(String aName) {
            super(aName);
        }

        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
            return new LargeSteamTurbineTitanium(this.mName);
        }

        @Override
        public MultiblockTooltipBuilder createTooltip() {
            MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
            tt.addMachineType(StatCollector.translateToLocal("LargeSteamTurbineRecipeType"))
                .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamTurbineTitanium_00"))
                .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamTurbine_00"))
                .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamTurbine_01"))
                .beginStructureBlock(3, 4, 3, false)
                .addDynamoHatch(StatCollector.translateToLocal("Tooltip_LargeSteamTurbine_Casing_00"), 1)
                .addInputHatch(StatCollector.translateToLocal("Tooltip_LargeSteamTurbine_Casing_00"), 1)
                .addOutputHatch(StatCollector.translateToLocal("Tooltip_LargeSteamTurbine_Casing_01"), 2)
                .toolTipFinisher();
            return tt;
        }

        @Override
        public Block getCasingBlock() {
            return GregTechAPI.sBlockCasings4;
        }

        @Override
        public byte getCasingMeta() {
            return 2;
        }

        @Override
        public byte getCasingTextureIndex() {
            return 50;
        }

        @Override
        public Block getPipeBlock() {
            return GregTechAPI.sBlockCasings2;
        }

        @Override
        public byte getPipeMeta() {
            return 14;
        }

        @Override
        public int getEUt() {
            return 1600;
        }

        @Override
        public int getOptimalSteamFlow() {
            return 3200;
        }

        @Override
        public int getEfficiencyPercent() {
            return 95;
        }
    }

    public static class LargeSteamTurbineTungstenSteel extends LargeSteamTurbine {

        public LargeSteamTurbineTungstenSteel(int aID, String aName, String aNameRegional) {
            super(aID, aName, aNameRegional);
        }

        public LargeSteamTurbineTungstenSteel(String aName) {
            super(aName);
        }

        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
            return new LargeSteamTurbineTungstenSteel(this.mName);
        }

        @Override
        public MultiblockTooltipBuilder createTooltip() {
            MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
            tt.addMachineType(StatCollector.translateToLocal("LargeSteamTurbineRecipeType"))
                .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamTurbineTungstenSteel_00"))
                .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamTurbine_00"))
                .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamTurbine_01"))
                .beginStructureBlock(3, 4, 3, false)
                .addDynamoHatch(StatCollector.translateToLocal("Tooltip_LargeSteamTurbine_Casing_00"), 1)
                .addInputHatch(StatCollector.translateToLocal("Tooltip_LargeSteamTurbine_Casing_00"), 1)
                .addOutputHatch(StatCollector.translateToLocal("Tooltip_LargeSteamTurbine_Casing_01"), 2)
                .toolTipFinisher();
            return tt;
        }

        @Override
        public Block getCasingBlock() {
            return GregTechAPI.sBlockCasings4;
        }

        @Override
        public byte getCasingMeta() {
            return 0;
        }

        @Override
        public byte getCasingTextureIndex() {
            return 48;
        }

        @Override
        public Block getPipeBlock() {
            return GregTechAPI.sBlockCasings2;
        }

        @Override
        public byte getPipeMeta() {
            return 15;
        }

        @Override
        public int getEUt() {
            return 3200;
        }

        @Override
        public int getOptimalSteamFlow() {
            return 6400;
        }

        @Override
        public int getEfficiencyPercent() {
            return 100;
        }
    }
}
