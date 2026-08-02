package com.xyp.gtnc.Common.gui.modularui.multiblock.steam;

import com.cleanroommc.modularui.drawable.UITexture;

import gregtech.common.gui.modularui.multiblock.godforge.MTEBaseModuleGui;
import gregtech.common.gui.modularui.multiblock.godforge.sync.Modules;
import tectech.thing.metaTileEntity.multi.godforge.MTESmeltingModule;

/**
 * 继承 MTESmeltingModule 的自定义蒸汽神锻模块通用 GUI。
 *
 * 传入模式图标：显示一个模式按钮。
 * 不传模式图标：不显示模式按钮。
 */
public class SteamGodforgeSmeltingModeModuleGui<T extends MTESmeltingModule> extends MTEBaseModuleGui<T> {

    /**
     * 无模式切换模块使用。
     */
    public SteamGodforgeSmeltingModeModuleGui(T multiblock) {
        super(multiblock);
    }

    /**
     * 有模式切换模块使用。
     *
     * modeIcons 的顺序必须和 machineMode 对应：
     * modeIcons[0] 对应 machineMode == 0
     * modeIcons[1] 对应 machineMode == 1
     */
    public SteamGodforgeSmeltingModeModuleGui(T multiblock, UITexture... modeIcons) {

        super(multiblock);

        if (modeIcons != null && modeIcons.length > 0) {
            withMachineModeIcons(modeIcons);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Modules<T> getModuleType() {
        /*
         * 自定义模块虽然配方功能不同，
         * 但在神锻升级和同步系统中作为熔炼模块处理。
         */
        return (Modules) Modules.SMELTING;
    }

    /*
     * 不要覆盖以下方法：
     * createRightPanelGapRow()
     * createModeSwitchButton()
     * usesExtraButton()
     * createExtraButton()
     * MTEBaseModuleGui 默认 usesExtraButton() == false，
     * 因此不会显示 MTESmeltingModuleGui 的熔炉/高炉按钮。
     * 基础 MTEMultiBlockBaseGui 会在 modeIcons 非空时，
     * 自动添加且只添加一个 machineMode 模式按钮。
     */
}
