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
import thaumcraft.common.tiles.TileResearchTable;

/** Replaces both Thaumcraft and ThaumcraftResearchTweaks research GUIs on the physical client only. */
public final class ResearchGuiCompatibility {

    private static final String THAUMCRAFT = "Thaumcraft";
    private static final String RESEARCH_TWEAKS = "ThaumcraftResearchTweaks";

    private ResearchGuiCompatibility() {}

    @SuppressWarnings("unchecked")
    public static void install() {
        try {
            Field field = NetworkRegistry.class.getDeclaredField("clientGuiHandlers");
            field.setAccessible(true);
            Map<ModContainer, IGuiHandler> handlers = (Map<ModContainer, IGuiHandler>) field
                .get(NetworkRegistry.INSTANCE);
            installFor(handlers, THAUMCRAFT, 10);
            if (Loader.isModLoaded(RESEARCH_TWEAKS)) installFor(handlers, RESEARCH_TWEAKS, 0);
        } catch (ReflectiveOperationException | RuntimeException exception) {
            ScienceNotCool.LOG.warn("Could not install the automatic Thaumcraft research GUI", exception);
        }
    }

    private static void installFor(Map<ModContainer, IGuiHandler> handlers, String modId, int researchGuiId) {
        ModContainer container = Loader.instance()
            .getIndexedModList()
            .get(modId);
        if (container == null) return;
        IGuiHandler previous = handlers.get(container);
        if (previous instanceof AutoResearchGuiHandler) return;
        handlers.put(container, new AutoResearchGuiHandler(previous, researchGuiId));
        ScienceNotCool.LOG.info("Installed GTNC automatic research GUI handler for {} (GUI {})", modId, researchGuiId);
    }

    private static final class AutoResearchGuiHandler implements IGuiHandler {

        private final IGuiHandler delegate;
        private final int researchGuiId;

        private AutoResearchGuiHandler(IGuiHandler delegate, int researchGuiId) {
            this.delegate = delegate;
            this.researchGuiId = researchGuiId;
        }

        @Override
        public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
            return delegate == null ? null : delegate.getServerGuiElement(id, player, world, x, y, z);
        }

        @Override
        public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
            if (id == researchGuiId && world.getTileEntity(x, y, z) instanceof TileResearchTable) {
                return new GuiAutoResearchTable(player, (TileResearchTable) world.getTileEntity(x, y, z));
            }
            return delegate == null ? null : delegate.getClientGuiElement(id, player, world, x, y, z);
        }
    }
}
