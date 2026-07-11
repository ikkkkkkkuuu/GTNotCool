package com.xyp.gtnc.utils.enums;

import static gregtech.api.enums.GTValues.NI;
import static gregtech.api.enums.ItemList.Machine_LV_Miner;

import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.xyp.gtnc.Client.GTNCCreativeTabs;
import com.xyp.gtnc.ScienceNotCool;
import com.xyp.gtnc.utils.Utils;

import gregtech.api.GregTechAPI;
import gregtech.api.interfaces.IItemContainer;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.util.GTLanguageManager;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;

/**
 * GT-Not-Cool 自定义机器 ItemList
 * 每个枚举项对应一个注册的机器
 */
public enum GTNCItemList implements IItemContainer {

    BiowareSMDInductor,

    ChipTier1,
    ChipTier2,
    ChipTier3,
    ChipTier4,
    ChipTier5,
    ChipTier6,
    ChipTier7,

    LargeBoilerBronze,
    LargeBoilerSteel,
    LargeBoilerTitanium,
    LargeBoilerTungstenSteel,

    LargeSteamTurbineBronze,
    LargeSteamTurbineSteel,
    LargeSteamTurbineTitanium,
    LargeSteamTurbineTungstenSteel,

    LargeSteamLaserEngraver,
    LargeSteamAssembler,
    LargeSteamCentrifuge,
    LargeSteamElectrolyzer,
    LargeSteamBending,
    LargeSteamFluidExtractor,
    LargeSteamFluidSolidifier,
    LargeSteamChemicalReactor,
    LargeSteamWireMill,
    LargeSteamMixer,
    LargeSteamAlloySmelter,
    LargeSteamCircuitAssembler,
    LargeSteamCompressor,
    LargeSteamCutting,
    LargeSteamFormingPress,
    LargeSteamHammer,
    LargeSteamExtruder,
    LargeSteamDistillationTower,
    LargeSteamBeeBreeder,
    LargeSteamCombProcessor,
    LargeSteamVoidMiner,

    SteamEyeOfHarmony,

    LargeSteamCrucibleSteel,
    LargeSteamCrucibleInvar,
    LargeSteamCrucibleStainless,
    LargeSteamCrucibleTitanium,
    LargeSteamCrucibleTungstenSteel,

    GeneralChemicalFactory,

    DrillingRig,
    MiningRig,

    SteamTurbineLV,
    SteamTurbineMV,
    SteamTurbineHV,
    SteamTurbineEV,
    SteamTurbineIV,
    SteamTurbineLuV,

    LaserEnergyHatchIV256A,
    LaserEnergyHatchIV1024A,
    LaserEnergyHatchIV4096A,
    LaserEnergyHatchIV16384A,
    LaserEnergyHatchIV65536A,
    LaserEnergyHatchIV262144A,
    LaserEnergyHatchIV1048576A,
    LaserEnergyHatchIV4194304A,
    LaserEnergyHatchIV16777216A,

    LaserEnergyHatchLuV256A,
    LaserEnergyHatchLuV1024A,
    LaserEnergyHatchLuV4096A,
    LaserEnergyHatchLuV16384A,
    LaserEnergyHatchLuV65536A,
    LaserEnergyHatchLuV262144A,
    LaserEnergyHatchLuV1048576A,
    LaserEnergyHatchLuV4194304A,
    LaserEnergyHatchLuV16777216A,

    LaserEnergyHatchZPM256A,
    LaserEnergyHatchZPM1024A,
    LaserEnergyHatchZPM4096A,
    LaserEnergyHatchZPM16384A,
    LaserEnergyHatchZPM65536A,
    LaserEnergyHatchZPM262144A,
    LaserEnergyHatchZPM1048576A,
    LaserEnergyHatchZPM4194304A,
    LaserEnergyHatchZPM16777216A,

    LaserEnergyHatchUV256A,
    LaserEnergyHatchUV1024A,
    LaserEnergyHatchUV4096A,
    LaserEnergyHatchUV16384A,
    LaserEnergyHatchUV65536A,
    LaserEnergyHatchUV262144A,
    LaserEnergyHatchUV1048576A,
    LaserEnergyHatchUV4194304A,
    LaserEnergyHatchUV16777216A,

    LaserEnergyHatchUHV256A,
    LaserEnergyHatchUHV1024A,
    LaserEnergyHatchUHV4096A,
    LaserEnergyHatchUHV16384A,
    LaserEnergyHatchUHV65536A,
    LaserEnergyHatchUHV262144A,
    LaserEnergyHatchUHV1048576A,
    LaserEnergyHatchUHV4194304A,
    LaserEnergyHatchUHV16777216A,

    LaserEnergyHatchUEV256A,
    LaserEnergyHatchUEV1024A,
    LaserEnergyHatchUEV4096A,
    LaserEnergyHatchUEV16384A,
    LaserEnergyHatchUEV65536A,
    LaserEnergyHatchUEV262144A,
    LaserEnergyHatchUEV1048576A,
    LaserEnergyHatchUEV4194304A,
    LaserEnergyHatchUEV16777216A,

    LaserEnergyHatchUIV256A,
    LaserEnergyHatchUIV1024A,
    LaserEnergyHatchUIV4096A,
    LaserEnergyHatchUIV16384A,
    LaserEnergyHatchUIV65536A,
    LaserEnergyHatchUIV262144A,
    LaserEnergyHatchUIV1048576A,
    LaserEnergyHatchUIV4194304A,
    LaserEnergyHatchUIV16777216A,

    LaserEnergyHatchUMV256A,
    LaserEnergyHatchUMV1024A,
    LaserEnergyHatchUMV4096A,
    LaserEnergyHatchUMV16384A,
    LaserEnergyHatchUMV65536A,
    LaserEnergyHatchUMV262144A,
    LaserEnergyHatchUMV1048576A,
    LaserEnergyHatchUMV4194304A,
    LaserEnergyHatchUMV16777216A,

    LaserEnergyHatchUXV256A,
    LaserEnergyHatchUXV1024A,
    LaserEnergyHatchUXV4096A,
    LaserEnergyHatchUXV16384A,
    LaserEnergyHatchUXV65536A,
    LaserEnergyHatchUXV262144A,
    LaserEnergyHatchUXV1048576A,
    LaserEnergyHatchUXV4194304A,
    LaserEnergyHatchUXV16777216A,

    LaserEnergyHatchMAX256A,
    LaserEnergyHatchMAX1024A,
    LaserEnergyHatchMAX4096A,
    LaserEnergyHatchMAX16384A,
    LaserEnergyHatchMAX65536A,
    LaserEnergyHatchMAX262144A,
    LaserEnergyHatchMAX1048576A,
    LaserEnergyHatchMAX4194304A,
    LaserEnergyHatchMAX16777216A,

    EnergyHatchLV,
    EnergyHatchLV4A,
    EnergyHatchLV16A,
    EnergyHatchLV64A,

    EnergyHatchMV,
    EnergyHatchMV4A,
    EnergyHatchMV16A,
    EnergyHatchMV64A,

    EnergyHatchHV,
    EnergyHatchHV4A,
    EnergyHatchHV16A,
    EnergyHatchHV64A,

    EnergyHatchEV,
    EnergyHatchEV4A,
    EnergyHatchEV16A,
    EnergyHatchEV64A,

    EnergyHatchIV,
    EnergyHatchIV4A,
    EnergyHatchIV16A,
    EnergyHatchIV64A,

