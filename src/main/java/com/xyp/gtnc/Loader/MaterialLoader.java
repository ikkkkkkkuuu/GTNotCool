package com.xyp.gtnc.Loader;

import com.xyp.gtnc.Common.material.GTNCMaterials;
import com.xyp.gtnc.utils.enums.GTNCItemList;

import bartworks.API.WerkstoffAdderRegistry;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTOreDictUnificator;

public class MaterialLoader {

    /** preInit：注册 Werkstoff 自定义材料 */
    public static void loadPreInit() {
        WerkstoffAdderRegistry.addWerkstoffAdder(new GTNCMaterials());
    }

    /** init：注册中文名并注册 OreDict */
    public static void loadInit() {
        GTNCLanguageLoader.registry();
        registryOreDictionary();
    }

    public static void registryOreDictionary() {
        GTOreDictUnificator
            .registerOre(OrePrefixes.circuit.get(Materials.ULV), GTNCItemList.CircuitResonaticULV.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.LV), GTNCItemList.CircuitResonaticLV.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.MV), GTNCItemList.CircuitResonaticMV.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.HV), GTNCItemList.CircuitResonaticHV.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.EV), GTNCItemList.CircuitResonaticEV.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.IV), GTNCItemList.CircuitResonaticIV.get(1));
        GTOreDictUnificator
            .registerOre(OrePrefixes.circuit.get(Materials.LuV), GTNCItemList.CircuitResonaticLuV.get(1));
        GTOreDictUnificator
            .registerOre(OrePrefixes.circuit.get(Materials.ZPM), GTNCItemList.CircuitResonaticZPM.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.UV), GTNCItemList.CircuitResonaticUV.get(1));
        GTOreDictUnificator
            .registerOre(OrePrefixes.circuit.get(Materials.UHV), GTNCItemList.CircuitResonaticUHV.get(1));
        GTOreDictUnificator
            .registerOre(OrePrefixes.circuit.get(Materials.UEV), GTNCItemList.CircuitResonaticUEV.get(1));
        GTOreDictUnificator
            .registerOre(OrePrefixes.circuit.get(Materials.UIV), GTNCItemList.CircuitResonaticUIV.get(1));

        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.ULV), GTNCItemList.VerySimpleCircuit.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.LV), GTNCItemList.SimpleCircuit.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.MV), GTNCItemList.BasicCircuit.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.HV), GTNCItemList.AdvancedCircuit.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.EV), GTNCItemList.EliteCircuit.get(1));
    }
}
