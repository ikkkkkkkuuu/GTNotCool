package com.xyp.gtnc.Loader;

import static com.xyp.gtnc.utils.text.AnimatedTooltipHandler.addItemTooltip;

import net.minecraft.util.StatCollector;

import com.xyp.gtnc.Common.machines.basicMachine.DieselGenerator;
import com.xyp.gtnc.Common.machines.basicMachine.SteamTurbine;
import com.xyp.gtnc.Common.machines.cover.WirelessMultiEnergyCover;
import com.xyp.gtnc.Common.machines.hatch.SuperMTEHatchCraftingInputME;
import com.xyp.gtnc.Common.machines.hatch.SuperMTEHatchCraftingInputSlave;
import com.xyp.gtnc.Common.machines.hatch.VaultPortHatch;
import com.xyp.gtnc.Common.machines.multiblock.LargeOreProcessor;
import com.xyp.gtnc.Common.machines.multiblock.LargeSteamCombProcessor;
import com.xyp.gtnc.Common.machines.multiblock.MTEMegaIndustrialApiary;
import com.xyp.gtnc.Common.machines.multiblock.SingularityDataHub;
import com.xyp.gtnc.Common.machines.multiblock.steam.LargeBoiler;
import com.xyp.gtnc.Common.machines.multiblock.steam.LargeSteamAlloySmelter;
import com.xyp.gtnc.Common.machines.multiblock.steam.LargeSteamAssembler;
import com.xyp.gtnc.Common.machines.multiblock.steam.LargeSteamBeeBreeder;
import com.xyp.gtnc.Common.machines.multiblock.steam.LargeSteamBending;
import com.xyp.gtnc.Common.machines.multiblock.steam.LargeSteamCentrifuge;
import com.xyp.gtnc.Common.machines.multiblock.steam.LargeSteamChemicalReactor;
import com.xyp.gtnc.Common.machines.multiblock.steam.LargeSteamCircuitAssembler;
import com.xyp.gtnc.Common.machines.multiblock.steam.LargeSteamCompressor;
import com.xyp.gtnc.Common.machines.multiblock.steam.LargeSteamCutting;
import com.xyp.gtnc.Common.machines.multiblock.steam.LargeSteamDistillationTower;
import com.xyp.gtnc.Common.machines.multiblock.steam.LargeSteamElectrolyzer;
import com.xyp.gtnc.Common.machines.multiblock.steam.LargeSteamExtruder;
import com.xyp.gtnc.Common.machines.multiblock.steam.LargeSteamFluidExtractor;
import com.xyp.gtnc.Common.machines.multiblock.steam.LargeSteamFluidSolidifier;
import com.xyp.gtnc.Common.machines.multiblock.steam.LargeSteamFormingPress;
import com.xyp.gtnc.Common.machines.multiblock.steam.LargeSteamHammer;
import com.xyp.gtnc.Common.machines.multiblock.steam.LargeSteamLaserEngraver;
import com.xyp.gtnc.Common.machines.multiblock.steam.LargeSteamMixer;
import com.xyp.gtnc.Common.machines.multiblock.steam.LargeSteamTurbine;
import com.xyp.gtnc.Common.machines.multiblock.steam.LargeSteamWireMill;
import com.xyp.gtnc.utils.enums.GTNCItemList;
import com.xyp.gtnc.utils.enums.GTNCMachineID;
import com.xyp.gtnc.utils.text.AnimatedText;
import com.xyp.gtnc.utils.text.AnimatedTooltipHandler;

import gregtech.api.covers.CoverRegistry;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Textures;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.render.TextureFactory;
import tectech.thing.CustomItemList;
import tectech.thing.metaTileEntity.hatch.MTEHatchEnergyMulti;
import tectech.thing.metaTileEntity.hatch.MTEHatchEnergyTunnel;
import tectech.thing.metaTileEntity.hatch.MTEHatchWirelessDynamoMulti;
import tectech.thing.metaTileEntity.hatch.MTEHatchWirelessMulti;

public class MachineLoader {