    EnergyHatchLuV,
    EnergyHatchLuV4A,
    EnergyHatchLuV16A,
    EnergyHatchLuV64A,

    EnergyHatchZPM,
    EnergyHatchZPM4A,
    EnergyHatchZPM16A,
    EnergyHatchZPM64A,

    EnergyHatchUV,
    EnergyHatchUV4A,
    EnergyHatchUV16A,
    EnergyHatchUV64A,

    EnergyHatchUHV,
    EnergyHatchUHV4A,
    EnergyHatchUHV16A,
    EnergyHatchUHV64A,

    EnergyHatchUEV,
    EnergyHatchUEV4A,
    EnergyHatchUEV16A,
    EnergyHatchUEV64A,

    EnergyHatchUIV,
    EnergyHatchUIV4A,
    EnergyHatchUIV16A,
    EnergyHatchUIV64A,

    EnergyHatchUMV,
    EnergyHatchUMV4A,
    EnergyHatchUMV16A,
    EnergyHatchUMV64A,

    EnergyHatchUXV,
    EnergyHatchUXV4A,
    EnergyHatchUXV16A,
    EnergyHatchUXV64A,

    EnergyHatchMAX,
    EnergyHatchMAX4A,
    EnergyHatchMAX16A,
    EnergyHatchMAX64A,

    WirelessEnergyHatchLV,
    WirelessEnergyHatchLV4A,
    WirelessEnergyHatchLV16A,
    WirelessEnergyHatchLV64A,

    WirelessEnergyHatchMV,
    WirelessEnergyHatchMV4A,
    WirelessEnergyHatchMV16A,
    WirelessEnergyHatchMV64A,

    WirelessEnergyHatchHV,
    WirelessEnergyHatchHV4A,
    WirelessEnergyHatchHV16A,
    WirelessEnergyHatchHV64A,

    WirelessEnergyHatchEV,
    WirelessEnergyHatchEV4A,
    WirelessEnergyHatchEV16A,
    WirelessEnergyHatchEV64A,

    WirelessEnergyHatchIV,
    WirelessEnergyHatchIV4A,
    WirelessEnergyHatchIV16A,
    WirelessEnergyHatchIV64A,
    WirelessEnergyHatchIV256A,
    WirelessEnergyHatchIV1024A,
    WirelessEnergyHatchIV4096A,
    WirelessEnergyHatchIV16384A,
    WirelessEnergyHatchIV65536A,
    WirelessEnergyHatchIV262144A,
    WirelessEnergyHatchIV1048576A,
    WirelessEnergyHatchIV4194304A,
    WirelessEnergyHatchIV16777216A,

    WirelessEnergyHatchLuV,
    WirelessEnergyHatchLuV4A,
    WirelessEnergyHatchLuV16A,
    WirelessEnergyHatchLuV64A,
    WirelessEnergyHatchLuV256A,
    WirelessEnergyHatchLuV1024A,
    WirelessEnergyHatchLuV4096A,
    WirelessEnergyHatchLuV16384A,
    WirelessEnergyHatchLuV65536A,
    WirelessEnergyHatchLuV262144A,
    WirelessEnergyHatchLuV1048576A,
    WirelessEnergyHatchLuV4194304A,
    WirelessEnergyHatchLuV16777216A,

    WirelessEnergyHatchZPM,
    WirelessEnergyHatchZPM4A,
    WirelessEnergyHatchZPM16A,
    WirelessEnergyHatchZPM64A,
    WirelessEnergyHatchZPM256A,
    WirelessEnergyHatchZPM1024A,
    WirelessEnergyHatchZPM4096A,
    WirelessEnergyHatchZPM16384A,
    WirelessEnergyHatchZPM65536A,
    WirelessEnergyHatchZPM262144A,
    WirelessEnergyHatchZPM1048576A,
    WirelessEnergyHatchZPM4194304A,
    WirelessEnergyHatchZPM16777216A,

    WirelessEnergyHatchUV,
    WirelessEnergyHatchUV4A,
    WirelessEnergyHatchUV16A,
    WirelessEnergyHatchUV64A,
    WirelessEnergyHatchUV256A,
    WirelessEnergyHatchUV1024A,
    WirelessEnergyHatchUV4096A,
    WirelessEnergyHatchUV16384A,
    WirelessEnergyHatchUV65536A,
    WirelessEnergyHatchUV262144A,
    WirelessEnergyHatchUV1048576A,
    WirelessEnergyHatchUV4194304A,
    WirelessEnergyHatchUV16777216A,

    WirelessEnergyHatchUHV,
    WirelessEnergyHatchUHV4A,
    WirelessEnergyHatchUHV16A,
    WirelessEnergyHatchUHV64A,
    WirelessEnergyHatchUHV256A,
    WirelessEnergyHatchUHV1024A,
    WirelessEnergyHatchUHV4096A,
    WirelessEnergyHatchUHV16384A,
    WirelessEnergyHatchUHV65536A,
    WirelessEnergyHatchUHV262144A,
    WirelessEnergyHatchUHV1048576A,
    WirelessEnergyHatchUHV4194304A,
    WirelessEnergyHatchUHV16777216A,

    WirelessEnergyHatchUEV,
    WirelessEnergyHatchUEV4A,
    WirelessEnergyHatchUEV16A,
    WirelessEnergyHatchUEV64A,
    WirelessEnergyHatchUEV256A,
    WirelessEnergyHatchUEV1024A,
    WirelessEnergyHatchUEV4096A,
    WirelessEnergyHatchUEV16384A,
    WirelessEnergyHatchUEV65536A,
    WirelessEnergyHatchUEV262144A,
    WirelessEnergyHatchUEV1048576A,
    WirelessEnergyHatchUEV4194304A,
    WirelessEnergyHatchUEV16777216A,

    WirelessEnergyHatchUIV,
    WirelessEnergyHatchUIV4A,
    WirelessEnergyHatchUIV16A,
    WirelessEnergyHatchUIV64A,
    WirelessEnergyHatchUIV256A,
    WirelessEnergyHatchUIV1024A,
    WirelessEnergyHatchUIV4096A,
    WirelessEnergyHatchUIV16384A,
    WirelessEnergyHatchUIV65536A,
    WirelessEnergyHatchUIV262144A,
    WirelessEnergyHatchUIV1048576A,
    WirelessEnergyHatchUIV4194304A,
    WirelessEnergyHatchUIV16777216A,

    WirelessEnergyHatchUMV,
    WirelessEnergyHatchUMV4A,
    WirelessEnergyHatchUMV16A,
    WirelessEnergyHatchUMV64A,
    WirelessEnergyHatchUMV256A,
    WirelessEnergyHatchUMV1024A,
    WirelessEnergyHatchUMV4096A,
    WirelessEnergyHatchUMV16384A,
    WirelessEnergyHatchUMV65536A,
    WirelessEnergyHatchUMV262144A,
    WirelessEnergyHatchUMV1048576A,
    WirelessEnergyHatchUMV4194304A,
    WirelessEnergyHatchUMV16777216A,

