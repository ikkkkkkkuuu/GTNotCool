package com.xyp.gtnc.Common.recipe.gtnc;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import com.xyp.gtnc.Loader.GTNCRecipeMaps;

import appeng.api.AEApi;
import bartworks.system.material.WerkstoffLoader;
import goodgenerator.items.GGMaterial;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.fluids.GTPPFluids;
import gtPlusPlus.core.material.MaterialMisc;
import gtPlusPlus.core.material.MaterialsAlloy;
import gtPlusPlus.core.material.MaterialsOres;

public class MiningRigRecipes {

    /** Helper: add one recipe for given circuit + fuel → ore outputs */
    private static void addMR(RecipeMap<?> map, int circuit, FluidStack fuel, long eut, int duration, int tier,
        ItemStack... outputs) {
        if (fuel == null) return;
        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(circuit))
            .itemOutputs(outputs)
            .fluidInputs(fuel)
            .eut(eut)
            .duration(duration)
            .specialValue(tier)
            .addTo(map);
    }

    /** Helper: add recipes for both fuels */
    private static void addMRs(RecipeMap<?> map, int circuit, FluidStack[] fuels, int[] durations, long eut, int tier,
        ItemStack... outputs) {
        for (int i = 0; i < fuels.length; i++) {
            addMR(map, circuit, fuels[i], eut, durations[i], tier, outputs);
        }
    }

    private static ItemStack ore(Materials m, int amount) {
        return GTUtility.copyAmountUnsafe(amount, GTOreDictUnificator.get(OrePrefixes.ore, m, 1));
    }

    public static void loadRecipes() {
        RecipeMap<?> MRR = GTNCRecipeMaps.MiningRigRecipes;

        // ============ Fuels ============
        FluidStack[] fuels = { Materials.GasolinePremium.getFluid(100000),
            new FluidStack(GTPPFluids.RP1RocketFuel, 60000) };
        int[] durations = { 600, 400 };

        // ============ Circuit 24 — Tier 1 ============
        addMRs(
            MRR,
            24,
            fuels,
            durations,
            TierEU.RECIPE_UV,
            1,
            ore(Materials.Naquadah, 180),
            ore(Materials.Chromite, 120),
            ore(Materials.Plutonium, 60),
            ore(Materials.NaquadahEnriched, 30),
            ore(Materials.Trinium, 90),
            ore(Materials.Indium, 30),
            ore(Materials.Draconium, 120),
            ore(Materials.DraconiumAwakened, 80),
            ore(Materials.ElectrumFlux, 50));

        // ============ Circuit 23 — Tier 1 ============
        addMRs(
            MRR,
            23,
            fuels,
            durations,
            TierEU.RECIPE_UV,
            1,
            ore(Materials.Scheelite, 120),
            ore(Materials.Tungstate, 80),
            ore(Materials.Lithium, 40),
            ore(Materials.Tellurium, 20),
            ore(Materials.Tungsten, 30),
            ore(Materials.Pitchblende, 180),
            ore(Materials.Bismuth, 120),
            ore(Materials.Antimony, 90),
            ore(Materials.Gallium, 70));

        // ============ Circuit 22 — Tier 3 ============
        addMRs(
            MRR,
            22,
            fuels,
            durations,
            TierEU.RECIPE_UEV,
            3,
            ore(Materials.Garnierite, 120),
            ore(Materials.Nickel, 80),
            ore(Materials.Cobaltite, 80),
            ore(Materials.Pentlandite, 40),
            ore(Materials.Platinum, 40),
            ore(Materials.Palladium, 20),
            ore(Materials.Rutile, 60),
            GTUtility.copyAmountUnsafe(300, new ItemStack(Blocks.clay, 1)),
            MaterialsOres.CROCROITE.getOre(1));

        // ============ Circuit 21 — Tier 1 ============
        addMRs(
            MRR,
            21,
            fuels,
            durations,
            TierEU.RECIPE_UV,
            1,
            GTUtility.copyAmountUnsafe(60, WerkstoffLoader.Bornite.get(OrePrefixes.ore, 1)),
            GTUtility.copyAmountUnsafe(40, WerkstoffLoader.PTMetallicPowder.get(OrePrefixes.ore, 1)),
            ore(Materials.Graphite, 120),
            ore(Materials.Diamond, 80),
            ore(Materials.Coal, 40),
            ore(Materials.Titanium, 40),
            ore(Materials.Ardite, 80),
            ore(Materials.Manyullyn, 80),
            ore(Materials.Opal, 120));

        // ============ Circuit 20 — Tier 3 ============
        addMRs(
            MRR,
            20,
            fuels,
            durations,
            TierEU.RECIPE_UEV,
            3,
            ore(Materials.Oilsands, 240),
            ore(Materials.Gold, 60),
            ore(Materials.InfusedGold, 80),
            ore(Materials.Bauxite, 160),
            ore(Materials.Ilmenite, 80),
            ore(Materials.Aluminium, 80),
            ore(Materials.BlueTopaz, 120),
            ore(Materials.Osmium, 20),
            ore(Materials.Iridium, 20));

        // ============ Circuit 19 — Tier 1 ============
        addMRs(
            MRR,
            19,
            fuels,
            durations,
            TierEU.RECIPE_UV,
            1,
            ore(Materials.Magnetite, 180),
            ore(Materials.VanadiumMagnetite, 120),
            ore(Materials.CassiteriteSand, 240),
            ore(Materials.GarnetSand, 160),
            ore(Materials.Asbestos, 160),
            ore(Materials.Diatomite, 80),
            ore(Materials.Shadow, 80),
            ore(Materials.NetherStar, 40),
            ore(Materials.Quantium, 60));

        // ============ Circuit 18 — Tier 1 ============
        addMRs(
            MRR,
            18,
            fuels,
            durations,
            TierEU.RECIPE_UV,
            1,
            ore(Materials.Apatite, 120),
            ore(Materials.TricalciumPhosphate, 80),
            ore(Materials.Pyrochlore, 40),
            ore(Materials.Sulfur, 300),
            ore(Materials.Pyrite, 200),
            ore(Materials.Sphalerite, 100),
            ore(Materials.Lignite, 150),
            ore(Materials.Cadmium, 80),
            ore(Materials.Zinc, 120));

        // ============ Circuit 17 — Tier 1 ============
        addMRs(
            MRR,
            17,
            fuels,
            durations,
            TierEU.RECIPE_UV,
            1,
            ore(Materials.Redstone, 180),
            ore(Materials.Ruby, 120),
            ore(Materials.Cinnabar, 60),
            ore(Materials.NetherQuartz, 240),
            ore(Materials.Quartzite, 80),
            ore(Materials.Lanthanum, 50),
            ore(Materials.Niobium, 50),
            ore(Materials.Ytterbium, 50),
            ore(Materials.Yttrium, 50));

        // ============ Circuit 16 — Tier 1 ============
        addMRs(
            MRR,
            16,
            fuels,
            durations,
            TierEU.RECIPE_UV,
            1,
            ore(Materials.RockSalt, 150),
            ore(Materials.Salt, 10),
            ore(Materials.Lepidolite, 50),
            ore(Materials.Spodumene, 50),
            GTUtility.copyAmountUnsafe(140, WerkstoffLoader.Djurleit.get(OrePrefixes.ore, 1)),
            GTUtility.copyAmountUnsafe(70, WerkstoffLoader.Bornite.get(OrePrefixes.ore, 1)),
            ore(Materials.Silicon, 80),
            ore(Materials.SiliconSG, 50),
            ore(Materials.Saltpeter, 120));

        // ============ Circuit 15 — Tier 1 ============
        addMRs(
            MRR,
            15,
            fuels,
            durations,
            TierEU.RECIPE_UV,
            1,
            ore(Materials.BlueTopaz, 210),
            ore(Materials.Topaz, 140),
            ore(Materials.BasalticMineralSand, 240),
            ore(Materials.GraniticMineralSand, 160),
            ore(Materials.FullersEarth, 160),
            ore(Materials.Gypsum, 80),
            ore(Materials.Phosphate, 120),
            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Void, 80),
            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Thaumium, 80));

        // ============ Circuit 14 — Tier 3 ============
        addMRs(
            MRR,
            14,
            fuels,
            durations,
            TierEU.RECIPE_UEV,
            3,
            ore(Materials.Barite, 40),
            ore(Materials.GarnetRed, 120),
            ore(Materials.GarnetYellow, 80),
            ore(Materials.Amethyst, 80),
            ore(Materials.GreenSapphire, 40),
            GTUtility.copyAmountUnsafe(20, WerkstoffLoader.Roquesit.get(OrePrefixes.ore, 1)),
            ore(Materials.Naquadria, 40),
            ore(Materials.MysteriousCrystal, 40),
            ore(Materials.Oriharukon, 40));

        // ============ Circuit 13 — Tier 4 ============
        addMRs(
            MRR,
            13,
            fuels,
            durations,
            TierEU.RECIPE_UIV,
            4,
            ore(Materials.Desh, 60),
            ore(Materials.CertusQuartz, 80),
            ore(Materials.BrownLimonite, 90),
            ore(Materials.Cassiterite, 160),
            ore(Materials.BandedIron, 60),
            ore(Materials.Gold, 30),
            GGMaterial.orundum.get(OrePrefixes.ore, 1),
            ore(Materials.Uranium, 90),
            ore(Materials.Uranium235, 60));

        // ============ Circuit 12 — Tier 1 ============
        addMRs(
            MRR,
            12,
            fuels,
            durations,
            TierEU.RECIPE_UV,
            1,
            MaterialsOres.ZIRCON.getOre(1),
            ore(Materials.YellowLimonite, 60),
            ore(Materials.Kyanite, 60),
            ore(Materials.Mica, 40),
            ore(Materials.Bauxite, 40),
            ore(Materials.Almandine, 20),
            ore(Materials.Plutonium241, 80),
            GTUtility.copyAmountUnsafe(60, WerkstoffLoader.Tiberium.get(OrePrefixes.ore, 1)),
            GTUtility.copyAmountUnsafe(40, WerkstoffLoader.Thorianit.get(OrePrefixes.ore, 1)));

        // ============ Circuit 11 — Tier 1 ============
        addMRs(
            MRR,
            11,
            fuels,
            durations,
            TierEU.RECIPE_UV,
            1,
            ore(Materials.Galena, 120),
            ore(Materials.Silver, 80),
            ore(Materials.Lead, 40),
            ore(Materials.Molybdenite, 100),
            ore(Materials.Molybdenum, 50),
            ore(Materials.Magnesium, 50),
            ore(Materials.Manganese, 80),
            GTUtility.copyAmountUnsafe(50, WerkstoffLoader.Fluorspar.get(OrePrefixes.ore, 1)),
            ore(Materials.Vanadium, 80));

        // ============ Circuit 10 — Tier 3 ============
        addMRs(
            MRR,
            10,
            fuels,
            durations,
            TierEU.RECIPE_UEV,
            3,
            ore(Materials.Lazurite, 120),
            ore(Materials.Sodalite, 80),
            ore(Materials.Lapis, 80),
            ore(Materials.Calcite, 40),
            ore(Materials.Wulfenite, 150),
            ore(Materials.Quantium, 40),
            ore(Materials.Europium, 40),
            ore(Materials.Samarium, 40),
            ore(Materials.Strontium, 40));

        // ============ Circuit 9 — Tier 3 ============
        addMRs(
            MRR,
            9,
            fuels,
            durations,
            TierEU.RECIPE_UEV,
            3,
            ore(Materials.Grossular, 60),
            ore(Materials.Pyrolusite, 40),
            ore(Materials.Tantalite, 20),
            ore(Materials.Magnetite, 240),
            ore(Materials.VanadiumMagnetite, 160),
            ore(Materials.Gold, 80),
            ore(Materials.Endium, 60),
            GTModHandler.getModItem(Mods.HardcoreEnderExpansion.ID, "end_powder_ore", 1),
            ore(Materials.Cheese, 240));

        // ============ Circuit 8 — Tier 1 ============
        addMRs(
            MRR,
            8,
            fuels,
            durations,
            TierEU.RECIPE_UV,
            1,
            ore(Materials.Beryllium, 90),
            ore(Materials.Emerald, 120),
            ore(Materials.Chalcopyrite, 40),
            ore(Materials.Iron, 160),
            ore(Materials.Pyrite, 160),
            ore(Materials.Copper, 160),
            ore(Materials.Arsenic, 90),
            ore(Materials.Barium, 90),
            ore(Materials.Lepidolite, 50));

        // ============ Circuit 7 — Tier 3 ============
        addMRs(
            MRR,
            7,
            fuels,
            durations,
            TierEU.RECIPE_UEV,
            3,
            ore(Materials.Saltpeter, 120),
            ore(Materials.Diatomite, 80),
            ore(Materials.Electrotine, 80),
            ore(Materials.Alunite, 40),
            ore(Materials.Coal, 240),
            ore(Materials.Rubidium, 40),
            ore(Materials.Ledox, 80),
            ore(Materials.CallistoIce, 80),
            ore(Materials.Borax, 80));

        // ============ Circuit 6 — Tier 4 ============
        addMRs(
            MRR,
            6,
            fuels,
            durations,
            TierEU.RECIPE_UIV,
            4,
            ore(Materials.Chalcopyrite, 250),
            ore(Materials.Zeolite, 10),
            ore(Materials.Cassiterite, 10),
            ore(Materials.Realgar, 50),
            ore(Materials.Mytryl, 60),
            AEApi.instance()
                .definitions()
                .blocks()
                .skyStone()
                .maybeStack(1)
                .orNull(),
            ore(Materials.Americium, 40),
            ore(Materials.Dilithium, 80),
            ore(Materials.MeteoricIron, 60));

        // ============ Circuit 5 — Tier 1 ============
        addMRs(
            MRR,
            5,
            fuels,
            durations,
            TierEU.RECIPE_UV,
            1,
            ore(Materials.Redstone, 180),
            ore(Materials.Ruby, 120),
            ore(Materials.Grossular, 60),
            ore(Materials.Spessartine, 40),
            ore(Materials.Draconium, 40),
            ore(Materials.Tantalite, 20),
            ore(Materials.Thulium, 60),
            ore(Materials.Tantalum, 40),
            ore(Materials.Lutetium, 40));

        // ============ Circuit 4 — Tier 1 ============
        addMRs(
            MRR,
            4,
            fuels,
            durations,
            TierEU.RECIPE_UV,
            1,
            ore(Materials.Soapstone, 120),
            ore(Materials.Talc, 80),
            ore(Materials.Glauconite, 80),
            ore(Materials.Pentlandite, 40),
            ore(Materials.Neodymium, 30),
            ore(Materials.Monazite, 60),
            ore(Materials.Adamantium, 40),
            ore(Materials.Vinteum, 40),
            MaterialsAlloy.KOBOLDITE.getOre(1));

        // ============ Circuit 3 — Tier 4 ============
        addMRs(
            MRR,
            3,
            fuels,
            durations,
            TierEU.RECIPE_UIV,
            4,
            ore(Materials.Bastnasite, 90),
            ore(Materials.Molybdenum, 30),
            ore(Materials.BrownLimonite, 60),
            ore(Materials.YellowLimonite, 240),
            ore(Materials.BandedIron, 240),
            ore(Materials.Malachite, 120),
            ore(Materials.Holmium, 30),
            ore(Materials.Ichorium, 30),
            ore(Materials.ShadowIron, 60));

        // ============ Circuit 2 — Tier 1 ============
        addMRs(
            MRR,
            2,
            fuels,
            durations,
            TierEU.RECIPE_UV,
            1,
            ore(Materials.Almandine, 180),
            ore(Materials.Pyrope, 120),
            ore(Materials.Sapphire, 60),
            ore(Materials.GreenSapphire, 60),
            ore(Materials.Stibnite, 70),
            ore(Materials.Uraninite, 120),
            ore(Materials.InfusedAir, 80),
            ore(Materials.InfusedEarth, 80),
            ore(Materials.InfusedFire, 80));

        // ============ Circuit 1 — Tier 1 ============
        addMRs(
            MRR,
            1,
            fuels,
            durations,
            TierEU.RECIPE_UV,
            1,
            ore(Materials.Tetrahedrite, 280),
            ore(Materials.Copper, 140),
            ore(Materials.Bentonite, 60),
            ore(Materials.Magnetite, 40),
            ore(Materials.Olivine, 40),
            ore(Materials.GlauconiteSand, 20),
            ore(Materials.InfusedWater, 80),
            ore(Materials.InfusedEntropy, 60),
            ore(Materials.InfusedOrder, 60));

        // ============ Circuit 25 — Tier 1 ============
        addMRs(
            MRR,
            25,
            fuels,
            durations,
            TierEU.RECIPE_UV,
            1,
            ore(Materials.Tin, 280),
            ore(Materials.Cerium, 60),
            ore(Materials.Promethium, 60),
            ore(Materials.Praseodymium, 60),
            ore(Materials.Scandium, 60),
            ore(Materials.Dysprosium, 60),
            ore(Materials.Jasper, 120),
            ore(Materials.Tanzanite, 120),
            ore(Materials.Vulcanite, 80));

        // ============ Circuit 26 — Tier 1 ============
        addMRs(
            MRR,
            26,
            fuels,
            durations,
            TierEU.RECIPE_UV,
            1,
            ore(Materials.Jade, 120),
            ore(Materials.Mithril, 60),
            ore(Materials.Tritanium, 40),
            ore(Materials.DarkIron, 80),
            ore(Materials.Firestone, 80),
            ore(Materials.Spinel, 60),
            ore(Materials.Duralumin, 100),
            ore(Materials.Forcicium, 60),
            ore(Materials.Forcillium, 60));

        // ============ Circuit 27 — Tier 1 ============
        addMRs(
            MRR,
            27,
            fuels,
            durations,
            TierEU.RECIPE_UV,
            1,
            ore(Materials.Orichalcum, 20),
            ore(Materials.Olivine, 80),
            ore(Materials.Vyroxeres, 80),
            ore(Materials.Perlite, 240),
            ore(Materials.Chrysotile, 120),
            ore(Materials.Trona, 120),
            ore(Materials.Mirabilite, 120),
            ore(Materials.DeepIron, 60),
            ore(Materials.Electrum, 80));

        // ============ Circuit 28 — Tier 1 ============
        addMRs(
            MRR,
            28,
            fuels,
            durations,
            TierEU.RECIPE_UV,
            1,
            MaterialsOres.LAUTARITE.getOre(1),
            MaterialsOres.LEPERSONNITE.getOre(1),
            MaterialsOres.GADOLINITE_Y.getOre(1),
            MaterialsOres.ZIRCON.getOre(1),
            MaterialsOres.HONEAITE.getOre(1),
            MaterialsOres.ALBURNITE.getOre(1),
            MaterialMisc.RARE_EARTH_LOW.getOre(1),
            MaterialMisc.RARE_EARTH_MID.getOre(1),
            MaterialMisc.RARE_EARTH_HIGH.getOre(1));

        // ============ Circuit 29 — Tier 1 ============
        addMRs(
            MRR,
            29,
            fuels,
            durations,
            TierEU.RECIPE_UV,
            1,
            GTUtility.copyAmountUnsafe(60, WerkstoffLoader.Bismutite.get(OrePrefixes.ore, 1)),
            GTUtility.copyAmountUnsafe(60, WerkstoffLoader.FluorBuergerit.get(OrePrefixes.ore, 1)),
            GTUtility.copyAmountUnsafe(60, WerkstoffLoader.DescloiziteCUVO4.get(OrePrefixes.ore, 1)),
            GTUtility.copyAmountUnsafe(60, WerkstoffLoader.DescloiziteZNVO4.get(OrePrefixes.ore, 1)),
            GTUtility.copyAmountUnsafe(60, WerkstoffLoader.Fayalit.get(OrePrefixes.ore, 1)),
            GTUtility.copyAmountUnsafe(60, WerkstoffLoader.Forsterit.get(OrePrefixes.ore, 1)),
            GTUtility.copyAmountUnsafe(60, WerkstoffLoader.FuchsitCR.get(OrePrefixes.ore, 1)),
            GTUtility.copyAmountUnsafe(60, WerkstoffLoader.FuchsitAL.get(OrePrefixes.ore, 1)),
            GTUtility.copyAmountUnsafe(60, WerkstoffLoader.Djurleit.get(OrePrefixes.ore, 1)));

        // ============ Circuit 30 — Tier 1 ============
        addMRs(
            MRR,
            30,
            fuels,
            durations,
            TierEU.RECIPE_UV,
            1,
            GTUtility.copyAmountUnsafe(60, WerkstoffLoader.Bornite.get(OrePrefixes.ore, 1)),
            GTUtility.copyAmountUnsafe(60, WerkstoffLoader.Wittichenit.get(OrePrefixes.ore, 1)),
            GTUtility.copyAmountUnsafe(60, WerkstoffLoader.ChromoAluminoPovondrait.get(OrePrefixes.ore, 1)),
            GTUtility.copyAmountUnsafe(60, WerkstoffLoader.VanadioOxyDravit.get(OrePrefixes.ore, 1)),
            GTUtility.copyAmountUnsafe(60, WerkstoffLoader.Olenit.get(OrePrefixes.ore, 1)),
            GTUtility.copyAmountUnsafe(60, WerkstoffLoader.RedZircon.get(OrePrefixes.ore, 1)),
            GTUtility.copyAmountUnsafe(60, WerkstoffLoader.Hedenbergit.get(OrePrefixes.ore, 1)),
            GTUtility.copyAmountUnsafe(60, WerkstoffLoader.Prasiolite.get(OrePrefixes.ore, 1)),
            GTUtility.copyAmountUnsafe(40, WerkstoffLoader.BArTiMaEuSNeK.get(OrePrefixes.ore, 1)));

        // ============ Circuit 31 — Tier 4 ============
        addMRs(
            MRR,
            31,
            fuels,
            durations,
            TierEU.RECIPE_UIV,
            4,
            ore(Materials.Tartarite, 20),
            ore(Materials.Neutronium, 20),
            ore(Materials.CosmicNeutronium, 20),
            ore(Materials.BlackPlutonium, 20),
            ore(Materials.Bedrockium, 20),
            ore(Materials.InfinityCatalyst, 20),
            ore(Materials.Ichorium, 20),
            ore(Materials.Flerovium, 20),
            ore(Materials.TengamRaw, 20));
    }
}
