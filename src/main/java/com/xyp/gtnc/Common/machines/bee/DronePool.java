package com.xyp.gtnc.Common.machines.bee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * 雄蜂池管理器
 * <p>
 * 按品种存储和管理雄蜂，支持自动补充机制
 */
public class DronePool {

    /** 按品种名存储的雄蜂列表 */
    private final Map<String, List<ItemStack>> droneInventory;

    /** 需要补充雄蜂的品种队列 */
    private final List<String> replenishQueue;

    /** 每个品种的最小雄蜂数量（低于此数量触发补充） */
    private static final int MIN_DRONE_COUNT = 8;

    public DronePool() {
        this.droneInventory = new HashMap<>();
        this.replenishQueue = new ArrayList<>();
    }

    /**
     * 添加雄蜂到池中
     */
    public void addDrone(ItemStack droneStack) {
        if (droneStack == null || !BeeBreedingHelper.isDrone(droneStack)) return;

        // 从实际基因组中获取唯一 UID，确保同 unlocalizedName 但不同 UID 的品种不会混淆
        String uid = BeeBreedingHelper.getBeeUID(droneStack);
        if (uid == null) return;

        List<ItemStack> drones = droneInventory.computeIfAbsent(uid, k -> new ArrayList<>());

        for (ItemStack existing : drones) {
            if (existing.stackSize < existing.getMaxStackSize()) {
                int toAdd = Math.min(droneStack.stackSize, existing.getMaxStackSize() - existing.stackSize);
                existing.stackSize += toAdd;
                droneStack.stackSize -= toAdd;
                if (droneStack.stackSize <= 0) return;
            }
        }

        if (droneStack.stackSize > 0) {
            drones.add(droneStack.copy());
        }
    }

    /**
     * 检查指定品种是否有雄蜂
     */
    public boolean hasDrone(String species) {
        return getDroneCount(species) > 0;
    }

    /**
     * 获取指定品种的雄蜂数量
     */
    public int getDroneCount(String species) {
        List<ItemStack> drones = droneInventory.get(species);
        if (drones == null) return 0;

        int count = 0;
        for (ItemStack stack : drones) {
            count += stack.stackSize;
        }
        return count;
    }

    /**
     * 获取所有有雄蜂的品种
     */
    public Set<String> getAvailableSpecies() {
        return droneInventory.keySet();
    }

    /**
     * 清空所有雄蜂
     */
    public void clear() {
        droneInventory.clear();
        replenishQueue.clear();
    }

    // ==================== NBT 序列化 ====================

    public NBTTagCompound toNBT() {
        NBTTagCompound tag = new NBTTagCompound();

        NBTTagList inventoryList = new NBTTagList();
        for (Map.Entry<String, List<ItemStack>> entry : droneInventory.entrySet()) {
            NBTTagCompound speciesTag = new NBTTagCompound();
            speciesTag.setString("species", entry.getKey());

            NBTTagList droneList = new NBTTagList();
            for (ItemStack stack : entry.getValue()) {
                NBTTagCompound stackTag = new NBTTagCompound();
                stack.writeToNBT(stackTag);
                droneList.appendTag(stackTag);
            }
            speciesTag.setTag("drones", droneList);
            inventoryList.appendTag(speciesTag);
        }
        tag.setTag("inventory", inventoryList);

        NBTTagList queueList = new NBTTagList();
        for (String species : replenishQueue) {
            NBTTagCompound queueTag = new NBTTagCompound();
            queueTag.setString("species", species);
            queueList.appendTag(queueTag);
        }
        tag.setTag("replenishQueue", queueList);

        return tag;
    }

    public static DronePool fromNBT(NBTTagCompound tag) {
        DronePool pool = new DronePool();

        NBTTagList inventoryList = tag.getTagList("inventory", 10);
        for (int i = 0; i < inventoryList.tagCount(); i++) {
            NBTTagCompound speciesTag = inventoryList.getCompoundTagAt(i);
            String species = speciesTag.getString("species");
            List<ItemStack> drones = new ArrayList<>();

            NBTTagList droneList = speciesTag.getTagList("drones", 10);
            for (int j = 0; j < droneList.tagCount(); j++) {
                ItemStack stack = ItemStack.loadItemStackFromNBT(droneList.getCompoundTagAt(j));
                if (stack != null) {
                    drones.add(stack);
                }
            }

            if (!drones.isEmpty()) {
                pool.droneInventory.put(species, drones);
            }
        }

        NBTTagList queueList = tag.getTagList("replenishQueue", 10);
        for (int i = 0; i < queueList.tagCount(); i++) {
            pool.replenishQueue.add(
                queueList.getCompoundTagAt(i)
                    .getString("species"));
        }

        return pool;
    }
}