    WirelessEnergyHatchUXV,
    WirelessEnergyHatchUXV4A,
    WirelessEnergyHatchUXV16A,
    WirelessEnergyHatchUXV64A,
    WirelessEnergyHatchUXV256A,
    WirelessEnergyHatchUXV1024A,
    WirelessEnergyHatchUXV4096A,
    WirelessEnergyHatchUXV16384A,
    WirelessEnergyHatchUXV65536A,
    WirelessEnergyHatchUXV262144A,
    WirelessEnergyHatchUXV1048576A,
    WirelessEnergyHatchUXV4194304A,
    WirelessEnergyHatchUXV16777216A,

    WirelessEnergyHatchMAX,
    WirelessEnergyHatchMAX4A,
    WirelessEnergyHatchMAX16A,
    WirelessEnergyHatchMAX64A,
    WirelessEnergyHatchMAX256A,
    WirelessEnergyHatchMAX1024A,
    WirelessEnergyHatchMAX4096A,
    WirelessEnergyHatchMAX16384A,
    WirelessEnergyHatchMAX65536A,
    WirelessEnergyHatchMAX262144A,
    WirelessEnergyHatchMAX1048576A,
    WirelessEnergyHatchMAX4194304A,
    WirelessEnergyHatchMAX16777216A,

    WirelessDynamoHatchLV1A,
    WirelessDynamoHatchLV4A,
    WirelessDynamoHatchLV16A,
    WirelessDynamoHatchLV64A,

    WirelessDynamoHatchMV1A,
    WirelessDynamoHatchMV4A,
    WirelessDynamoHatchMV16A,
    WirelessDynamoHatchMV64A,

    WirelessDynamoHatchHV1A,
    WirelessDynamoHatchHV4A,
    WirelessDynamoHatchHV16A,
    WirelessDynamoHatchHV64A,

    WirelessDynamoHatchIV1A,
    WirelessDynamoHatchIV4A,
    WirelessDynamoHatchIV16A,
    WirelessDynamoHatchIV64A,
    WirelessDynamoHatchIV256A,
    WirelessDynamoHatchIV1024A,
    WirelessDynamoHatchIV4096A,
    WirelessDynamoHatchIV16384A,
    WirelessDynamoHatchIV65536A,
    WirelessDynamoHatchIV262144A,
    WirelessDynamoHatchIV1048576A,
    WirelessDynamoHatchIV4194304A,
    WirelessDynamoHatchIV16777216A,

    WirelessDynamoHatchLuV1A,
    WirelessDynamoHatchLuV4A,
    WirelessDynamoHatchLuV16A,
    WirelessDynamoHatchLuV64A,
    WirelessDynamoHatchLuV256A,
    WirelessDynamoHatchLuV1024A,
    WirelessDynamoHatchLuV4096A,
    WirelessDynamoHatchLuV16384A,
    WirelessDynamoHatchLuV65536A,
    WirelessDynamoHatchLuV262144A,
    WirelessDynamoHatchLuV1048576A,
    WirelessDynamoHatchLuV4194304A,
    WirelessDynamoHatchLuV16777216A,

    WirelessDynamoHatchZPM1A,
    WirelessDynamoHatchZPM4A,
    WirelessDynamoHatchZPM16A,
    WirelessDynamoHatchZPM64A,
    WirelessDynamoHatchZPM256A,
    WirelessDynamoHatchZPM1024A,
    WirelessDynamoHatchZPM4096A,
    WirelessDynamoHatchZPM16384A,
    WirelessDynamoHatchZPM65536A,
    WirelessDynamoHatchZPM262144A,
    WirelessDynamoHatchZPM1048576A,
    WirelessDynamoHatchZPM4194304A,
    WirelessDynamoHatchZPM16777216A,

    WirelessDynamoHatchUV1A,
    WirelessDynamoHatchUV4A,
    WirelessDynamoHatchUV16A,
    WirelessDynamoHatchUV64A,
    WirelessDynamoHatchUV256A,
    WirelessDynamoHatchUV1024A,
    WirelessDynamoHatchUV4096A,
    WirelessDynamoHatchUV16384A,
    WirelessDynamoHatchUV65536A,
    WirelessDynamoHatchUV262144A,
    WirelessDynamoHatchUV1048576A,
    WirelessDynamoHatchUV4194304A,
    WirelessDynamoHatchUV16777216A,

    WirelessDynamoHatchUHV1A,
    WirelessDynamoHatchUHV4A,
    WirelessDynamoHatchUHV16A,
    WirelessDynamoHatchUHV64A,
    WirelessDynamoHatchUHV256A,
    WirelessDynamoHatchUHV1024A,
    WirelessDynamoHatchUHV4096A,
    WirelessDynamoHatchUHV16384A,
    WirelessDynamoHatchUHV65536A,
    WirelessDynamoHatchUHV262144A,
    WirelessDynamoHatchUHV1048576A,
    WirelessDynamoHatchUHV4194304A,
    WirelessDynamoHatchUHV16777216A,

    WirelessDynamoHatchUEV1A,
    WirelessDynamoHatchUEV4A,
    WirelessDynamoHatchUEV16A,
    WirelessDynamoHatchUEV64A,
    WirelessDynamoHatchUEV256A,
    WirelessDynamoHatchUEV1024A,
    WirelessDynamoHatchUEV4096A,
    WirelessDynamoHatchUEV16384A,
    WirelessDynamoHatchUEV65536A,
    WirelessDynamoHatchUEV262144A,
    WirelessDynamoHatchUEV1048576A,
    WirelessDynamoHatchUEV4194304A,
    WirelessDynamoHatchUEV16777216A,

    WirelessDynamoHatchUIV1A,
    WirelessDynamoHatchUIV4A,
    WirelessDynamoHatchUIV16A,
    WirelessDynamoHatchUIV64A,
    WirelessDynamoHatchUIV256A,
    WirelessDynamoHatchUIV1024A,
    WirelessDynamoHatchUIV4096A,
    WirelessDynamoHatchUIV16384A,
    WirelessDynamoHatchUIV65536A,
    WirelessDynamoHatchUIV262144A,
    WirelessDynamoHatchUIV1048576A,
    WirelessDynamoHatchUIV4194304A,
    WirelessDynamoHatchUIV16777216A,

    WirelessDynamoHatchUMV1A,
    WirelessDynamoHatchUMV4A,
    WirelessDynamoHatchUMV16A,
    WirelessDynamoHatchUMV64A,
    WirelessDynamoHatchUMV256A,
    WirelessDynamoHatchUMV1024A,
    WirelessDynamoHatchUMV4096A,
    WirelessDynamoHatchUMV16384A,
    WirelessDynamoHatchUMV65536A,
    WirelessDynamoHatchUMV262144A,
    WirelessDynamoHatchUMV1048576A,
    WirelessDynamoHatchUMV4194304A,
    WirelessDynamoHatchUMV16777216A,

    WirelessDynamoHatchMAX1A,
    WirelessDynamoHatchMAX4A,
    WirelessDynamoHatchMAX16A,
    WirelessDynamoHatchMAX64A,
    WirelessDynamoHatchMAX256A,
    WirelessDynamoHatchMAX1024A,
    WirelessDynamoHatchMAX4096A,
    WirelessDynamoHatchMAX16384A,
    WirelessDynamoHatchMAX65536A,
    WirelessDynamoHatchMAX262144A,
    WirelessDynamoHatchMAX1048576A,
    WirelessDynamoHatchMAX4194304A,
    WirelessDynamoHatchMAX16777216A,

