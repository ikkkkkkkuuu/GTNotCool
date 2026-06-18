package com.xyp.gtnc.Common.machines.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlockAnyMeta;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlocksMap;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static com.xyp.gtnc.Client.utils.BlockIcons.OVERLAY_FRONT_SINGULARITY_DATA_HUB;
import static com.xyp.gtnc.Client.utils.BlockIcons.OVERLAY_FRONT_SINGULARITY_DATA_HUB_ACTIVE;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.ExoticEnergy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.chainAllGlasses;
import static gregtech.api.util.GTStructureUtility.ofOreDictBlockMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.xyp.gtnc.Loader.BlockLoader;
import com.xyp.gtnc.Loader.GTNCRecipeMaps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEEnhancedMultiBlockBase;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;

// #tr NameLargeSteamCombProcessor
// # Comb Processor
// # zh_CN 蜂窝处理机

// #tr LargeSteamCombProcessorRecipeType
// # Comb Processor
// # zh_CN 蜂窝处理

// #tr Tooltip_LargeSteamCombProcessor_00
// # Processes bee combs directly into final products
// # zh_CN 直接将蜂窝加工为最终产物

// #tr Tooltip_LargeSteamCombProcessor_01
// # Produces dust/gems for metal combs, fluids for gas combs, and special items for exotic combs
// # zh_CN 金属蜂窝产粉/宝石，气体蜂窝产流体，特殊蜂窝产特殊物品

// #tr Tooltip_LargeSteamCombProcessor_Casing
// # Solid Steel Machine Casing
// # zh_CN 固态钢机器外壳

