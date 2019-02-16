package de.novusmc.bedwars.util;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.concurrent.TimeUnit;

public class SkullCache {

    private TimedHashMap<String, net.minecraft.server.v1_8_R3.ItemStack> skulls;

    public SkullCache() {
        this(TimeUnit.MINUTES, 30);
    }

    public SkullCache(TimeUnit unit, long expireAfter) {
        this.skulls = new TimedHashMap<>(unit, expireAfter);
    }

    public ItemStack getSkull(String owner) {
        if (skulls.containsKey(owner)) {
            return CraftItemStack.asBukkitCopy(skulls.get(owner));
        } else {
            saveSkull(owner);
            return getSkull(owner);
        }
    }

    public void saveSkull(String owner) {
        ItemStack stack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        meta.setOwner(owner);
        stack.setItemMeta(meta);
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);
        skulls.put(owner, nmsStack);
    }

}