    LVWirelessEnergyCover,
    MVWirelessEnergyCover,
    HVWirelessEnergyCover,
    EVWirelessEnergyCover,
    IVWirelessEnergyCover,
    LuVWirelessEnergyCover,
    ZPMWirelessEnergyCover,
    UVWirelessEnergyCover,
    UHVWirelessEnergyCover,
    UEVWirelessEnergyCover,
    UIVWirelessEnergyCover,
    UMVWirelessEnergyCover,
    UXVWirelessEnergyCover,
    MAXWirelessEnergyCover,

    LVWirelessEnergyCover4A,
    MVWirelessEnergyCover4A,
    HVWirelessEnergyCover4A,
    EVWirelessEnergyCover4A,
    IVWirelessEnergyCover4A,
    LuVWirelessEnergyCover4A,
    ZPMWirelessEnergyCover4A,
    UVWirelessEnergyCover4A,
    UHVWirelessEnergyCover4A,
    UEVWirelessEnergyCover4A,
    UIVWirelessEnergyCover4A,
    UMVWirelessEnergyCover4A,
    UXVWirelessEnergyCover4A,
    MAXWirelessEnergyCover4A,

    DieselGeneratorLV,
    DieselGeneratorMV,
    DieselGeneratorHV,
    DieselGeneratorEV,

    SuperMTEHatchCraftingInputME,
    SuperMTEHatchCraftingInputBusME,
    SuperMTEHatchCraftingInputSlave,
    VaultPortHatch,
    SingularityDataHub,
    LargeOreProcessor,
    MegaIndustrialApiary,
    SuperSpaceElevator,
    QuantumComputer,
    AssemblerMatrix,
    VeinMiningPickaxe,
    // ---- 外壳方块 (MetaCasing02) ----
    MineralprocessingFrame,
    AssemblerMatrixWall,
    AssemblerMatrixPatternCore,
    AssemblerMatrixCrafterCore,
    AssemblerMatrixSingularityCrafterCore,
    AssemblerMatrixSpeedCore,
    AssemblerMatrixDebugCrafterCore,
    QuantumComputerCasing,
    QuantumComputerUnit,
    QuantumComputerCraftingStorage128M,
    QuantumComputerCraftingStorage256M,
    QuantumComputerDataEntangler,
    QuantumComputerAccelerator,
    QuantumComputerMultiThreader,
    QuantumComputerCore,
    QuantumComputerSingularityCore,

    WhiteLamp,

    TimeAcceleratorLV,
    TimeAcceleratorMV,
    TimeAcceleratorHV,
    TimeAcceleratorEV,
    TimeAcceleratorIV,
    TimeAcceleratorLuV,
    TimeAcceleratorZPM,
    TimeAcceleratorUV,
    TimeAcceleratorUHV,

    MiracleDoor,
    MiracleDoorPhoton,
    MiracleDoorMold;

    public static final GTNCItemList[] ENERGY_HATCH_LV = { EnergyHatchLV, EnergyHatchLV4A, EnergyHatchLV16A,
        EnergyHatchLV64A };
    public static final GTNCItemList[] ENERGY_HATCH_MV = { EnergyHatchMV, EnergyHatchMV4A, EnergyHatchMV16A,
        EnergyHatchMV64A };
    public static final GTNCItemList[] ENERGY_HATCH_HV = { EnergyHatchHV, EnergyHatchHV4A, EnergyHatchHV16A,
        EnergyHatchHV64A };
    public static final GTNCItemList[] ENERGY_HATCH_EV = { EnergyHatchEV, EnergyHatchEV4A, EnergyHatchEV16A,
        EnergyHatchEV64A };
    public static final GTNCItemList[] ENERGY_HATCH_IV = { EnergyHatchIV, EnergyHatchIV4A, EnergyHatchIV16A,
        EnergyHatchIV64A };
    public static final GTNCItemList[] ENERGY_HATCH_LUV = { EnergyHatchLuV, EnergyHatchLuV4A, EnergyHatchLuV16A,
        EnergyHatchLuV64A };
    public static final GTNCItemList[] ENERGY_HATCH_ZPM = { EnergyHatchZPM, EnergyHatchZPM4A, EnergyHatchZPM16A,
        EnergyHatchZPM64A };
    public static final GTNCItemList[] ENERGY_HATCH_UV = { EnergyHatchUV, EnergyHatchUV4A, EnergyHatchUV16A,
        EnergyHatchUV64A };
    public static final GTNCItemList[] ENERGY_HATCH_UHV = { EnergyHatchUHV, EnergyHatchUHV4A, EnergyHatchUHV16A,
        EnergyHatchUHV64A };
    public static final GTNCItemList[] ENERGY_HATCH_UEV = { EnergyHatchUEV, EnergyHatchUEV4A, EnergyHatchUEV16A,
        EnergyHatchUEV64A };
    public static final GTNCItemList[] ENERGY_HATCH_UIV = { EnergyHatchUIV, EnergyHatchUIV4A, EnergyHatchUIV16A,
        EnergyHatchUIV64A };
    public static final GTNCItemList[] ENERGY_HATCH_UMV = { EnergyHatchUMV, EnergyHatchUMV4A, EnergyHatchUMV16A,
        EnergyHatchUMV64A };
    public static final GTNCItemList[] ENERGY_HATCH_UXV = { EnergyHatchUXV, EnergyHatchUXV4A, EnergyHatchUXV16A,
        EnergyHatchUXV64A };
    public static final GTNCItemList[] ENERGY_HATCH_MAX = { EnergyHatchMAX, EnergyHatchMAX4A, EnergyHatchMAX16A,
        EnergyHatchMAX64A };

    public static final GTNCItemList[][] ENERGY_HATCH = { ENERGY_HATCH_LV, ENERGY_HATCH_MV, ENERGY_HATCH_HV,
        ENERGY_HATCH_EV, ENERGY_HATCH_IV, ENERGY_HATCH_LUV, ENERGY_HATCH_ZPM, ENERGY_HATCH_UV, ENERGY_HATCH_UHV,
        ENERGY_HATCH_UEV, ENERGY_HATCH_UIV, ENERGY_HATCH_UMV, ENERGY_HATCH_UXV, ENERGY_HATCH_MAX };

    public static final GTNCItemList[] LASER_ENERGY_HATCH_IV = { LaserEnergyHatchIV256A, LaserEnergyHatchIV1024A,
        LaserEnergyHatchIV4096A, LaserEnergyHatchIV16384A, LaserEnergyHatchIV65536A, LaserEnergyHatchIV262144A,
        LaserEnergyHatchIV1048576A, LaserEnergyHatchIV4194304A, LaserEnergyHatchIV16777216A };

    public static final GTNCItemList[] LASER_ENERGY_HATCH_LUV = { LaserEnergyHatchLuV256A, LaserEnergyHatchLuV1024A,
        LaserEnergyHatchLuV4096A, LaserEnergyHatchLuV16384A, LaserEnergyHatchLuV65536A, LaserEnergyHatchLuV262144A,
        LaserEnergyHatchLuV1048576A, LaserEnergyHatchLuV4194304A, LaserEnergyHatchLuV16777216A };

    public static final GTNCItemList[] LASER_ENERGY_HATCH_ZPM = { LaserEnergyHatchZPM256A, LaserEnergyHatchZPM1024A,
        LaserEnergyHatchZPM4096A, LaserEnergyHatchZPM16384A, LaserEnergyHatchZPM65536A, LaserEnergyHatchZPM262144A,
        LaserEnergyHatchZPM1048576A, LaserEnergyHatchZPM4194304A, LaserEnergyHatchZPM16777216A };