    public static void registerMachines() {
        // #tr NameSingularityDataHub
        // # Singularity Data Hub
        // # zh_CN 奇点数据枢纽
        GTNCItemList.SingularityDataHub.set(
            new SingularityDataHub(
                GTNCMachineID.SINGULARITY_DATA_HUB.ID,
                "SingularityDataHub",
                StatCollector.translateToLocal("NameSingularityDataHub")));
        addItemTooltip(GTNCItemList.SingularityDataHub.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr NameVaultPortHatch
        // # Vault Port Hatch
        // # zh_CN 仓库端口仓
        GTNCItemList.VaultPortHatch.set(
            new VaultPortHatch(
                GTNCMachineID.VAULT_PORT_HATCH.ID,
                "VaultPortHatch",
                StatCollector.translateToLocal("NameVaultPortHatch")));
        addItemTooltip(GTNCItemList.VaultPortHatch.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr NameLargeOreProcessor
        // # Large Ore Processor
        // # zh_CN 大型矿石处理器
        GTNCItemList.LargeOreProcessor.set(
            new LargeOreProcessor(
                GTNCMachineID.LARGE_ORE_PROCESSOR.ID,
                "LargeOreProcessor",
                StatCollector.translateToLocal("NameLargeOreProcessor")));
        addItemTooltip(GTNCItemList.LargeOreProcessor.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr NameMegaIndustrialApiary
        // # Mega Industrial Apiary
        // # zh_CN 巨型工业蜂箱
        GTNCItemList.MegaIndustrialApiary.set(
            new MTEMegaIndustrialApiary(
                GTNCMachineID.MEGA_INDUSTRIAL_APIARY.ID,
                "MegaIndustrialApiary",
                StatCollector.translateToLocal("NameMegaIndustrialApiary")));
        addItemTooltip(GTNCItemList.MegaIndustrialApiary.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr NameLargeBoilerBronze
        // # Large Bronze Boiler
        // # zh_CN 大型青铜锅炉
        GTNCItemList.LargeBoilerBronze.set(
            new LargeBoiler.LargeBoilerBronze(
                GTNCMachineID.LARGE_BOILER_BRONZE.ID,
                "LargeBoilerBronze",
                StatCollector.translateToLocal("NameLargeBoilerBronze")));
        AnimatedTooltipHandler.addItemTooltip(GTNCItemList.LargeBoilerBronze.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr NameLargeBoilerSteel
        // # Large Steel Boiler
        // # zh_CN 大型钢锅炉
        GTNCItemList.LargeBoilerSteel.set(
            new LargeBoiler.LargeBoilerSteel(
                GTNCMachineID.LARGE_BOILER_STEEL.ID,
                "LargeBoilerSteel",
                StatCollector.translateToLocal("NameLargeBoilerSteel")));
        AnimatedTooltipHandler.addItemTooltip(GTNCItemList.LargeBoilerSteel.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr NameLargeBoilerTitanium
        // # Large Titanium Boiler
        // # zh_CN 大型钛锅炉
        GTNCItemList.LargeBoilerTitanium.set(
            new LargeBoiler.LargeBoilerTitanium(
                GTNCMachineID.LARGE_BOILER_TITANIUM.ID,
                "LargeBoilerTitanium",
                StatCollector.translateToLocal("NameLargeBoilerTitanium")));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LargeBoilerTitanium.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr NameLargeBoilerTungstenSteel
        // # Large Tungstensteel Boiler
        // # zh_CN 大型钨钢锅炉
        GTNCItemList.LargeBoilerTungstenSteel.set(
            new LargeBoiler.LargeBoilerTungstenSteel(
                GTNCMachineID.LARGE_BOILER_TUNGSTEN_STEEL.ID,
                "LargeBoilerTungstenSteel",
                StatCollector.translateToLocal("NameLargeBoilerTungstenSteel")));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LargeBoilerTungstenSteel.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr NameLargeSteamTurbineBronze
        // # Large Bronze Steam Turbine
        // # zh_CN 大型青铜蒸汽涡轮
        GTNCItemList.LargeSteamTurbineBronze.set(
            new LargeSteamTurbine.LargeSteamTurbineBronze(
                GTNCMachineID.LARGE_STEAM_TURBINE_BRONZE.ID,
                "LargeSteamTurbineBronze",
                StatCollector.translateToLocal("NameLargeSteamTurbineBronze")));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LargeSteamTurbineBronze.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr NameLargeSteamTurbineSteel
        // # Large Steel Steam Turbine
        // # zh_CN 大型钢蒸汽涡轮
        GTNCItemList.LargeSteamTurbineSteel.set(
            new LargeSteamTurbine.LargeSteamTurbineSteel(
                GTNCMachineID.LARGE_STEAM_TURBINE_STEEL.ID,
                "LargeSteamTurbineSteel",
                StatCollector.translateToLocal("NameLargeSteamTurbineSteel")));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LargeSteamTurbineSteel.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr NameLargeSteamTurbineTitanium
        // # Large Titanium Steam Turbine
        // # zh_CN 大型钛蒸汽涡轮
        GTNCItemList.LargeSteamTurbineTitanium.set(
            new LargeSteamTurbine.LargeSteamTurbineTitanium(
                GTNCMachineID.LARGE_STEAM_TURBINE_TITANIUM.ID,
                "LargeSteamTurbineTitanium",
                StatCollector.translateToLocal("NameLargeSteamTurbineTitanium")));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LargeSteamTurbineTitanium.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr NameLargeSteamTurbineTungstenSteel
        // # Large Tungstensteel Steam Turbine
        // # zh_CN 大型钨钢蒸汽涡轮
        GTNCItemList.LargeSteamTurbineTungstenSteel.set(
            new LargeSteamTurbine.LargeSteamTurbineTungstenSteel(
                GTNCMachineID.LARGE_STEAM_TURBINE_TUNGSTEN_STEEL.ID,
                "LargeSteamTurbineTungstenSteel",
                StatCollector.translateToLocal("NameLargeSteamTurbineTungstenSteel")));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LargeSteamTurbineTungstenSteel.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr NameLargeSteamLaserEngraver
        // # Large Steam Laser Engraver
        // # zh_CN 大型蒸汽激光雕刻机
        GTNCItemList.LargeSteamLaserEngraver.set(
            new LargeSteamLaserEngraver(
                GTNCMachineID.LARGE_STEAM_LASER_ENGRAVER.ID,
                "LargeSteamLaserEngraver",
                StatCollector.translateToLocal("NameLargeSteamLaserEngraver")));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LargeSteamLaserEngraver.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr NameLargeSteamAssembler
        // # Large Steam Assembler
        // # zh_CN 大型蒸汽组装机
        GTNCItemList.LargeSteamAssembler.set(
            new LargeSteamAssembler(
                GTNCMachineID.LARGE_STEAM_ASSEMBLER.ID,
                "LargeSteamAssembler",
                StatCollector.translateToLocal("NameLargeSteamAssembler")));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LargeSteamAssembler.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr NameLargeSteamCentrifuge
        // # Large Steam Centrifuge
        // # zh_CN 大型蒸汽离心机
        GTNCItemList.LargeSteamCentrifuge.set(
            new LargeSteamCentrifuge(
                GTNCMachineID.LARGE_STEAM_CENTRIFUGE.ID,
                "LargeSteamCentrifuge",
                StatCollector.translateToLocal("NameLargeSteamCentrifuge")));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LargeSteamCentrifuge.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr NameLargeSteamElectrolyzer
        // # Large Steam Electrolyzer
        // # zh_CN 大型蒸汽电解机
        GTNCItemList.LargeSteamElectrolyzer.set(
            new LargeSteamElectrolyzer(
                GTNCMachineID.LARGE_STEAM_ELECTROLYZER.ID,
                "LargeSteamElectrolyzer",
                StatCollector.translateToLocal("NameLargeSteamElectrolyzer")));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LargeSteamElectrolyzer.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr NameLargeSteamBending
        // # Large Steam Bending Machine
        // # zh_CN 大型蒸汽卷板机
        GTNCItemList.LargeSteamBending.set(
            new LargeSteamBending(
                GTNCMachineID.LARGE_STEAM_BENDING.ID,
                "LargeSteamBending",
                StatCollector.translateToLocal("NameLargeSteamBending")));
        AnimatedTooltipHandler.addItemTooltip(GTNCItemList.LargeSteamBending.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr NameLargeSteamFluidExtractor
        // # Large Steam Fluid Extractor
        // # zh_CN 大型蒸汽流体提取机
        GTNCItemList.LargeSteamFluidExtractor.set(
            new LargeSteamFluidExtractor(
                GTNCMachineID.LARGE_STEAM_FLUID_EXTRACTOR.ID,
                "LargeSteamFluidExtractor",
                StatCollector.translateToLocal("NameLargeSteamFluidExtractor")));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LargeSteamFluidExtractor.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr NameLargeSteamFluidSolidifier
        // # Large Steam Fluid Solidifier
        // # zh_CN 大型蒸汽流体固化机
        GTNCItemList.LargeSteamFluidSolidifier.set(
            new LargeSteamFluidSolidifier(
                GTNCMachineID.LARGE_STEAM_FLUID_SOLIDIFIER.ID,
                "LargeSteamFluidSolidifier",
                StatCollector.translateToLocal("NameLargeSteamFluidSolidifier")));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LargeSteamFluidSolidifier.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr NameLargeSteamChemicalReactor
        // # Large Steam Chemical Reactor
        // # zh_CN 大型蒸汽化学反应釜
        GTNCItemList.LargeSteamChemicalReactor.set(
            new LargeSteamChemicalReactor(
                GTNCMachineID.LARGE_STEAM_CHEMICAL_REACTOR.ID,
                "LargeSteamChemicalReactor",
                StatCollector.translateToLocal("NameLargeSteamChemicalReactor")));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LargeSteamChemicalReactor.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr NameLargeSteamWireMill
        // # Large Steam Wire Mill
        // # zh_CN 大型蒸汽线材轧机
        GTNCItemList.LargeSteamWireMill.set(
            new LargeSteamWireMill(
                GTNCMachineID.LARGE_STEAM_WIRE_MILL.ID,
                "LargeSteamWireMill",
                StatCollector.translateToLocal("NameLargeSteamWireMill")));
        AnimatedTooltipHandler.addItemTooltip(GTNCItemList.LargeSteamWireMill.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr NameLargeSteamMixer
        // # Large Steam Mixer
        // # zh_CN 大型蒸汽搅拌机
        GTNCItemList.LargeSteamMixer.set(
            new LargeSteamMixer(
                GTNCMachineID.LARGE_STEAM_MIXER.ID,
                "LargeSteamMixer",
                StatCollector.translateToLocal("NameLargeSteamMixer")));
        AnimatedTooltipHandler.addItemTooltip(GTNCItemList.LargeSteamMixer.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr NameLargeSteamAlloySmelter
        // # Large Steam Alloy Smelter
        // # zh_CN 大型蒸汽合金炉
        GTNCItemList.LargeSteamAlloySmelter.set(
            new LargeSteamAlloySmelter(
                GTNCMachineID.LARGE_STEAM_ALLOY_SMELTER.ID,
                "LargeSteamAlloySmelter",
                StatCollector.translateToLocal("NameLargeSteamAlloySmelter")));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LargeSteamAlloySmelter.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr NameLargeSteamCircuitAssembler
        // # Large Steam Circuit Assembler
        // # zh_CN 大型蒸汽电路组装机
        GTNCItemList.LargeSteamCircuitAssembler.set(
            new LargeSteamCircuitAssembler(
                GTNCMachineID.LARGE_STEAM_CIRCUIT_ASSEMBLER.ID,
                "LargeSteamCircuitAssembler",
                StatCollector.translateToLocal("NameLargeSteamCircuitAssembler")));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LargeSteamCircuitAssembler.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr NameLargeSteamCompressor
        // # Large Steam Compressor
        // # zh_CN 大型蒸汽压缩机
        GTNCItemList.LargeSteamCompressor.set(
            new LargeSteamCompressor(
                GTNCMachineID.LARGE_STEAM_COMPRESSOR.ID,
                "LargeSteamCompressor",
                StatCollector.translateToLocal("NameLargeSteamCompressor")));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LargeSteamCompressor.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr NameLargeSteamCutting
        // # Large Steam Cutting Machine
        // # zh_CN 大型蒸汽切割机
        GTNCItemList.LargeSteamCutting.set(
            new LargeSteamCutting(
                GTNCMachineID.LARGE_STEAM_CUTTING.ID,
                "LargeSteamCutting",
                StatCollector.translateToLocal("NameLargeSteamCutting")));
        AnimatedTooltipHandler.addItemTooltip(GTNCItemList.LargeSteamCutting.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr NameLargeSteamFormingPress
        // # Large Steam Forming Press
        // # zh_CN 大型蒸汽冲压机床
        GTNCItemList.LargeSteamFormingPress.set(
            new LargeSteamFormingPress(
                GTNCMachineID.LARGE_STEAM_FORMING_PRESS.ID,
                "LargeSteamFormingPress",
                StatCollector.translateToLocal("NameLargeSteamFormingPress")));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LargeSteamFormingPress.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr NameLargeSteamHammer
        // # Large Steam Forge Hammer
        // # zh_CN 大型蒸汽锻造锤
        GTNCItemList.LargeSteamHammer.set(
            new LargeSteamHammer(
                GTNCMachineID.LARGE_STEAM_HAMMER.ID,
                "LargeSteamHammer",
                StatCollector.translateToLocal("NameLargeSteamHammer")));
        AnimatedTooltipHandler.addItemTooltip(GTNCItemList.LargeSteamHammer.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr NameLargeSteamExtruder
        // # Large Steam Extruder
        // # zh_CN 大型蒸汽压模机
        GTNCItemList.LargeSteamExtruder.set(
            new LargeSteamExtruder(
                GTNCMachineID.LARGE_STEAM_EXTRUDER.ID,
                "LargeSteamExtruder",
                StatCollector.translateToLocal("NameLargeSteamExtruder")));
        AnimatedTooltipHandler.addItemTooltip(GTNCItemList.LargeSteamExtruder.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr NameLargeSteamDistillationTower
        // # Large Steam Distillation Tower
        // # zh_CN 大型蒸汽蒸馏塔
        GTNCItemList.LargeSteamDistillationTower.set(
            new LargeSteamDistillationTower(
                GTNCMachineID.LARGE_STEAM_DISTILLATION_TOWER.ID,
                "LargeSteamDistillationTower",
                StatCollector.translateToLocal("NameLargeSteamDistillationTower")));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LargeSteamDistillationTower.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr NameLargeSteamBeeBreeder
        // # Large Steam Bee Breeder
        // # zh_CN 大型蒸汽养蜂机
        GTNCItemList.LargeSteamBeeBreeder.set(
            new LargeSteamBeeBreeder(
                GTNCMachineID.LARGE_STEAM_BEE_BREEDER.ID,
                "LargeSteamBeeBreeder",
                StatCollector.translateToLocal("NameLargeSteamBeeBreeder")));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LargeSteamBeeBreeder.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr NameLargeSteamCombProcessor
        // # Large Steam Comb Processor
        // # zh_CN 大型蒸汽蜂窝处理机
        GTNCItemList.LargeSteamCombProcessor.set(
            new LargeSteamCombProcessor(
                GTNCMachineID.LARGE_STEAM_COMB_PROCESSOR.ID,
                "LargeSteamCombProcessor",
                StatCollector.translateToLocal("NameLargeSteamCombProcessor")));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LargeSteamCombProcessor.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

    }

    public static void registerHatch() {
        // #tr NameSuperMTEHatchCraftingInputBusME
        // # Super Pattern Input Bus (ME)
        // # zh_CN 超级样板输入总线 (ME)
        GTNCItemList.SuperMTEHatchCraftingInputBusME.set(
            new SuperMTEHatchCraftingInputME(
                GTNCMachineID.SUPER_CRAFTING_INPUT_BUS_ME.ID,
                "SuperMTEHatchCraftingInputBusME",
                StatCollector.translateToLocal("NameSuperMTEHatchCraftingInputBusME"),
                false));
        addItemTooltip(GTNCItemList.SuperMTEHatchCraftingInputBusME.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr NameSuperMTEHatchCraftingInputME
        // # Super Pattern Input Hatch (ME)
        // # zh_CN 超级样板输入总成 (ME)
        GTNCItemList.SuperMTEHatchCraftingInputME.set(
            new SuperMTEHatchCraftingInputME(
                GTNCMachineID.SUPER_CRAFTING_INPUT_ME.ID,
                "SuperMTEHatchCraftingInputME",
                StatCollector.translateToLocal("NameSuperMTEHatchCraftingInputME"),
                true));
        addItemTooltip(GTNCItemList.SuperMTEHatchCraftingInputME.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr NameSuperMTEHatchCraftingInputSlave
        // # Super Pattern Input Mirror (ME)
        // # zh_CN 超级样板输入镜像 (ME)
        GTNCItemList.SuperMTEHatchCraftingInputSlave.set(
            new SuperMTEHatchCraftingInputSlave(
                GTNCMachineID.SUPERMTEHATCHCRAFTINGINPUTSLAVE.ID,
                "SuperCraftingInputProxy",
                StatCollector.translateToLocal("NameSuperMTEHatchCraftingInputSlave")).getStackForm(1L));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.SuperMTEHatchCraftingInputSlave.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNCItemList.WirelessEnergyHatchLV.set(ItemList.Wireless_Hatch_Energy_LV.get(1));

        // #tr WirelessEnergyHatchLV4A
        // # 4A LV Wireless Energy Hatch
        // # zh_CN 4安LV无线能源仓
        GTNCItemList.WirelessEnergyHatchLV4A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_LV_4A.ID,
                "WirelessEnergyHatchLV4A",
                StatCollector.translateToLocal("WirelessEnergyHatchLV4A"),
                1,
                4));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchLV4A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchLV16A
        // # 16A LV Wireless Energy Hatch
        // # zh_CN 16安LV无线能源仓
        GTNCItemList.WirelessEnergyHatchLV16A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_LV_16A.ID,
                "WirelessEnergyHatchLV16A",
                StatCollector.translateToLocal("WirelessEnergyHatchLV16A"),
                1,
                16));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchLV16A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchLV64A
        // # 64A LV Wireless Energy Hatch
        // # zh_CN 64安LV无线能源仓
        GTNCItemList.WirelessEnergyHatchLV64A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_LV_64A.ID,
                "WirelessEnergyHatchLV64A",
                StatCollector.translateToLocal("WirelessEnergyHatchLV64A"),
                1,
                64));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchLV64A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNCItemList.WirelessEnergyHatchMV.set(ItemList.Wireless_Hatch_Energy_MV.get(1));

        // #tr WirelessEnergyHatchMV4A
        // # 4A MV Wireless Energy Hatch
        // # zh_CN 4安MV无线能源仓
        GTNCItemList.WirelessEnergyHatchMV4A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_MV_4A.ID,
                "WirelessEnergyHatchMV4A",
                StatCollector.translateToLocal("WirelessEnergyHatchMV4A"),
                2,
                4));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchMV4A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchMV16A
        // # 16A MV Wireless Energy Hatch
        // # zh_CN 16安MV无线能源仓
        GTNCItemList.WirelessEnergyHatchMV16A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_MV_16A.ID,
                "WirelessEnergyHatchMV16A",
                StatCollector.translateToLocal("WirelessEnergyHatchMV16A"),
                2,
                16));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchMV16A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchMV64A
        // # 64A MV Wireless Energy Hatch
        // # zh_CN 64安MV无线能源仓
        GTNCItemList.WirelessEnergyHatchMV64A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_MV_64A.ID,
                "WirelessEnergyHatchMV64A",
                StatCollector.translateToLocal("WirelessEnergyHatchMV64A"),
                2,
                64));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchMV64A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNCItemList.WirelessEnergyHatchHV.set(ItemList.Wireless_Hatch_Energy_HV.get(1));

        // #tr WirelessEnergyHatchHV4A
        // # 4A HV Wireless Energy Hatch
        // # zh_CN 4安HV无线能源仓
        GTNCItemList.WirelessEnergyHatchHV4A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_HV_4A.ID,
                "WirelessEnergyHatchHV4A",
                StatCollector.translateToLocal("WirelessEnergyHatchHV4A"),
                3,
                4));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchHV4A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchHV16A
        // # 16A HV Wireless Energy Hatch
        // # zh_CN 16安HV无线能源仓
        GTNCItemList.WirelessEnergyHatchHV16A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_HV_16A.ID,
                "WirelessEnergyHatchHV16A",
                StatCollector.translateToLocal("WirelessEnergyHatchHV16A"),
                3,
                16));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchHV16A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchHV64A
        // # 64A HV Wireless Energy Hatch
        // # zh_CN 64安HV无线能源仓
        GTNCItemList.WirelessEnergyHatchHV64A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_HV_64A.ID,
                "WirelessEnergyHatchHV64A",
                StatCollector.translateToLocal("WirelessEnergyHatchHV64A"),
                3,
                64));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchHV64A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNCItemList.WirelessEnergyHatchEV.set(ItemList.Wireless_Hatch_Energy_EV.get(1));

        GTNCItemList.WirelessEnergyHatchEV4A.set(CustomItemList.eM_energyWirelessMulti4_EV.get(1));

        GTNCItemList.WirelessEnergyHatchEV16A.set(CustomItemList.eM_energyWirelessMulti16_EV.get(1));

        GTNCItemList.WirelessEnergyHatchEV64A.set(CustomItemList.eM_energyWirelessMulti64_EV.get(1));
        GTNCItemList.WirelessEnergyHatchIV.set(ItemList.Wireless_Hatch_Energy_IV.get(1));

        GTNCItemList.WirelessEnergyHatchIV4A.set(CustomItemList.eM_energyWirelessMulti4_IV.get(1));

        GTNCItemList.WirelessEnergyHatchIV16A.set(CustomItemList.eM_energyWirelessMulti16_IV.get(1));

        GTNCItemList.WirelessEnergyHatchIV64A.set(CustomItemList.eM_energyWirelessMulti64_IV.get(1));

        // #tr WirelessEnergyHatchIV256A
        // # 256A IV Wireless Energy Hatch
        // # zh_CN 256安IV无线能源仓
        GTNCItemList.WirelessEnergyHatchIV256A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_IV_256A.ID,
                "WirelessEnergyHatchIV256A",
                StatCollector.translateToLocal("WirelessEnergyHatchIV256A"),
                5,
                256));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchIV256A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchIV1024A
        // # 1024A IV Wireless Energy Hatch
        // # zh_CN 1024安IV无线能源仓
        GTNCItemList.WirelessEnergyHatchIV1024A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_IV_1024A.ID,
                "WirelessEnergyHatchIV1024A",
                StatCollector.translateToLocal("WirelessEnergyHatchIV1024A"),
                5,
                1024));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchIV1024A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchIV4096A
        // # 4096A IV Wireless Energy Hatch
        // # zh_CN 4096安IV无线能源仓
        GTNCItemList.WirelessEnergyHatchIV4096A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_IV_4096A.ID,
                "WirelessEnergyHatchIV4096A",
                StatCollector.translateToLocal("WirelessEnergyHatchIV4096A"),
                5,
                4096));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchIV4096A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchIV16384A
        // # 16384A IV Wireless Energy Hatch
        // # zh_CN 16384安IV无线能源仓
        GTNCItemList.WirelessEnergyHatchIV16384A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_IV_16384A.ID,
                "WirelessEnergyHatchIV16384A",
                StatCollector.translateToLocal("WirelessEnergyHatchIV16384A"),
                5,
                16384));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchIV16384A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchIV65536A
        // # 65536A IV Wireless Energy Hatch
        // # zh_CN 65536安IV无线能源仓
        GTNCItemList.WirelessEnergyHatchIV65536A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_IV_65536A.ID,
                "WirelessEnergyHatchIV65536A",
                StatCollector.translateToLocal("WirelessEnergyHatchIV65536A"),
                5,
                65536));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchIV65536A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchIV262144A
        // # 262144A IV Wireless Energy Hatch
        // # zh_CN 262144安IV无线能源仓
        GTNCItemList.WirelessEnergyHatchIV262144A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_IV_262144A.ID,
                "WirelessEnergyHatchIV262144A",
                StatCollector.translateToLocal("WirelessEnergyHatchIV262144A"),
                5,
                262144));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchIV262144A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchIV1048576A
        // # 1048576A IV Wireless Energy Hatch
        // # zh_CN 1048576安IV无线能源仓
        GTNCItemList.WirelessEnergyHatchIV1048576A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_IV_1048576A.ID,
                "WirelessEnergyHatchIV1048576A",
                StatCollector.translateToLocal("WirelessEnergyHatchIV1048576A"),
                5,
                1048576));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchIV1048576A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchIV4194304A
        // # 4194304A IV Wireless Energy Hatch
        // # zh_CN 4194304安IV无线能源仓
        GTNCItemList.WirelessEnergyHatchIV4194304A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_IV_4194304A.ID,
                "WirelessEnergyHatchIV4194304A",
                StatCollector.translateToLocal("WirelessEnergyHatchIV4194304A"),
                5,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchIV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchIV16777216A
        // # 16777216A IV Wireless Energy Hatch
        // # zh_CN 16777216安IV无线能源仓
        GTNCItemList.WirelessEnergyHatchIV16777216A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_IV_16777216A.ID,
                "WirelessEnergyHatchIV16777216A",
                StatCollector.translateToLocal("WirelessEnergyHatchIV16777216A"),
                5,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchIV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNCItemList.WirelessEnergyHatchLuV.set(ItemList.Wireless_Hatch_Energy_LuV.get(1));

        GTNCItemList.WirelessEnergyHatchLuV4A.set(CustomItemList.eM_energyWirelessMulti4_LuV.get(1));

        GTNCItemList.WirelessEnergyHatchLuV16A.set(CustomItemList.eM_energyWirelessMulti16_LuV.get(1));

        GTNCItemList.WirelessEnergyHatchLuV64A.set(CustomItemList.eM_energyWirelessMulti64_LuV.get(1));

        // #tr WirelessEnergyHatchLuV256A
        // # 256A LuV Wireless Energy Hatch
        // # zh_CN 256安LuV无线能源仓
        GTNCItemList.WirelessEnergyHatchLuV256A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_LUV_256A.ID,
                "WirelessEnergyHatchLuV256A",
                StatCollector.translateToLocal("WirelessEnergyHatchLuV256A"),
                6,
                256));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchLuV256A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchLuV1024A
        // # 1024A LuV Wireless Energy Hatch
        // # zh_CN 1024安LuV无线能源仓
        GTNCItemList.WirelessEnergyHatchLuV1024A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_LUV_1024A.ID,
                "WirelessEnergyHatchLuV1024A",
                StatCollector.translateToLocal("WirelessEnergyHatchLuV1024A"),
                6,
                1024));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchLuV1024A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchLuV4096A
        // # 4096A LuV Wireless Energy Hatch
        // # zh_CN 4096安LuV无线能源仓
        GTNCItemList.WirelessEnergyHatchLuV4096A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_LUV_4096A.ID,
                "WirelessEnergyHatchLuV4096A",
                StatCollector.translateToLocal("WirelessEnergyHatchLuV4096A"),
                6,
                4096));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchLuV4096A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchLuV16384A
        // # 16384A LuV Wireless Energy Hatch
        // # zh_CN 16384安LuV无线能源仓
        GTNCItemList.WirelessEnergyHatchLuV16384A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_LUV_16384A.ID,
                "WirelessEnergyHatchLuV16384A",
                StatCollector.translateToLocal("WirelessEnergyHatchLuV16384A"),
                6,
                16384));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchLuV16384A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchLuV65536A
        // # 65536A LuV Wireless Energy Hatch
        // # zh_CN 65536安LuV无线能源仓
        GTNCItemList.WirelessEnergyHatchLuV65536A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_LUV_65536A.ID,
                "WirelessEnergyHatchLuV65536A",
                StatCollector.translateToLocal("WirelessEnergyHatchLuV65536A"),
                6,
                65536));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchLuV65536A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchLuV262144A
        // # 262144A LuV Wireless Energy Hatch
        // # zh_CN 262144安LuV无线能源仓
        GTNCItemList.WirelessEnergyHatchLuV262144A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_LUV_262144A.ID,
                "WirelessEnergyHatchLuV262144A",
                StatCollector.translateToLocal("WirelessEnergyHatchLuV262144A"),
                6,
                262144));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchLuV262144A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchLuV1048576A
        // # 1048576A LuV Wireless Energy Hatch
        // # zh_CN 1048576安LuV无线能源仓
        GTNCItemList.WirelessEnergyHatchLuV1048576A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_LUV_1048576A.ID,
                "WirelessEnergyHatchLuV1048576A",
                StatCollector.translateToLocal("WirelessEnergyHatchLuV1048576A"),
                6,
                1048576));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchLuV1048576A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchLuV4194304A
        // # 4194304A LuV Wireless Energy Hatch
        // # zh_CN 4194304安LuV无线能源仓
        GTNCItemList.WirelessEnergyHatchLuV4194304A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_LUV_4194304A.ID,
                "WirelessEnergyHatchLuV4194304A",
                StatCollector.translateToLocal("WirelessEnergyHatchLuV4194304A"),
                6,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchLuV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchLuV16777216A
        // # 16777216A LuV Wireless Energy Hatch
        // # zh_CN 16777216安LuV无线能源仓
        GTNCItemList.WirelessEnergyHatchLuV16777216A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_LUV_16777216A.ID,
                "WirelessEnergyHatchLuV16777216A",
                StatCollector.translateToLocal("WirelessEnergyHatchLuV16777216A"),
                6,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchLuV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNCItemList.WirelessEnergyHatchZPM.set(ItemList.Wireless_Hatch_Energy_ZPM.get(1));

        GTNCItemList.WirelessEnergyHatchZPM4A.set(CustomItemList.eM_energyWirelessMulti4_ZPM.get(1));

        GTNCItemList.WirelessEnergyHatchZPM16A.set(CustomItemList.eM_energyWirelessMulti16_ZPM.get(1));

        GTNCItemList.WirelessEnergyHatchZPM64A.set(CustomItemList.eM_energyWirelessMulti64_ZPM.get(1));

        // #tr WirelessEnergyHatchZPM256A
        // # 256A ZPM Wireless Energy Hatch
        // # zh_CN 256安ZPM无线能源仓
        GTNCItemList.WirelessEnergyHatchZPM256A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_ZPM_256A.ID,
                "WirelessEnergyHatchZPM256A",
                StatCollector.translateToLocal("WirelessEnergyHatchZPM256A"),
                7,
                256));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchZPM256A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchZPM1024A
        // # 1024A ZPM Wireless Energy Hatch
        // # zh_CN 1024安ZPM无线能源仓
        GTNCItemList.WirelessEnergyHatchZPM1024A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_ZPM_1024A.ID,
                "WirelessEnergyHatchZPM1024A",
                StatCollector.translateToLocal("WirelessEnergyHatchZPM1024A"),
                7,
                1024));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchZPM1024A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchZPM4096A
        // # 4096A ZPM Wireless Energy Hatch
        // # zh_CN 4096安ZPM无线能源仓
        GTNCItemList.WirelessEnergyHatchZPM4096A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_ZPM_4096A.ID,
                "WirelessEnergyHatchZPM4096A",
                StatCollector.translateToLocal("WirelessEnergyHatchZPM4096A"),
                7,
                4096));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchZPM4096A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchZPM16384A
        // # 16384A ZPM Wireless Energy Hatch
        // # zh_CN 16384安ZPM无线能源仓
        GTNCItemList.WirelessEnergyHatchZPM16384A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_ZPM_16384A.ID,
                "WirelessEnergyHatchZPM16384A",
                StatCollector.translateToLocal("WirelessEnergyHatchZPM16384A"),
                7,
                16384));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchZPM16384A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchZPM65536A
        // # 65536A ZPM Wireless Energy Hatch
        // # zh_CN 65536安ZPM无线能源仓
        GTNCItemList.WirelessEnergyHatchZPM65536A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_ZPM_65536A.ID,
                "WirelessEnergyHatchZPM65536A",
                StatCollector.translateToLocal("WirelessEnergyHatchZPM65536A"),
                7,
                65536));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchZPM65536A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchZPM262144A
        // # 262144A ZPM Wireless Energy Hatch
        // # zh_CN 262144安ZPM无线能源仓
        GTNCItemList.WirelessEnergyHatchZPM262144A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_ZPM_262144A.ID,
                "WirelessEnergyHatchZPM262144A",
                StatCollector.translateToLocal("WirelessEnergyHatchZPM262144A"),
                7,
                262144));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchZPM262144A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchZPM1048576A
        // # 1048576A ZPM Wireless Energy Hatch
        // # zh_CN 1048576安ZPM无线能源仓
        GTNCItemList.WirelessEnergyHatchZPM1048576A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_ZPM_1048576A.ID,
                "WirelessEnergyHatchZPM1048576A",
                StatCollector.translateToLocal("WirelessEnergyHatchZPM1048576A"),
                7,
                1048576));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchZPM1048576A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchZPM4194304A
        // # 4194304A ZPM Wireless Energy Hatch
        // # zh_CN 4194304安ZPM无线能源仓
        GTNCItemList.WirelessEnergyHatchZPM4194304A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_ZPM_4194304A.ID,
                "WirelessEnergyHatchZPM4194304A",
                StatCollector.translateToLocal("WirelessEnergyHatchZPM4194304A"),
                7,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchZPM4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchZPM16777216A
        // # 16777216A ZPM Wireless Energy Hatch
        // # zh_CN 16777216安ZPM无线能源仓
        GTNCItemList.WirelessEnergyHatchZPM16777216A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_ZPM_16777216A.ID,
                "WirelessEnergyHatchZPM16777216A",
                StatCollector.translateToLocal("WirelessEnergyHatchZPM16777216A"),
                7,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchZPM16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNCItemList.WirelessEnergyHatchUV.set(ItemList.Wireless_Hatch_Energy_UV.get(1));

        GTNCItemList.WirelessEnergyHatchUV4A.set(CustomItemList.eM_energyWirelessMulti4_UV.get(1));

        GTNCItemList.WirelessEnergyHatchUV16A.set(CustomItemList.eM_energyWirelessMulti16_UV.get(1));

        GTNCItemList.WirelessEnergyHatchUV64A.set(CustomItemList.eM_energyWirelessMulti64_UV.get(1));

        // #tr WirelessEnergyHatchUV256A
        // # 256A UV Wireless Energy Hatch
        // # zh_CN 256安UV无线能源仓
        GTNCItemList.WirelessEnergyHatchUV256A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UV_256A.ID,
                "WirelessEnergyHatchUV256A",
                StatCollector.translateToLocal("WirelessEnergyHatchUV256A"),
                8,
                256));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUV256A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUV1024A
        // # 1024A UV Wireless Energy Hatch
        // # zh_CN 1024安UV无线能源仓
        GTNCItemList.WirelessEnergyHatchUV1024A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UV_1024A.ID,
                "WirelessEnergyHatchUV1024A",
                StatCollector.translateToLocal("WirelessEnergyHatchUV1024A"),
                8,
                1024));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUV1024A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUV4096A
        // # 4096A UV Wireless Energy Hatch
        // # zh_CN 4096安UV无线能源仓
        GTNCItemList.WirelessEnergyHatchUV4096A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UV_4096A.ID,
                "WirelessEnergyHatchUV4096A",
                StatCollector.translateToLocal("WirelessEnergyHatchUV4096A"),
                8,
                4096));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUV4096A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUV16384A
        // # 16384A UV Wireless Energy Hatch
        // # zh_CN 16384安UV无线能源仓
        GTNCItemList.WirelessEnergyHatchUV16384A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UV_16384A.ID,
                "WirelessEnergyHatchUV16384A",
                StatCollector.translateToLocal("WirelessEnergyHatchUV16384A"),
                8,
                16384));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUV16384A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUV65536A
        // # 65536A UV Wireless Energy Hatch
        // # zh_CN 65536安UV无线能源仓
        GTNCItemList.WirelessEnergyHatchUV65536A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UV_65536A.ID,
                "WirelessEnergyHatchUV65536A",
                StatCollector.translateToLocal("WirelessEnergyHatchUV65536A"),
                8,
                65536));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUV65536A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUV262144A
        // # 262144A UV Wireless Energy Hatch
        // # zh_CN 262144安UV无线能源仓
        GTNCItemList.WirelessEnergyHatchUV262144A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UV_262144A.ID,
                "WirelessEnergyHatchUV262144A",
                StatCollector.translateToLocal("WirelessEnergyHatchUV262144A"),
                8,
                262144));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUV262144A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUV1048576A
        // # 1048576A UV Wireless Energy Hatch
        // # zh_CN 1048576安UV无线能源仓
        GTNCItemList.WirelessEnergyHatchUV1048576A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UV_1048576A.ID,
                "WirelessEnergyHatchUV1048576A",
                StatCollector.translateToLocal("WirelessEnergyHatchUV1048576A"),
                8,
                1048576));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUV1048576A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUV4194304A
        // # 4194304A UV Wireless Energy Hatch
        // # zh_CN 4194304安UV无线能源仓
        GTNCItemList.WirelessEnergyHatchUV4194304A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UV_4194304A.ID,
                "WirelessEnergyHatchUV4194304A",
                StatCollector.translateToLocal("WirelessEnergyHatchUV4194304A"),
                8,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUV16777216A
        // # 16777216A UV Wireless Energy Hatch
        // # zh_CN 16777216安UV无线能源仓
        GTNCItemList.WirelessEnergyHatchUV16777216A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UV_16777216A.ID,
                "WirelessEnergyHatchUV16777216A",
                StatCollector.translateToLocal("WirelessEnergyHatchUV16777216A"),
                8,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNCItemList.WirelessEnergyHatchUHV.set(ItemList.Wireless_Hatch_Energy_UHV.get(1));

        GTNCItemList.WirelessEnergyHatchUHV4A.set(CustomItemList.eM_energyWirelessMulti4_UHV.get(1));

        GTNCItemList.WirelessEnergyHatchUHV16A.set(CustomItemList.eM_energyWirelessMulti16_UHV.get(1));

        GTNCItemList.WirelessEnergyHatchUHV64A.set(CustomItemList.eM_energyWirelessMulti64_UHV.get(1));

        // #tr WirelessEnergyHatchUHV256A
        // # 256A UHV Wireless Energy Hatch
        // # zh_CN 256安UHV无线能源仓
        GTNCItemList.WirelessEnergyHatchUHV256A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UHV_256A.ID,
                "WirelessEnergyHatchUHV256A",
                StatCollector.translateToLocal("WirelessEnergyHatchUHV256A"),
                9,
                256));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUHV256A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUHV1024A
        // # 1024A UHV Wireless Energy Hatch
        // # zh_CN 1024安UHV无线能源仓
        GTNCItemList.WirelessEnergyHatchUHV1024A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UHV_1024A.ID,
                "WirelessEnergyHatchUHV1024A",
                StatCollector.translateToLocal("WirelessEnergyHatchUHV1024A"),
                9,
                1024));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUHV1024A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUHV4096A
        // # 4096A UHV Wireless Energy Hatch
        // # zh_CN 4096安UHV无线能源仓
        GTNCItemList.WirelessEnergyHatchUHV4096A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UHV_4096A.ID,
                "WirelessEnergyHatchUHV4096A",
                StatCollector.translateToLocal("WirelessEnergyHatchUHV4096A"),
                9,
                4096));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUHV4096A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUHV16384A
        // # 16384A UHV Wireless Energy Hatch
        // # zh_CN 16384安UHV无线能源仓
        GTNCItemList.WirelessEnergyHatchUHV16384A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UHV_16384A.ID,
                "WirelessEnergyHatchUHV16384A",
                StatCollector.translateToLocal("WirelessEnergyHatchUHV16384A"),
                9,
                16384));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUHV16384A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUHV65536A
        // # 65536A UHV Wireless Energy Hatch
        // # zh_CN 65536安UHV无线能源仓
        GTNCItemList.WirelessEnergyHatchUHV65536A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UHV_65536A.ID,
                "WirelessEnergyHatchUHV65536A",
                StatCollector.translateToLocal("WirelessEnergyHatchUHV65536A"),
                9,
                65536));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUHV65536A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUHV262144A
        // # 262144A UHV Wireless Energy Hatch
        // # zh_CN 262144安UHV无线能源仓
        GTNCItemList.WirelessEnergyHatchUHV262144A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UHV_262144A.ID,
                "WirelessEnergyHatchUHV262144A",
                StatCollector.translateToLocal("WirelessEnergyHatchUHV262144A"),
                9,
                262144));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUHV262144A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUHV1048576A
        // # 1048576A UHV Wireless Energy Hatch
        // # zh_CN 1048576安UHV无线能源仓
        GTNCItemList.WirelessEnergyHatchUHV1048576A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UHV_1048576A.ID,
                "WirelessEnergyHatchUHV1048576A",
                StatCollector.translateToLocal("WirelessEnergyHatchUHV1048576A"),
                9,
                1048576));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUHV1048576A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUHV4194304A
        // # 4194304A UHV Wireless Energy Hatch
        // # zh_CN 4194304安UHV无线能源仓
        GTNCItemList.WirelessEnergyHatchUHV4194304A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UHV_4194304A.ID,
                "WirelessEnergyHatchUHV4194304A",
                StatCollector.translateToLocal("WirelessEnergyHatchUHV4194304A"),
                9,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUHV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUHV16777216A
        // # 16777216A UHV Wireless Energy Hatch
        // # zh_CN 16777216安UHV无线能源仓
        GTNCItemList.WirelessEnergyHatchUHV16777216A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UHV_16777216A.ID,
                "WirelessEnergyHatchUHV16777216A",
                StatCollector.translateToLocal("WirelessEnergyHatchUHV16777216A"),
                9,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUHV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNCItemList.WirelessEnergyHatchUEV.set(ItemList.Wireless_Hatch_Energy_UEV.get(1));

        GTNCItemList.WirelessEnergyHatchUEV4A.set(CustomItemList.eM_energyWirelessMulti4_UEV.get(1));

        GTNCItemList.WirelessEnergyHatchUEV16A.set(CustomItemList.eM_energyWirelessMulti16_UEV.get(1));

        GTNCItemList.WirelessEnergyHatchUEV64A.set(CustomItemList.eM_energyWirelessMulti64_UEV.get(1));

        // #tr WirelessEnergyHatchUEV256A
        // # 256A UEV Wireless Energy Hatch
        // # zh_CN 256安UEV无线能源仓
        GTNCItemList.WirelessEnergyHatchUEV256A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UEV_256A.ID,
                "WirelessEnergyHatchUEV256A",
                StatCollector.translateToLocal("WirelessEnergyHatchUEV256A"),
                10,
                256));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUEV256A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUEV1024A
        // # 1024A UEV Wireless Energy Hatch
        // # zh_CN 1024安UEV无线能源仓
        GTNCItemList.WirelessEnergyHatchUEV1024A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UEV_1024A.ID,
                "WirelessEnergyHatchUEV1024A",
                StatCollector.translateToLocal("WirelessEnergyHatchUEV1024A"),
                10,
                1024));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUEV1024A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUEV4096A
        // # 4096A UEV Wireless Energy Hatch
        // # zh_CN 4096安UEV无线能源仓
        GTNCItemList.WirelessEnergyHatchUEV4096A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UEV_4096A.ID,
                "WirelessEnergyHatchUEV4096A",
                StatCollector.translateToLocal("WirelessEnergyHatchUEV4096A"),
                10,
                4096));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUEV4096A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUEV16384A
        // # 16384A UEV Wireless Energy Hatch
        // # zh_CN 16384安UEV无线能源仓
        GTNCItemList.WirelessEnergyHatchUEV16384A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UEV_16384A.ID,
                "WirelessEnergyHatchUEV16384A",
                StatCollector.translateToLocal("WirelessEnergyHatchUEV16384A"),
                10,
                16384));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUEV16384A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUEV65536A
        // # 65536A UEV Wireless Energy Hatch
        // # zh_CN 65536安UEV无线能源仓
        GTNCItemList.WirelessEnergyHatchUEV65536A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UEV_65536A.ID,
                "WirelessEnergyHatchUEV65536A",
                StatCollector.translateToLocal("WirelessEnergyHatchUEV65536A"),
                10,
                65536));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUEV65536A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUEV262144A
        // # 262144A UEV Wireless Energy Hatch
        // # zh_CN 262144安UEV无线能源仓
        GTNCItemList.WirelessEnergyHatchUEV262144A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UEV_262144A.ID,
                "WirelessEnergyHatchUEV262144A",
                StatCollector.translateToLocal("WirelessEnergyHatchUEV262144A"),
                10,
                262144));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUEV262144A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUEV1048576A
        // # 1048576A UEV Wireless Energy Hatch
        // # zh_CN 1048576安UEV无线能源仓
        GTNCItemList.WirelessEnergyHatchUEV1048576A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UEV_1048576A.ID,
                "WirelessEnergyHatchUEV1048576A",
                StatCollector.translateToLocal("WirelessEnergyHatchUEV1048576A"),
                10,
                1048576));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUEV1048576A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUEV4194304A
        // # 4194304A UEV Wireless Energy Hatch
        // # zh_CN 4194304安UEV无线能源仓
        GTNCItemList.WirelessEnergyHatchUEV4194304A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UEV_4194304A.ID,
                "WirelessEnergyHatchUEV4194304A",
                StatCollector.translateToLocal("WirelessEnergyHatchUEV4194304A"),
                10,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUEV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUEV16777216A
        // # 16777216A UEV Wireless Energy Hatch
        // # zh_CN 16777216安UEV无线能源仓
        GTNCItemList.WirelessEnergyHatchUEV16777216A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UEV_16777216A.ID,
                "WirelessEnergyHatchUEV16777216A",
                StatCollector.translateToLocal("WirelessEnergyHatchUEV16777216A"),
                10,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUEV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNCItemList.WirelessEnergyHatchUIV.set(ItemList.Wireless_Hatch_Energy_UIV.get(1));

        GTNCItemList.WirelessEnergyHatchUIV4A.set(CustomItemList.eM_energyWirelessMulti4_UIV.get(1));

        GTNCItemList.WirelessEnergyHatchUIV16A.set(CustomItemList.eM_energyWirelessMulti16_UIV.get(1));

        GTNCItemList.WirelessEnergyHatchUIV64A.set(CustomItemList.eM_energyWirelessMulti64_UIV.get(1));

        // #tr WirelessEnergyHatchUIV256A
        // # 256A UIV Wireless Energy Hatch
        // # zh_CN 256安UIV无线能源仓
        GTNCItemList.WirelessEnergyHatchUIV256A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UIV_256A.ID,
                "WirelessEnergyHatchUIV256A",
                StatCollector.translateToLocal("WirelessEnergyHatchUIV256A"),
                11,
                256));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUIV256A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUIV1024A
        // # 1024A UIV Wireless Energy Hatch
        // # zh_CN 1024安UIV无线能源仓
        GTNCItemList.WirelessEnergyHatchUIV1024A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UIV_1024A.ID,
                "WirelessEnergyHatchUIV1024A",
                StatCollector.translateToLocal("WirelessEnergyHatchUIV1024A"),
                11,
                1024));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUIV1024A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUIV4096A
        // # 4096A UIV Wireless Energy Hatch
        // # zh_CN 4096安UIV无线能源仓
        GTNCItemList.WirelessEnergyHatchUIV4096A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UIV_4096A.ID,
                "WirelessEnergyHatchUIV4096A",
                StatCollector.translateToLocal("WirelessEnergyHatchUIV4096A"),
                11,
                4096));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUIV4096A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUIV16384A
        // # 16384A UIV Wireless Energy Hatch
        // # zh_CN 16384安UIV无线能源仓
        GTNCItemList.WirelessEnergyHatchUIV16384A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UIV_16384A.ID,
                "WirelessEnergyHatchUIV16384A",
                StatCollector.translateToLocal("WirelessEnergyHatchUIV16384A"),
                11,
                16384));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUIV16384A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUIV65536A
        // # 65536A UIV Wireless Energy Hatch
        // # zh_CN 65536安UIV无线能源仓
        GTNCItemList.WirelessEnergyHatchUIV65536A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UIV_65536A.ID,
                "WirelessEnergyHatchUIV65536A",
                StatCollector.translateToLocal("WirelessEnergyHatchUIV65536A"),
                11,
                65536));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUIV65536A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUIV262144A
        // # 262144A UIV Wireless Energy Hatch
        // # zh_CN 262144安UIV无线能源仓
        GTNCItemList.WirelessEnergyHatchUIV262144A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UIV_262144A.ID,
                "WirelessEnergyHatchUIV262144A",
                StatCollector.translateToLocal("WirelessEnergyHatchUIV262144A"),
                11,
                262144));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUIV262144A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUIV1048576A
        // # 1048576A UIV Wireless Energy Hatch
        // # zh_CN 1048576安UIV无线能源仓
        GTNCItemList.WirelessEnergyHatchUIV1048576A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UIV_1048576A.ID,
                "WirelessEnergyHatchUIV1048576A",
                StatCollector.translateToLocal("WirelessEnergyHatchUIV1048576A"),
                11,
                1048576));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUIV1048576A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUIV4194304A
        // # 4194304A UIV Wireless Energy Hatch
        // # zh_CN 4194304安UIV无线能源仓
        GTNCItemList.WirelessEnergyHatchUIV4194304A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UIV_4194304A.ID,
                "WirelessEnergyHatchUIV4194304A",
                StatCollector.translateToLocal("WirelessEnergyHatchUIV4194304A"),
                11,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUIV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUIV16777216A
        // # 16777216A UIV Wireless Energy Hatch
        // # zh_CN 16777216安UIV无线能源仓
        GTNCItemList.WirelessEnergyHatchUIV16777216A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UIV_16777216A.ID,
                "WirelessEnergyHatchUIV16777216A",
                StatCollector.translateToLocal("WirelessEnergyHatchUIV16777216A"),
                11,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUIV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNCItemList.WirelessEnergyHatchUMV.set(ItemList.Wireless_Hatch_Energy_UMV.get(1));

        GTNCItemList.WirelessEnergyHatchUMV4A.set(CustomItemList.eM_energyWirelessMulti4_UMV.get(1));

        GTNCItemList.WirelessEnergyHatchUMV16A.set(CustomItemList.eM_energyWirelessMulti16_UMV.get(1));

        GTNCItemList.WirelessEnergyHatchUMV64A.set(CustomItemList.eM_energyWirelessMulti64_UMV.get(1));

        // #tr WirelessEnergyHatchUMV256A
        // # 256A UMV Wireless Energy Hatch
        // # zh_CN 256安UMV无线能源仓
        GTNCItemList.WirelessEnergyHatchUMV256A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UMV_256A.ID,
                "WirelessEnergyHatchUMV256A",
                StatCollector.translateToLocal("WirelessEnergyHatchUMV256A"),
                12,
                256));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUMV256A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUMV1024A
        // # 1024A UMV Wireless Energy Hatch
        // # zh_CN 1024安UMV无线能源仓
        GTNCItemList.WirelessEnergyHatchUMV1024A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UMV_1024A.ID,
                "WirelessEnergyHatchUMV1024A",
                StatCollector.translateToLocal("WirelessEnergyHatchUMV1024A"),
                12,
                1024));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUMV1024A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUMV4096A
        // # 4096A UMV Wireless Energy Hatch
        // # zh_CN 4096安UMV无线能源仓
        GTNCItemList.WirelessEnergyHatchUMV4096A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UMV_4096A.ID,
                "WirelessEnergyHatchUMV4096A",
                StatCollector.translateToLocal("WirelessEnergyHatchUMV4096A"),
                12,
                4096));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUMV4096A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUMV16384A
        // # 16384A UMV Wireless Energy Hatch
        // # zh_CN 16384安UMV无线能源仓
        GTNCItemList.WirelessEnergyHatchUMV16384A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UMV_16384A.ID,
                "WirelessEnergyHatchUMV16384A",
                StatCollector.translateToLocal("WirelessEnergyHatchUMV16384A"),
                12,
                16384));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUMV16384A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUMV65536A
        // # 65536A UMV Wireless Energy Hatch
        // # zh_CN 65536安UMV无线能源仓
        GTNCItemList.WirelessEnergyHatchUMV65536A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UMV_65536A.ID,
                "WirelessEnergyHatchUMV65536A",
                StatCollector.translateToLocal("WirelessEnergyHatchUMV65536A"),
                12,
                65536));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUMV65536A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUMV262144A
        // # 262144A UMV Wireless Energy Hatch
        // # zh_CN 262144安UMV无线能源仓
        GTNCItemList.WirelessEnergyHatchUMV262144A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UMV_262144A.ID,
                "WirelessEnergyHatchUMV262144A",
                StatCollector.translateToLocal("WirelessEnergyHatchUMV262144A"),
                12,
                262144));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUMV262144A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUMV1048576A
        // # 1048576A UMV Wireless Energy Hatch
        // # zh_CN 1048576安UMV无线能源仓
        GTNCItemList.WirelessEnergyHatchUMV1048576A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UMV_1048576A.ID,
                "WirelessEnergyHatchUMV1048576A",
                StatCollector.translateToLocal("WirelessEnergyHatchUMV1048576A"),
                12,
                1048576));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUMV1048576A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUMV4194304A
        // # 4194304A UMV Wireless Energy Hatch
        // # zh_CN 4194304安UMV无线能源仓
        GTNCItemList.WirelessEnergyHatchUMV4194304A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UMV_4194304A.ID,
                "WirelessEnergyHatchUMV4194304A",
                StatCollector.translateToLocal("WirelessEnergyHatchUMV4194304A"),
                12,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUMV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchUMV16777216A
        // # 16777216A UMV Wireless Energy Hatch
        // # zh_CN 16777216安UMV无线能源仓
        GTNCItemList.WirelessEnergyHatchUMV16777216A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_UMV_16777216A.ID,
                "WirelessEnergyHatchUMV16777216A",
                StatCollector.translateToLocal("WirelessEnergyHatchUMV16777216A"),
                12,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchUMV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNCItemList.WirelessEnergyHatchUXV.set(ItemList.Wireless_Hatch_Energy_UXV.get(1));

        GTNCItemList.WirelessEnergyHatchUXV4A.set(CustomItemList.eM_energyWirelessMulti4_UXV.get(1));

        GTNCItemList.WirelessEnergyHatchUXV16A.set(CustomItemList.eM_energyWirelessMulti16_UXV.get(1));

        GTNCItemList.WirelessEnergyHatchUXV64A.set(CustomItemList.eM_energyWirelessMulti64_UXV.get(1));

        GTNCItemList.WirelessEnergyHatchUXV256A.set(CustomItemList.eM_energyWirelessTunnel1_UXV.get(1));

        GTNCItemList.WirelessEnergyHatchUXV1024A.set(CustomItemList.eM_energyWirelessTunnel2_UXV.get(1));

        GTNCItemList.WirelessEnergyHatchUXV4096A.set(CustomItemList.eM_energyWirelessTunnel3_UXV.get(1));

        GTNCItemList.WirelessEnergyHatchUXV16384A.set(CustomItemList.eM_energyWirelessTunnel4_UXV.get(1));

        GTNCItemList.WirelessEnergyHatchUXV65536A.set(CustomItemList.eM_energyWirelessTunnel5_UXV.get(1));

        GTNCItemList.WirelessEnergyHatchUXV262144A.set(CustomItemList.eM_energyWirelessTunnel6_UXV.get(1));

        GTNCItemList.WirelessEnergyHatchUXV1048576A.set(CustomItemList.eM_energyWirelessTunnel7_UXV.get(1));

        GTNCItemList.WirelessEnergyHatchUXV4194304A.set(CustomItemList.eM_energyWirelessTunnel8_UXV.get(1));

        GTNCItemList.WirelessEnergyHatchUXV16777216A.set(CustomItemList.eM_energyWirelessTunnel9_UXV.get(1));

        GTNCItemList.WirelessEnergyHatchMAX.set(ItemList.Wireless_Hatch_Energy_MAX.get(1));

        GTNCItemList.WirelessEnergyHatchMAX4A.set(CustomItemList.eM_energyWirelessMulti4_MAX.get(1));

        GTNCItemList.WirelessEnergyHatchMAX16A.set(CustomItemList.eM_energyWirelessMulti16_MAX.get(1));

        GTNCItemList.WirelessEnergyHatchMAX64A.set(CustomItemList.eM_energyWirelessMulti64_MAX.get(1));

        // #tr WirelessEnergyHatchMAX256A
        // # 256A MAX Wireless Energy Hatch
        // # zh_CN 256安MAX无线能源仓
        GTNCItemList.WirelessEnergyHatchMAX256A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_MAX_256A.ID,
                "WirelessEnergyHatchMAX256A",
                StatCollector.translateToLocal("WirelessEnergyHatchMAX256A"),
                14,
                256));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchMAX256A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchMAX1024A
        // # 1024A MAX Wireless Energy Hatch
        // # zh_CN 1024安MAX无线能源仓
        GTNCItemList.WirelessEnergyHatchMAX1024A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_MAX_1024A.ID,
                "WirelessEnergyHatchMAX1024A",
                StatCollector.translateToLocal("WirelessEnergyHatchMAX1024A"),
                14,
                1024));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchMAX1024A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchMAX4096A
        // # 4096A MAX Wireless Energy Hatch
        // # zh_CN 4096安MAX无线能源仓
        GTNCItemList.WirelessEnergyHatchMAX4096A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_MAX_4096A.ID,
                "WirelessEnergyHatchMAX4096A",
                StatCollector.translateToLocal("WirelessEnergyHatchMAX4096A"),
                14,
                4096));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchMAX4096A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchMAX16384A
        // # 16384A MAX Wireless Energy Hatch
        // # zh_CN 16384安MAX无线能源仓
        GTNCItemList.WirelessEnergyHatchMAX16384A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_MAX_16384A.ID,
                "WirelessEnergyHatchMAX16384A",
                StatCollector.translateToLocal("WirelessEnergyHatchMAX16384A"),
                14,
                16384));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchMAX16384A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchMAX65536A
        // # 65536A MAX Wireless Energy Hatch
        // # zh_CN 65536安MAX无线能源仓
        GTNCItemList.WirelessEnergyHatchMAX65536A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_MAX_65536A.ID,
                "WirelessEnergyHatchMAX65536A",
                StatCollector.translateToLocal("WirelessEnergyHatchMAX65536A"),
                14,
                65536));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchMAX65536A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchMAX262144A
        // # 262144A MAX Wireless Energy Hatch
        // # zh_CN 262144安MAX无线能源仓
        GTNCItemList.WirelessEnergyHatchMAX262144A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_MAX_262144A.ID,
                "WirelessEnergyHatchMAX262144A",
                StatCollector.translateToLocal("WirelessEnergyHatchMAX262144A"),
                14,
                262144));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchMAX262144A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchMAX1048576A
        // # 1048576A MAX Wireless Energy Hatch
        // # zh_CN 1048576安MAX无线能源仓
        GTNCItemList.WirelessEnergyHatchMAX1048576A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_MAX_1048576A.ID,
                "WirelessEnergyHatchMAX1048576A",
                StatCollector.translateToLocal("WirelessEnergyHatchMAX1048576A"),
                14,
                1048576));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchMAX1048576A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchMAX4194304A
        // # 4194304A MAX Wireless Energy Hatch
        // # zh_CN 4194304安MAX无线能源仓
        GTNCItemList.WirelessEnergyHatchMAX4194304A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_MAX_4194304A.ID,
                "WirelessEnergyHatchMAX4194304A",
                StatCollector.translateToLocal("WirelessEnergyHatchMAX4194304A"),
                14,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchMAX4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessEnergyHatchMAX16777216A
        // # 16777216A MAX Wireless Energy Hatch
        // # zh_CN 16777216安MAX无线能源仓
        GTNCItemList.WirelessEnergyHatchMAX16777216A.set(
            new MTEHatchWirelessMulti(
                GTNCMachineID.WIRELESS_ENERGY_HATCH_MAX_16777216A.ID,
                "WirelessEnergyHatchMAX16777216A",
                StatCollector.translateToLocal("WirelessEnergyHatchMAX16777216A"),
                14,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessEnergyHatchMAX16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // 无线动力仓 LV
        // #tr WirelessDynamoHatchLV4A
        // # 4A LV Wireless Dynamo Hatch
        // # zh_CN 4安LV无线动力仓
        GTNCItemList.WirelessDynamoHatchLV4A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_LV_4A.ID,
                "WirelessDynamoHatchLV4A",
                StatCollector.translateToLocal("WirelessDynamoHatchLV4A"),
                1,
                4));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchLV4A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchLV16A
        // # 16A LV Wireless Dynamo Hatch
        // # zh_CN 16安LV无线动力仓
        GTNCItemList.WirelessDynamoHatchLV16A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_LV_16A.ID,
                "WirelessDynamoHatchLV16A",
                StatCollector.translateToLocal("WirelessDynamoHatchLV16A"),
                1,
                16));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchLV16A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchLV64A
        // # 64A LV Wireless Dynamo Hatch
        // # zh_CN 64安LV无线动力仓
        GTNCItemList.WirelessDynamoHatchLV64A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_LV_64A.ID,
                "WirelessDynamoHatchLV64A",
                StatCollector.translateToLocal("WirelessDynamoHatchLV64A"),
                1,
                64));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchLV64A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // 无线动力仓 MV
        // #tr WirelessDynamoHatchMV4A
        // # 4A MV Wireless Dynamo Hatch
        // # zh_CN 4安MV无线动力仓
        GTNCItemList.WirelessDynamoHatchMV4A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_MV_4A.ID,
                "WirelessDynamoHatchMV4A",
                StatCollector.translateToLocal("WirelessDynamoHatchMV4A"),
                2,
                4));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchMV4A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchMV16A
        // # 16A MV Wireless Dynamo Hatch
        // # zh_CN 16安MV无线动力仓
        GTNCItemList.WirelessDynamoHatchMV16A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_MV_16A.ID,
                "WirelessDynamoHatchMV16A",
                StatCollector.translateToLocal("WirelessDynamoHatchMV16A"),
                2,
                16));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchMV16A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchMV64A
        // # 64A MV Wireless Dynamo Hatch
        // # zh_CN 64安MV无线动力仓
        GTNCItemList.WirelessDynamoHatchMV64A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_MV_64A.ID,
                "WirelessDynamoHatchMV64A",
                StatCollector.translateToLocal("WirelessDynamoHatchMV64A"),
                2,
                64));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchMV64A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // 无线动力仓 HV
        // #tr WirelessDynamoHatchHV4A
        // # 4A HV Wireless Dynamo Hatch
        // # zh_CN 4安HV无线动力仓
        GTNCItemList.WirelessDynamoHatchHV4A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_HV_4A.ID,
                "WirelessDynamoHatchHV4A",
                StatCollector.translateToLocal("WirelessDynamoHatchHV4A"),
                3,
                4));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchHV4A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchHV16A
        // # 16A HV Wireless Dynamo Hatch
        // # zh_CN 16安HV无线动力仓
        GTNCItemList.WirelessDynamoHatchHV16A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_HV_16A.ID,
                "WirelessDynamoHatchHV16A",
                StatCollector.translateToLocal("WirelessDynamoHatchHV16A"),
                3,
                16));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchHV16A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchHV64A
        // # 64A HV Wireless Dynamo Hatch
        // # zh_CN 64安HV无线动力仓
        GTNCItemList.WirelessDynamoHatchHV64A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_HV_64A.ID,
                "WirelessDynamoHatchHV64A",
                StatCollector.translateToLocal("WirelessDynamoHatchHV64A"),
                3,
                64));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchHV64A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // 无线动力仓 IV
        // #tr WirelessDynamoHatchIV256A
        // # 256A IV Wireless Dynamo Hatch
        // # zh_CN 256安IV无线动力仓
        GTNCItemList.WirelessDynamoHatchIV256A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_IV_256A.ID,
                "WirelessDynamoHatchIV256A",
                StatCollector.translateToLocal("WirelessDynamoHatchIV256A"),
                5,
                256));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchIV256A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchIV1024A
        // # 1024A IV Wireless Dynamo Hatch
        // # zh_CN 1024安IV无线动力仓
        GTNCItemList.WirelessDynamoHatchIV1024A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_IV_1024A.ID,
                "WirelessDynamoHatchIV1024A",
                StatCollector.translateToLocal("WirelessDynamoHatchIV1024A"),
                5,
                1024));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchIV1024A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchIV4096A
        // # 4096A IV Wireless Dynamo Hatch
        // # zh_CN 4096安IV无线动力仓
        GTNCItemList.WirelessDynamoHatchIV4096A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_IV_4096A.ID,
                "WirelessDynamoHatchIV4096A",
                StatCollector.translateToLocal("WirelessDynamoHatchIV4096A"),
                5,
                4096));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchIV4096A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchIV16384A
        // # 16384A IV Wireless Dynamo Hatch
        // # zh_CN 16384安IV无线动力仓
        GTNCItemList.WirelessDynamoHatchIV16384A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_IV_16384A.ID,
                "WirelessDynamoHatchIV16384A",
                StatCollector.translateToLocal("WirelessDynamoHatchIV16384A"),
                5,
                16384));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchIV16384A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchIV65536A
        // # 65536A IV Wireless Dynamo Hatch
        // # zh_CN 65536安IV无线动力仓
        GTNCItemList.WirelessDynamoHatchIV65536A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_IV_65536A.ID,
                "WirelessDynamoHatchIV65536A",
                StatCollector.translateToLocal("WirelessDynamoHatchIV65536A"),
                5,
                65536));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchIV65536A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchIV262144A
        // # 262144A IV Wireless Dynamo Hatch
        // # zh_CN 262144安IV无线动力仓
        GTNCItemList.WirelessDynamoHatchIV262144A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_IV_262144A.ID,
                "WirelessDynamoHatchIV262144A",
                StatCollector.translateToLocal("WirelessDynamoHatchIV262144A"),
                5,
                262144));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchIV262144A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchIV1048576A
        // # 1048576A IV Wireless Dynamo Hatch
        // # zh_CN 1048576安IV无线动力仓
        GTNCItemList.WirelessDynamoHatchIV1048576A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_IV_1048576A.ID,
                "WirelessDynamoHatchIV1048576A",
                StatCollector.translateToLocal("WirelessDynamoHatchIV1048576A"),
                5,
                1048576));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchIV1048576A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchIV4194304A
        // # 4194304A IV Wireless Dynamo Hatch
        // # zh_CN 4194304安IV无线动力仓
        GTNCItemList.WirelessDynamoHatchIV4194304A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_IV_4194304A.ID,
                "WirelessDynamoHatchIV4194304A",
                StatCollector.translateToLocal("WirelessDynamoHatchIV4194304A"),
                5,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchIV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchIV16777216A
        // # 16777216A IV Wireless Dynamo Hatch
        // # zh_CN 16777216安IV无线动力仓
        GTNCItemList.WirelessDynamoHatchIV16777216A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_IV_16777216A.ID,
                "WirelessDynamoHatchIV16777216A",
                StatCollector.translateToLocal("WirelessDynamoHatchIV16777216A"),
                5,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchIV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // 无线动力仓 LuV
        // #tr WirelessDynamoHatchLuV256A
        // # 256A LuV Wireless Dynamo Hatch
        // # zh_CN 256安LuV无线动力仓
        GTNCItemList.WirelessDynamoHatchLuV256A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_LUV_256A.ID,
                "WirelessDynamoHatchLuV256A",
                StatCollector.translateToLocal("WirelessDynamoHatchLuV256A"),
                6,
                256));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchLuV256A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchLuV1024A
        // # 1024A LuV Wireless Dynamo Hatch
        // # zh_CN 1024安LuV无线动力仓
        GTNCItemList.WirelessDynamoHatchLuV1024A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_LUV_1024A.ID,
                "WirelessDynamoHatchLuV1024A",
                StatCollector.translateToLocal("WirelessDynamoHatchLuV1024A"),
                6,
                1024));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchLuV1024A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchLuV4096A
        // # 4096A LuV Wireless Dynamo Hatch
        // # zh_CN 4096安LuV无线动力仓
        GTNCItemList.WirelessDynamoHatchLuV4096A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_LUV_4096A.ID,
                "WirelessDynamoHatchLuV4096A",
                StatCollector.translateToLocal("WirelessDynamoHatchLuV4096A"),
                6,
                4096));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchLuV4096A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchLuV16384A
        // # 16384A LuV Wireless Dynamo Hatch
        // # zh_CN 16384安LuV无线动力仓
        GTNCItemList.WirelessDynamoHatchLuV16384A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_LUV_16384A.ID,
                "WirelessDynamoHatchLuV16384A",
                StatCollector.translateToLocal("WirelessDynamoHatchLuV16384A"),
                6,
                16384));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchLuV16384A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchLuV65536A
        // # 65536A LuV Wireless Dynamo Hatch
        // # zh_CN 65536安LuV无线动力仓
        GTNCItemList.WirelessDynamoHatchLuV65536A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_LUV_65536A.ID,
                "WirelessDynamoHatchLuV65536A",
                StatCollector.translateToLocal("WirelessDynamoHatchLuV65536A"),
                6,
                65536));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchLuV65536A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchLuV262144A
        // # 262144A LuV Wireless Dynamo Hatch
        // # zh_CN 262144安LuV无线动力仓
        GTNCItemList.WirelessDynamoHatchLuV262144A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_LUV_262144A.ID,
                "WirelessDynamoHatchLuV262144A",
                StatCollector.translateToLocal("WirelessDynamoHatchLuV262144A"),
                6,
                262144));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchLuV262144A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchLuV1048576A
        // # 1048576A LuV Wireless Dynamo Hatch
        // # zh_CN 1048576安LuV无线动力仓
        GTNCItemList.WirelessDynamoHatchLuV1048576A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_LUV_1048576A.ID,
                "WirelessDynamoHatchLuV1048576A",
                StatCollector.translateToLocal("WirelessDynamoHatchLuV1048576A"),
                6,
                1048576));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchLuV1048576A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchLuV4194304A
        // # 4194304A LuV Wireless Dynamo Hatch
        // # zh_CN 4194304安LuV无线动力仓
        GTNCItemList.WirelessDynamoHatchLuV4194304A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_LUV_4194304A.ID,
                "WirelessDynamoHatchLuV4194304A",
                StatCollector.translateToLocal("WirelessDynamoHatchLuV4194304A"),
                6,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchLuV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchLuV16777216A
        // # 16777216A LuV Wireless Dynamo Hatch
        // # zh_CN 16777216安LuV无线动力仓
        GTNCItemList.WirelessDynamoHatchLuV16777216A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_LUV_16777216A.ID,
                "WirelessDynamoHatchLuV16777216A",
                StatCollector.translateToLocal("WirelessDynamoHatchLuV16777216A"),
                6,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchLuV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // 无线动力仓 ZPM
        // #tr WirelessDynamoHatchZPM256A
        // # 256A ZPM Wireless Dynamo Hatch
        // # zh_CN 256安ZPM无线动力仓
        GTNCItemList.WirelessDynamoHatchZPM256A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_ZPM_256A.ID,
                "WirelessDynamoHatchZPM256A",
                StatCollector.translateToLocal("WirelessDynamoHatchZPM256A"),
                7,
                256));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchZPM256A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchZPM1024A
        // # 1024A ZPM Wireless Dynamo Hatch
        // # zh_CN 1024安ZPM无线动力仓
        GTNCItemList.WirelessDynamoHatchZPM1024A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_ZPM_1024A.ID,
                "WirelessDynamoHatchZPM1024A",
                StatCollector.translateToLocal("WirelessDynamoHatchZPM1024A"),
                7,
                1024));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchZPM1024A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchZPM4096A
        // # 4096A ZPM Wireless Dynamo Hatch
        // # zh_CN 4096安ZPM无线动力仓
        GTNCItemList.WirelessDynamoHatchZPM4096A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_ZPM_4096A.ID,
                "WirelessDynamoHatchZPM4096A",
                StatCollector.translateToLocal("WirelessDynamoHatchZPM4096A"),
                7,
                4096));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchZPM4096A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchZPM16384A
        // # 16384A ZPM Wireless Dynamo Hatch
        // # zh_CN 16384安ZPM无线动力仓
        GTNCItemList.WirelessDynamoHatchZPM16384A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_ZPM_16384A.ID,
                "WirelessDynamoHatchZPM16384A",
                StatCollector.translateToLocal("WirelessDynamoHatchZPM16384A"),
                7,
                16384));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchZPM16384A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchZPM65536A
        // # 65536A ZPM Wireless Dynamo Hatch
        // # zh_CN 65536安ZPM无线动力仓
        GTNCItemList.WirelessDynamoHatchZPM65536A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_ZPM_65536A.ID,
                "WirelessDynamoHatchZPM65536A",
                StatCollector.translateToLocal("WirelessDynamoHatchZPM65536A"),
                7,
                65536));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchZPM65536A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchZPM262144A
        // # 262144A ZPM Wireless Dynamo Hatch
        // # zh_CN 262144安ZPM无线动力仓
        GTNCItemList.WirelessDynamoHatchZPM262144A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_ZPM_262144A.ID,
                "WirelessDynamoHatchZPM262144A",
                StatCollector.translateToLocal("WirelessDynamoHatchZPM262144A"),
                7,
                262144));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchZPM262144A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchZPM1048576A
        // # 1048576A ZPM Wireless Dynamo Hatch
        // # zh_CN 1048576安ZPM无线动力仓
        GTNCItemList.WirelessDynamoHatchZPM1048576A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_ZPM_1048576A.ID,
                "WirelessDynamoHatchZPM1048576A",
                StatCollector.translateToLocal("WirelessDynamoHatchZPM1048576A"),
                7,
                1048576));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchZPM1048576A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchZPM4194304A
        // # 4194304A ZPM Wireless Dynamo Hatch
        // # zh_CN 4194304安ZPM无线动力仓
        GTNCItemList.WirelessDynamoHatchZPM4194304A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_ZPM_4194304A.ID,
                "WirelessDynamoHatchZPM4194304A",
                StatCollector.translateToLocal("WirelessDynamoHatchZPM4194304A"),
                7,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchZPM4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchZPM16777216A
        // # 16777216A ZPM Wireless Dynamo Hatch
        // # zh_CN 16777216安ZPM无线动力仓
        GTNCItemList.WirelessDynamoHatchZPM16777216A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_ZPM_16777216A.ID,
                "WirelessDynamoHatchZPM16777216A",
                StatCollector.translateToLocal("WirelessDynamoHatchZPM16777216A"),
                7,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchZPM16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // 无线动力仓 UV
        // #tr WirelessDynamoHatchUV256A
        // # 256A UV Wireless Dynamo Hatch
        // # zh_CN 256安UV无线动力仓
        GTNCItemList.WirelessDynamoHatchUV256A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UV_256A.ID,
                "WirelessDynamoHatchUV256A",
                StatCollector.translateToLocal("WirelessDynamoHatchUV256A"),
                8,
                256));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUV256A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUV1024A
        // # 1024A UV Wireless Dynamo Hatch
        // # zh_CN 1024安UV无线动力仓
        GTNCItemList.WirelessDynamoHatchUV1024A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UV_1024A.ID,
                "WirelessDynamoHatchUV1024A",
                StatCollector.translateToLocal("WirelessDynamoHatchUV1024A"),
                8,
                1024));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUV1024A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUV4096A
        // # 4096A UV Wireless Dynamo Hatch
        // # zh_CN 4096安UV无线动力仓
        GTNCItemList.WirelessDynamoHatchUV4096A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UV_4096A.ID,
                "WirelessDynamoHatchUV4096A",
                StatCollector.translateToLocal("WirelessDynamoHatchUV4096A"),
                8,
                4096));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUV4096A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUV16384A
        // # 16384A UV Wireless Dynamo Hatch
        // # zh_CN 16384安UV无线动力仓
        GTNCItemList.WirelessDynamoHatchUV16384A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UV_16384A.ID,
                "WirelessDynamoHatchUV16384A",
                StatCollector.translateToLocal("WirelessDynamoHatchUV16384A"),
                8,
                16384));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUV16384A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUV65536A
        // # 65536A UV Wireless Dynamo Hatch
        // # zh_CN 65536安UV无线动力仓
        GTNCItemList.WirelessDynamoHatchUV65536A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UV_65536A.ID,
                "WirelessDynamoHatchUV65536A",
                StatCollector.translateToLocal("WirelessDynamoHatchUV65536A"),
                8,
                65536));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUV65536A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUV262144A
        // # 262144A UV Wireless Dynamo Hatch
        // # zh_CN 262144安UV无线动力仓
        GTNCItemList.WirelessDynamoHatchUV262144A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UV_262144A.ID,
                "WirelessDynamoHatchUV262144A",
                StatCollector.translateToLocal("WirelessDynamoHatchUV262144A"),
                8,
                262144));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUV262144A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUV1048576A
        // # 1048576A UV Wireless Dynamo Hatch
        // # zh_CN 1048576安UV无线动力仓
        GTNCItemList.WirelessDynamoHatchUV1048576A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UV_1048576A.ID,
                "WirelessDynamoHatchUV1048576A",
                StatCollector.translateToLocal("WirelessDynamoHatchUV1048576A"),
                8,
                1048576));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUV1048576A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUV4194304A
        // # 4194304A UV Wireless Dynamo Hatch
        // # zh_CN 4194304安UV无线动力仓
        GTNCItemList.WirelessDynamoHatchUV4194304A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UV_4194304A.ID,
                "WirelessDynamoHatchUV4194304A",
                StatCollector.translateToLocal("WirelessDynamoHatchUV4194304A"),
                8,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUV16777216A
        // # 16777216A UV Wireless Dynamo Hatch
        // # zh_CN 16777216安UV无线动力仓
        GTNCItemList.WirelessDynamoHatchUV16777216A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UV_16777216A.ID,
                "WirelessDynamoHatchUV16777216A",
                StatCollector.translateToLocal("WirelessDynamoHatchUV16777216A"),
                8,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // 无线动力仓 UHV
        // #tr WirelessDynamoHatchUHV256A
        // # 256A UHV Wireless Dynamo Hatch
        // # zh_CN 256安UHV无线动力仓
        GTNCItemList.WirelessDynamoHatchUHV256A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UHV_256A.ID,
                "WirelessDynamoHatchUHV256A",
                StatCollector.translateToLocal("WirelessDynamoHatchUHV256A"),
                9,
                256));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUHV256A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUHV1024A
        // # 1024A UHV Wireless Dynamo Hatch
        // # zh_CN 1024安UHV无线动力仓
        GTNCItemList.WirelessDynamoHatchUHV1024A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UHV_1024A.ID,
                "WirelessDynamoHatchUHV1024A",
                StatCollector.translateToLocal("WirelessDynamoHatchUHV1024A"),
                9,
                1024));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUHV1024A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUHV4096A
        // # 4096A UHV Wireless Dynamo Hatch
        // # zh_CN 4096安UHV无线动力仓
        GTNCItemList.WirelessDynamoHatchUHV4096A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UHV_4096A.ID,
                "WirelessDynamoHatchUHV4096A",
                StatCollector.translateToLocal("WirelessDynamoHatchUHV4096A"),
                9,
                4096));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUHV4096A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUHV16384A
        // # 16384A UHV Wireless Dynamo Hatch
        // # zh_CN 16384安UHV无线动力仓
        GTNCItemList.WirelessDynamoHatchUHV16384A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UHV_16384A.ID,
                "WirelessDynamoHatchUHV16384A",
                StatCollector.translateToLocal("WirelessDynamoHatchUHV16384A"),
                9,
                16384));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUHV16384A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUHV65536A
        // # 65536A UHV Wireless Dynamo Hatch
        // # zh_CN 65536安UHV无线动力仓
        GTNCItemList.WirelessDynamoHatchUHV65536A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UHV_65536A.ID,
                "WirelessDynamoHatchUHV65536A",
                StatCollector.translateToLocal("WirelessDynamoHatchUHV65536A"),
                9,
                65536));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUHV65536A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUHV262144A
        // # 262144A UHV Wireless Dynamo Hatch
        // # zh_CN 262144安UHV无线动力仓
        GTNCItemList.WirelessDynamoHatchUHV262144A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UHV_262144A.ID,
                "WirelessDynamoHatchUHV262144A",
                StatCollector.translateToLocal("WirelessDynamoHatchUHV262144A"),
                9,
                262144));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUHV262144A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUHV1048576A
        // # 1048576A UHV Wireless Dynamo Hatch
        // # zh_CN 1048576安UHV无线动力仓
        GTNCItemList.WirelessDynamoHatchUHV1048576A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UHV_1048576A.ID,
                "WirelessDynamoHatchUHV1048576A",
                StatCollector.translateToLocal("WirelessDynamoHatchUHV1048576A"),
                9,
                1048576));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUHV1048576A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUHV4194304A
        // # 4194304A UHV Wireless Dynamo Hatch
        // # zh_CN 4194304安UHV无线动力仓
        GTNCItemList.WirelessDynamoHatchUHV4194304A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UHV_4194304A.ID,
                "WirelessDynamoHatchUHV4194304A",
                StatCollector.translateToLocal("WirelessDynamoHatchUHV4194304A"),
                9,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUHV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUHV16777216A
        // # 16777216A UHV Wireless Dynamo Hatch
        // # zh_CN 16777216安UHV无线动力仓
        GTNCItemList.WirelessDynamoHatchUHV16777216A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UHV_16777216A.ID,
                "WirelessDynamoHatchUHV16777216A",
                StatCollector.translateToLocal("WirelessDynamoHatchUHV16777216A"),
                9,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUHV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // 无线动力仓 UEV
        // #tr WirelessDynamoHatchUEV256A
        // # 256A UEV Wireless Dynamo Hatch
        // # zh_CN 256安UEV无线动力仓
        GTNCItemList.WirelessDynamoHatchUEV256A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UEV_256A.ID,
                "WirelessDynamoHatchUEV256A",
                StatCollector.translateToLocal("WirelessDynamoHatchUEV256A"),
                10,
                256));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUEV256A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUEV1024A
        // # 1024A UEV Wireless Dynamo Hatch
        // # zh_CN 1024安UEV无线动力仓
        GTNCItemList.WirelessDynamoHatchUEV1024A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UEV_1024A.ID,
                "WirelessDynamoHatchUEV1024A",
                StatCollector.translateToLocal("WirelessDynamoHatchUEV1024A"),
                10,
                1024));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUEV1024A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUEV4096A
        // # 4096A UEV Wireless Dynamo Hatch
        // # zh_CN 4096安UEV无线动力仓
        GTNCItemList.WirelessDynamoHatchUEV4096A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UEV_4096A.ID,
                "WirelessDynamoHatchUEV4096A",
                StatCollector.translateToLocal("WirelessDynamoHatchUEV4096A"),
                10,
                4096));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUEV4096A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUEV16384A
        // # 16384A UEV Wireless Dynamo Hatch
        // # zh_CN 16384安UEV无线动力仓
        GTNCItemList.WirelessDynamoHatchUEV16384A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UEV_16384A.ID,
                "WirelessDynamoHatchUEV16384A",
                StatCollector.translateToLocal("WirelessDynamoHatchUEV16384A"),
                10,
                16384));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUEV16384A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUEV65536A
        // # 65536A UEV Wireless Dynamo Hatch
        // # zh_CN 65536安UEV无线动力仓
        GTNCItemList.WirelessDynamoHatchUEV65536A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UEV_65536A.ID,
                "WirelessDynamoHatchUEV65536A",
                StatCollector.translateToLocal("WirelessDynamoHatchUEV65536A"),
                10,
                65536));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUEV65536A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUEV262144A
        // # 262144A UEV Wireless Dynamo Hatch
        // # zh_CN 262144安UEV无线动力仓
        GTNCItemList.WirelessDynamoHatchUEV262144A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UEV_262144A.ID,
                "WirelessDynamoHatchUEV262144A",
                StatCollector.translateToLocal("WirelessDynamoHatchUEV262144A"),
                10,
                262144));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUEV262144A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUEV1048576A
        // # 1048576A UEV Wireless Dynamo Hatch
        // # zh_CN 1048576安UEV无线动力仓
        GTNCItemList.WirelessDynamoHatchUEV1048576A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UEV_1048576A.ID,
                "WirelessDynamoHatchUEV1048576A",
                StatCollector.translateToLocal("WirelessDynamoHatchUEV1048576A"),
                10,
                1048576));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUEV1048576A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUEV4194304A
        // # 4194304A UEV Wireless Dynamo Hatch
        // # zh_CN 4194304安UEV无线动力仓
        GTNCItemList.WirelessDynamoHatchUEV4194304A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UEV_4194304A.ID,
                "WirelessDynamoHatchUEV4194304A",
                StatCollector.translateToLocal("WirelessDynamoHatchUEV4194304A"),
                10,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUEV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUEV16777216A
        // # 16777216A UEV Wireless Dynamo Hatch
        // # zh_CN 16777216安UEV无线动力仓
        GTNCItemList.WirelessDynamoHatchUEV16777216A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UEV_16777216A.ID,
                "WirelessDynamoHatchUEV16777216A",
                StatCollector.translateToLocal("WirelessDynamoHatchUEV16777216A"),
                10,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUEV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // 无线动力仓 UIV
        // #tr WirelessDynamoHatchUIV256A
        // # 256A UIV Wireless Dynamo Hatch
        // # zh_CN 256安UIV无线动力仓
        GTNCItemList.WirelessDynamoHatchUIV256A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UIV_256A.ID,
                "WirelessDynamoHatchUIV256A",
                StatCollector.translateToLocal("WirelessDynamoHatchUIV256A"),
                11,
                256));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUIV256A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUIV1024A
        // # 1024A UIV Wireless Dynamo Hatch
        // # zh_CN 1024安UIV无线动力仓
        GTNCItemList.WirelessDynamoHatchUIV1024A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UIV_1024A.ID,
                "WirelessDynamoHatchUIV1024A",
                StatCollector.translateToLocal("WirelessDynamoHatchUIV1024A"),
                11,
                1024));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUIV1024A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUIV4096A
        // # 4096A UIV Wireless Dynamo Hatch
        // # zh_CN 4096安UIV无线动力仓
        GTNCItemList.WirelessDynamoHatchUIV4096A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UIV_4096A.ID,
                "WirelessDynamoHatchUIV4096A",
                StatCollector.translateToLocal("WirelessDynamoHatchUIV4096A"),
                11,
                4096));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUIV4096A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUIV16384A
        // # 16384A UIV Wireless Dynamo Hatch
        // # zh_CN 16384安UIV无线动力仓
        GTNCItemList.WirelessDynamoHatchUIV16384A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UIV_16384A.ID,
                "WirelessDynamoHatchUIV16384A",
                StatCollector.translateToLocal("WirelessDynamoHatchUIV16384A"),
                11,
                16384));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUIV16384A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUIV65536A
        // # 65536A UIV Wireless Dynamo Hatch
        // # zh_CN 65536安UIV无线动力仓
        GTNCItemList.WirelessDynamoHatchUIV65536A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UIV_65536A.ID,
                "WirelessDynamoHatchUIV65536A",
                StatCollector.translateToLocal("WirelessDynamoHatchUIV65536A"),
                11,
                65536));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUIV65536A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUIV262144A
        // # 262144A UIV Wireless Dynamo Hatch
        // # zh_CN 262144安UIV无线动力仓
        GTNCItemList.WirelessDynamoHatchUIV262144A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UIV_262144A.ID,
                "WirelessDynamoHatchUIV262144A",
                StatCollector.translateToLocal("WirelessDynamoHatchUIV262144A"),
                11,
                262144));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUIV262144A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUIV1048576A
        // # 1048576A UIV Wireless Dynamo Hatch
        // # zh_CN 1048576安UIV无线动力仓
        GTNCItemList.WirelessDynamoHatchUIV1048576A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UIV_1048576A.ID,
                "WirelessDynamoHatchUIV1048576A",
                StatCollector.translateToLocal("WirelessDynamoHatchUIV1048576A"),
                11,
                1048576));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUIV1048576A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUIV4194304A
        // # 4194304A UIV Wireless Dynamo Hatch
        // # zh_CN 4194304安UIV无线动力仓
        GTNCItemList.WirelessDynamoHatchUIV4194304A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UIV_4194304A.ID,
                "WirelessDynamoHatchUIV4194304A",
                StatCollector.translateToLocal("WirelessDynamoHatchUIV4194304A"),
                11,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUIV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUIV16777216A
        // # 16777216A UIV Wireless Dynamo Hatch
        // # zh_CN 16777216安UIV无线动力仓
        GTNCItemList.WirelessDynamoHatchUIV16777216A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UIV_16777216A.ID,
                "WirelessDynamoHatchUIV16777216A",
                StatCollector.translateToLocal("WirelessDynamoHatchUIV16777216A"),
                11,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUIV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // 无线动力仓 UMV
        // #tr WirelessDynamoHatchUMV256A
        // # 256A UMV Wireless Dynamo Hatch
        // # zh_CN 256安UMV无线动力仓
        GTNCItemList.WirelessDynamoHatchUMV256A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UMV_256A.ID,
                "WirelessDynamoHatchUMV256A",
                StatCollector.translateToLocal("WirelessDynamoHatchUMV256A"),
                12,
                256));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUMV256A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUMV1024A
        // # 1024A UMV Wireless Dynamo Hatch
        // # zh_CN 1024安UMV无线动力仓
        GTNCItemList.WirelessDynamoHatchUMV1024A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UMV_1024A.ID,
                "WirelessDynamoHatchUMV1024A",
                StatCollector.translateToLocal("WirelessDynamoHatchUMV1024A"),
                12,
                1024));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUMV1024A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUMV4096A
        // # 4096A UMV Wireless Dynamo Hatch
        // # zh_CN 4096安UMV无线动力仓
        GTNCItemList.WirelessDynamoHatchUMV4096A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UMV_4096A.ID,
                "WirelessDynamoHatchUMV4096A",
                StatCollector.translateToLocal("WirelessDynamoHatchUMV4096A"),
                12,
                4096));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUMV4096A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUMV16384A
        // # 16384A UMV Wireless Dynamo Hatch
        // # zh_CN 16384安UMV无线动力仓
        GTNCItemList.WirelessDynamoHatchUMV16384A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UMV_16384A.ID,
                "WirelessDynamoHatchUMV16384A",
                StatCollector.translateToLocal("WirelessDynamoHatchUMV16384A"),
                12,
                16384));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUMV16384A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUMV65536A
        // # 65536A UMV Wireless Dynamo Hatch
        // # zh_CN 65536安UMV无线动力仓
        GTNCItemList.WirelessDynamoHatchUMV65536A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UMV_65536A.ID,
                "WirelessDynamoHatchUMV65536A",
                StatCollector.translateToLocal("WirelessDynamoHatchUMV65536A"),
                12,
                65536));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUMV65536A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUMV262144A
        // # 262144A UMV Wireless Dynamo Hatch
        // # zh_CN 262144安UMV无线动力仓
        GTNCItemList.WirelessDynamoHatchUMV262144A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UMV_262144A.ID,
                "WirelessDynamoHatchUMV262144A",
                StatCollector.translateToLocal("WirelessDynamoHatchUMV262144A"),
                12,
                262144));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUMV262144A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUMV1048576A
        // # 1048576A UMV Wireless Dynamo Hatch
        // # zh_CN 1048576安UMV无线动力仓
        GTNCItemList.WirelessDynamoHatchUMV1048576A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UMV_1048576A.ID,
                "WirelessDynamoHatchUMV1048576A",
                StatCollector.translateToLocal("WirelessDynamoHatchUMV1048576A"),
                12,
                1048576));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUMV1048576A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUMV4194304A
        // # 4194304A UMV Wireless Dynamo Hatch
        // # zh_CN 4194304安UMV无线动力仓
        GTNCItemList.WirelessDynamoHatchUMV4194304A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UMV_4194304A.ID,
                "WirelessDynamoHatchUMV4194304A",
                StatCollector.translateToLocal("WirelessDynamoHatchUMV4194304A"),
                12,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUMV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUMV16777216A
        // # 16777216A UMV Wireless Dynamo Hatch
        // # zh_CN 16777216安UMV无线动力仓
        GTNCItemList.WirelessDynamoHatchUMV16777216A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UMV_16777216A.ID,
                "WirelessDynamoHatchUMV16777216A",
                StatCollector.translateToLocal("WirelessDynamoHatchUMV16777216A"),
                12,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUMV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // 无线动力仓 MAX
        // #tr WirelessDynamoHatchMAX256A
        // # 256A MAX Wireless Dynamo Hatch
        // # zh_CN 256安MAX无线动力仓
        GTNCItemList.WirelessDynamoHatchMAX256A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_MAX_256A.ID,
                "WirelessDynamoHatchMAX256A",
                StatCollector.translateToLocal("WirelessDynamoHatchMAX256A"),
                14,
                256));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchMAX256A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchMAX1024A
        // # 1024A MAX Wireless Dynamo Hatch
        // # zh_CN 1024安MAX无线动力仓
        GTNCItemList.WirelessDynamoHatchMAX1024A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_MAX_1024A.ID,
                "WirelessDynamoHatchMAX1024A",
                StatCollector.translateToLocal("WirelessDynamoHatchMAX1024A"),
                14,
                1024));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchMAX1024A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchMAX4096A
        // # 4096A MAX Wireless Dynamo Hatch
        // # zh_CN 4096安MAX无线动力仓
        GTNCItemList.WirelessDynamoHatchMAX4096A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_MAX_4096A.ID,
                "WirelessDynamoHatchMAX4096A",
                StatCollector.translateToLocal("WirelessDynamoHatchMAX4096A"),
                14,
                4096));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchMAX4096A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchMAX16384A
        // # 16384A MAX Wireless Dynamo Hatch
        // # zh_CN 16384安MAX无线动力仓
        GTNCItemList.WirelessDynamoHatchMAX16384A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_MAX_16384A.ID,
                "WirelessDynamoHatchMAX16384A",
                StatCollector.translateToLocal("WirelessDynamoHatchMAX16384A"),
                14,
                16384));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchMAX16384A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchMAX65536A
        // # 65536A MAX Wireless Dynamo Hatch
        // # zh_CN 65536安MAX无线动力仓
        GTNCItemList.WirelessDynamoHatchMAX65536A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_MAX_65536A.ID,
                "WirelessDynamoHatchMAX65536A",
                StatCollector.translateToLocal("WirelessDynamoHatchMAX65536A"),
                14,
                65536));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchMAX65536A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchMAX262144A
        // # 262144A MAX Wireless Dynamo Hatch
        // # zh_CN 262144安MAX无线动力仓
        GTNCItemList.WirelessDynamoHatchMAX262144A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_MAX_262144A.ID,
                "WirelessDynamoHatchMAX262144A",
                StatCollector.translateToLocal("WirelessDynamoHatchMAX262144A"),
                14,
                262144));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchMAX262144A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchMAX1048576A
        // # 1048576A MAX Wireless Dynamo Hatch
        // # zh_CN 1048576安MAX无线动力仓
        GTNCItemList.WirelessDynamoHatchMAX1048576A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_MAX_1048576A.ID,
                "WirelessDynamoHatchMAX1048576A",
                StatCollector.translateToLocal("WirelessDynamoHatchMAX1048576A"),
                14,
                1048576));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchMAX1048576A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchMAX4194304A
        // # 4194304A MAX Wireless Dynamo Hatch
        // # zh_CN 4194304安MAX无线动力仓
        GTNCItemList.WirelessDynamoHatchMAX4194304A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_MAX_4194304A.ID,
                "WirelessDynamoHatchMAX4194304A",
                StatCollector.translateToLocal("WirelessDynamoHatchMAX4194304A"),
                14,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchMAX4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchMAX16777216A
        // # 16777216A MAX Wireless Dynamo Hatch
        // # zh_CN 16777216安MAX无线动力仓
        GTNCItemList.WirelessDynamoHatchMAX16777216A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_MAX_16777216A.ID,
                "WirelessDynamoHatchMAX16777216A",
                StatCollector.translateToLocal("WirelessDynamoHatchMAX16777216A"),
                14,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchMAX16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // 无线动力仓 LV 1A
        // #tr WirelessDynamoHatchLV1A
        // # 1A LV Wireless Dynamo Hatch
        // # zh_CN 1安LV无线动力仓
        GTNCItemList.WirelessDynamoHatchLV1A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_LV_1A.ID,
                "WirelessDynamoHatchLV1A",
                StatCollector.translateToLocal("WirelessDynamoHatchLV1A"),
                1,
                1));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchLV1A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // 无线动力仓 MV 1A
        // #tr WirelessDynamoHatchMV1A
        // # 1A MV Wireless Dynamo Hatch
        // # zh_CN 1安MV无线动力仓
        GTNCItemList.WirelessDynamoHatchMV1A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_MV_1A.ID,
                "WirelessDynamoHatchMV1A",
                StatCollector.translateToLocal("WirelessDynamoHatchMV1A"),
                2,
                1));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchMV1A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // 无线动力仓 HV 1A
        // #tr WirelessDynamoHatchHV1A
        // # 1A HV Wireless Dynamo Hatch
        // # zh_CN 1安HV无线动力仓
        GTNCItemList.WirelessDynamoHatchHV1A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_HV_1A.ID,
                "WirelessDynamoHatchHV1A",
                StatCollector.translateToLocal("WirelessDynamoHatchHV1A"),
                3,
                1));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchHV1A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // 无线动力仓 IV 1A
        // #tr WirelessDynamoHatchIV1A
        // # 1A IV Wireless Dynamo Hatch
        // # zh_CN 1安IV无线动力仓
        GTNCItemList.WirelessDynamoHatchIV1A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_IV_1A.ID,
                "WirelessDynamoHatchIV1A",
                StatCollector.translateToLocal("WirelessDynamoHatchIV1A"),
                5,
                1));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchIV1A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchIV4A
        // # 4A IV Wireless Dynamo Hatch
        // # zh_CN 4安IV无线动力仓
        GTNCItemList.WirelessDynamoHatchIV4A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_IV_4A.ID,
                "WirelessDynamoHatchIV4A",
                StatCollector.translateToLocal("WirelessDynamoHatchIV4A"),
                5,
                4));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchIV4A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchIV16A
        // # 16A IV Wireless Dynamo Hatch
        // # zh_CN 16安IV无线动力仓
        GTNCItemList.WirelessDynamoHatchIV16A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_IV_16A.ID,
                "WirelessDynamoHatchIV16A",
                StatCollector.translateToLocal("WirelessDynamoHatchIV16A"),
                5,
                16));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchIV16A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchIV64A
        // # 64A IV Wireless Dynamo Hatch
        // # zh_CN 64安IV无线动力仓
        GTNCItemList.WirelessDynamoHatchIV64A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_IV_64A.ID,
                "WirelessDynamoHatchIV64A",
                StatCollector.translateToLocal("WirelessDynamoHatchIV64A"),
                5,
                64));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchIV64A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // 无线动力仓 LuV 1A
        // #tr WirelessDynamoHatchLuV1A
        // # 1A LuV Wireless Dynamo Hatch
        // # zh_CN 1安LuV无线动力仓
        GTNCItemList.WirelessDynamoHatchLuV1A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_LUV_1A.ID,
                "WirelessDynamoHatchLuV1A",
                StatCollector.translateToLocal("WirelessDynamoHatchLuV1A"),
                6,
                1));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchLuV1A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchLuV4A
        // # 4A LuV Wireless Dynamo Hatch
        // # zh_CN 4安LuV无线动力仓
        GTNCItemList.WirelessDynamoHatchLuV4A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_LUV_4A.ID,
                "WirelessDynamoHatchLuV4A",
                StatCollector.translateToLocal("WirelessDynamoHatchLuV4A"),
                6,
                4));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchLuV4A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchLuV16A
        // # 16A LuV Wireless Dynamo Hatch
        // # zh_CN 16安LuV无线动力仓
        GTNCItemList.WirelessDynamoHatchLuV16A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_LUV_16A.ID,
                "WirelessDynamoHatchLuV16A",
                StatCollector.translateToLocal("WirelessDynamoHatchLuV16A"),
                6,
                16));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchLuV16A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchLuV64A
        // # 64A LuV Wireless Dynamo Hatch
        // # zh_CN 64安LuV无线动力仓
        GTNCItemList.WirelessDynamoHatchLuV64A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_LUV_64A.ID,
                "WirelessDynamoHatchLuV64A",
                StatCollector.translateToLocal("WirelessDynamoHatchLuV64A"),
                6,
                64));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchLuV64A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // 无线动力仓 ZPM 1A
        // #tr WirelessDynamoHatchZPM1A
        // # 1A ZPM Wireless Dynamo Hatch
        // # zh_CN 1安ZPM无线动力仓
        GTNCItemList.WirelessDynamoHatchZPM1A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_ZPM_1A.ID,
                "WirelessDynamoHatchZPM1A",
                StatCollector.translateToLocal("WirelessDynamoHatchZPM1A"),
                7,
                1));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchZPM1A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchZPM4A
        // # 4A ZPM Wireless Dynamo Hatch
        // # zh_CN 4安ZPM无线动力仓
        GTNCItemList.WirelessDynamoHatchZPM4A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_ZPM_4A.ID,
                "WirelessDynamoHatchZPM4A",
                StatCollector.translateToLocal("WirelessDynamoHatchZPM4A"),
                7,
                4));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchZPM4A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchZPM16A
        // # 16A ZPM Wireless Dynamo Hatch
        // # zh_CN 16安ZPM无线动力仓
        GTNCItemList.WirelessDynamoHatchZPM16A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_ZPM_16A.ID,
                "WirelessDynamoHatchZPM16A",
                StatCollector.translateToLocal("WirelessDynamoHatchZPM16A"),
                7,
                16));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchZPM16A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchZPM64A
        // # 64A ZPM Wireless Dynamo Hatch
        // # zh_CN 64安ZPM无线动力仓
        GTNCItemList.WirelessDynamoHatchZPM64A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_ZPM_64A.ID,
                "WirelessDynamoHatchZPM64A",
                StatCollector.translateToLocal("WirelessDynamoHatchZPM64A"),
                7,
                64));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchZPM64A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // 无线动力仓 UV 1A
        // #tr WirelessDynamoHatchUV1A
        // # 1A UV Wireless Dynamo Hatch
        // # zh_CN 1安UV无线动力仓
        GTNCItemList.WirelessDynamoHatchUV1A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UV_1A.ID,
                "WirelessDynamoHatchUV1A",
                StatCollector.translateToLocal("WirelessDynamoHatchUV1A"),
                8,
                1));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUV1A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUV4A
        // # 4A UV Wireless Dynamo Hatch
        // # zh_CN 4安UV无线动力仓
        GTNCItemList.WirelessDynamoHatchUV4A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UV_4A.ID,
                "WirelessDynamoHatchUV4A",
                StatCollector.translateToLocal("WirelessDynamoHatchUV4A"),
                8,
                4));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUV4A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUV16A
        // # 16A UV Wireless Dynamo Hatch
        // # zh_CN 16安UV无线动力仓
        GTNCItemList.WirelessDynamoHatchUV16A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UV_16A.ID,
                "WirelessDynamoHatchUV16A",
                StatCollector.translateToLocal("WirelessDynamoHatchUV16A"),
                8,
                16));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUV16A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUV64A
        // # 64A UV Wireless Dynamo Hatch
        // # zh_CN 64安UV无线动力仓
        GTNCItemList.WirelessDynamoHatchUV64A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UV_64A.ID,
                "WirelessDynamoHatchUV64A",
                StatCollector.translateToLocal("WirelessDynamoHatchUV64A"),
                8,
                64));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUV64A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // 无线动力仓 UHV 1A
        // #tr WirelessDynamoHatchUHV1A
        // # 1A UHV Wireless Dynamo Hatch
        // # zh_CN 1安UHV无线动力仓
        GTNCItemList.WirelessDynamoHatchUHV1A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UHV_1A.ID,
                "WirelessDynamoHatchUHV1A",
                StatCollector.translateToLocal("WirelessDynamoHatchUHV1A"),
                9,
                1));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUHV1A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUHV4A
        // # 4A UHV Wireless Dynamo Hatch
        // # zh_CN 4安UHV无线动力仓
        GTNCItemList.WirelessDynamoHatchUHV4A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UHV_4A.ID,
                "WirelessDynamoHatchUHV4A",
                StatCollector.translateToLocal("WirelessDynamoHatchUHV4A"),
                9,
                4));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUHV4A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUHV16A
        // # 16A UHV Wireless Dynamo Hatch
        // # zh_CN 16安UHV无线动力仓
        GTNCItemList.WirelessDynamoHatchUHV16A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UHV_16A.ID,
                "WirelessDynamoHatchUHV16A",
                StatCollector.translateToLocal("WirelessDynamoHatchUHV16A"),
                9,
                16));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUHV16A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUHV64A
        // # 64A UHV Wireless Dynamo Hatch
        // # zh_CN 64安UHV无线动力仓
        GTNCItemList.WirelessDynamoHatchUHV64A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UHV_64A.ID,
                "WirelessDynamoHatchUHV64A",
                StatCollector.translateToLocal("WirelessDynamoHatchUHV64A"),
                9,
                64));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUHV64A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // 无线动力仓 UEV 1A
        // #tr WirelessDynamoHatchUEV1A
        // # 1A UEV Wireless Dynamo Hatch
        // # zh_CN 1安UEV无线动力仓
        GTNCItemList.WirelessDynamoHatchUEV1A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UEV_1A.ID,
                "WirelessDynamoHatchUEV1A",
                StatCollector.translateToLocal("WirelessDynamoHatchUEV1A"),
                10,
                1));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUEV1A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUEV4A
        // # 4A UEV Wireless Dynamo Hatch
        // # zh_CN 4安UEV无线动力仓
        GTNCItemList.WirelessDynamoHatchUEV4A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UEV_4A.ID,
                "WirelessDynamoHatchUEV4A",
                StatCollector.translateToLocal("WirelessDynamoHatchUEV4A"),
                10,
                4));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUEV4A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUEV16A
        // # 16A UEV Wireless Dynamo Hatch
        // # zh_CN 16安UEV无线动力仓
        GTNCItemList.WirelessDynamoHatchUEV16A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UEV_16A.ID,
                "WirelessDynamoHatchUEV16A",
                StatCollector.translateToLocal("WirelessDynamoHatchUEV16A"),
                10,
                16));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUEV16A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUEV64A
        // # 64A UEV Wireless Dynamo Hatch
        // # zh_CN 64安UEV无线动力仓
        GTNCItemList.WirelessDynamoHatchUEV64A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UEV_64A.ID,
                "WirelessDynamoHatchUEV64A",
                StatCollector.translateToLocal("WirelessDynamoHatchUEV64A"),
                10,
                64));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUEV64A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // 无线动力仓 UIV 1A
        // #tr WirelessDynamoHatchUIV1A
        // # 1A UIV Wireless Dynamo Hatch
        // # zh_CN 1安UIV无线动力仓
        GTNCItemList.WirelessDynamoHatchUIV1A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UIV_1A.ID,
                "WirelessDynamoHatchUIV1A",
                StatCollector.translateToLocal("WirelessDynamoHatchUIV1A"),
                11,
                1));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUIV1A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUIV4A
        // # 4A UIV Wireless Dynamo Hatch
        // # zh_CN 4安UIV无线动力仓
        GTNCItemList.WirelessDynamoHatchUIV4A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UIV_4A.ID,
                "WirelessDynamoHatchUIV4A",
                StatCollector.translateToLocal("WirelessDynamoHatchUIV4A"),
                11,
                4));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUIV4A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUIV16A
        // # 16A UIV Wireless Dynamo Hatch
        // # zh_CN 16安UIV无线动力仓
        GTNCItemList.WirelessDynamoHatchUIV16A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UIV_16A.ID,
                "WirelessDynamoHatchUIV16A",
                StatCollector.translateToLocal("WirelessDynamoHatchUIV16A"),
                11,
                16));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUIV16A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUIV64A
        // # 64A UIV Wireless Dynamo Hatch
        // # zh_CN 64安UIV无线动力仓
        GTNCItemList.WirelessDynamoHatchUIV64A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UIV_64A.ID,
                "WirelessDynamoHatchUIV64A",
                StatCollector.translateToLocal("WirelessDynamoHatchUIV64A"),
                11,
                64));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUIV64A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // 无线动力仓 UMV 1A
        // #tr WirelessDynamoHatchUMV1A
        // # 1A UMV Wireless Dynamo Hatch
        // # zh_CN 1安UMV无线动力仓
        GTNCItemList.WirelessDynamoHatchUMV1A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UMV_1A.ID,
                "WirelessDynamoHatchUMV1A",
                StatCollector.translateToLocal("WirelessDynamoHatchUMV1A"),
                12,
                1));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUMV1A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUMV4A
        // # 4A UMV Wireless Dynamo Hatch
        // # zh_CN 4安UMV无线动力仓
        GTNCItemList.WirelessDynamoHatchUMV4A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UMV_4A.ID,
                "WirelessDynamoHatchUMV4A",
                StatCollector.translateToLocal("WirelessDynamoHatchUMV4A"),
                12,
                4));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUMV4A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUMV16A
        // # 16A UMV Wireless Dynamo Hatch
        // # zh_CN 16安UMV无线动力仓
        GTNCItemList.WirelessDynamoHatchUMV16A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UMV_16A.ID,
                "WirelessDynamoHatchUMV16A",
                StatCollector.translateToLocal("WirelessDynamoHatchUMV16A"),
                12,
                16));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUMV16A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchUMV64A
        // # 64A UMV Wireless Dynamo Hatch
        // # zh_CN 64安UMV无线动力仓
        GTNCItemList.WirelessDynamoHatchUMV64A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_UMV_64A.ID,
                "WirelessDynamoHatchUMV64A",
                StatCollector.translateToLocal("WirelessDynamoHatchUMV64A"),
                12,
                64));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchUMV64A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // 无线动力仓 MAX 1A
        // #tr WirelessDynamoHatchMAX1A
        // # 1A MAX Wireless Dynamo Hatch
        // # zh_CN 1安MAX无线动力仓
        GTNCItemList.WirelessDynamoHatchMAX1A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_MAX_1A.ID,
                "WirelessDynamoHatchMAX1A",
                StatCollector.translateToLocal("WirelessDynamoHatchMAX1A"),
                14,
                1));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchMAX1A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchMAX4A
        // # 4A MAX Wireless Dynamo Hatch
        // # zh_CN 4安MAX无线动力仓
        GTNCItemList.WirelessDynamoHatchMAX4A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_MAX_4A.ID,
                "WirelessDynamoHatchMAX4A",
                StatCollector.translateToLocal("WirelessDynamoHatchMAX4A"),
                14,
                4));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchMAX4A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchMAX16A
        // # 16A MAX Wireless Dynamo Hatch
        // # zh_CN 16安MAX无线动力仓
        GTNCItemList.WirelessDynamoHatchMAX16A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_MAX_16A.ID,
                "WirelessDynamoHatchMAX16A",
                StatCollector.translateToLocal("WirelessDynamoHatchMAX16A"),
                14,
                16));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchMAX16A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr WirelessDynamoHatchMAX64A
        // # 64A MAX Wireless Dynamo Hatch
        // # zh_CN 64安MAX无线动力仓
        GTNCItemList.WirelessDynamoHatchMAX64A.set(
            new MTEHatchWirelessDynamoMulti(
                GTNCMachineID.WIRELESS_DYNAMO_HATCH_MAX_64A.ID,
                "WirelessDynamoHatchMAX64A",
                StatCollector.translateToLocal("WirelessDynamoHatchMAX64A"),
                14,
                64));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.WirelessDynamoHatchMAX64A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNCItemList.EnergyHatchLV.set(ItemList.Hatch_Energy_LV.get(1));

        // #tr EnergyHatchLV4A
        // # 4A LV Energy Hatch
        // # zh_CN 4安LV能源仓
        GTNCItemList.EnergyHatchLV4A.set(
            new MTEHatchEnergyMulti(
                GTNCMachineID.ENERGY_HATCH_LV_4A.ID,
                "EnergyHatchLV4A",
                StatCollector.translateToLocal("EnergyHatchLV4A"),
                1,
                4));
        AnimatedTooltipHandler.addItemTooltip(GTNCItemList.EnergyHatchLV4A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr EnergyHatchLV16A
        // # 16A LV Energy Hatch
        // # zh_CN 16安LV能源仓
        GTNCItemList.EnergyHatchLV16A.set(
            new MTEHatchEnergyMulti(
                GTNCMachineID.ENERGY_HATCH_LV_16A.ID,
                "EnergyHatchLV16A",
                StatCollector.translateToLocal("EnergyHatchLV16A"),
                1,
                16));
        AnimatedTooltipHandler.addItemTooltip(GTNCItemList.EnergyHatchLV16A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr EnergyHatchLV64A
        // # 64A LV Energy Hatch
        // # zh_CN 64安LV能源仓
        GTNCItemList.EnergyHatchLV64A.set(
            new MTEHatchEnergyMulti(
                GTNCMachineID.ENERGY_HATCH_LV_64A.ID,
                "EnergyHatchLV64A",
                StatCollector.translateToLocal("EnergyHatchLV64A"),
                1,
                64));
        AnimatedTooltipHandler.addItemTooltip(GTNCItemList.EnergyHatchLV64A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNCItemList.EnergyHatchMV.set(ItemList.Hatch_Energy_MV.get(1));

        // #tr EnergyHatchMV4A
        // # 4A MV Energy Hatch
        // # zh_CN 4安MV能源仓
        GTNCItemList.EnergyHatchMV4A.set(
            new MTEHatchEnergyMulti(
                GTNCMachineID.ENERGY_HATCH_MV_4A.ID,
                "EnergyHatchMV4A",
                StatCollector.translateToLocal("EnergyHatchMV4A"),
                2,
                4));
        AnimatedTooltipHandler.addItemTooltip(GTNCItemList.EnergyHatchMV4A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr EnergyHatchMV16A
        // # 16A MV Energy Hatch
        // # zh_CN 16安MV能源仓
        GTNCItemList.EnergyHatchMV16A.set(
            new MTEHatchEnergyMulti(
                GTNCMachineID.ENERGY_HATCH_MV_16A.ID,
                "EnergyHatchMV16A",
                StatCollector.translateToLocal("EnergyHatchMV16A"),
                2,
                16));
        AnimatedTooltipHandler.addItemTooltip(GTNCItemList.EnergyHatchMV16A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr EnergyHatchMV64A
        // # 64A MV Energy Hatch
        // # zh_CN 64安MV能源仓
        GTNCItemList.EnergyHatchMV64A.set(
            new MTEHatchEnergyMulti(
                GTNCMachineID.ENERGY_HATCH_MV_64A.ID,
                "EnergyHatchMV64A",
                StatCollector.translateToLocal("EnergyHatchMV64A"),
                2,
                64));
        AnimatedTooltipHandler.addItemTooltip(GTNCItemList.EnergyHatchMV64A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNCItemList.EnergyHatchHV.set(ItemList.Hatch_Energy_HV.get(1));

        // #tr EnergyHatchHV4A
        // # 4A HV Energy Hatch
        // # zh_CN 4安HV能源仓
        GTNCItemList.EnergyHatchHV4A.set(
            new MTEHatchEnergyMulti(
                GTNCMachineID.ENERGY_HATCH_HV_4A.ID,
                "EnergyHatchHV4A",
                StatCollector.translateToLocal("EnergyHatchHV4A"),
                3,
                4));
        AnimatedTooltipHandler.addItemTooltip(GTNCItemList.EnergyHatchHV4A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr EnergyHatchHV16A
        // # 16A HV Energy Hatch
        // # zh_CN 16安HV能源仓
        GTNCItemList.EnergyHatchHV16A.set(
            new MTEHatchEnergyMulti(
                GTNCMachineID.ENERGY_HATCH_HV_16A.ID,
                "EnergyHatchHV16A",
                StatCollector.translateToLocal("EnergyHatchHV16A"),
                3,
                16));
        AnimatedTooltipHandler.addItemTooltip(GTNCItemList.EnergyHatchHV16A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr EnergyHatchHV64A
        // # 64A HV Energy Hatch
        // # zh_CN 64安HV能源仓
        GTNCItemList.EnergyHatchHV64A.set(
            new MTEHatchEnergyMulti(
                GTNCMachineID.ENERGY_HATCH_HV_64A.ID,
                "EnergyHatchHV64A",
                StatCollector.translateToLocal("EnergyHatchHV64A"),
                3,
                64));
        AnimatedTooltipHandler.addItemTooltip(GTNCItemList.EnergyHatchHV64A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNCItemList.EnergyHatchEV.set(ItemList.Hatch_Energy_EV.get(1));

        GTNCItemList.EnergyHatchEV4A.set(CustomItemList.eM_energyMulti4_EV.get(1));

        GTNCItemList.EnergyHatchEV16A.set(CustomItemList.eM_energyMulti16_EV.get(1));

        GTNCItemList.EnergyHatchEV64A.set(CustomItemList.eM_energyMulti64_EV.get(1));

        GTNCItemList.EnergyHatchIV.set(ItemList.Hatch_Energy_IV.get(1));

        GTNCItemList.EnergyHatchIV4A.set(CustomItemList.eM_energyMulti4_IV.get(1));

        GTNCItemList.EnergyHatchIV16A.set(CustomItemList.eM_energyMulti16_IV.get(1));

        GTNCItemList.EnergyHatchIV64A.set(CustomItemList.eM_energyMulti64_IV.get(1));

        GTNCItemList.EnergyHatchLuV.set(ItemList.Hatch_Energy_LuV.get(1));

        GTNCItemList.EnergyHatchLuV4A.set(CustomItemList.eM_energyMulti4_LuV.get(1));

        GTNCItemList.EnergyHatchLuV16A.set(CustomItemList.eM_energyMulti16_LuV.get(1));

        GTNCItemList.EnergyHatchLuV64A.set(CustomItemList.eM_energyMulti64_LuV.get(1));

        GTNCItemList.EnergyHatchZPM.set(ItemList.Hatch_Energy_ZPM.get(1));

        GTNCItemList.EnergyHatchZPM4A.set(CustomItemList.eM_energyMulti4_ZPM.get(1));

        GTNCItemList.EnergyHatchZPM16A.set(CustomItemList.eM_energyMulti16_ZPM.get(1));

        GTNCItemList.EnergyHatchZPM64A.set(CustomItemList.eM_energyMulti64_ZPM.get(1));

        GTNCItemList.EnergyHatchUV.set(ItemList.Hatch_Energy_UV.get(1));

        GTNCItemList.EnergyHatchUV4A.set(CustomItemList.eM_energyMulti4_UV.get(1));

        GTNCItemList.EnergyHatchUV16A.set(CustomItemList.eM_energyMulti16_UV.get(1));

        GTNCItemList.EnergyHatchUV64A.set(CustomItemList.eM_energyMulti64_UV.get(1));

        GTNCItemList.EnergyHatchUHV.set(ItemList.Hatch_Energy_UHV.get(1));

        GTNCItemList.EnergyHatchUHV4A.set(CustomItemList.eM_energyMulti4_UHV.get(1));

        GTNCItemList.EnergyHatchUHV16A.set(CustomItemList.eM_energyMulti16_UHV.get(1));

        GTNCItemList.EnergyHatchUHV64A.set(CustomItemList.eM_energyMulti64_UHV.get(1));

        GTNCItemList.EnergyHatchUEV.set(ItemList.Hatch_Energy_UEV.get(1));

        GTNCItemList.EnergyHatchUEV4A.set(CustomItemList.eM_energyMulti4_UEV.get(1));

        GTNCItemList.EnergyHatchUEV16A.set(CustomItemList.eM_energyMulti16_UEV.get(1));

        GTNCItemList.EnergyHatchUEV64A.set(CustomItemList.eM_energyMulti64_UEV.get(1));

        GTNCItemList.EnergyHatchUIV.set(ItemList.Hatch_Energy_UIV.get(1));

        GTNCItemList.EnergyHatchUIV4A.set(CustomItemList.eM_energyMulti4_UIV.get(1));

        GTNCItemList.EnergyHatchUIV16A.set(CustomItemList.eM_energyMulti16_UIV.get(1));

        GTNCItemList.EnergyHatchUIV64A.set(CustomItemList.eM_energyMulti64_UIV.get(1));

        GTNCItemList.EnergyHatchUMV.set(ItemList.Hatch_Energy_UMV.get(1));

        GTNCItemList.EnergyHatchUMV4A.set(CustomItemList.eM_energyMulti4_UMV.get(1));

        GTNCItemList.EnergyHatchUMV16A.set(CustomItemList.eM_energyMulti16_UMV.get(1));

        GTNCItemList.EnergyHatchUMV64A.set(CustomItemList.eM_energyMulti64_UMV.get(1));

        GTNCItemList.EnergyHatchUXV.set(ItemList.Hatch_Energy_UXV.get(1));

        GTNCItemList.EnergyHatchUXV4A.set(CustomItemList.eM_energyMulti4_UXV.get(1));

        GTNCItemList.EnergyHatchUXV16A.set(CustomItemList.eM_energyMulti16_UXV.get(1));

        GTNCItemList.EnergyHatchUXV64A.set(CustomItemList.eM_energyMulti64_UXV.get(1));

        // #tr EnergyHatchMAX
        // # MAX Energy Hatch
        // # zh_CN MAX能源仓
        GTNCItemList.EnergyHatchMAX.set(
            new MTEHatchEnergy(
                GTNCMachineID.ENERGY_HATCH_MAX.ID,
                "EnergyHatchMAX",
                StatCollector.translateToLocal("EnergyHatchMAX"),
                14));
        AnimatedTooltipHandler.addItemTooltip(GTNCItemList.EnergyHatchMAX.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr EnergyHatchMAX4A
        // # 4A MAX Energy Hatch
        // # zh_CN 4安MAX能源仓
        GTNCItemList.EnergyHatchMAX4A.set(
            new MTEHatchEnergyMulti(
                GTNCMachineID.ENERGY_HATCH_MAX_4A.ID,
                "EnergyHatchMAX4A",
                StatCollector.translateToLocal("EnergyHatchMAX4A"),
                14,
                4));
        AnimatedTooltipHandler.addItemTooltip(GTNCItemList.EnergyHatchMAX4A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr EnergyHatchMAX16A
        // # 16A MAX Energy Hatch
        // # zh_CN 16安MAX能源仓
        GTNCItemList.EnergyHatchMAX16A.set(
            new MTEHatchEnergyMulti(
                GTNCMachineID.ENERGY_HATCH_MAX_16A.ID,
                "EnergyHatchMAX16A",
                StatCollector.translateToLocal("EnergyHatchMAX16A"),
                14,
                16));
        AnimatedTooltipHandler.addItemTooltip(GTNCItemList.EnergyHatchMAX16A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr EnergyHatchMAX64A
        // # 64A MAX Energy Hatch
        // # zh_CN 64安MAX能源仓
        GTNCItemList.EnergyHatchMAX64A.set(
            new MTEHatchEnergyMulti(
                GTNCMachineID.ENERGY_HATCH_MAX_64A.ID,
                "EnergyHatchMAX64A",
                StatCollector.translateToLocal("EnergyHatchMAX64A"),
                14,
                64));
        AnimatedTooltipHandler.addItemTooltip(GTNCItemList.EnergyHatchMAX64A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNCItemList.LaserEnergyHatchIV256A.set(CustomItemList.eM_energyTunnel1_IV.get(1));

        GTNCItemList.LaserEnergyHatchIV1024A.set(CustomItemList.eM_energyTunnel1_LuV.get(1));

        GTNCItemList.LaserEnergyHatchIV4096A.set(CustomItemList.eM_energyTunnel2_LuV.get(1));

        GTNCItemList.LaserEnergyHatchIV16384A.set(CustomItemList.eM_energyTunnel2_ZPM.get(1));

        GTNCItemList.LaserEnergyHatchIV65536A.set(CustomItemList.eM_energyTunnel3_ZPM.get(1));

        GTNCItemList.LaserEnergyHatchIV262144A.set(CustomItemList.eM_energyTunnel3_UV.get(1));

        GTNCItemList.LaserEnergyHatchIV1048576A.set(CustomItemList.eM_energyTunnel4_UV.get(1));

        // #tr LaserEnergyHatchIV4194304A
        // # IV 4,194,304A/t Laser Energy Hatch
        // # zh_CN IV 4,194,304A/t 激光靶仓
        GTNCItemList.LaserEnergyHatchIV4194304A.set(
            new MTEHatchEnergyTunnel(
                GTNCMachineID.LASER_ENERGY_HATCH_IV_4194304A.ID,
                "LaserEnergyHatchIV4194304A",
                StatCollector.translateToLocal("LaserEnergyHatchIV4194304A"),
                5,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LaserEnergyHatchIV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr LaserEnergyHatchIV16777216A
        // # IV 16,777,216A/t Laser Energy Hatch
        // # zh_CN IV 16,777,216A/t 激光靶仓
        GTNCItemList.LaserEnergyHatchIV16777216A.set(
            new MTEHatchEnergyTunnel(
                GTNCMachineID.LASER_ENERGY_HATCH_IV_16777216A.ID,
                "LaserEnergyHatchIV16777216A",
                StatCollector.translateToLocal("LaserEnergyHatchIV16777216A"),
                5,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LaserEnergyHatchIV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNCItemList.LaserEnergyHatchLuV256A.set(CustomItemList.eM_energyTunnel1_LuV.get(1));

        GTNCItemList.LaserEnergyHatchLuV1024A.set(CustomItemList.eM_energyTunnel2_LuV.get(1));

        GTNCItemList.LaserEnergyHatchLuV4096A.set(CustomItemList.eM_energyTunnel2_ZPM.get(1));

        GTNCItemList.LaserEnergyHatchLuV16384A.set(CustomItemList.eM_energyTunnel3_ZPM.get(1));

        GTNCItemList.LaserEnergyHatchLuV65536A.set(CustomItemList.eM_energyTunnel3_UV.get(1));

        GTNCItemList.LaserEnergyHatchLuV262144A.set(CustomItemList.eM_energyTunnel4_UV.get(1));

        GTNCItemList.LaserEnergyHatchLuV1048576A.set(CustomItemList.eM_energyTunnel4_UHV.get(1));

        // #tr LaserEnergyHatchLuV4194304A
        // # LuV 4,194,304A/t Laser Energy Hatch
        // # zh_CN LuV 4,194,304A/t 激光靶仓
        GTNCItemList.LaserEnergyHatchLuV4194304A.set(
            new MTEHatchEnergyTunnel(
                GTNCMachineID.LASER_ENERGY_HATCH_LUV_4194304A.ID,
                "LaserEnergyHatchLuV4194304A",
                StatCollector.translateToLocal("LaserEnergyHatchLuV4194304A"),
                6,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LaserEnergyHatchLuV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr LaserEnergyHatchLuV16777216A
        // # LuV 16,777,216A/t Laser Energy Hatch
        // # zh_CN LuV 16,777,216A/t 激光靶仓
        GTNCItemList.LaserEnergyHatchLuV16777216A.set(
            new MTEHatchEnergyTunnel(
                GTNCMachineID.LASER_ENERGY_HATCH_LUV_16777216A.ID,
                "LaserEnergyHatchLuV16777216A",
                StatCollector.translateToLocal("LaserEnergyHatchLuV16777216A"),
                6,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LaserEnergyHatchLuV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNCItemList.LaserEnergyHatchZPM256A.set(CustomItemList.eM_energyTunnel1_ZPM.get(1));

        GTNCItemList.LaserEnergyHatchZPM1024A.set(CustomItemList.eM_energyTunnel2_ZPM.get(1));

        GTNCItemList.LaserEnergyHatchZPM4096A.set(CustomItemList.eM_energyTunnel3_ZPM.get(1));

        GTNCItemList.LaserEnergyHatchZPM16384A.set(CustomItemList.eM_energyTunnel3_UV.get(1));

        GTNCItemList.LaserEnergyHatchZPM65536A.set(CustomItemList.eM_energyTunnel4_UV.get(1));

        GTNCItemList.LaserEnergyHatchZPM262144A.set(CustomItemList.eM_energyTunnel4_UHV.get(1));

        GTNCItemList.LaserEnergyHatchZPM1048576A.set(CustomItemList.eM_energyTunnel5_UHV.get(1));

        // #tr LaserEnergyHatchZPM4194304A
        // # ZPM 4,194,304A/t Laser Energy Hatch
        // # zh_CN ZPM 4,194,304A/t 激光靶仓
        GTNCItemList.LaserEnergyHatchZPM4194304A.set(
            new MTEHatchEnergyTunnel(
                GTNCMachineID.LASER_ENERGY_HATCH_ZPM_4194304A.ID,
                "LaserEnergyHatchZPM4194304A",
                StatCollector.translateToLocal("LaserEnergyHatchZPM4194304A"),
                7,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LaserEnergyHatchZPM4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr LaserEnergyHatchZPM16777216A
        // # ZPM 16,777,216A/t Laser Energy Hatch
        // # zh_CN ZPM 16,777,216A/t 激光靶仓
        GTNCItemList.LaserEnergyHatchZPM16777216A.set(
            new MTEHatchEnergyTunnel(
                GTNCMachineID.LASER_ENERGY_HATCH_ZPM_16777216A.ID,
                "LaserEnergyHatchZPM16777216A",
                StatCollector.translateToLocal("LaserEnergyHatchZPM16777216A"),
                7,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LaserEnergyHatchZPM16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNCItemList.LaserEnergyHatchUV256A.set(CustomItemList.eM_energyTunnel1_UV.get(1));

        GTNCItemList.LaserEnergyHatchUV1024A.set(CustomItemList.eM_energyTunnel2_UV.get(1));

        GTNCItemList.LaserEnergyHatchUV4096A.set(CustomItemList.eM_energyTunnel3_UV.get(1));

        GTNCItemList.LaserEnergyHatchUV16384A.set(CustomItemList.eM_energyTunnel4_UV.get(1));

        GTNCItemList.LaserEnergyHatchUV65536A.set(CustomItemList.eM_energyTunnel4_UHV.get(1));

        GTNCItemList.LaserEnergyHatchUV262144A.set(CustomItemList.eM_energyTunnel5_UHV.get(1));

        GTNCItemList.LaserEnergyHatchUV1048576A.set(CustomItemList.eM_energyTunnel5_UEV.get(1));

        // #tr LaserEnergyHatchUV4194304A
        // # UV 4,194,304A/t Laser Energy Hatch
        // # zh_CN UV 4,194,304A/t 激光靶仓
        GTNCItemList.LaserEnergyHatchUV4194304A.set(
            new MTEHatchEnergyTunnel(
                GTNCMachineID.LASER_ENERGY_HATCH_UV_4194304A.ID,
                "LaserEnergyHatchUV4194304A",
                StatCollector.translateToLocal("LaserEnergyHatchUV4194304A"),
                8,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LaserEnergyHatchUV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr LaserEnergyHatchUV16777216A
        // # UV 16,777,216A/t Laser Energy Hatch
        // # zh_CN UV 16,777,216A/t 激光靶仓
        GTNCItemList.LaserEnergyHatchUV16777216A.set(
            new MTEHatchEnergyTunnel(
                GTNCMachineID.LASER_ENERGY_HATCH_UV_16777216A.ID,
                "LaserEnergyHatchUV16777216A",
                StatCollector.translateToLocal("LaserEnergyHatchUV16777216A"),
                8,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LaserEnergyHatchUV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNCItemList.LaserEnergyHatchUHV256A.set(CustomItemList.eM_energyTunnel1_UHV.get(1));

        GTNCItemList.LaserEnergyHatchUHV1024A.set(CustomItemList.eM_energyTunnel2_UHV.get(1));

        GTNCItemList.LaserEnergyHatchUHV4096A.set(CustomItemList.eM_energyTunnel3_UHV.get(1));

        GTNCItemList.LaserEnergyHatchUHV16384A.set(CustomItemList.eM_energyTunnel4_UHV.get(1));

        GTNCItemList.LaserEnergyHatchUHV65536A.set(CustomItemList.eM_energyTunnel5_UHV.get(1));

        GTNCItemList.LaserEnergyHatchUHV262144A.set(CustomItemList.eM_energyTunnel5_UEV.get(1));

        GTNCItemList.LaserEnergyHatchUHV1048576A.set(CustomItemList.eM_energyTunnel6_UEV.get(1));

        // #tr LaserEnergyHatchUHV4194304A
        // # UHV 4,194,304A/t Laser Energy Hatch
        // # zh_CN UHV 4,194,304A/t 激光靶仓
        GTNCItemList.LaserEnergyHatchUHV4194304A.set(
            new MTEHatchEnergyTunnel(
                GTNCMachineID.LASER_ENERGY_HATCH_UHV_4194304A.ID,
                "LaserEnergyHatchUHV4194304A",
                StatCollector.translateToLocal("LaserEnergyHatchUHV4194304A"),
                9,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LaserEnergyHatchUHV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr LaserEnergyHatchUHV16777216A
        // # UHV 16,777,216A/t Laser Energy Hatch
        // # zh_CN UHV 16,777,216A/t 激光靶仓
        GTNCItemList.LaserEnergyHatchUHV16777216A.set(
            new MTEHatchEnergyTunnel(
                GTNCMachineID.LASER_ENERGY_HATCH_UHV_16777216A.ID,
                "LaserEnergyHatchUHV16777216A",
                StatCollector.translateToLocal("LaserEnergyHatchUHV16777216A"),
                9,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LaserEnergyHatchUHV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNCItemList.LaserEnergyHatchUEV256A.set(CustomItemList.eM_energyTunnel1_UEV.get(1));

        GTNCItemList.LaserEnergyHatchUEV1024A.set(CustomItemList.eM_energyTunnel2_UEV.get(1));

        GTNCItemList.LaserEnergyHatchUEV4096A.set(CustomItemList.eM_energyTunnel3_UEV.get(1));

        GTNCItemList.LaserEnergyHatchUEV16384A.set(CustomItemList.eM_energyTunnel4_UEV.get(1));

        GTNCItemList.LaserEnergyHatchUEV65536A.set(CustomItemList.eM_energyTunnel5_UEV.get(1));

        GTNCItemList.LaserEnergyHatchUEV262144A.set(CustomItemList.eM_energyTunnel6_UEV.get(1));

        GTNCItemList.LaserEnergyHatchUEV1048576A.set(CustomItemList.eM_energyTunnel6_UIV.get(1));

        // #tr LaserEnergyHatchUEV4194304A
        // # UEV 4,194,304A/t Laser Energy Hatch
        // # zh_CN UEV 4,194,304A/t 激光靶仓
        GTNCItemList.LaserEnergyHatchUEV4194304A.set(
            new MTEHatchEnergyTunnel(
                GTNCMachineID.LASER_ENERGY_HATCH_UEV_4194304A.ID,
                "LaserEnergyHatchUEV4194304A",
                StatCollector.translateToLocal("LaserEnergyHatchUEV4194304A"),
                10,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LaserEnergyHatchUEV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr LaserEnergyHatchUEV16777216A
        // # UEV 16,777,216A/t Laser Energy Hatch
        // # zh_CN UEV 16,777,216A/t 激光靶仓
        GTNCItemList.LaserEnergyHatchUEV16777216A.set(
            new MTEHatchEnergyTunnel(
                GTNCMachineID.LASER_ENERGY_HATCH_UEV_16777216A.ID,
                "LaserEnergyHatchUEV16777216A",
                StatCollector.translateToLocal("LaserEnergyHatchUEV16777216A"),
                10,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LaserEnergyHatchUEV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNCItemList.LaserEnergyHatchUIV256A.set(CustomItemList.eM_energyTunnel1_UIV.get(1));

        GTNCItemList.LaserEnergyHatchUIV1024A.set(CustomItemList.eM_energyTunnel2_UIV.get(1));

        GTNCItemList.LaserEnergyHatchUIV4096A.set(CustomItemList.eM_energyTunnel3_UIV.get(1));

        GTNCItemList.LaserEnergyHatchUIV16384A.set(CustomItemList.eM_energyTunnel4_UIV.get(1));

        GTNCItemList.LaserEnergyHatchUIV65536A.set(CustomItemList.eM_energyTunnel5_UIV.get(1));

        GTNCItemList.LaserEnergyHatchUIV262144A.set(CustomItemList.eM_energyTunnel6_UIV.get(1));

        GTNCItemList.LaserEnergyHatchUIV1048576A.set(CustomItemList.eM_energyTunnel7_UIV.get(1));

        // #tr LaserEnergyHatchUIV4194304A
        // # UIV 4,194,304A/t Laser Energy Hatch
        // # zh_CN UIV 4,194,304A/t 激光靶仓
        GTNCItemList.LaserEnergyHatchUIV4194304A.set(
            new MTEHatchEnergyTunnel(
                GTNCMachineID.LASER_ENERGY_HATCH_UIV_4194304A.ID,
                "LaserEnergyHatchUIV4194304A",
                StatCollector.translateToLocal("LaserEnergyHatchUIV4194304A"),
                11,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LaserEnergyHatchUIV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr LaserEnergyHatchUIV16777216A
        // # UIV 16,777,216A/t Laser Energy Hatch
        // # zh_CN UIV 16,777,216A/t 激光靶仓
        GTNCItemList.LaserEnergyHatchUIV16777216A.set(
            new MTEHatchEnergyTunnel(
                GTNCMachineID.LASER_ENERGY_HATCH_UIV_16777216A.ID,
                "LaserEnergyHatchUIV16777216A",
                StatCollector.translateToLocal("LaserEnergyHatchUIV16777216A"),
                11,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LaserEnergyHatchUIV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNCItemList.LaserEnergyHatchUMV256A.set(CustomItemList.eM_energyTunnel1_UMV.get(1));

        GTNCItemList.LaserEnergyHatchUMV1024A.set(CustomItemList.eM_energyTunnel2_UMV.get(1));

        GTNCItemList.LaserEnergyHatchUMV4096A.set(CustomItemList.eM_energyTunnel3_UMV.get(1));

        GTNCItemList.LaserEnergyHatchUMV16384A.set(CustomItemList.eM_energyTunnel4_UMV.get(1));

        GTNCItemList.LaserEnergyHatchUMV65536A.set(CustomItemList.eM_energyTunnel5_UMV.get(1));

        GTNCItemList.LaserEnergyHatchUMV262144A.set(CustomItemList.eM_energyTunnel6_UMV.get(1));

        GTNCItemList.LaserEnergyHatchUMV1048576A.set(CustomItemList.eM_energyTunnel7_UMV.get(1));

        GTNCItemList.LaserEnergyHatchUMV4194304A.set(CustomItemList.eM_energyTunnel8_UMV.get(1));

        // #tr LaserEnergyHatchUMV16777216A
        // # UMV 16,777,216A/t Laser Energy Hatch
        // # zh_CN UMV 16,777,216A/t 激光靶仓
        GTNCItemList.LaserEnergyHatchUMV16777216A.set(
            new MTEHatchEnergyTunnel(
                GTNCMachineID.LASER_ENERGY_HATCH_UMV_16777216A.ID,
                "LaserEnergyHatchUMV16777216A",
                StatCollector.translateToLocal("LaserEnergyHatchUMV16777216A"),
                12,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LaserEnergyHatchUMV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNCItemList.LaserEnergyHatchUXV256A.set(CustomItemList.eM_energyTunnel1_UXV.get(1));

        GTNCItemList.LaserEnergyHatchUXV1024A.set(CustomItemList.eM_energyTunnel2_UXV.get(1));

        GTNCItemList.LaserEnergyHatchUXV4096A.set(CustomItemList.eM_energyTunnel3_UXV.get(1));

        GTNCItemList.LaserEnergyHatchUXV16384A.set(CustomItemList.eM_energyTunnel4_UXV.get(1));

        GTNCItemList.LaserEnergyHatchUXV65536A.set(CustomItemList.eM_energyTunnel5_UXV.get(1));

        GTNCItemList.LaserEnergyHatchUXV262144A.set(CustomItemList.eM_energyTunnel6_UXV.get(1));

        GTNCItemList.LaserEnergyHatchUXV1048576A.set(CustomItemList.eM_energyTunnel7_UXV.get(1));

        GTNCItemList.LaserEnergyHatchUXV4194304A.set(CustomItemList.eM_energyTunnel8_UXV.get(1));

        GTNCItemList.LaserEnergyHatchUXV16777216A.set(CustomItemList.eM_energyTunnel9_UXV.get(1));

        // #tr LaserEnergyHatchMAX256A
        // # MAX 256A/t Laser Energy Hatch
        // # zh_CN MAX 256A/t 激光靶仓
        GTNCItemList.LaserEnergyHatchMAX256A.set(
            new MTEHatchEnergyTunnel(
                GTNCMachineID.LASER_ENERGY_HATCH_MAX_256A.ID,
                "LaserEnergyHatchMAX256A",
                StatCollector.translateToLocal("LaserEnergyHatchMAX256A"),
                14,
                256));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LaserEnergyHatchMAX256A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr LaserEnergyHatchMAX1024A
        // # MAX 1,024A/t Laser Energy Hatch
        // # zh_CN MAX 1,024A/t 激光靶仓
        GTNCItemList.LaserEnergyHatchMAX1024A.set(
            new MTEHatchEnergyTunnel(
                GTNCMachineID.LASER_ENERGY_HATCH_MAX_1024A.ID,
                "LaserEnergyHatchMAX1024A",
                StatCollector.translateToLocal("LaserEnergyHatchMAX1024A"),
                14,
                1024));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LaserEnergyHatchMAX1024A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr LaserEnergyHatchMAX4096A
        // # MAX 4,096A/t Laser Energy Hatch
        // # zh_CN MAX 4,096A/t 激光靶仓
        GTNCItemList.LaserEnergyHatchMAX4096A.set(
            new MTEHatchEnergyTunnel(
                GTNCMachineID.LASER_ENERGY_HATCH_MAX_4096A.ID,
                "LaserEnergyHatchMAX4096A",
                StatCollector.translateToLocal("LaserEnergyHatchMAX4096A"),
                14,
                4096));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LaserEnergyHatchMAX4096A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr LaserEnergyHatchMAX16384A
        // # MAX 16,384A/t Laser Energy Hatch
        // # zh_CN MAX 16,384A/t 激光靶仓
        GTNCItemList.LaserEnergyHatchMAX16384A.set(
            new MTEHatchEnergyTunnel(
                GTNCMachineID.LASER_ENERGY_HATCH_MAX_16384A.ID,
                "LaserEnergyHatchMAX16384A",
                StatCollector.translateToLocal("LaserEnergyHatchMAX16384A"),
                14,
                16384));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LaserEnergyHatchMAX16384A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr LaserEnergyHatchMAX65536A
        // # MAX 65,536A/t Laser Energy Hatch
        // # zh_CN MAX 65,536A/t 激光靶仓
        GTNCItemList.LaserEnergyHatchMAX65536A.set(
            new MTEHatchEnergyTunnel(
                GTNCMachineID.LASER_ENERGY_HATCH_MAX_65536A.ID,
                "LaserEnergyHatchMAX65536A",
                StatCollector.translateToLocal("LaserEnergyHatchMAX65536A"),
                14,
                65536));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LaserEnergyHatchMAX65536A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr LaserEnergyHatchMAX262144A
        // # MAX 262,144A/t Laser Energy Hatch
        // # zh_CN MAX 262,144A/t 激光靶仓
        GTNCItemList.LaserEnergyHatchMAX262144A.set(
            new MTEHatchEnergyTunnel(
                GTNCMachineID.LASER_ENERGY_HATCH_MAX_262144A.ID,
                "LaserEnergyHatchMAX262144A",
                StatCollector.translateToLocal("LaserEnergyHatchMAX262144A"),
                14,
                262144));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LaserEnergyHatchMAX262144A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr LaserEnergyHatchMAX1048576A
        // # MAX 1,048,576A/t Laser Energy Hatch
        // # zh_CN MAX 1,048,576A/t 激光靶仓
        GTNCItemList.LaserEnergyHatchMAX1048576A.set(
            new MTEHatchEnergyTunnel(
                GTNCMachineID.LASER_ENERGY_HATCH_MAX_1048576A.ID,
                "LaserEnergyHatchMAX1048576A",
                StatCollector.translateToLocal("LaserEnergyHatchMAX1048576A"),
                14,
                1048576));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LaserEnergyHatchMAX1048576A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr LaserEnergyHatchMAX4194304A
        // # MAX 4,194,304A/t Laser Energy Hatch
        // # zh_CN MAX 4,194,304A/t 激光靶仓
        GTNCItemList.LaserEnergyHatchMAX4194304A.set(
            new MTEHatchEnergyTunnel(
                GTNCMachineID.LASER_ENERGY_HATCH_MAX_4194304A.ID,
                "LaserEnergyHatchMAX4194304A",
                StatCollector.translateToLocal("LaserEnergyHatchMAX4194304A"),
                14,
                4194304));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LaserEnergyHatchMAX4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr LaserEnergyHatchMAX16777216A
        // # MAX 16,777,216A/t Laser Energy Hatch
        // # zh_CN MAX 16,777,216A/t 激光靶仓
        GTNCItemList.LaserEnergyHatchMAX16777216A.set(
            new MTEHatchEnergyTunnel(
                GTNCMachineID.LASER_ENERGY_HATCH_MAX_16777216A.ID,
                "LaserEnergyHatchMAX16777216A",
                StatCollector.translateToLocal("LaserEnergyHatchMAX16777216A"),
                14,
                16777216));
        AnimatedTooltipHandler
            .addItemTooltip(GTNCItemList.LaserEnergyHatchMAX16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

    }

    public static void registerCovers() {

        for (int i = 0; i < 14; i++) {
            int tier = i + 1;
            CoverRegistry.registerCover(
                GTNCItemList.WIRELESS_ENERGY_COVER_4A[i].get(1),
                TextureFactory.of(
                    Textures.BlockIcons.MACHINE_CASINGS[1][0],
                    TextureFactory.of(Textures.BlockIcons.OVERLAYS_ENERGY_ON_WIRELESS_4A[0])),
                context -> new WirelessMultiEnergyCover(context, (int) GTValues.V[tier], 4),
                CoverRegistry.INTERCEPTS_RIGHT_CLICK_COVER_PLACER);
        }

    }

    public static void registerbasicMachine() {

        // #tr DieselGeneratorLV
        // # Gas Turbine LV
        // # zh_CN 基础内燃发电机
        GTNCItemList.DieselGeneratorLV.set(
            new DieselGenerator(
                GTNCMachineID.Diesel_Generator_LV.ID,
                "DieselGeneratorLV",
                StatCollector.translateToLocal("DieselGeneratorLV"),
                1));
        addItemTooltip(GTNCItemList.DieselGeneratorLV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr DieselGeneratorMV
        // # Gas Turbine MV
        // # zh_CN 进阶内燃发电机
        GTNCItemList.DieselGeneratorMV.set(
            new DieselGenerator(
                GTNCMachineID.Diesel_Generator_MV.ID,
                "DieselGeneratorMV",
                StatCollector.translateToLocal("DieselGeneratorMV"),
                2));
        addItemTooltip(GTNCItemList.DieselGeneratorMV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr DieselGeneratorHV
        // # Gas Turbine HV
        // # zh_CN 进阶内燃发电机 II
        GTNCItemList.DieselGeneratorHV.set(
            new DieselGenerator(
                GTNCMachineID.Diesel_Generator_HV.ID,
                "DieselGeneratorHV",
                StatCollector.translateToLocal("DieselGeneratorHV"),
                3));
        addItemTooltip(GTNCItemList.DieselGeneratorHV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr DieselGeneratorEV
        // # Gas Turbine EV
        // # zh_CN 进阶内燃发电机 III
        GTNCItemList.DieselGeneratorEV.set(
            new DieselGenerator(
                GTNCMachineID.Diesel_Generator_EV.ID,
                "DieselGeneratorEV",
                StatCollector.translateToLocal("DieselGeneratorEV"),
                4));
        addItemTooltip(GTNCItemList.DieselGeneratorEV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr SteamTurbineLV
        // # Steam Turbine LV
        // # zh_CN 基础蒸汽轮机
        GTNCItemList.SteamTurbineLV.set(
            new SteamTurbine(
                GTNCMachineID.STEAM_TURBINE_LV.ID,
                "SteamTurbineLV",
                StatCollector.translateToLocal("SteamTurbineLV"),
                1));
        addItemTooltip(GTNCItemList.SteamTurbineLV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr SteamTurbineMV
        // # Steam Turbine MV
        // # zh_CN 进阶蒸汽轮机
        GTNCItemList.SteamTurbineMV.set(
            new SteamTurbine(
                GTNCMachineID.STEAM_TURBINE_MV.ID,
                "SteamTurbineMV",
                StatCollector.translateToLocal("SteamTurbineMV"),
                2));
        addItemTooltip(GTNCItemList.SteamTurbineMV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr SteamTurbineHV
        // # Steam Turbine HV
        // # zh_CN 进阶蒸汽轮机 II
        GTNCItemList.SteamTurbineHV.set(
            new SteamTurbine(
                GTNCMachineID.STEAM_TURBINE_HV.ID,
                "SteamTurbineHV",
                StatCollector.translateToLocal("SteamTurbineHV"),
                3));
        addItemTooltip(GTNCItemList.SteamTurbineHV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr SteamTurbineEV
        // # Steam Turbine EV
        // # zh_CN 进阶蒸汽轮机 III
        GTNCItemList.SteamTurbineEV.set(
            new SteamTurbine(
                GTNCMachineID.STEAM_TURBINE_EV.ID,
                "SteamTurbineEV",
                StatCollector.translateToLocal("SteamTurbineEV"),
                4));
        addItemTooltip(GTNCItemList.SteamTurbineEV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr SteamTurbineIV
        // # Steam Turbine IV
        // # zh_CN 进阶蒸汽轮机 IV
        GTNCItemList.SteamTurbineIV.set(
            new SteamTurbine(
                GTNCMachineID.STEAM_TURBINE_IV.ID,
                "SteamTurbineIV",
                StatCollector.translateToLocal("SteamTurbineIV"),
                5));
        addItemTooltip(GTNCItemList.SteamTurbineIV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        // #tr SteamTurbineLuV
        // # Steam Turbine V
        // # zh_CN 进阶蒸汽轮机 V
        GTNCItemList.SteamTurbineLuV.set(
            new SteamTurbine(
                GTNCMachineID.STEAM_TURBINE_LUV.ID,
                "SteamTurbineLuV",
                StatCollector.translateToLocal("SteamTurbineLuV"),
                6));
        addItemTooltip(GTNCItemList.SteamTurbineLuV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

    }

    public static void registry() {

        registerMachines();
        registerHatch();
        registerbasicMachine();
        registerCovers();
    }

}
