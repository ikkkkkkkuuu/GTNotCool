package com.xyp.gtnc.utils.structure;

import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrors;

public class GTNCStructureErrors {

    private GTNCStructureErrors() {}

    // #tr GTNC.gui.text.structure_error.invalid_hatch_configuration
    // # Invalid hatch configuration
    // # zh_CN 仓室配置无效
    public static StructureError invalidHatchConfiguration() {
        return StructureErrors.of("GTNC.gui.text.structure_error.invalid_hatch_configuration");
    }

    // #tr GTNC.gui.text.structure_error.missing_crystal_wand
    // # Missing crystal-wrapped cosmic neutron wand in main controller slot
    // # zh_CN 主控制器槽内缺少水晶缠绕宇宙中子态素法杖
    public static StructureError missingCrystalWand() {
        return StructureErrors.of("GTNC.gui.text.structure_error.missing_crystal_wand");
    }

    // #tr GTNC.gui.text.structure_error.invalid_energy_hatch_configuration
    // # Invalid energy hatch configuration
    // # zh_CN 无效的能源仓配置
    public static StructureError invalidEnergyHatchConfiguration() {
        return StructureErrors.of("GTNC.gui.text.structure_error.invalid_energy_hatch_configuration");
    }

    // #tr GTNC.gui.text.structure_error.laser_energy_tunnel_disabled
    // # Laser energy tunnel is disabled
    // # zh_CN 激光仓已禁用
    public static StructureError laserEnergyTunnelDisabled() {
        return StructureErrors.of("GTNC.gui.text.structure_error.laser_energy_tunnel_disabled");
    }

    // #tr GTNC.gui.text.structure_error.energy_input_amperage_too_high
    // # Energy input amperage is too high
    // # zh_CN 能量输入安培过高
    public static StructureError energyInputAmperageTooHigh() {
        return StructureErrors.of("GTNC.gui.text.structure_error.energy_input_amperage_too_high");
    }

    // #tr GTNC.gui.text.structure_error.missing_distillation_layer_output_hatch
    // # Missing distillation layer output hatch
    // # zh_CN 缺少蒸馏层输出仓
    public static StructureError missingDistillationLayerOutputHatch() {
        return StructureErrors.of("GTNC.gui.text.structure_error.missing_distillation_layer_output_hatch");
    }

    // #tr GTNC.gui.text.structure_error.legacy_check_failed
    // # Structure validation failed
    // # zh_CN 结构验证失败
    public static StructureError unknownLegacyCheckFailure() {
        return StructureErrors.of("GTNC.gui.text.structure_error.legacy_check_failed");
    }
}