    public static final GTNCItemList[] LASER_ENERGY_HATCH_UV = { LaserEnergyHatchUV256A, LaserEnergyHatchUV1024A,
        LaserEnergyHatchUV4096A, LaserEnergyHatchUV16384A, LaserEnergyHatchUV65536A, LaserEnergyHatchUV262144A,
        LaserEnergyHatchUV1048576A, LaserEnergyHatchUV4194304A, LaserEnergyHatchUV16777216A };

    public static final GTNCItemList[] LASER_ENERGY_HATCH_UHV = { LaserEnergyHatchUHV256A, LaserEnergyHatchUHV1024A,
        LaserEnergyHatchUHV4096A, LaserEnergyHatchUHV16384A, LaserEnergyHatchUHV65536A, LaserEnergyHatchUHV262144A,
        LaserEnergyHatchUHV1048576A, LaserEnergyHatchUHV4194304A, LaserEnergyHatchUHV16777216A };

    public static final GTNCItemList[] LASER_ENERGY_HATCH_UEV = { LaserEnergyHatchUEV256A, LaserEnergyHatchUEV1024A,
        LaserEnergyHatchUEV4096A, LaserEnergyHatchUEV16384A, LaserEnergyHatchUEV65536A, LaserEnergyHatchUEV262144A,
        LaserEnergyHatchUEV1048576A, LaserEnergyHatchUEV4194304A, LaserEnergyHatchUEV16777216A };

    public static final GTNCItemList[] LASER_ENERGY_HATCH_UIV = { LaserEnergyHatchUIV256A, LaserEnergyHatchUIV1024A,
        LaserEnergyHatchUIV4096A, LaserEnergyHatchUIV16384A, LaserEnergyHatchUIV65536A, LaserEnergyHatchUIV262144A,
        LaserEnergyHatchUIV1048576A, LaserEnergyHatchUIV4194304A, LaserEnergyHatchUIV16777216A };

    public static final GTNCItemList[] LASER_ENERGY_HATCH_UMV = { LaserEnergyHatchUMV256A, LaserEnergyHatchUMV1024A,
        LaserEnergyHatchUMV4096A, LaserEnergyHatchUMV16384A, LaserEnergyHatchUMV65536A, LaserEnergyHatchUMV262144A,
        LaserEnergyHatchUMV1048576A, LaserEnergyHatchUMV4194304A, LaserEnergyHatchUMV16777216A };

    public static final GTNCItemList[] LASER_ENERGY_HATCH_UXV = { LaserEnergyHatchUXV256A, LaserEnergyHatchUXV1024A,
        LaserEnergyHatchUXV4096A, LaserEnergyHatchUXV16384A, LaserEnergyHatchUXV65536A, LaserEnergyHatchUXV262144A,
        LaserEnergyHatchUXV1048576A, LaserEnergyHatchUXV4194304A, LaserEnergyHatchUXV16777216A };

    public static final GTNCItemList[] LASER_ENERGY_HATCH_MAX = { LaserEnergyHatchMAX256A, LaserEnergyHatchMAX1024A,
        LaserEnergyHatchMAX4096A, LaserEnergyHatchMAX16384A, LaserEnergyHatchMAX65536A, LaserEnergyHatchMAX262144A,
        LaserEnergyHatchMAX1048576A, LaserEnergyHatchMAX4194304A, LaserEnergyHatchMAX16777216A };

    public static final GTNCItemList[][] LASER_ENERGY_HATCH = { LASER_ENERGY_HATCH_IV, LASER_ENERGY_HATCH_LUV,
        LASER_ENERGY_HATCH_ZPM, LASER_ENERGY_HATCH_UV, LASER_ENERGY_HATCH_UHV, LASER_ENERGY_HATCH_UEV,
        LASER_ENERGY_HATCH_UIV, LASER_ENERGY_HATCH_UMV, LASER_ENERGY_HATCH_UXV, LASER_ENERGY_HATCH_MAX };

    public static final GTNCItemList[] WIRELESS_ENERGY_HATCH_LV = { WirelessEnergyHatchLV, WirelessEnergyHatchLV4A,
        WirelessEnergyHatchLV16A, WirelessEnergyHatchLV64A };

    public static final GTNCItemList[] WIRELESS_ENERGY_HATCH_MV = { WirelessEnergyHatchMV, WirelessEnergyHatchMV4A,
        WirelessEnergyHatchMV16A, WirelessEnergyHatchMV64A };

    public static final GTNCItemList[] WIRELESS_ENERGY_HATCH_HV = { WirelessEnergyHatchHV, WirelessEnergyHatchHV4A,
        WirelessEnergyHatchHV16A, WirelessEnergyHatchHV64A };

    public static final GTNCItemList[] WIRELESS_ENERGY_HATCH_EV = { WirelessEnergyHatchEV, WirelessEnergyHatchEV4A,
        WirelessEnergyHatchEV16A, WirelessEnergyHatchEV64A };

    public static final GTNCItemList[] WIRELESS_ENERGY_HATCH_IV = { WirelessEnergyHatchIV, WirelessEnergyHatchIV4A,
        WirelessEnergyHatchIV16A, WirelessEnergyHatchIV64A, WirelessEnergyHatchIV256A, WirelessEnergyHatchIV1024A,
        WirelessEnergyHatchIV4096A, WirelessEnergyHatchIV16384A, WirelessEnergyHatchIV65536A,
        WirelessEnergyHatchIV262144A, WirelessEnergyHatchIV1048576A, WirelessEnergyHatchIV4194304A,
        WirelessEnergyHatchIV16777216A };

    public static final GTNCItemList[] WIRELESS_ENERGY_HATCH_LUV = { WirelessEnergyHatchLuV, WirelessEnergyHatchLuV4A,
        WirelessEnergyHatchLuV16A, WirelessEnergyHatchLuV64A, WirelessEnergyHatchLuV256A, WirelessEnergyHatchLuV1024A,
        WirelessEnergyHatchLuV4096A, WirelessEnergyHatchLuV16384A, WirelessEnergyHatchLuV65536A,
        WirelessEnergyHatchLuV262144A, WirelessEnergyHatchLuV1048576A, WirelessEnergyHatchLuV4194304A,
        WirelessEnergyHatchLuV16777216A };

    public static final GTNCItemList[] WIRELESS_ENERGY_HATCH_ZPM = { WirelessEnergyHatchZPM, WirelessEnergyHatchZPM4A,
        WirelessEnergyHatchZPM16A, WirelessEnergyHatchZPM64A, WirelessEnergyHatchZPM256A, WirelessEnergyHatchZPM1024A,
        WirelessEnergyHatchZPM4096A, WirelessEnergyHatchZPM16384A, WirelessEnergyHatchZPM65536A,
        WirelessEnergyHatchZPM262144A, WirelessEnergyHatchZPM1048576A, WirelessEnergyHatchZPM4194304A,
        WirelessEnergyHatchZPM16777216A };

    public static final GTNCItemList[] WIRELESS_ENERGY_HATCH_UV = { WirelessEnergyHatchUV, WirelessEnergyHatchUV4A,
        WirelessEnergyHatchUV16A, WirelessEnergyHatchUV64A, WirelessEnergyHatchUV256A, WirelessEnergyHatchUV1024A,
        WirelessEnergyHatchUV4096A, WirelessEnergyHatchUV16384A, WirelessEnergyHatchUV65536A,
        WirelessEnergyHatchUV262144A, WirelessEnergyHatchUV1048576A, WirelessEnergyHatchUV4194304A,
        WirelessEnergyHatchUV16777216A };

