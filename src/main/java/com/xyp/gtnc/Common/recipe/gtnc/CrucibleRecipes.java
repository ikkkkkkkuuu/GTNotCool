package com.xyp.gtnc.Common.recipe.gtnc;

import com.xyp.gtnc.Loader.GTNCRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gtPlusPlus.core.material.MaterialsAlloy;

public class CrucibleRecipes {

    public static void loadRecipes() {

        RecipeMap<?> SCR = GTNCRecipeMaps.SteamCrucibleRecipes;

        // ==================== 坩埚配方 ====================

        // 朱砂 → 液态汞
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Cinnabar.getDust(1))
            .fluidOutputs(Materials.Mercury.getFluid(1000))
            .eut(30)
            .duration(100)
            .circuit(1)
            .specialValue(1)
            .addTo(SCR);

        // 熔融铁/铁粉 + 氧气 → 液态钢
        GTValues.RA.stdBuilder()
            .fluidInputs(Materials.Iron.getMolten(144), Materials.Oxygen.getGas(1000))
            .fluidOutputs(Materials.Steel.getMolten(144))
            .eut(30)
            .duration(50)
            .circuit(2)
            .specialValue(1)
            .addTo(SCR);

        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Iron.getDust(1))
            .fluidInputs(Materials.Oxygen.getGas(1000))
            .fluidOutputs(Materials.Steel.getMolten(144))
            .eut(30)
            .duration(100)
            .circuit(2)
            .specialValue(1)
            .addTo(SCR);

        // 铁矿石
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Magnetite.getDust(1))
            .fluidOutputs(Materials.Iron.getMolten(144))
            .eut(30)
            .duration(100)
            .circuit(1)
            .specialValue(1)
            .addTo(SCR);

        GTValues.RA.stdBuilder()
            .itemInputs(Materials.BandedIron.getDust(1))
            .fluidOutputs(Materials.Iron.getMolten(144))
            .eut(30)
            .duration(100)
            .circuit(1)
            .specialValue(1)
            .addTo(SCR);

        GTValues.RA.stdBuilder()
            .itemInputs(Materials.BrownLimonite.getDust(1))
            .fluidOutputs(Materials.Iron.getMolten(144))
            .eut(30)
            .duration(100)
            .circuit(1)
            .specialValue(1)
            .addTo(SCR);

        GTValues.RA.stdBuilder()
            .itemInputs(Materials.YellowLimonite.getDust(1))
            .fluidOutputs(Materials.Iron.getMolten(144))
            .eut(30)
            .duration(100)
            .circuit(1)
            .specialValue(1)
            .addTo(SCR);

        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Pyrite.getDust(1))
            .fluidOutputs(Materials.Iron.getMolten(144))
            .eut(30)
            .duration(100)
            .circuit(1)
            .specialValue(1)
            .addTo(SCR);

        // 孔雀石
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Malachite.getDust(1))
            .fluidOutputs(Materials.Copper.getMolten(144))
            .eut(30)
            .duration(100)
            .circuit(1)
            .specialValue(1)
            .addTo(SCR);

        // 黄铜矿
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Chalcopyrite.getDust(2))
            .fluidOutputs(Materials.Copper.getMolten(144), Materials.Iron.getMolten(144))
            .eut(30)
            .duration(100)
            .circuit(1)
            .specialValue(1)
            .addTo(SCR);

        // 熔融铁/铁粉
        GTValues.RA.stdBuilder()
            .fluidInputs(Materials.Iron.getMolten(144))
            .itemInputs(Materials.Coal.getDust(1))
            .fluidOutputs(Materials.CastIron.getMolten(144))
            .eut(30)
            .duration(50)
            .circuit(2)
            .specialValue(1)
            .addTo(SCR);

        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Iron.getDust(1), Materials.Coal.getDust(1))
            .fluidOutputs(Materials.CastIron.getMolten(144))
            .eut(30)
            .duration(100)
            .circuit(2)
            .specialValue(1)
            .addTo(SCR);

        GTValues.RA.stdBuilder()
            .fluidInputs(Materials.Iron.getMolten(144))
            .itemInputs(Materials.Carbon.getDust(1))
            .fluidOutputs(Materials.CastIron.getMolten(144))
            .eut(30)
            .duration(50)
            .circuit(2)
            .specialValue(1)
            .addTo(SCR);

        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Iron.getDust(1), Materials.Carbon.getDust(1))
            .fluidOutputs(Materials.CastIron.getMolten(144))
            .eut(30)
            .duration(100)
            .circuit(2)
            .specialValue(1)
            .addTo(SCR);

        // 闪锌矿
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Sphalerite.getDust(1))
            .itemOutputs(Materials.Gallium.getDust(1))
            .outputChances(800)
            .fluidOutputs(Materials.Zinc.getMolten(144))
            .eut(30)
            .duration(100)
            .circuit(1)
            .specialValue(1)
            .addTo(SCR);

        // 黝铜矿
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Tetrahedrite.getDust(8))
            .fluidOutputs(
                Materials.Copper.getMolten(432),
                Materials.Iron.getMolten(144),
                Materials.Antimony.getMolten(144))
            .eut(30)
            .duration(150)
            .circuit(1)
            .specialValue(1)
            .addTo(SCR);

        // 辉锑矿
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Stibnite.getDust(1))
            .fluidOutputs(Materials.Antimony.getMolten(144))
            .eut(30)
            .duration(100)
            .circuit(1)
            .specialValue(1)
            .addTo(SCR);

        // 锡石矿/锡石矿砂
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Cassiterite.getDust(1))
            .fluidOutputs(Materials.Tin.getMolten(144))
            .eut(30)
            .duration(100)
            .circuit(1)
            .specialValue(1)
            .addTo(SCR);

        GTValues.RA.stdBuilder()
            .itemInputs(Materials.CassiteriteSand.getDust(1))
            .fluidOutputs(Materials.Tin.getMolten(144))
            .eut(30)
            .duration(100)
            .circuit(1)
            .specialValue(1)
            .addTo(SCR);

        // 锡粉/金粉
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Tin.getDust(1))
            .fluidOutputs(Materials.Tin.getMolten(144))
            .eut(16)
            .duration(50)
            .circuit(1)
            .specialValue(1)
            .addTo(SCR);

        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Gold.getDust(1))
            .fluidOutputs(Materials.Gold.getMolten(144))
            .eut(16)
            .duration(50)
            .circuit(1)
            .specialValue(1)
            .addTo(SCR);

        // 镍黄铁矿
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Pentlandite.getDust(17))
            .fluidOutputs(Materials.Nickel.getMolten(1296))
            .eut(30)
            .duration(200)
            .circuit(1)
            .specialValue(1)
            .addTo(SCR);

        // 花岗岩矿砂/玄武岩矿砂
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.GraniticMineralSand.getDust(1))
            .fluidOutputs(Materials.Iron.getMolten(144))
            .eut(30)
            .duration(100)
            .circuit(1)
            .specialValue(1)
            .addTo(SCR);

        GTValues.RA.stdBuilder()
            .itemInputs(Materials.BasalticMineralSand.getDust(1))
            .fluidOutputs(Materials.Iron.getMolten(144))
            .eut(30)
            .duration(100)
            .circuit(1)
            .specialValue(1)
            .addTo(SCR);

        // ==================== 合金配方 ====================

        // 铜+锌 → 黄铜
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Copper.getDust(3), Materials.Zinc.getDust(1))
            .fluidOutputs(Materials.Brass.getMolten(576))
            .eut(30)
            .duration(100)
            .circuit(1)
            .specialValue(1)
            .addTo(SCR);

        // 硼+玻璃 → 硼玻璃
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Boron.getDust(1), Materials.Glass.getDust(7))
            .fluidOutputs(Materials.BorosilicateGlass.getMolten(1152))
            .eut(30)
            .duration(100)
            .circuit(2)
            .specialValue(1)
            .addTo(SCR);

        // 硫 + 生橡胶 → 橡胶
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Sulfur.getDust(1), Materials.RubberRaw.getDust(9))
            .fluidOutputs(Materials.Rubber.getMolten(1296))
            .eut(30)
            .duration(100)
            .circuit(2)
            .specialValue(1)
            .addTo(SCR);

        // 红色合金 RedAlloy
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Copper.getDust(1), Materials.Redstone.getDust(4))
            .fluidOutputs(Materials.RedAlloy.getMolten(144))
            .eut(30)
            .duration(100)
            .circuit(2)
            .specialValue(1)
            .addTo(SCR);

        GTValues.RA.stdBuilder()
            .fluidInputs(Materials.Copper.getMolten(144))
            .itemInputs(Materials.Redstone.getDust(4))
            .fluidOutputs(Materials.RedAlloy.getMolten(144))
            .eut(30)
            .duration(50)
            .circuit(2)
            .specialValue(1)
            .addTo(SCR);

        // 红石合金 RedstoneAlloy
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Silicon.getDust(1), Materials.Carbon.getDust(1), Materials.Redstone.getDust(1))
            .fluidOutputs(Materials.RedstoneAlloy.getMolten(432))
            .eut(30)
            .duration(50)
            .circuit(2)
            .specialValue(1)
            .addTo(SCR);

        // 青铜 Bronze
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Tin.getDust(1), Materials.Copper.getDust(3))
            .fluidOutputs(Materials.Bronze.getMolten(576))
            .eut(30)
            .duration(100)
            .circuit(2)
            .specialValue(1)
            .addTo(SCR);

        GTValues.RA.stdBuilder()
            .fluidInputs(Materials.Tin.getMolten(144), Materials.Copper.getMolten(432))
            .fluidOutputs(Materials.Bronze.getMolten(576))
            .eut(30)
            .duration(50)
            .circuit(2)
            .specialValue(1)
            .addTo(SCR);

        // 白铜
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Copper.getDust(1), Materials.Nickel.getDust(1))
            .fluidOutputs(Materials.Cupronickel.getMolten(288))
            .eut(30)
            .duration(100)
            .circuit(2)
            .specialValue(1)
            .addTo(SCR);

        GTValues.RA.stdBuilder()
            .fluidInputs(Materials.Copper.getMolten(144), Materials.Nickel.getMolten(144))
            .fluidOutputs(Materials.Cupronickel.getMolten(288))
            .eut(30)
            .duration(50)
            .circuit(2)
            .specialValue(1)
            .addTo(SCR);

        // 殷钢
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Iron.getDust(2), Materials.Nickel.getDust(1))
            .fluidOutputs(Materials.Invar.getMolten(432))
            .eut(30)
            .duration(100)
            .circuit(2)
            .specialValue(1)
            .addTo(SCR);

        GTValues.RA.stdBuilder()
            .fluidInputs(Materials.Iron.getMolten(288), Materials.Nickel.getMolten(144))
            .fluidOutputs(Materials.Invar.getMolten(432))
            .eut(30)
            .duration(50)
            .circuit(2)
            .specialValue(1)
            .addTo(SCR);

        // 琥珀金
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Silver.getDust(1), Materials.Gold.getDust(1))
            .fluidOutputs(Materials.Electrum.getMolten(288))
            .eut(30)
            .duration(100)
            .circuit(2)
            .specialValue(1)
            .addTo(SCR);

        GTValues.RA.stdBuilder()
            .fluidInputs(Materials.Silver.getMolten(144), Materials.Gold.getMolten(144))
            .fluidOutputs(Materials.Electrum.getMolten(288))
            .eut(30)
            .duration(50)
            .circuit(2)
            .specialValue(1)
            .addTo(SCR);

        // 焊锡
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Tin.getDust(9), Materials.Antimony.getDust(1))
            .fluidOutputs(Materials.SolderingAlloy.getMolten(1440))
            .eut(30)
            .duration(100)
            .circuit(2)
            .specialValue(1)
            .addTo(SCR);

        GTValues.RA.stdBuilder()
            .fluidInputs(Materials.Tin.getMolten(1296), Materials.Antimony.getMolten(144))
            .fluidOutputs(Materials.SolderingAlloy.getMolten(1440))
            .eut(30)
            .duration(50)
            .circuit(2)
            .specialValue(1)
            .addTo(SCR);

        // 钴黄铜 CobaltBrass: 7黄铜 + 1钴 + 1锡 → 9×144 = 1296L
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Brass.getDust(7), Materials.Cobalt.getDust(1), Materials.Tin.getDust(1))
            .fluidOutputs(Materials.CobaltBrass.getMolten(1296))
            .eut(30)
            .duration(100)
            .circuit(3)
            .specialValue(1)
            .addTo(SCR);

        // 镁铝合金 Magnalium: 1镁 + 2铝 → 3×144 = 432L
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Magnesium.getDust(1), Materials.Aluminium.getDust(2))
            .fluidOutputs(Materials.Magnalium.getMolten(432))
            .eut(30)
            .duration(100)
            .circuit(2)
            .specialValue(1)
            .addTo(SCR);

        // 退火铜 AnnealedCopper: 1铜 + 1000L 氧气
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Copper.getDust(1))
            .fluidInputs(Materials.Oxygen.getGas(1000))
            .fluidOutputs(Materials.AnnealedCopper.getMolten(144))
            .eut(30)
            .duration(100)
            .circuit(11)
            .specialValue(1)
            .addTo(SCR);

        // Potin: 2铅 + 1锡 + 2青铜 → 5×144 = 720L
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Lead.getDust(2), Materials.Tin.getDust(1), Materials.Bronze.getDust(2))
            .fluidOutputs(MaterialsAlloy.POTIN.getFluidStack(720))
            .eut(30)
            .duration(100)
            .circuit(13)
            .specialValue(1)
            .addTo(SCR);

        // 玄钢
        GTValues.RA.stdBuilder()
            .fluidInputs(Materials.Steel.getMolten(144))
            .itemInputs(Materials.Coal.getDust(4), Materials.Silicon.getDust(1), Materials.Obsidian.getDust(3))
            .fluidOutputs(Materials.DarkSteel.getMolten(1296))
            .eut(60)
            .duration(200)
            .circuit(14)
            .specialValue(2)
            .addTo(SCR);

        // 魂金
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTModHandler.getModItem("minecraft", "soul_sand", 1, 0),
                Materials.Gold.getDust(1),
                Materials.Ash.getDust(1))
            .fluidOutputs(Materials.Soularium.getMolten(432))
            .eut(60)
            .duration(200)
            .circuit(2)
            .specialValue(2)
            .addTo(SCR);

        // 熔融铝
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Aluminiumoxide.getDust(10), Materials.Cryolite.getDust(5))
            .fluidOutputs(Materials.Aluminium.getMolten(576))
            .eut(60)
            .duration(200)
            .specialValue(2)
            .addTo(SCR);

        // ==================== 高级配方 v7 (specialValue 2-5) ====================

        // ---- specialValue(2) ----

        // 导电铁+黑钢+金 → 充能合金
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.ConductiveIron.getDust(1), Materials.BlackSteel.getDust(1), Materials.Gold.getDust(1))
            .fluidOutputs(Materials.EnergeticAlloy.getMolten(432))
            .eut(30)
            .duration(300)
            .circuit(3)
            .specialValue(2)
            .addTo(SCR);

        // 末影之眼+铬+充能合金 → 充能银合金
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.EnderEye.getDust(1), Materials.Chrome.getDust(1), Materials.EnergeticAlloy.getDust(1))
            .fluidOutputs(Materials.VibrantAlloy.getMolten(432))
            .eut(30)
            .duration(300)
            .circuit(3)
            .specialValue(2)
            .addTo(SCR);

        // 钽铁矿 → 锰 + 钽
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Tantalite.getDust(9))
            .fluidOutputs(Materials.Manganese.getMolten(144), Materials.Tantalum.getMolten(288))
            .eut(30)
            .duration(300)
            .specialValue(2)
            .addTo(SCR);

        // 钽粉 → 熔融钽
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Tantalum.getDust(1))
            .fluidOutputs(Materials.Tantalum.getMolten(144))
            .eut(30)
            .duration(300)
            .specialValue(2)
            .circuit(1)
            .addTo(SCR);

        // 红石合金+铁+银 → 导电铁
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.RedstoneAlloy.getDust(1), Materials.Iron.getDust(1), Materials.Silver.getDust(1))
            .fluidOutputs(Materials.ConductiveIron.getMolten(432))
            .eut(30)
            .duration(300)
            .circuit(3)
            .specialValue(2)
            .addTo(SCR);

        // 铜+琥珀金 → 黑铜
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Copper.getDust(3), Materials.Electrum.getDust(2))
            .fluidOutputs(Materials.BlackBronze.getMolten(720))
            .eut(30)
            .duration(300)
            .circuit(2)
            .specialValue(2)
            .addTo(SCR);

        // 黑铜+银+钢 → 黑钢
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.BlackBronze.getDust(1), Materials.Silver.getDust(1), Materials.Steel.getDust(3))
            .fluidOutputs(Materials.BlackSteel.getMolten(720))
            .eut(30)
            .duration(300)
            .circuit(3)
            .specialValue(2)
            .addTo(SCR);

        // ---- specialValue(3) ----

        // 铝粉 → 熔融铝
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Aluminium.getDust(1))
            .fluidOutputs(Materials.Aluminium.getMolten(144))
            .eut(30)
            .duration(400)
            .specialValue(3)
            .circuit(1)
            .addTo(SCR);

        // 熔融铁+铝+铬 → 坎塔尔合金
        GTValues.RA.stdBuilder()
            .fluidInputs(
                Materials.Iron.getMolten(144),
                Materials.Aluminium.getMolten(144),
                Materials.Chrome.getMolten(144))
            .fluidOutputs(Materials.Kanthal.getMolten(432))
            .eut(30)
            .duration(400)
            .circuit(3)
            .specialValue(3)
            .addTo(SCR);

        // 铬粉 → 熔融铬
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Chrome.getDust(1))
            .fluidOutputs(Materials.Chrome.getMolten(144))
            .eut(30)
            .duration(400)
            .specialValue(3)
            .circuit(1)
            .addTo(SCR);

        // 铂金属粉 → 熔融铂
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Platinum.getDust(1))
            .fluidOutputs(Materials.Platinum.getMolten(144))
            .eut(30)
            .duration(400)
            .specialValue(3)
            .circuit(1)
            .addTo(SCR);

        // 银+蓝石 → 蓝色合金
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Silver.getDust(1), Materials.Electrotine.getDust(4))
            .fluidOutputs(Materials.BlueAlloy.getMolten(144))
            .eut(30)
            .duration(400)
            .circuit(2)
            .specialValue(3)
            .addTo(SCR);

        // ---- specialValue(4) ----

        // 熔融镍+铬 → 镍铬合金
        GTValues.RA.stdBuilder()
            .fluidInputs(Materials.Nickel.getMolten(576), Materials.Chrome.getMolten(144))
            .fluidOutputs(Materials.Nichrome.getMolten(144))
            .eut(30)
            .duration(500)
            .circuit(2)
            .specialValue(4)
            .addTo(SCR);

        // 铁+镍+锰+铬 → 不锈钢
        GTValues.RA.stdBuilder()
            .itemInputs(
                Materials.Iron.getDust(6),
                Materials.Nickel.getDust(1),
                Materials.Manganese.getDust(1),
                Materials.Chrome.getDust(1))
            .fluidOutputs(Materials.StainlessSteel.getMolten(1296))
            .eut(30)
            .duration(500)
            .circuit(6)
            .specialValue(4)
            .addTo(SCR);

        // ---- specialValue(5) ----

        // 钛粉 → 熔融钛
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Titanium.getDust(1))
            .fluidOutputs(Materials.Titanium.getMolten(144))
            .eut(30)
            .duration(600)
            .specialValue(5)
            .circuit(1)
            .addTo(SCR);

        // 金红石+碳 → 钛
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Rutile.getDust(2), Materials.Carbon.getDust(2))
            .fluidOutputs(Materials.Titanium.getMolten(144))
            .eut(30)
            .duration(600)
            .circuit(2)
            .specialValue(5)
            .addTo(SCR);

        // 钨粉 → 熔融钨
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Tungsten.getDust(1))
            .fluidOutputs(Materials.Tungsten.getMolten(144))
            .eut(30)
            .duration(600)
            .specialValue(5)
            .circuit(1)
            .addTo(SCR);

        // 钨+钢 → 钨钢
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Tungsten.getDust(1), Materials.Steel.getDust(1))
            .fluidOutputs(Materials.TungstenSteel.getMolten(288))
            .eut(30)
            .duration(600)
            .circuit(2)
            .specialValue(5)
            .addTo(SCR);

    }

}
