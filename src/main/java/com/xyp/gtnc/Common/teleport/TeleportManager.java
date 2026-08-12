package com.xyp.gtnc.Common.teleport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;

import com.xyp.gtnc.ae2thing.util.Util;

/** Server-authoritative storage and execution for the no-cost personal teleport terminal. */
public final class TeleportManager {

    public static final int MAX_DESTINATIONS = 24;

    private static final String PERSISTED_TAG = "PlayerPersisted";
    private static final String ROOT_TAG = "GTNCTeleportTerminal";
    private static final String DESTINATIONS_TAG = "Destinations";

    private TeleportManager() {}

    public static List<TeleportDestination> getDestinations(EntityPlayer player) {
        NBTTagList destinationsTag = getRootTag(player).getTagList(DESTINATIONS_TAG, 10);
        List<TeleportDestination> destinations = new ArrayList<>(destinationsTag.tagCount());
        for (int index = 0; index < destinationsTag.tagCount(); index++) {
            destinations.add(TeleportDestination.readFromNbt(destinationsTag.getCompoundTagAt(index)));
        }
        return Collections.unmodifiableList(destinations);
    }

    public static boolean addCurrentPosition(EntityPlayerMP player, String requestedName) {
        List<TeleportDestination> destinations = getDestinations(player);
        if (destinations.size() >= MAX_DESTINATIONS) {
            // #tr message.gtnc.teleport_terminal.limit_reached
            // # You have reached the maximum number of teleport destinations.
            // # zh_CN 已达到传送坐标点上限。
            player.addChatMessage(new ChatComponentTranslation("message.gtnc.teleport_terminal.limit_reached"));
            return false;
        }

        String name = sanitizeName(requestedName);
        if (name.isEmpty()) name = "Location " + (destinations.size() + 1);

        destinations = new ArrayList<>(destinations);
        destinations.add(
            new TeleportDestination(
                name,
                player.dimension,
                MathHelper.floor_double(player.posX),
                MathHelper.floor_double(player.posY),
                MathHelper.floor_double(player.posZ)));
        writeDestinations(player, destinations);
        return true;
    }

    public static boolean removeDestination(EntityPlayerMP player, int index) {
        List<TeleportDestination> destinations = new ArrayList<>(getDestinations(player));
        if (index < 0 || index >= destinations.size()) return false;
        if (destinations.get(index).locked) {
            // #tr message.gtnc.teleport_terminal.destination_locked
            // # Unlock this destination before changing it.
            // # zh_CN 请先解锁此坐标点再进行修改。
            player.addChatMessage(new ChatComponentTranslation("message.gtnc.teleport_terminal.destination_locked"));
            return false;
        }
        destinations.remove(index);
        writeDestinations(player, destinations);
        return true;
    }

    public static boolean renameDestination(EntityPlayerMP player, int index, String requestedName) {
        List<TeleportDestination> destinations = new ArrayList<>(getDestinations(player));
        if (index < 0 || index >= destinations.size()) return false;
        TeleportDestination destination = destinations.get(index);
        if (destination.locked) {
            sendLocked(player);
            return false;
        }
        String name = sanitizeName(requestedName);
        if (name.isEmpty()) return false;
        destinations.set(index, destination.withName(name));
        writeDestinations(player, destinations);
        return true;
    }

    public static boolean toggleLock(EntityPlayerMP player, int index) {
        List<TeleportDestination> destinations = new ArrayList<>(getDestinations(player));
        if (index < 0 || index >= destinations.size()) return false;
        TeleportDestination destination = destinations.get(index);
        destinations.set(index, destination.withLocked(!destination.locked));
        writeDestinations(player, destinations);
        return true;
    }