    public static final GTNCItemList[] WIRELESS_ENERGY_HATCH_UHV = { WirelessEnergyHatchUHV, WirelessEnergyHatchUHV4A,
        WirelessEnergyHatchUHV16A, WirelessEnergyHatchUHV64A, WirelessEnergyHatchUHV256A, WirelessEnergyHatchUHV1024A,
        WirelessEnergyHatchUHV4096A, WirelessEnergyHatchUHV16384A, WirelessEnergyHatchUHV65536A,
        WirelessEnergyHatchUHV262144A, WirelessEnergyHatchUHV1048576A, WirelessEnergyHatchUHV4194304A,
        WirelessEnergyHatchUHV16777216A };

    public static final GTNCItemList[] WIRELESS_ENERGY_HATCH_UEV = { WirelessEnergyHatchUEV, WirelessEnergyHatchUEV4A,
        WirelessEnergyHatchUEV16A, WirelessEnergyHatchUEV64A, WirelessEnergyHatchUEV256A, WirelessEnergyHatchUEV1024A,
        WirelessEnergyHatchUEV4096A, WirelessEnergyHatchUEV16384A, WirelessEnergyHatchUEV65536A,
        WirelessEnergyHatchUEV262144A, WirelessEnergyHatchUEV1048576A, WirelessEnergyHatchUEV4194304A,
        WirelessEnergyHatchUEV16777216A };

    public static final GTNCItemList[] WIRELESS_ENERGY_HATCH_UIV = { WirelessEnergyHatchUIV, WirelessEnergyHatchUIV4A,
        WirelessEnergyHatchUIV16A, WirelessEnergyHatchUIV64A, WirelessEnergyHatchUIV256A, WirelessEnergyHatchUIV1024A,
        WirelessEnergyHatchUIV4096A, WirelessEnergyHatchUIV16384A, WirelessEnergyHatchUIV65536A,
        WirelessEnergyHatchUIV262144A, WirelessEnergyHatchUIV1048576A, WirelessEnergyHatchUIV4194304A,
        WirelessEnergyHatchUIV16777216A };

    public static final GTNCItemList[] WIRELESS_ENERGY_HATCH_UMV = { WirelessEnergyHatchUMV, WirelessEnergyHatchUMV4A,
        WirelessEnergyHatchUMV16A, WirelessEnergyHatchUMV64A, WirelessEnergyHatchUMV256A, WirelessEnergyHatchUMV1024A,
        WirelessEnergyHatchUMV4096A, WirelessEnergyHatchUMV16384A, WirelessEnergyHatchUMV65536A,
        WirelessEnergyHatchUMV262144A, WirelessEnergyHatchUMV1048576A, WirelessEnergyHatchUMV4194304A,
        WirelessEnergyHatchUMV16777216A };

    public static final GTNCItemList[] WIRELESS_ENERGY_HATCH_UXV = { WirelessEnergyHatchUXV, WirelessEnergyHatchUXV4A,
        WirelessEnergyHatchUXV16A, WirelessEnergyHatchUXV64A, WirelessEnergyHatchUXV256A, WirelessEnergyHatchUXV1024A,
        WirelessEnergyHatchUXV4096A, WirelessEnergyHatchUXV16384A, WirelessEnergyHatchUXV65536A,
        WirelessEnergyHatchUXV262144A, WirelessEnergyHatchUXV1048576A, WirelessEnergyHatchUXV4194304A,
        WirelessEnergyHatchUXV16777216A };

    public static final GTNCItemList[] WIRELESS_ENERGY_HATCH_MAX = { WirelessEnergyHatchMAX, WirelessEnergyHatchMAX4A,
        WirelessEnergyHatchMAX16A, WirelessEnergyHatchMAX64A, WirelessEnergyHatchMAX256A, WirelessEnergyHatchMAX1024A,
        WirelessEnergyHatchMAX4096A, WirelessEnergyHatchMAX16384A, WirelessEnergyHatchMAX65536A,
        WirelessEnergyHatchMAX262144A, WirelessEnergyHatchMAX1048576A, WirelessEnergyHatchMAX4194304A,
        WirelessEnergyHatchMAX16777216A };

    public static final GTNCItemList[][] WIRELESS_ENERGY_HATCHES = { WIRELESS_ENERGY_HATCH_LV, WIRELESS_ENERGY_HATCH_MV,
        WIRELESS_ENERGY_HATCH_HV, WIRELESS_ENERGY_HATCH_EV, WIRELESS_ENERGY_HATCH_IV, WIRELESS_ENERGY_HATCH_LUV,
        WIRELESS_ENERGY_HATCH_ZPM, WIRELESS_ENERGY_HATCH_UV, WIRELESS_ENERGY_HATCH_UHV, WIRELESS_ENERGY_HATCH_UEV,
        WIRELESS_ENERGY_HATCH_UIV, WIRELESS_ENERGY_HATCH_UMV, WIRELESS_ENERGY_HATCH_UXV, WIRELESS_ENERGY_HATCH_MAX };

    public static final GTNCItemList[] WIRELESS_DYNAMO_HATCH_LV = { WirelessDynamoHatchLV1A, WirelessDynamoHatchLV4A,
        WirelessDynamoHatchLV16A, WirelessDynamoHatchLV64A };

    public static final GTNCItemList[] WIRELESS_DYNAMO_HATCH_MV = { WirelessDynamoHatchMV1A, WirelessDynamoHatchMV4A,
        WirelessDynamoHatchMV16A, WirelessDynamoHatchMV64A };

    public static final GTNCItemList[] WIRELESS_DYNAMO_HATCH_HV = { WirelessDynamoHatchHV1A, WirelessDynamoHatchHV4A,
        WirelessDynamoHatchHV16A, WirelessDynamoHatchHV64A };

    public static final GTNCItemList[] WIRELESS_DYNAMO_HATCH_IV = { WirelessDynamoHatchIV1A, WirelessDynamoHatchIV4A,
        WirelessDynamoHatchIV16A, WirelessDynamoHatchIV64A, WirelessDynamoHatchIV256A, WirelessDynamoHatchIV1024A,
        WirelessDynamoHatchIV4096A, WirelessDynamoHatchIV16384A, WirelessDynamoHatchIV65536A,
        WirelessDynamoHatchIV262144A, WirelessDynamoHatchIV1048576A, WirelessDynamoHatchIV4194304A,
        WirelessDynamoHatchIV16777216A };

    public static final GTNCItemList[] WIRELESS_DYNAMO_HATCH_LUV = { WirelessDynamoHatchLuV1A, WirelessDynamoHatchLuV4A,
        WirelessDynamoHatchLuV16A, WirelessDynamoHatchLuV64A, WirelessDynamoHatchLuV256A, WirelessDynamoHatchLuV1024A,
        WirelessDynamoHatchLuV4096A, WirelessDynamoHatchLuV16384A, WirelessDynamoHatchLuV65536A,
        WirelessDynamoHatchLuV262144A, WirelessDynamoHatchLuV1048576A, WirelessDynamoHatchLuV4194304A,
        WirelessDynamoHatchLuV16777216A };

