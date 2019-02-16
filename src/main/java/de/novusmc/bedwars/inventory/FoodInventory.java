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

import static de.novusmc.bedwars.manager.SpawnerManager.SpawnerType.BRONZE;
import static de.novusmc.bedwars.manager.SpawnerManager.SpawnerType.IRON;

/**
 * Created by Paul
 * on 03.01.2019
 *
 * @author pauhull
 */
public class FoodInventory implements Listener {

    private static final String TITLE = "§6Essen";
    private static final ItemStack APPLES = new ItemBuilder(Material.APPLE, 2).setDisplayName("§9Äpfel").setLore(" ", "§8» §7Kosten: §c1 Bronze", " ").build();
    private static final ItemStack STEAK = new ItemBuilder(Material.COOKED_BEEF).setDisplayName("§9Steak").setLore(" ", "§8» §7Kosten: §c2 Bronze", " ").build();
    private static final ItemStack COOKIES = new ItemBuilder(Material.COOKIE, 4).setDisplayName("§9Kekse").setLore(" ", "§8» §7Kosten: §c1 Bronze", " ").build();
    private static final ItemStack CAKE = new ItemBuilder(Material.CAKE).setDisplayName("§9Kuchen").setLore(" ", "§8» §7Kosten: §c5 Bronze", " ").build();
    private static final ItemStack GOLDEN_APPLE = new ItemBuilder(Material.GOLDEN_APPLE).setDisplayName("§9Goldapfel").setLore(" ", "§8» §7Kosten: §f5 Eisen", " ").build();

    private ShopInventory shopInventory;

    public FoodInventory(ShopInventory shopInventory) {
        this.shopInventory = shopInventory;
        Bukkit.getPluginManager().registerEvents(this, shopInventory.getBedWars());
    }

    public void show(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, TITLE);
        this.shopInventory.setShopInventory(inventory, 0);

        // enchant block item
        ItemStack stack = inventory.getItem(14);
        ItemMeta meta = stack.getItemMeta();
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        stack.setItemMeta(meta);
        inventory.setItem(14, stack);

        inventory.setItem(37, APPLES);
        inventory.setItem(39, STEAK);
        inventory.setItem(40, COOKIES);
        inventory.setItem(41, CAKE);
        inventory.setItem(43, GOLDEN_APPLE);

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
                if (stack.equals(APPLES)) {
                    shopInventory.buyItem(player, BRONZE.getItem(), APPLES, 1, buyWholeStack);
                } else if (stack.equals(STEAK)) {
                    shopInventory.buyItem(player, BRONZE.getItem(), STEAK, 2, buyWholeStack);
                } else if (stack.equals(COOKIES)) {
                    shopInventory.buyItem(player, BRONZE.getItem(), COOKIES, 1, buyWholeStack);
                } else if (stack.equals(CAKE)) {
                    shopInventory.buyItem(player, BRONZE.getItem(), CAKE, 5, buyWholeStack);
                } else if (stack.equals(GOLDEN_APPLE)) {
                    shopInventory.buyItem(player, IRON.getItem(), GOLDEN_APPLE, 5, buyWholeStack);
                }
            }

            shopInventory.checkForCategoryClick(player, stack);
        }
    }

}
