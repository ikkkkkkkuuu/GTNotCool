package com.xyp.gtnc.Common.recipe.gtnc;

import static thaumcraft.common.config.ConfigItems.itemJarNode;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.gtnewhorizon.gtnhlib.util.data.ItemId;
import com.xyp.gtnc.Common.recipe.thaumcraft.TCRecipeTools;
import com.xyp.gtnc.Loader.GTNCRecipeMaps;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.registry.GameRegistry;
import fox.spiteful.avaritia.items.LudicrousItems;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Mods;
import gregtech.api.enums.TierEU;
import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.util.GTModHandler;

public class InfusionCraftingRecipes {

    public static final ItemStack IC2_MACHINE = GTModHandler.getModItem(Mods.IndustrialCraft2.ID, "blockMachine", 1, 1);

    public static final ItemStack BLAST_FURNACE_TEMPLATE = GTModHandler
        .getModItem(Mods.EtFuturumRequiem.ID, "blast_furnace", 1);

    public static final Set<ItemId> UNCONSUMED_ITEMS = new HashSet<>();

    static {
        if (Mods.Avaritia.isModLoaded()) addAvaritia();

        if (Mods.BloodMagic.isModLoaded()) {
            UNCONSUMED_ITEMS.add(ItemId.create(GTModHandler.getModItem(Mods.BloodMagic.ID, "weakBloodOrb", 1)));
            UNCONSUMED_ITEMS.add(ItemId.create(GTModHandler.getModItem(Mods.BloodMagic.ID, "apprenticeBloodOrb", 1)));
            UNCONSUMED_ITEMS.add(ItemId.create(GTModHandler.getModItem(Mods.BloodMagic.ID, "magicianBloodOrb", 1)));
            UNCONSUMED_ITEMS.add(ItemId.create(GTModHandler.getModItem(Mods.BloodMagic.ID, "masterBloodOrb", 1)));
            UNCONSUMED_ITEMS.add(ItemId.create(GTModHandler.getModItem(Mods.BloodMagic.ID, "archmageBloodOrb", 1)));
            UNCONSUMED_ITEMS.add(ItemId.create(GTModHandler.getModItem(Mods.BloodMagic.ID, "transcendentBloodOrb", 1)));
            UNCONSUMED_ITEMS.add(ItemId.create(GTModHandler.getModItem(Mods.BloodMagic.ID, "creativeFiller", 1)));
        }
        if (Mods.ForbiddenMagic.isModLoaded()) {
            UNCONSUMED_ITEMS.add(ItemId.create(GTModHandler.getModItem(Mods.ForbiddenMagic.ID, "EldritchOrb", 1)));
        }
        UNCONSUMED_ITEMS.add(ItemId.create(GTModHandler.getModItem(Mods.Thaumcraft.ID, "FocusWarding", 1)));
    }

    @Optional.Method(modid = "Avaritia")
    public static void addAvaritia() {
        UNCONSUMED_ITEMS.add(ItemId.create(new ItemStack(LudicrousItems.bigPearl)));
        UNCONSUMED_ITEMS.add(ItemId.create(new ItemStack(LudicrousItems.armok_orb)));
    }

    public static ItemStack[] checkInputSpecial(ItemStack... itemStacks) {
        final int len = itemStacks.length;
        ItemStack[] copy = new ItemStack[len];

        for (int idx = 0; idx < len; idx++) {
            ItemStack i = itemStacks[idx];
            if (i == null) {
                copy[idx] = null;
                continue;
            }

            ItemStack out = i.copy();

            if (out.getItem() == IC2_MACHINE.getItem() && BLAST_FURNACE_TEMPLATE != null) {
                ItemStack bf = BLAST_FURNACE_TEMPLATE.copy();
                bf.stackSize = out.stackSize;
                out = bf;
            }

            if (UNCONSUMED_ITEMS.contains(ItemId.create(out))) {
                out.stackSize = 0;
            }

            copy[idx] = out;
        }

        return copy;
    }

    public static Set<Item> skips;

    public static boolean shouldSkip(Item item) {
        if (null == skips) {
            skips = new HashSet<>();
            skips.add(itemJarNode);
            if (Mods.ThaumicBases.isModLoaded()) {
                Item revolver = GameRegistry.findItem(Mods.ThaumicBases.ID, "revolver");
                if (null != revolver) {
                    skips.add(revolver);
                }
            }
            if (Mods.Gadomancy.isModLoaded()) {
                Item itemEtherealFamiliar = GameRegistry.findItem(Mods.Gadomancy.ID, "ItemEtherealFamiliar");
                if (null != itemEtherealFamiliar) {
                    skips.add(itemEtherealFamiliar);
                }
            }
        }

        return skips.contains(item);
    }

    public static void loadRecipes() {
        TCRecipeTools.getInfusionCraftingRecipe();

        IRecipeMap IIC = GTNCRecipeMaps.IndustrialInfusionCraftingRecipes;
        for (TCRecipeTools.InfusionCraftingRecipe Recipe : TCRecipeTools.ICR) {
            if (shouldSkip(
                Recipe.getOutput()
                    .getItem()))
                continue;

            GTValues.RA.stdBuilder()
                .ignoreCollision()
                .clearInvalid()
                .itemInputsUnified(checkInputSpecial(Recipe.getInputItem()))
                .itemOutputs((Recipe.getOutput()))
                .fluidInputs()
                .fluidOutputs()
                .duration(20)
                .eut(TierEU.RECIPE_LV)
                .addTo(IIC);
        }
    }
}