    public static final GTNCItemList[] WIRELESS_DYNAMO_HATCH_ZPM = { WirelessDynamoHatchZPM1A, WirelessDynamoHatchZPM4A,
        WirelessDynamoHatchZPM16A, WirelessDynamoHatchZPM64A, WirelessDynamoHatchZPM256A, WirelessDynamoHatchZPM1024A,
        WirelessDynamoHatchZPM4096A, WirelessDynamoHatchZPM16384A, WirelessDynamoHatchZPM65536A,
        WirelessDynamoHatchZPM262144A, WirelessDynamoHatchZPM1048576A, WirelessDynamoHatchZPM4194304A,
        WirelessDynamoHatchZPM16777216A };

    public static final GTNCItemList[] WIRELESS_DYNAMO_HATCH_UV = { WirelessDynamoHatchUV1A, WirelessDynamoHatchUV4A,
        WirelessDynamoHatchUV16A, WirelessDynamoHatchUV64A, WirelessDynamoHatchUV256A, WirelessDynamoHatchUV1024A,
        WirelessDynamoHatchUV4096A, WirelessDynamoHatchUV16384A, WirelessDynamoHatchUV65536A,
        WirelessDynamoHatchUV262144A, WirelessDynamoHatchUV1048576A, WirelessDynamoHatchUV4194304A,
        WirelessDynamoHatchUV16777216A };

    public static final GTNCItemList[] WIRELESS_DYNAMO_HATCH_UHV = { WirelessDynamoHatchUHV1A, WirelessDynamoHatchUHV4A,
        WirelessDynamoHatchUHV16A, WirelessDynamoHatchUHV64A, WirelessDynamoHatchUHV256A, WirelessDynamoHatchUHV1024A,
        WirelessDynamoHatchUHV4096A, WirelessDynamoHatchUHV16384A, WirelessDynamoHatchUHV65536A,
        WirelessDynamoHatchUHV262144A, WirelessDynamoHatchUHV1048576A, WirelessDynamoHatchUHV4194304A,
        WirelessDynamoHatchUHV16777216A };

    public static final GTNCItemList[] WIRELESS_DYNAMO_HATCH_UEV = { WirelessDynamoHatchUEV1A, WirelessDynamoHatchUEV4A,
        WirelessDynamoHatchUEV16A, WirelessDynamoHatchUEV64A, WirelessDynamoHatchUEV256A, WirelessDynamoHatchUEV1024A,
        WirelessDynamoHatchUEV4096A, WirelessDynamoHatchUEV16384A, WirelessDynamoHatchUEV65536A,
        WirelessDynamoHatchUEV262144A, WirelessDynamoHatchUEV1048576A, WirelessDynamoHatchUEV4194304A,
        WirelessDynamoHatchUEV16777216A };

    public static final GTNCItemList[] WIRELESS_DYNAMO_HATCH_UIV = { WirelessDynamoHatchUIV1A, WirelessDynamoHatchUIV4A,
        WirelessDynamoHatchUIV16A, WirelessDynamoHatchUIV64A, WirelessDynamoHatchUIV256A, WirelessDynamoHatchUIV1024A,
        WirelessDynamoHatchUIV4096A, WirelessDynamoHatchUIV16384A, WirelessDynamoHatchUIV65536A,
        WirelessDynamoHatchUIV262144A, WirelessDynamoHatchUIV1048576A, WirelessDynamoHatchUIV4194304A,
        WirelessDynamoHatchUIV16777216A };

    public static final GTNCItemList[] WIRELESS_DYNAMO_HATCH_UMV = { WirelessDynamoHatchUMV1A, WirelessDynamoHatchUMV4A,
        WirelessDynamoHatchUMV16A, WirelessDynamoHatchUMV64A, WirelessDynamoHatchUMV256A, WirelessDynamoHatchUMV1024A,
        WirelessDynamoHatchUMV4096A, WirelessDynamoHatchUMV16384A, WirelessDynamoHatchUMV65536A,
        WirelessDynamoHatchUMV262144A, WirelessDynamoHatchUMV1048576A, WirelessDynamoHatchUMV4194304A,
        WirelessDynamoHatchUMV16777216A };

    public static final GTNCItemList[] WIRELESS_DYNAMO_HATCH_MAX = { WirelessDynamoHatchMAX1A, WirelessDynamoHatchMAX4A,
        WirelessDynamoHatchMAX16A, WirelessDynamoHatchMAX64A, WirelessDynamoHatchMAX256A, WirelessDynamoHatchMAX1024A,
        WirelessDynamoHatchMAX4096A, WirelessDynamoHatchMAX16384A, WirelessDynamoHatchMAX65536A,
        WirelessDynamoHatchMAX262144A, WirelessDynamoHatchMAX1048576A, WirelessDynamoHatchMAX4194304A,
        WirelessDynamoHatchMAX16777216A };

    public static final GTNCItemList[][] WIRELESS_DYNAMO_HATCHES = { WIRELESS_DYNAMO_HATCH_LV, WIRELESS_DYNAMO_HATCH_MV,
        WIRELESS_DYNAMO_HATCH_HV, WIRELESS_DYNAMO_HATCH_IV, WIRELESS_DYNAMO_HATCH_LUV, WIRELESS_DYNAMO_HATCH_ZPM,
        WIRELESS_DYNAMO_HATCH_UV, WIRELESS_DYNAMO_HATCH_UHV, WIRELESS_DYNAMO_HATCH_UEV, WIRELESS_DYNAMO_HATCH_UIV,
        WIRELESS_DYNAMO_HATCH_UMV, WIRELESS_DYNAMO_HATCH_MAX };

    public static final GTNCItemList[] WIRELESS_ENERGY_COVER = new GTNCItemList[] { LVWirelessEnergyCover,
        MVWirelessEnergyCover, HVWirelessEnergyCover, EVWirelessEnergyCover, IVWirelessEnergyCover,
        LuVWirelessEnergyCover, ZPMWirelessEnergyCover, UVWirelessEnergyCover, UHVWirelessEnergyCover,
        UEVWirelessEnergyCover, UIVWirelessEnergyCover, UMVWirelessEnergyCover, UXVWirelessEnergyCover,
        MAXWirelessEnergyCover };

    public static final GTNCItemList[] WIRELESS_ENERGY_COVER_4A = new GTNCItemList[] { LVWirelessEnergyCover4A,
        MVWirelessEnergyCover4A, HVWirelessEnergyCover4A, EVWirelessEnergyCover4A, IVWirelessEnergyCover4A,
        LuVWirelessEnergyCover4A, ZPMWirelessEnergyCover4A, UVWirelessEnergyCover4A, UHVWirelessEnergyCover4A,
        UEVWirelessEnergyCover4A, UIVWirelessEnergyCover4A, UMVWirelessEnergyCover4A, UXVWirelessEnergyCover4A,
        MAXWirelessEnergyCover4A };

    public boolean mHasNotBeenSet;
    public boolean mDeprecated;
    public boolean mWarned;

    public ItemStack mStack;

    GTNCItemList() {
        mHasNotBeenSet = true;
    }

    GTNCItemList(boolean aDeprecated) {
        if (aDeprecated) {
            mDeprecated = true;
            mHasNotBeenSet = true;
        }
    }

