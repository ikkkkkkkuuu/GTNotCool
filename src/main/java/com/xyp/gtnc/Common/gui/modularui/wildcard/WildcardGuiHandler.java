package com.xyp.gtnc.Common.gui.modularui.wildcard;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.IGuiHandler;

public class WildcardGuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == WildcardPatternGuiHandler.GUI_WILDCARD_PATTERN) {
            try {
                com.gtnewhorizons.modularui.api.screen.UIBuildContext buildContext = new com.gtnewhorizons.modularui.api.screen.UIBuildContext(
                    player);
                com.gtnewhorizons.modularui.api.screen.ModularUIContext context = new com.gtnewhorizons.modularui.api.screen.ModularUIContext(
                    buildContext,
                    () -> {});
                com.gtnewhorizons.modularui.api.screen.ModularWindow window = WildcardPatternWindow
                    .createWindow(buildContext, player, x);
                if (window != null) {
                    return new com.gtnewhorizons.modularui.common.internal.wrapper.ModularUIContainer(context, window);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == WildcardPatternGuiHandler.GUI_WILDCARD_PATTERN) {
            try {
                com.gtnewhorizons.modularui.api.screen.UIBuildContext buildContext = new com.gtnewhorizons.modularui.api.screen.UIBuildContext(
                    player);
                com.gtnewhorizons.modularui.api.screen.ModularUIContext context = new com.gtnewhorizons.modularui.api.screen.ModularUIContext(
                    buildContext,
                    () -> {});
                com.gtnewhorizons.modularui.api.screen.ModularWindow window = WildcardPatternWindow
                    .createWindow(buildContext, player, x);
                if (window != null) {
                    return new com.gtnewhorizons.modularui.common.internal.wrapper.ModularGui(
                        new com.gtnewhorizons.modularui.common.internal.wrapper.ModularUIContainer(context, window));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
