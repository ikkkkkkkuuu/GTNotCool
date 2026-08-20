package com.xyp.gtnc.Common.machines.crop;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import com.gtnewhorizon.cropsnh.api.IBreedingRequirement;
import com.gtnewhorizon.cropsnh.api.ICropCard;
import com.gtnewhorizon.cropsnh.api.ICropMutation;
import com.gtnewhorizon.cropsnh.api.IMachineBreedingRequirement;
import com.gtnewhorizon.cropsnh.farming.requirements.BlockUnderRequirement;
import com.gtnewhorizon.cropsnh.utility.CropsNHUtils;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;

public final class CropMutationMachineRequirements {

    private CropMutationMachineRequirements() {}

    @Nullable
    public static int[] canBreedIgnoringBlockUnder(ICropMutation mutation, ArrayList<ICropCard> parents,
        IGregTechTileEntity te, ItemStack[] catalysts) {
        int[] consumptionTracker = new int[catalysts.length];
        for (IBreedingRequirement requirement : mutation.getRequirements()) {
            if (requirement instanceof BlockUnderRequirement) continue;
            if (!(requirement instanceof IMachineBreedingRequirement machineRequirement)) continue;
            if (!machineRequirement.canBreed(parents, te, catalysts, consumptionTracker)) return null;
        }
        return consumptionTracker;
    }

    public static List<List<ItemStack>> getCatalystsForNEIIgnoringBlockUnder(ICropMutation mutation) {
        List<List<ItemStack>> result = new LinkedList<>();
        for (IBreedingRequirement requirement : mutation.getRequirements()) {
            if (requirement instanceof BlockUnderRequirement) continue;
            if (!(requirement instanceof IMachineBreedingRequirement machineRequirement)) continue;

            List<ItemStack> catalysts = machineRequirement.getMachineOnlyCatalystsForNEI();
            if (catalysts == null || catalysts.isEmpty()) continue;

            CropsNHUtils.deduplicateItemList(catalysts);
            result.add(catalysts);
        }
        return result;
    }
}