    public void sanityCheck() {
        if (mHasNotBeenSet)
            throw new IllegalAccessError("The Enum '" + name() + "' has not been set to an Item at this time!");
        if (mDeprecated && !mWarned) {
            ScienceNotCool.LOG.error("{} is now deprecated", this, new Exception());
            // warn only once
            mWarned = true;
        }
    }

    public Item getItem() {
        sanityCheck();
        if (GTUtility.isStackInvalid(mStack)) return null;
        return mStack.getItem();
    }

    public Block getBlock() {
        sanityCheck();
        return Block.getBlockFromItem(getItem());
    }

    @Override
    public ItemStack get(long aAmount, Object... aReplacements) {
        sanityCheck();
        // if invalid, return a replacements
        if (GTUtility.isStackInvalid(mStack)) {
            ScienceNotCool.LOG.warn("Object in the GTNCItemList is null at:", new NullPointerException());
            return GTUtility.copyAmountUnsafe(Math.toIntExact(aAmount), Machine_LV_Miner.get(1));
        }
        return GTUtility.copyAmountUnsafe(Math.toIntExact(aAmount), mStack);
    }

    public ItemStack getWithMeta(long aAmount, int meta, Object... aReplacements) {
        sanityCheck();
        // if invalid, return a replacements
        if (GTUtility.isStackInvalid(mStack)) {
            ScienceNotCool.LOG.warn("Object in the GTNCItemList is null at:", new NullPointerException());
            ItemStack fallback = Machine_LV_Miner.get(1);
            fallback.setItemDamage(meta);
            return GTUtility.copyAmountUnsafe(Math.toIntExact(aAmount), fallback);
        }

        ItemStack stack = GTUtility.copyAmountUnsafe(Math.toIntExact(aAmount), mStack);
        stack.setItemDamage(meta);
        return stack;
    }

    public int getWithMeta() {
        return mStack.getItemDamage();
    }

    public GTNCItemList set(Item aItem) {
        if (aItem == null) return this;
        return set(Utils.newItemStack(aItem));
    }

    public GTNCItemList set(ItemStack aStack) {
        if (aStack == null) return this;
        mHasNotBeenSet = false;
        mStack = GTUtility.copyAmountUnsafe(1, aStack);
        if (Utils.isClientSide()) {
            Item item = mStack.getItem();
            if (item == null) return this;
            if (Block.getBlockFromItem(item) == GregTechAPI.sBlockMachines) {
                GTNCCreativeTabs.addToMachineList(mStack.copy());
            }
        }
        return this;
    }

    public GTNCItemList set(IMetaTileEntity metaTileEntity) {
        if (metaTileEntity == null) throw new IllegalArgumentException();
        return set(metaTileEntity.getStackForm(1L));
    }

    public boolean hasBeenSet() {
        return !mHasNotBeenSet;
    }

    /**
     * Returns the internal stack. This method is unsafe. It's here only for quick operations. DON'T CHANGE THE RETURNED
     * VALUE!
     */
    public ItemStack getInternalStack_unsafe() {
        return mStack;
    }

    @Override
    public boolean isStackEqual(Object aStack) {
        return isStackEqual(aStack, false, false);
    }

    @Override
    public boolean isStackEqual(Object aStack, boolean aWildcard, boolean aIgnoreNBT) {
        if (mDeprecated && !mWarned) {
            ScienceNotCool.LOG.error("{} is now deprecated", this, new Exception());
            // warn only once
            mWarned = true;
        }
        if (GTUtility.isStackInvalid(aStack)) return false;
        return GTUtility.areUnificationsEqual((ItemStack) aStack, aWildcard ? getWildcard(1) : get(1), aIgnoreNBT);
    }

    @Override
    public ItemStack getWildcard(long aAmount, Object... aReplacements) {
        sanityCheck();
        if (GTUtility.isStackInvalid(mStack)) return GTUtility.copyAmount(aAmount, aReplacements);
        return GTUtility.copyAmountAndMetaData(aAmount, GTRecipeBuilder.WILDCARD, GTOreDictUnificator.get(mStack));
    }

    @Override
    public ItemStack getUndamaged(long aAmount, Object... aReplacements) {
        sanityCheck();
        if (GTUtility.isStackInvalid(mStack)) return GTUtility.copyAmount(aAmount, aReplacements);
        return GTUtility.copyAmountAndMetaData(aAmount, 0, GTOreDictUnificator.get(mStack));
    }

    @Override
    public ItemStack getAlmostBroken(long aAmount, Object... aReplacements) {
        sanityCheck();
        if (GTUtility.isStackInvalid(mStack)) return GTUtility.copyAmount(aAmount, aReplacements);
        return GTUtility.copyAmountAndMetaData(aAmount, mStack.getMaxDamage() - 1, GTOreDictUnificator.get(mStack));
    }

    @Override
    public ItemStack getWithName(long aAmount, String aDisplayName, Object... aReplacements) {
        ItemStack rStack = get(1, aReplacements);
        if (GTUtility.isStackInvalid(rStack)) return NI;

        // CamelCase alphanumeric words from aDisplayName
        StringBuilder tCamelCasedDisplayNameBuilder = new StringBuilder();
        final String[] tDisplayNameWords = aDisplayName.split("\\W");
        for (String tWord : tDisplayNameWords) {
            if (!tWord.isEmpty()) tCamelCasedDisplayNameBuilder.append(
                tWord.substring(0, 1)
                    .toUpperCase(Locale.US));
            if (tWord.length() > 1) tCamelCasedDisplayNameBuilder.append(
                tWord.substring(1)
                    .toLowerCase(Locale.US));
        }
        if (tCamelCasedDisplayNameBuilder.length() == 0) {
            // CamelCased DisplayName is empty, so use hash of aDisplayName
            tCamelCasedDisplayNameBuilder.append(((Long) (long) aDisplayName.hashCode()));
        }

        // Construct a translation key from UnlocalizedName and CamelCased DisplayName
        final String tKey = rStack.getUnlocalizedName() + ".with." + tCamelCasedDisplayNameBuilder + ".name";

        GTLanguageManager.addStringLocalization(tKey, aDisplayName);
        rStack.setStackDisplayName(StatCollector.translateToLocal(tKey));
        return GTUtility.copyAmount(aAmount, rStack);
    }

    @Override
    public ItemStack getWithCharge(long aAmount, int aEnergy, Object... aReplacements) {
        ItemStack rStack = get(1, aReplacements);
        if (GTUtility.isStackInvalid(rStack)) return null;
        GTModHandler.chargeElectricItem(rStack, aEnergy, Integer.MAX_VALUE, true, false);
        return GTUtility.copyAmount(aAmount, rStack);
    }

    @Override
    public ItemStack getWithDamage(long aAmount, long aMetaValue, Object... aReplacements) {
        sanityCheck();
        if (GTUtility.isStackInvalid(mStack)) return GTUtility.copyAmount(aAmount, aReplacements);
        return GTUtility.copyAmountAndMetaData(aAmount, aMetaValue, GTOreDictUnificator.get(mStack));
    }

    @Override
    public IItemContainer registerOre(Object... aOreNames) {
        sanityCheck();
        for (Object tOreName : aOreNames) GTOreDictUnificator.registerOre(tOreName, get(1));
        return this;
    }

    @Override
    public IItemContainer registerWildcardAsOre(Object... aOreNames) {
        sanityCheck();
        for (Object tOreName : aOreNames) GTOreDictUnificator.registerOre(tOreName, getWildcard(1));
        return this;
    }

}
