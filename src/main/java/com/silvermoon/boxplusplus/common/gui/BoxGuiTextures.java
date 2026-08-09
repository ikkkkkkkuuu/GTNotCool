package com.silvermoon.boxplusplus.common.gui;

import com.cleanroommc.modularui.drawable.UITexture;
import com.silvermoon.boxplusplus.Tags;

/** Original Box GUI artwork exposed to MUI2 without changing its visual layout. */
public final class BoxGuiTextures {

    public static final UITexture DREAM = texture("dream");
    public static final UITexture MODULE_FRAME = texture("01b");
    public static final UITexture MODULE_FIVE_FRAME = texture("05b");
    public static final UITexture DOUBLE = texture("double");
    public static final UITexture HALVE = texture("halve");
    public static final UITexture AE = texture("ae");
    public static final UITexture CLEAR = texture("clear");
    public static final UITexture TIME = texture("time");
    public static final UITexture VOLTAGE = texture("voteage");
    public static final UITexture ARROW_GREEN_UP = gregtechTexture("arrow_green_up");
    public static final UITexture ARROW_GREEN_DOWN = gregtechTexture("arrow_green_down");
    public static final UITexture NEI = gregtechTexture("nei");

    public static final UITexture[] RINGS = { adaptable("ring1", 696, 697, 4), adaptable("ring2", 696, 697, 4),
        adaptable("ring3", 696, 697, 4) };

    private BoxGuiTextures() {}

    public static UITexture moduleButton(int index) {
        return texture(String.format("%02da", index + 1));
    }

    public static UITexture modulePicture(int index) {
        return texture(Integer.toString(index + 1));
    }

    public static UITexture number(int number) {
        return texture("number" + number);
    }

    private static UITexture texture(String name) {
        return UITexture.fullImage(Tags.MODID, "gui/" + name);
    }

    private static UITexture gregtechTexture(String name) {
        return UITexture.fullImage(gregtech.api.enums.Mods.GregTech.ID, "gui/overlay_button/" + name);
    }

    private static UITexture adaptable(String name, int width, int height, int border) {
        return UITexture.builder()
            .location(Tags.MODID, "gui/" + name)
            .imageSize(width, height)
            .adaptable(border)
            .build();
    }
}
