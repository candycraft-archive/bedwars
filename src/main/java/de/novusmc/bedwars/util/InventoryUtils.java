package de.novusmc.bedwars.util;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Paul
 * on 03.01.2019
 *
 * @author pauhull
 */
public class InventoryUtils {

    public static int countItems(Inventory inventory, ItemStack stack) {
        int count = 0;
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack slot = inventory.getItem(i);

            if (slot != null && new ItemBuilder(slot).setAmount(1).equals(new ItemBuilder(stack).setAmount(1))) {
                count += slot.getAmount();
            }
        }

        return count;
    }

    public static int removeItems(Inventory inventory, ItemStack stack, int amount) {
        int remaining = amount;
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack slot = inventory.getItem(i);

            if (slot == null || !new ItemBuilder(stack).setAmount(1).equals(new ItemBuilder(slot).setAmount(1)))
                continue;

            if (slot.getAmount() <= remaining) {
                remaining -= slot.getAmount();
                inventory.setItem(i, null);
            } else {
                slot.setAmount(slot.getAmount() - remaining);
                remaining = 0;
            }

            if (remaining == 0)
                return 0;
        }

        return remaining;
    }

    public static int getFreeSpace(Inventory inventory, ItemStack stack) {
        int freeSpace = 0;
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack slot = inventory.getItem(i);
            if (slot == null) {
                freeSpace += 64;
                continue;
            }

            // everything is same except amount
            if (new ItemBuilder(slot).setAmount(1).equals(new ItemBuilder(stack).setAmount(1))) {
                freeSpace += 64 - slot.getAmount();
            }
        }

        return freeSpace;
    }

}