    public static boolean teleport(EntityPlayerMP player, int index) {
        List<TeleportDestination> destinations = getDestinations(player);
        if (index < 0 || index >= destinations.size()) return false;

        TeleportDestination destination = destinations.get(index);
        if (!DimensionManager.isDimensionRegistered(destination.dimension)) {
            sendDimensionUnavailable(player);
            return false;
        }

        WorldServer targetWorld = DimensionManager.getWorld(destination.dimension);
        if (targetWorld == null) {
            DimensionManager.initDimension(destination.dimension);
            targetWorld = DimensionManager.getWorld(destination.dimension);
        }
        if (targetWorld == null) {
            sendDimensionUnavailable(player);
            return false;
        }

        targetWorld.getChunkFromBlockCoords(destination.x, destination.z);
        int targetY = findSafeY(targetWorld, destination.x, destination.y, destination.z);
        if (targetY < 0) {
            // #tr message.gtnc.teleport_terminal.destination_unsafe
            // # No safe space was found at this destination.
            // # zh_CN 目标位置没有可安全站立的空间。
            player.addChatMessage(new ChatComponentTranslation("message.gtnc.teleport_terminal.destination_unsafe"));
            return false;
        }

        double targetX = destination.x + 0.5D;
        double targetZ = destination.z + 0.5D;
        if (player.dimension == destination.dimension) {
            player.playerNetServerHandler
                .setPlayerLocation(targetX, targetY, targetZ, player.rotationYaw, player.rotationPitch);
        } else {
            MinecraftServer.getServer()
                .getConfigurationManager()
                .transferPlayerToDimension(
                    player,
                    destination.dimension,
                    new DirectTeleportTeleporter(targetWorld, targetX, targetY, targetZ));
        }
        return true;
    }

    /** Teleports beside an interface selected from the server-validated wireless interface terminal list. */
    public static boolean teleportNearInterface(EntityPlayerMP player, Util.DimensionalCoordSide target) {
        int dimension = target.getDimension();
        if (!DimensionManager.isDimensionRegistered(dimension)) {
            sendInterfaceDimensionUnavailable(player);
            return false;
        }

        WorldServer targetWorld = DimensionManager.getWorld(dimension);
        if (targetWorld == null) {
            DimensionManager.initDimension(dimension);
            targetWorld = DimensionManager.getWorld(dimension);
        }
        if (targetWorld == null) {
            sendInterfaceDimensionUnavailable(player);
            return false;
        }

        targetWorld.getChunkFromBlockCoords(target.x, target.z);
        int[] arrival = findSafeInterfaceArrival(targetWorld, target.x, target.y, target.z, target.getSide());
        if (arrival == null) {
            // #tr sciencenotcool.message.interface_teleport.no_safe_location
            // # No safe standing space was found beside this interface.
            // # zh_CN 未在该接口旁找到安全的落脚位置。
            player.addChatMessage(
                new ChatComponentTranslation("sciencenotcool.message.interface_teleport.no_safe_location"));
            return false;
        }

        double targetX = arrival[0] + 0.5D;
        double targetY = arrival[1];
        double targetZ = arrival[2] + 0.5D;
        player.closeScreen();
        if (player.dimension == dimension) {
            player.playerNetServerHandler
                .setPlayerLocation(targetX, targetY, targetZ, player.rotationYaw, player.rotationPitch);
        } else {
            MinecraftServer.getServer()
                .getConfigurationManager()
                .transferPlayerToDimension(
                    player,
                    dimension,
                    new DirectTeleportTeleporter(targetWorld, targetX, targetY, targetZ));
        }
        return true;
    }

    private static void sendInterfaceDimensionUnavailable(EntityPlayerMP player) {
        // #tr sciencenotcool.message.interface_teleport.dimension_unavailable
        // # The interface dimension is currently unavailable.
        // # zh_CN 接口所在维度当前不可用。
        player.addChatMessage(
            new ChatComponentTranslation("sciencenotcool.message.interface_teleport.dimension_unavailable"));
    }

    private static int[] findSafeInterfaceArrival(WorldServer world, int x, int y, int z,
        ForgeDirection preferredSide) {
        ForgeDirection[] horizontal = { ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.WEST,
            ForgeDirection.EAST };
        if (preferredSide != null && preferredSide != ForgeDirection.UNKNOWN && preferredSide.offsetY == 0) {
            int[] preferred = findSafeColumn(world, x + preferredSide.offsetX, y, z + preferredSide.offsetZ);
            if (preferred != null) return preferred;
        }
        for (ForgeDirection direction : horizontal) {
            if (direction == preferredSide) continue;
            int[] result = findSafeColumn(world, x + direction.offsetX, y, z + direction.offsetZ);
            if (result != null) return result;
        }
        for (int radius = 2; radius <= 12; radius++) {
            for (int offsetX = -radius; offsetX <= radius; offsetX++) {
                for (int offsetZ = -radius; offsetZ <= radius; offsetZ++) {
                    if (Math.abs(offsetX) != radius && Math.abs(offsetZ) != radius) continue;
                    int[] result = findSafeColumn(world, x + offsetX, y, z + offsetZ);
                    if (result != null) return result;
                }
            }
        }
        return null;
    }

