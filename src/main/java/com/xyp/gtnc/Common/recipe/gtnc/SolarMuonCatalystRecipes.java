package com.xyp.gtnc.Common.recipe.gtnc;

import net.minecraftforge.fluids.FluidStack;

import com.xyp.gtnc.Loader.GTNCRecipeMaps;
import com.xyp.gtnc.utils.recipes.metadata.SolorMuonCatalystMetadata;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.material.MaterialsElements;
import gtPlusPlus.core.material.Particle;

public class SolarMuonCatalystRecipes {

    public static final RecipeMap<?> SMCR = GTNCRecipeMaps.SolarMuonCatalystRecipes;

    public static void loadRecipes() {

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.copyAmount(0, Particle.getBaseParticle(Particle.MUON)))
            .fluidInputs(
                Materials.Lutetium.getPlasma(2304),
                Materials.Uranium.getPlasma(2304),
                Materials.Europium.getPlasma(2304),
                Materials.Silicon.getPlasma(2304),
                Materials.Silver.getPlasma(2304),
                Materials.Indium.getPlasma(2304),
                Materials.Hydrogen.getPlasma(16000),
                Materials.Fluorine.getPlasma(16000))
            .fluidOutputs(Materials.QuarkGluonPlasma.getFluid(6400000))
            .eut(2013265192)
            .duration(200)
            .addTo(SMCR);

        GTValues.RA.stdBuilder()
            .metadata(SolorMuonCatalystMetadata.INSTANCE, true)
            .itemInputs(GTUtility.copyAmount(0, Particle.getBaseParticle(Particle.MUON)))
            .fluidInputs(
                Materials.Space.getMolten(100),
                Materials.Time.getMolten(50),
                Materials.Ichorium.getPlasma(1440),
                new FluidStack(MaterialsElements.STANDALONE.HYPOGEN.getPlasma(), 1440),
                Materials.Flerovium.getPlasma(1440),
                new FluidStack(MaterialsElements.STANDALONE.CHRONOMATIC_GLASS.getPlasma(), 1440),
                Materials.Bedrockium.getPlasma(1440),
                new FluidStack(MaterialsElements.STANDALONE.DRAGON_METAL.getPlasma(), 1440))
            .fluidOutputs(Materials.MagMatter.getMolten(64000))
            .eut(2013265192)
            .duration(200)
            .addTo(SMCR);

    }
}
