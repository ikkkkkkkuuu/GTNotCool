package com.xyp.gtnc.Client.research;

import java.lang.reflect.Field;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.xyp.gtnc.ScienceNotCool;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import thaumcraft.client.gui.GuiResearchTable;
import thaumcraft.common.tiles.TileResearchTable;

/** Client-side compatibility for ThaumcraftResearchTweaks, which replaces the vanilla research table GUI. */
public final class ResearchGuiCompatibility {

    private static final String RESEARCH_TWEAKS = "ThaumcraftResearchTweaks";

    private ResearchGuiCompatibility() {}

    @SuppressWarnings("unchecked")
    public static void install() {
        if (!Loader.isModLoaded(RESEARCH_TWEAKS)) return;
        try {
            Field field = NetworkRegistry.class.getDeclaredField("clientGuiHandlers");
            field.setAccessible(true);
            Map<ModContainer, IGuiHandler> handlers = (Map<ModContainer, IGuiHandler>) field
                .get(NetworkRegistry.INSTANCE);
            ModContainer container = Loader.instance()
                .getIndexedModList()
                .get(RESEARCH_TWEAKS);
            if (container == null) return;

            IGuiHandler previous = handlers.get(container);
            if (!(previous instanceof VanillaResearchGuiHandler)) {
                handlers.put(container, new VanillaResearchGuiHandler(previous));
                ScienceNotCool.LOG.info("Installed client-only ThaumcraftResearchTweaks research GUI compatibility");
            }
        } catch (ReflectiveOperationException | RuntimeException exception) {
            ScienceNotCool.LOG.warn(
                "Could not install ThaumcraftResearchTweaks GUI compatibility; automatic research will remain disabled for its custom GUI",
                exception);
        }
    }

    private static final class VanillaResearchGuiHandler implements IGuiHandler {

        private final IGuiHandler delegate;

        private VanillaResearchGuiHandler(IGuiHandler delegate) {
            this.delegate = delegate;
        }

        @Override
        public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
            return delegate == null ? null : delegate.getServerGuiElement(id, player, world, x, y, z);
        }

        @Override
        public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
            if (id == 0 && world.getTileEntity(x, y, z) instanceof TileResearchTable) {
                return new GuiResearchTable(player, (TileResearchTable) world.getTileEntity(x, y, z));
            }
            return delegate == null ? null : delegate.getClientGuiElement(id, player, world, x, y, z);
        }
    }
}
