package com.xyp.gtnc.Common.gui.modularui;

import net.minecraft.util.ResourceLocation;

import com.cleanroommc.modularui.drawable.UITexture;
import com.xyp.gtnc.ScienceNotCool;

/**
 * Centralized GUI texture definitions for GTNC (ScienceNotCool) mod.
 * Mirrors gregtech.api.modularui2.GTGuiTextures pattern.
 */
public final class GTNCGuiTextures {

    public static final String MODID = ScienceNotCool.MODID;

    // ==================== Button ====================
    public static final UITexture BUTTON_CELESTIAL_32x32 = UITexture.fullImage(MODID, "gui/button/celestial");
    public static final UITexture BUTTON_TRANSPARENT_16x16 = UITexture.fullImage(MODID, "gui/button/transparent_16x16");
    public static final UITexture BUTTON_TRANSPARENT_PRESSED_16x16 = UITexture
        .fullImage(MODID, "gui/button/transparent_pressed_16x16");
    public static final UITexture CLOSE_BUTTON_HOLLOW = UITexture.fullImage(MODID, "gui/button/transparent_x_10x10");
    // ==================== Shared Modernity Theme ====================
    public static final ResourceLocation MODERN_BACKGROUND_LOCATION = new ResourceLocation(
        MODID,
        "textures/gui/modernity/background.png");
    public static final ResourceLocation MODERN_BUTTON_COMPACT_LOCATION = new ResourceLocation(
        MODID,
        "textures/gui/modernity/button_compact.png");

    public static final UITexture MODERN_BUTTON = UITexture.builder()
        .location(MODID, "gui/modernity/button")
        .imageSize(20, 20)
        .adaptable(3)
        .build();
    public static final UITexture MODERN_BUTTON_HOVER = UITexture.builder()
        .location(MODID, "gui/modernity/button_hover")
        .imageSize(20, 20)
        .adaptable(3)
        .build();
    public static final UITexture MODERN_BUTTON_PRESSED = UITexture.builder()
        .location(MODID, "gui/modernity/button_pressed")
        .imageSize(20, 20)
        .adaptable(3)
        .build();
    public static final UITexture MODERN_BUTTON_DISABLED = UITexture.builder()
        .location(MODID, "gui/modernity/button_disabled")
        .imageSize(20, 20)
        .adaptable(3)
        .build();
    public static final UITexture MODERN_BUTTON_COMPACT = UITexture.builder()
        .location(MODID, "gui/modernity/button_compact")
        .imageSize(18, 18)
        .adaptable(2)
        .build();
    public static final UITexture MODERN_BUTTON_COMPACT_PRESSED = UITexture.builder()
        .location(MODID, "gui/modernity/button_compact_pressed")
        .imageSize(18, 18)
        .adaptable(2)
        .build();
    public static final UITexture MODERN_BACKGROUND = UITexture.builder()
        .location(MODID, "gui/modernity/background")
        .imageSize(176, 166)
        .adaptable(2)
        .build();
    public static final UITexture MODERN_PANEL_BORDER = UITexture.builder()
        .location(MODID, "gui/modernity/panel_border")
        .imageSize(8, 8)
        .adaptable(1)
        .build();
    public static final UITexture MODERN_DISPLAY = UITexture.builder()
        .location(MODID, "gui/modernity/display")
        .imageSize(143, 75)
        .adaptable(1)
        .build();
    public static final UITexture MODERN_ITEM_SLOT = UITexture.builder()
        .location(MODID, "gui/modernity/item_slot")
        .imageSize(18, 18)
        .adaptable(1)
        .build();
    public static final UITexture MODERN_VAULT_BACKGROUND = UITexture.builder()
        .location(MODID, "gui/modernity/vault_background")
        .imageSize(16, 16)
        .adaptable(2, 2, 2, 4)
        .build();
    public static final UITexture MODERN_VAULT_PANEL_BORDER = UITexture.builder()
        .location(MODID, "gui/modernity/vault_panel_border")
        .imageSize(16, 16)
        .adaptable(6)
        .build();
    public static final UITexture MODERN_VAULT_ITEM_SLOT = UITexture.builder()
        .location(MODID, "gui/modernity/vault_item_slot")
        .imageSize(18, 18)
        .adaptable(1)
        .build();

