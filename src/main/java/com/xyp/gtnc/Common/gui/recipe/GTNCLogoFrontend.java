package com.xyp.gtnc.Common.gui.recipe;

import static com.xyp.gtnc.ScienceNotCool.RESOURCE_ROOT_ID;

import javax.annotation.ParametersAreNonnullByDefault;

import com.gtnewhorizons.modularui.api.drawable.UITexture;
import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.math.Size;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.xyp.gtnc.utils.item.ItemUtils;

import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.api.recipe.maps.LargeNEIFrontend;
import gregtech.api.util.MethodsReturnNonnullByDefault;
import gregtech.nei.GTNEIDefaultHandler;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GTNCLogoFrontend extends LargeNEIFrontend {

    private static final int xDirMaxCount = 3;
    private static final UITexture SPACE_BACKGROUND = UITexture.fullImage(RESOURCE_ROOT_ID, "gui/background/space");

    public GTNCLogoFrontend(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        super(uiPropertiesBuilder, neiPropertiesBuilder);
    }

    @Override
    protected NEIRecipePropertiesBuilder modifyNEIProperties(NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        int itemRowCount = itemRowCount();
        int fluidRowCount = fluidRowCount();
        int bgHeight = 82 + (Math.max(itemRowCount + fluidRowCount - 4, 0)) * 18;
        // +45 for text area below (energy, duration, special info)
        int handlerHeight = bgHeight + 45;
        return neiPropertiesBuilder.recipeBackgroundSize(new Size(170, bgHeight))
            .handlerInfoCreator(builder -> builder.setHeight(handlerHeight));
    }

    private int itemRowCount() {
        return (Math.max(uiProperties.maxItemInputs, uiProperties.maxItemOutputs) - 1) / xDirMaxCount + 1;
    }

    private int fluidRowCount() {
        return (Math.max(uiProperties.maxFluidInputs, uiProperties.maxFluidOutputs) - 1) / xDirMaxCount + 1;
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
