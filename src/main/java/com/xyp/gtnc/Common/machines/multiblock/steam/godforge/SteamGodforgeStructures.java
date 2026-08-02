package com.xyp.gtnc.Common.machines.multiblock.steam.godforge;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.isAir;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static gregtech.api.GregTechAPI.sBlockCasings1;
import static gregtech.api.GregTechAPI.sBlockCasings2;
import static gregtech.api.GregTechAPI.sBlockCasings3;
import static gregtech.api.GregTechAPI.sBlockFrames;
import static gregtech.api.GregTechAPI.sBlockReinforced;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.OutputBus;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.init.Blocks;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.xyp.gtnc.Common.machines.hatch.SuperMTEHatchCraftingInputME;

import gregtech.api.enums.Materials;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.metatileentity.implementations.MTEHatchOutput;
import gregtech.api.metatileentity.implementations.MTEHatchOutputBus;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.HatchElementBuilder;
import gregtech.api.util.IGTHatchAdder;
import gregtech.common.tileentities.machines.MTEHatchCraftingInputME;
import tectech.thing.metaTileEntity.multi.godforge.MTEBaseModule;
import tectech.thing.metaTileEntity.multi.godforge.MTEForgeOfGods;
import tectech.thing.metaTileEntity.multi.godforge.structure.ForgeOfGodsRingsStructureString;
import tectech.thing.metaTileEntity.multi.godforge.structure.ForgeOfGodsStructureString;

/**
 * Bronze-age block palette applied to the upstream Forge of Gods geometry.
 *
 * <p>
 * The structure strings remain owned by GT5U. Only the element mapping is changed here, which keeps every block
 * coordinate, module socket, beam shaft and optional ring identical to the packaged Forge of Gods.
 * </p>
 */
public final class SteamGodforgeStructures {

    public static final String MAIN = "main";
    public static final String SHAFT = "beam_shaft";
    public static final String FIRST_RING = "first_ring";
    public static final String FIRST_RING_AIR = "first_ring_air";
    public static final String SECOND_RING = "second_ring";
    public static final String SECOND_RING_AIR = "second_ring_air";
    public static final String THIRD_RING = "third_ring";
    public static final String THIRD_RING_AIR = "third_ring_air";
    public static final String MODULE_MAIN = "main";

    private static final int BRONZE_CASING_TEXTURE = 10;

    private static final IStructureDefinition<MTEForgeOfGods> CONTROLLER = IStructureDefinition
        .<MTEForgeOfGods>builder()
        .addShape(MAIN, ForgeOfGodsStructureString.MAIN_STRUCTURE)
        .addShape(SHAFT, ForgeOfGodsStructureString.BEAM_SHAFT)
        .addShape(FIRST_RING, ForgeOfGodsStructureString.FIRST_RING)
        .addShape(FIRST_RING_AIR, ForgeOfGodsStructureString.FIRST_RING_AIR)
        .addShape(SECOND_RING, ForgeOfGodsRingsStructureString.SECOND_RING)
        .addShape(SECOND_RING_AIR, ForgeOfGodsRingsStructureString.SECOND_RING_AIR)
        .addShape(THIRD_RING, ForgeOfGodsRingsStructureString.THIRD_RING)
        .addShape(THIRD_RING_AIR, ForgeOfGodsRingsStructureString.THIRD_RING_AIR)
        .addElement(
            'A',
            HatchElementBuilder.<MTEForgeOfGods>builder()
                .atLeast(InputBus, InputHatch, OutputBus)
                .casingIndex(BRONZE_CASING_TEXTURE)
                .hint(1)
                .buildAndChain(sBlockCasings1, 10))
        .addElement('B', ofBlock(sBlockCasings1, 10))
        .addElement('C', ofBlock(sBlockReinforced, 0))
        .addElement('D', ofBlock(sBlockCasings2, 2))
        .addElement('E', ofBlock(sBlockCasings2, 12))
        .addElement('F', ofBlock(sBlockCasings3, 13))
        .addElement('G', ofBlock(sBlockFrames, Materials.Bronze.mMetaItemSubID))
        .addElement('H', ofBlock(Blocks.glass, 0))
        .addElement('I', ofBlock(sBlockReinforced, 0))
        .addElement(
            'J',
            HatchElementBuilder.<MTEForgeOfGods>builder()
                .atLeast(SteamModuleElement.Module)
                .casingIndex(BRONZE_CASING_TEXTURE)
                .hint(2)
                .buildAndChain(sBlockCasings1, 10))
        .addElement('K', ofBlock(sBlockFrames, Materials.Bronze.mMetaItemSubID))
        .addElement('L', isAir())
        .build();