    private static int[] findSafeColumn(WorldServer world, int x, int targetY, int z) {
        for (int deltaY = 0; deltaY <= 12; deltaY++) {
            int above = targetY + deltaY;
            if (isSafe(world, x, above, z)) return new int[] { x, above, z };
            if (deltaY > 0) {
                int below = targetY - deltaY;
                if (isSafe(world, x, below, z)) return new int[] { x, below, z };
            }
        }
        return null;
    }

    private static void sendDimensionUnavailable(EntityPlayerMP player) {
        // #tr message.gtnc.teleport_terminal.dimension_unavailable
        // # The destination dimension is no longer available.
        // # zh_CN 目标维度当前不可用。
        player.addChatMessage(new ChatComponentTranslation("message.gtnc.teleport_terminal.dimension_unavailable"));
    }

    private static void sendLocked(EntityPlayerMP player) {
        // #tr message.gtnc.teleport_terminal.destination_locked
        // # Unlock this destination before changing it.
        // # zh_CN 请先解锁此坐标点再进行修改。
        player.addChatMessage(new ChatComponentTranslation("message.gtnc.teleport_terminal.destination_locked"));
    }

    private static int findSafeY(WorldServer world, int x, int requestedY, int z) {
        if (isSafe(world, x, requestedY, z)) return requestedY;
        int topY = world.getTopSolidOrLiquidBlock(x, z);
        return isSafe(world, x, topY, z) ? topY : -1;
    }

    private static boolean isSafe(WorldServer world, int x, int y, int z) {
        if (y < 1 || y >= world.getActualHeight() - 1) return false;
        if (!world.isAirBlock(x, y, z) || !world.isAirBlock(x, y + 1, z)) return false;
        Block floor = world.getBlock(x, y - 1, z);
        return floor != null && floor.getMaterial()
            .blocksMovement();
    }

    private static void writeDestinations(EntityPlayer player, List<TeleportDestination> destinations) {
        NBTTagList destinationsTag = new NBTTagList();
        for (TeleportDestination destination : destinations) {
            destinationsTag.appendTag(destination.writeToNbt());
        }
        getRootTag(player).setTag(DESTINATIONS_TAG, destinationsTag);
    }

    private static NBTTagCompound getRootTag(EntityPlayer player) {
        NBTTagCompound entityTag = player.getEntityData();
        if (!entityTag.hasKey(PERSISTED_TAG, 10)) entityTag.setTag(PERSISTED_TAG, new NBTTagCompound());
        NBTTagCompound persistedTag = entityTag.getCompoundTag(PERSISTED_TAG);
        if (!persistedTag.hasKey(ROOT_TAG, 10)) persistedTag.setTag(ROOT_TAG, new NBTTagCompound());
        return persistedTag.getCompoundTag(ROOT_TAG);
    }

    private static String sanitizeName(String name) {
        if (name == null) return "";
        StringBuilder result = new StringBuilder();
        for (int index = 0; index < name.length() && result.length() < 32; index++) {
            char character = name.charAt(index);
            if (character >= 32 && character != 127) result.append(character);
        }
        return result.toString()
            .trim();
    }

    private static final class DirectTeleportTeleporter extends Teleporter {

        private final double x;
        private final double y;
        private final double z;

        private DirectTeleportTeleporter(WorldServer world, double x, double y, double z) {
            super(world);
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public void placeInPortal(Entity entity, double ignoredX, double ignoredY, double ignoredZ, float yaw) {
            entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
            entity.motionX = 0.0D;
            entity.motionY = 0.0D;
            entity.motionZ = 0.0D;
        }
    }
}
