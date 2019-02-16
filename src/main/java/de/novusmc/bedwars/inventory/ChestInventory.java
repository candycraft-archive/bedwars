package de.novusmc.bedwars.inventory;

import de.novusmc.bedwars.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static de.novusmc.bedwars.manager.SpawnerManager.SpawnerType.GOLD;
import static de.novusmc.bedwars.manager.SpawnerManager.SpawnerType.IRON;

/**
 * Created by Paul
 * on 03.01.2019
 *
 * @author pauhull
 */
public class ChestInventory implements Listener {

    private static final String TITLE = "§aKisten";
    private static final ItemStack CHEST = new ItemBuilder(Material.CHEST).setDisplayName("§9Kiste").setLore(" ", "§8» §7Kosten: §f2 Eisen", " ").build();
    private static final ItemStack ENDER_CHEST = new ItemBuilder(Material.ENDER_CHEST).setDisplayName("§9Teamkiste").setLore(" ", "§8» §7Kosten: §61 Gold", " ").build();

    private ShopInventory shopInventory;

    public ChestInventory(ShopInventory shopInventory) {
        this.shopInventory = shopInventory;
        Bukkit.getPluginManager().registerEvents(this, shopInventory.getBedWars());
    }

    public void show(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, TITLE);
        this.shopInventory.setShopInventory(inventory, 0);

        // enchant block item
        ItemStack stack = inventory.getItem(15);
        ItemMeta meta = stack.getItemMeta();
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        stack.setItemMeta(meta);
        inventory.setItem(15, stack);

        inventory.setItem(39, CHEST);
        inventory.setItem(41, ENDER_CHEST);

        player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        Player player = (Player) event.getWhoClicked();
        ItemStack stack = event.getCurrentItem();

        if (inventory == null || inventory.getTitle() == null || !inventory.getTitle().equals(TITLE)) {
            if (event.getInventory() != null && event.getInventory().getTitle() != null && event.getInventory().getTitle().equals(TITLE)
                    && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                // prevent putting things in the shop inventory
                event.setCancelled(true);
            }
            return;
        } else {
            event.setCancelled(true);
        }

        if (stack != null) {
            if (event.getAction() == InventoryAction.PICKUP_ALL || event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                boolean buyWholeStack = event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY && stack.getMaxStackSize() != 1;
                if (stack.equals(CHEST)) {
                    shopInventory.buyItem(player, IRON.getItem(), CHEST, 2, buyWholeStack);
                } else if (stack.equals(ENDER_CHEST)) {
                    shopInventory.buyItem(player, GOLD.getItem(), ENDER_CHEST, 1, buyWholeStack);
                }
            }

            shopInventory.checkForCategoryClick(player, stack);
        }
    }

}