    private static final IStructureDefinition<MTEBaseModule> MODULE = StructureDefinition.<MTEBaseModule>builder()
        .addShape(
            MODULE_MAIN,
            new String[][] { { "       ", "  BBB  ", " BBBBB ", " BB~BB ", " BBBBB ", "  BBB  ", "       " },
                { "  CCC  ", " CFFFC ", "CFFFFFC", "CFFFFFC", "CFFFFFC", " CFFFC ", "  CCC  " },
                { "       ", "       ", "   E   ", "  EAE  ", "   E   ", "       ", "       " },
                { "       ", "       ", "   E   ", "  EAE  ", "   E   ", "       ", "       " },
                { "       ", "       ", "   E   ", "  EAE  ", "   E   ", "       ", "       " },
                { "       ", "       ", "   E   ", "  EAE  ", "   E   ", "       ", "       " },
                { "       ", "       ", "   E   ", "  EAE  ", "   E   ", "       ", "       " },
                { "       ", "       ", "       ", "   D   ", "       ", "       ", "       " },
                { "       ", "       ", "       ", "   D   ", "       ", "       ", "       " },
                { "       ", "       ", "       ", "   D   ", "       ", "       ", "       " },
                { "       ", "       ", "       ", "   D   ", "       ", "       ", "       " },
                { "       ", "       ", "       ", "   D   ", "       ", "       ", "       " },
                { "       ", "       ", "       ", "   G   ", "       ", "       ", "       " } })
        .addElement('A', ofBlock(sBlockCasings3, 13))
        .addElement(
            'B',
            GTStructureUtility.ofHatchAdderOptional(
                SteamGodforgeStructures::addSteamModuleIO,
                BRONZE_CASING_TEXTURE,
                1,
                sBlockCasings1,
                10))
        // Keep the upstream module casing sequence intact: casing 0/1/2/3/4 becomes bronze plated,
        // reinforced, gearbox, pipe and firebox respectively. These blocks meet the controller structure.
        .addElement(
            'C',
            GTStructureUtility.ofHatchAdderOptional(
                SteamGodforgeStructures::addSteamModuleIO,
                BRONZE_CASING_TEXTURE,
                1,
                sBlockCasings1,
                10))
        .addElement('D', ofBlock(sBlockReinforced, 0))
        .addElement('E', ofBlock(sBlockCasings2, 2))
        .addElement('F', ofBlock(sBlockCasings2, 12))
        .addElement('G', ofBlock(sBlockCasings3, 13))
        .build();

    private SteamGodforgeStructures() {}

    public static IStructureDefinition<MTEForgeOfGods> controller() {
        return CONTROLLER;
    }

    public static IStructureDefinition<MTEBaseModule> module() {
        return MODULE;
    }

    private static boolean addSteamModuleIO(MTEBaseModule module, IGregTechTileEntity tileEntity, int casingIndex) {

        if (tileEntity == null) {
            return false;
        }

        IMetaTileEntity metaTileEntity = tileEntity.getMetaTileEntity();

        if (metaTileEntity instanceof MTEHatchInput || metaTileEntity instanceof MTEHatchInputBus) {

            boolean added = module.addInputToMachineList(tileEntity, casingIndex);

            if (added) {
                bindSteamModuleRecipeMap(module, metaTileEntity);
            }

            return added;
        }

        if (metaTileEntity instanceof MTEHatchOutput || metaTileEntity instanceof MTEHatchOutputBus) {

            return module.addOutputToMachineList(tileEntity, casingIndex);
        }

        return false;
    }

    private static void bindSteamModuleRecipeMap(MTEBaseModule module, IMetaTileEntity metaTileEntity) {

        RecipeMap<?> recipeMap = module.getRecipeMap();

        if (recipeMap == null) {
            return;
        }

        if (metaTileEntity instanceof SuperMTEHatchCraftingInputME superHatch) {

            superHatch.setControllerRecipeMap(recipeMap);

        } else if (metaTileEntity instanceof MTEHatchCraftingInputME craftingHatch) {

            craftingHatch.mRecipeMap = recipeMap;
        }
    }

    static boolean isSteamModule(IMetaTileEntity metaTileEntity) {
        return metaTileEntity instanceof SteamGodforgeSmeltingModule
            || metaTileEntity instanceof SteamGodforgeMoltenModule
            || metaTileEntity instanceof SteamGodforgePlasmaModule
            || metaTileEntity instanceof SteamGodforgeExoticModule
            || metaTileEntity instanceof SteamGodforgeAlloyBlastSmelterModule
            || metaTileEntity instanceof SteamGodforgeAlloySmelterModule
            || metaTileEntity instanceof SteamGodforgeExtractorModule
            || metaTileEntity instanceof SteamGodforgeSolarMuonCatalystModule
            || metaTileEntity instanceof SteamGodforgeProcessingModule;
    }

    private enum SteamModuleElement implements IHatchElement<MTEForgeOfGods> {

        Module(SteamGodforgeSmeltingModule.class, SteamGodforgeMoltenModule.class, SteamGodforgePlasmaModule.class,
            SteamGodforgeExoticModule.class, SteamGodforgeAlloyBlastSmelterModule.class,
            SteamGodforgeAlloySmelterModule.class, SteamGodforgeExtractorModule.class,
            SteamGodforgeSolarMuonCatalystModule.class, SteamGodforgeProcessingModule.class);

        private final List<Class<? extends IMetaTileEntity>> moduleClasses;
        private final IGTHatchAdder<MTEForgeOfGods> adder = (controller, tileEntity,
            casingIndex) -> controller instanceof SteamForgeOfGods steamController
                && steamController.addSteamModuleToMachineList(tileEntity, casingIndex);

        @SafeVarargs
        SteamModuleElement(Class<? extends IMetaTileEntity>... moduleClasses) {
            this.moduleClasses = Collections.unmodifiableList(Arrays.asList(moduleClasses));
        }

        @Override
        public List<? extends Class<? extends IMetaTileEntity>> mteClasses() {
            return moduleClasses;
        }

        @Override
        public IGTHatchAdder<? super MTEForgeOfGods> adder() {
            return adder;
        }

        @Override
        public long count(MTEForgeOfGods controller) {
            return controller instanceof SteamForgeOfGods steamController ? steamController.getSteamModuleCount() : 0;
        }
    }
}