    // ==================== Overlay Button ====================
    public static final UITexture OVERLAY_BUTTON_POWER_SWITCH_ON = UITexture
        .fullImage(MODID, "gui/overlay_button/power_switch_on");
    public static final UITexture OVERLAY_BUTTON_POWER_SWITCH_OFF = UITexture
        .fullImage(MODID, "gui/overlay_button/power_switch_off");
    public static final UITexture OVERLAY_BUTTON_POWER_SWITCH_DISABLED = UITexture
        .fullImage(MODID, "gui/overlay_button/power_switch_disabled");
    public static final UITexture OVERLAY_BUTTON_STRUCTURE_CHECK = UITexture
        .fullImage(MODID, "gui/overlay_button/structure_check_on");
    public static final UITexture OVERLAY_BUTTON_STRUCTURE_CHECK_OFF = UITexture
        .fullImage(MODID, "gui/overlay_button/structure_check_off");
    public static final UITexture OVERLAY_BUTTON_VOIDING_OFF = UITexture
        .fullImage(MODID, "gui/overlay_button/voiding_disabled");
    public static final UITexture OVERLAY_BUTTON_VOIDING_ITEMS = UITexture
        .fullImage(MODID, "gui/overlay_button/voiding_items");
    public static final UITexture OVERLAY_BUTTON_VOIDING_FLUIDS = UITexture
        .fullImage(MODID, "gui/overlay_button/voiding_fluids");
    public static final UITexture OVERLAY_BUTTON_VOIDING_BOTH = UITexture
        .fullImage(MODID, "gui/overlay_button/voiding_both");
    public static final UITexture OVERLAY_BUTTON_INPUT_SEPARATION = UITexture
        .fullImage(MODID, "gui/overlay_button/input_separation_on");
    public static final UITexture OVERLAY_BUTTON_INPUT_SEPARATION_OFF = UITexture
        .fullImage(MODID, "gui/overlay_button/input_separation_off");
    public static final UITexture OVERLAY_BUTTON_BATCH_MODE = UITexture
        .fullImage(MODID, "gui/overlay_button/batch_mode_on");
    public static final UITexture OVERLAY_BUTTON_BATCH_MODE_OFF = UITexture
        .fullImage(MODID, "gui/overlay_button/batch_mode_off");
    public static final UITexture OVERLAY_BUTTON_RECIPE_LOCKED = UITexture
        .fullImage(MODID, "gui/overlay_button/recipe_locked");
    public static final UITexture OVERLAY_BUTTON_RECIPE_UNLOCKED = UITexture
        .fullImage(MODID, "gui/overlay_button/recipe_unlocked");
    public static final UITexture OVERLAY_BUTTON_POWER_PANEL = UITexture
        .fullImage(MODID, "gui/overlay_button/power_panel");

    // Godforge-themed extras

    public static final UITexture OVERLAY_BUTTON_MACHINEMODE_LASER = UITexture
        .fullImage(MODID, "gui/button/laser_engraver");

    public static final UITexture OVERLAY_BUTTON_MACHINEMODE_MIXER = UITexture.fullImage(MODID, "gui/button/mixer");

    public static final UITexture OVERLAY_BUTTON_MACHINEMODE_FLUID_SOLIDIFIER = UITexture
        .fullImage(MODID, "gui/button/fluid_solidifier");

    public static final UITexture OVERLAY_BUTTON_FLAG = UITexture.fullImage(MODID, "gui/overlay_button/flag");
    public static final UITexture OVERLAY_BUTTON_HEAT_ON = UITexture.fullImage(MODID, "gui/overlay_button/heat_on");
    public static final UITexture OVERLAY_BUTTON_BATTERY_ON = UITexture
        .fullImage(MODID, "gui/overlay_button/battery_on");
    public static final UITexture OVERLAY_BUTTON_BATTERY_OFF = UITexture
        .fullImage(MODID, "gui/overlay_button/battery_off");
    public static final UITexture OVERLAY_BUTTON_RAINBOW_SPIRAL = UITexture
        .fullImage(MODID, "gui/overlay_button/rainbow_spiral");
    public static final UITexture OVERLAY_BUTTON_ARROW_BLUE_UP = UITexture
        .fullImage(MODID, "gui/overlay_button/arrow_blue_up");
    public static final UITexture OVERLAY_CYCLIC_BLUE = UITexture.fullImage(MODID, "gui/overlay_button/cyclic_blue");
    public static final UITexture OVERLAY_BUTTON_STATISTICS = UITexture
        .fullImage(MODID, "gui/overlay_button/statistics");
    public static final UITexture OVERLAY_EJECTION_ON = UITexture.fullImage(MODID, "gui/overlay_button/eject");
    public static final UITexture OVERLAY_EJECTION_LOCKED = UITexture
        .fullImage(MODID, "gui/overlay_button/eject_disabled");
    public static final UITexture OVERLAY_BUTTON_HEART = UITexture.fullImage(MODID, "gui/overlay_button/heart");
    public static final UITexture OVERLAY_BUTTON_FURNACE_MODE = UITexture
        .fullImage(MODID, "gui/overlay_button/furnace_mode_on");
    public static final UITexture OVERLAY_BUTTON_FURNACE_MODE_OFF = UITexture
        .fullImage(MODID, "gui/overlay_button/furnace_mode_off");
    public static final UITexture GODFORGE_SOUND_ON = UITexture.fullImage(MODID, "gui/overlay_button/sound_on");
    public static final UITexture GODFORGE_SOUND_OFF = UITexture.fullImage(MODID, "gui/overlay_button/sound_off");

    // ==================== Picture ====================
    public static final UITexture PICTURE_GODFORGE_LOGO = UITexture.fullImage(MODID, "gui/picture/gorge_logo");
    public static final UITexture PICTURE_HEAT_SINK_16x8 = UITexture.fullImage(MODID, "gui/picture/heat_sink_16x8");
    public static final UITexture CONTROLLER_SLOT_HEAT_SINK = UITexture.fullImage(MODID, "gui/picture/heat_sink_small");

    // ==================== Background ====================
    public static final UITexture BACKGROUND_RAINBOW_GLOW = UITexture.fullImage(MODID, "gui/background/rainbow_glow");
    public static final UITexture BACKGROUND_STAR = UITexture.fullImage(MODID, "gui/background/star");
    public static final UITexture BACKGROUND_SPACE = UITexture.fullImage(MODID, "gui/background/space");

    // ==================== Progressbar ====================
    public static final UITexture PROGRESSBAR_GODFORGE_PLASMA = UITexture
        .fullImage(MODID, "gui/progressbar/godforge_plasma");

    // ==================== Overlay Slot ====================
    public static final UITexture OVERLAY_SLOT_MESH = UITexture.fullImage(MODID, "gui/overlay_slot/mesh");
}
