package com.xyp.gtnc.Common.gui.recipe;

import static com.xyp.gtnc.ScienceNotCool.RESOURCE_ROOT_ID;

import javax.annotation.ParametersAreNonnullByDefault;

import com.gtnewhorizons.modularui.api.drawable.UITexture;
import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.xyp.gtnc.utils.item.ItemUtils;

import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.api.recipe.RecipeMapFrontend;
import gregtech.api.util.MethodsReturnNonnullByDefault;
import gregtech.nei.GTNEIDefaultHandler;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GTNCLogoFrontend extends RecipeMapFrontend {

    private static final UITexture SPACE_BACKGROUND = UITexture.fullImage(RESOURCE_ROOT_ID, "gui/background/space");

    public GTNCLogoFrontend(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        super(uiPropertiesBuilder, neiPropertiesBuilder);
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder, Pos2d windowOffset) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(uiProperties.logoPos.add(windowOffset)));
    }

    @Override
    public ModularWindow.Builder createNEITemplate(GTNEIDefaultHandler.NEITemplateContext ctx) {
        return super.createNEITemplate(ctx).setBackground(SPACE_BACKGROUND);
    }

}