public class LargeCombProcessor extends MTEEnhancedMultiBlockBase<LargeCombProcessor>
    implements ISurvivalConstructable {

    public LargeCombProcessor(String aName) {
        super(aName);
    }

    public LargeCombProcessor(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LargeCombProcessor(this.mName);
    }

    public String getMachineType() {
        return StatCollector.translateToLocal("LargeSteamCombProcessorRecipeType");
    }

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final int HORIZONTAL_OFF_SET = 7;
    private static final int VERTICAL_OFF_SET = 8;
    private static final int DEPTH_OFF_SET = 0;

    private int mCountCasing = 0;

    private IStructureDefinition<LargeCombProcessor> STRUCTURE_DEFINITION = null;

    // 15 wide (x), 17 tall (y), 15 deep (z)
    // A=glass, B=dirt/grass, G=casing+hatches, H=wood planks, I=wood slabs, J/K/L/N/O/P=bronze frame
    private final String[][] shape = transpose(
        new String[][] {
            { "               ", "               ", "               ", "      HHH      ", "    HHAAAHH    ",
                "    HAPLPAH    ", "   HAPAAAPAH   ", "   HALAAALAH   ", "   HAPAAAPAH   ", "    HAPLPAH    ",
                "    HHAAAHH    ", "      HHH      ", "               ", "               ", "               " },
            { "               ", "               ", "      GGG      ", "    GG   GG    ", "   G       G   ",
                "   G       G   ", "  G         G  ", "  G         G  ", "  G         G  ", "   G       G   ",
                "   G       G   ", "    GG   GG    ", "      GGG      ", "               ", "               " },
            { "               ", "      HHH      ", "   HHH   HHH   ", "  HG       GH  ", "  H         H  ",
                "  H         H  ", " H           H ", " H           H ", " H           H ", "  H         H  ",
                "  H         H  ", "  HG       GH  ", "   HHH   HHH   ", "      HHH      ", "               " },
            { "      GGG      ", "   GGG   GGG   ", "  G         G  ", " G           G ", " G           G ",
                " G           G ", "G             G", "G             G", "G             G", " G           G ",
                " G           G ", " G           G ", "  G         G  ", "   GGG   GGG   ", "      GGG      " },
            { "      AAA      ", "   OLA   ALO   ", "  P         P  ", " O           O ", " L           L ",
                " A           A ", "A             A", "A             A", "A             A", " A           A ",
                " L           L ", " O           O ", "  P         P  ", "   OLA   ALO   ", "      AAA      " },
            { "     AAAAA     ", "   NA     AO   ", "  P         P  ", " N           O ", " A           A ",
                "A             A", "A     III     A", "A     III     A", "A     III     A", "A             A",
                " A           A ", " N           N ", "  P         P  ", "   NA     AN   ", "     AAAAA     " },
            { "     AAAAA     ", "   NA     AO   ", "  P         P  ", " N           O ", " A           A ",
                "A             A", "A     JJJ     A", "A     JKJ     A", "A     JJJ     A", "A             A",
                " A           A ", " N           N ", "  P         P  ", "   NA     AN   ", "     AAAAA     " },
            { "      AAA      ", "   OLA   ALO   ", "  P         P  ", " O           O ", " L           L ",
                " A           A ", "A             A", "A             A", "A             A", " A           A ",
                " L           L ", " O           O ", "  P         P  ", "   OLA   ALO   ", "      AAA      " },
            { "      G~G      ", "   GGGBBBGGG   ", "  GBB     BBG  ", " GBB       BBG ", " GB         BG ",
                " G           G ", "GB           BG", "GB           BG", "GB           BG", " G           G ",
                " GB         BG ", " GBB       BBG ", "  GBB     BBG  ", "   GGGBBBGGG   ", "      GGG      " },
            { "      HHH      ", "    HHBBBHH    ", "  HHBBBBBBBHH  ", "  HBBB   BBBH  ", " HBB       BBH ",
                " HBB BBBBB BBH ", "HBB  BBBBBB BBH", "HBB BBBBBBB BBH", "HBB BBBBBB  BBH", " HB  BBBBB BBH ",
                " HBB   BB BBH ", "  HBBB    BBH  ", "  HHBBBBBBBHH  ", "    HHBBBHH    ", "      HHH      " },
            { "               ", "     GGGGG     ", "   GGBBBBBGG   ", "  GBBBBBBBBBG  ", "  GBBBBBBBBBG  ",
                " GBBBBBBBBBBBG ", " GBBBBBBBBBBBG ", " GBBBBBBBBBBBG ", " GBBBBBBBBBBBG ", " GBBBBBBBBBBBG ",
                "  GBBBBBBBBBG  ", "  GBBBBBBBBBG  ", "   GGBBBBBGG   ", "     GGGGG     ", "               " },
            { "               ", "      HHH      ", "    HHBBBHH    ", "   HBBBBBBBH   ", "  HBBBBBBBBBH  ",
                "  HBBBBBBBBBH  ", " HBBBBBBBBBBBH ", " HBBBBBBBBBBBH ", " HBBBBBBBBBBBH ", "  HBBBBBBBBBH  ",
                "  HBBBBBBBBBH  ", "   HBBBBBBBH   ", "    HHBBBHH    ", "      HHH      ", "               " },
            { "               ", "               ", "      GGG      ", "    GGBBBGG    ", "   GBBBBBBBG   ",
                "   GBBBBBBBG   ", "  GBBBBBBBBBG  ", "  GBBBBBBBBBG  ", "  GBBBBBBBBBG  ", "   GBBBBBBBG   ",
                "   GBBBBBBBG   ", "    GGBBBGG    ", "      GGG      ", "               ", "               " },
            { "               ", "               ", "       H       ", "     HHBHH     ", "    HBBBBBH    ",
                "   HBBBBBBBH   ", "   HBBBBBBBH   ", "  HBBBBBBBBBH  ", "   HBBBBBBBH   ", "   HBBBBBBBH   ",
                "    HBBBBBH    ", "     HHBHH     ", "       H       ", "               ", "               " },
            { "               ", "               ", "               ", "       G       ", "     GGBGG     ",
                "    GBBBBBG    ", "    GBBBBBG    ", "   GBBBBBBBG   ", "    GBBBBBG    ", "    GBBBBBG    ",
                "     GGBGG     ", "       G       ", "               ", "               ", "               " },
            { "               ", "               ", "               ", "               ", "      HHH      ",
                "     HHHHH     ", "    HHBBBHH    ", "    HHBBBHH    ", "    HHBBBHH    ", "     HHHHH     ",
                "      HHH      ", "               ", "               ", "               ", "               " },
            { "               ", "               ", "               ", "               ", "               ",
                "               ", "      GGG      ", "      GHG      ", "      GGG      ", "               ",
                "               ", "               ", "               ", "               ", "               " } });

    @Override
    public IStructureDefinition<LargeCombProcessor> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<LargeCombProcessor>builder()
                .addShape(STRUCTURE_PIECE_MAIN, shape)
                .addElement('A', chainAllGlasses())
                .addElement('B', ofChain(ofBlockAnyMeta(Blocks.dirt, 0), ofBlock(Blocks.grass, 0)))
                .addElement(
                    'G',
                    ofChain(
                        buildHatchAdder(LargeCombProcessor.class)
                            .atLeast(InputBus, OutputBus, InputHatch, OutputHatch, Energy.or(ExoticEnergy), Maintenance)
                            .casingIndex(getCasingTextureID())
                            .hint(1)
                            .build(),
                        onElementPass(x -> ++x.mCountCasing, ofBlock(BlockLoader.metaCasing02, 4))))
                .addElement('H', ofBlocksMap(ofOreDictBlockMap("plankWood"), Blocks.planks, 0))
                .addElement('I', ofBlocksMap(ofOreDictBlockMap("slabWood"), Blocks.wooden_slab, 0))
                .addElement('J', ofBlock(GregTechAPI.sBlockFrames, (int) Materials.Bronze.mMetaItemSubID))
                .addElement('K', ofBlock(GregTechAPI.sBlockFrames, (int) Materials.Bronze.mMetaItemSubID))
                .addElement('L', ofBlock(GregTechAPI.sBlockFrames, (int) Materials.Bronze.mMetaItemSubID))
                .addElement('N', ofBlock(GregTechAPI.sBlockFrames, (int) Materials.Bronze.mMetaItemSubID))
                .addElement('O', ofBlock(GregTechAPI.sBlockFrames, (int) Materials.Bronze.mMetaItemSubID))
                .addElement('P', ofBlock(GregTechAPI.sBlockFrames, (int) Materials.Bronze.mMetaItemSubID))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        mCountCasing = 0;
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;
        checkCasingMin(errors, mCountCasing, 1);
    }

    // ==================== 纹理 ====================

    public int getCasingTextureID() {
        return GTUtility.getTextureId((byte) 116, (byte) 36);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        int id = getCasingTextureID();
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id), TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_SINGULARITY_DATA_HUB_ACTIVE)
                .extFacing()
                .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id), TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_SINGULARITY_DATA_HUB)
                .extFacing()
                .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id) };
    }

    // ==================== 并行 ====================

    @Override
    public int getMaxParallelRecipes() {
        return 256;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNCRecipeMaps.SteamCombProcessingRecipes;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic().enablePerfectOverclock()
            .setMaxParallel(256);
    }

    // ==================== 信息显示 ====================

    @Override
    public String[] getInfoData() {
        ArrayList<String> info = new ArrayList<>(Arrays.asList(super.getInfoData()));
        info.add(
            StatCollector.translateToLocalFormatted(
                "GT5U.multiblock.curparallelism",
                "" + EnumChatFormatting.YELLOW + getMaxParallelRecipes()));
        return info.toArray(new String[0]);
    }

    // ==================== 多块构建 ====================

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (this.mMachine) return -1;
        return survivalBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET,
            elementBudget,
            env,
            false,
            true);
    }

    // ==================== Tooltip ====================

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("LargeSteamCombProcessorRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamCombProcessor_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamCombProcessor_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_PerfectOverclock"))
            .beginStructureBlock(15, 17, 15, false)
            .addController("Front center")
            .addInputBus(StatCollector.translateToLocal("Tooltip_LargeSteamCombProcessor_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_LargeSteamCombProcessor_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_LargeSteamCombProcessor_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_LargeSteamCombProcessor_Casing"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_LargeSteamCombProcessor_Casing"), 1)
            .toolTipFinisher();
        return tt;
    }

    // ==================== 音效 ====================

    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.IC2_MACHINES_INDUCTION_LOOP;
    }
}
