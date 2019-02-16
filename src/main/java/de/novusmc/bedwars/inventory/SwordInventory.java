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

import static de.novusmc.bedwars.manager.SpawnerManager.SpawnerType.*;

/**
 * Created by Paul
 * on 03.01.2019
 *
 * @author pauhull
 */
public class SwordInventory implements Listener {

    private static final String TITLE = "§4Schwerter";
    private static final ItemStack STICK = new ItemBuilder(Material.STICK).setDisplayName("§9Knüppel").setLore(" ", "§8» §7Kosten: §c10 Bronze", " ").addEnchant(Enchantment.KNOCKBACK, 1, true).build();
    private static final ItemStack GOLD_SWORD_1 = new ItemBuilder(Material.GOLD_SWORD).setDisplayName("§9Goldschwert I").setLore(" ", "§8» §7Kosten: §f1 Eisen", " ").addEnchant(Enchantment.DURABILITY, 1, true).addEnchant(Enchantment.DAMAGE_ALL, 1, true).build();
    private static final ItemStack GOLD_SWORD_2 = new ItemBuilder(Material.GOLD_SWORD).setDisplayName("§9Goldschwert II").setLore(" ", "§8» §7Kosten: §f3 Eisen", " ").addEnchant(Enchantment.DAMAGE_ALL, 2, true).addEnchant(Enchantment.DURABILITY, 1, true).build();
    private static final ItemStack IRON_SWORD = new ItemBuilder(Material.IRON_SWORD).setDisplayName("§9Eisenschwert").setLore(" ", "§8» §7Kosten: §65 Gold", " ").addEnchant(Enchantment.DAMAGE_ALL, 2, true).addEnchant(Enchantment.DURABILITY, 1, true).addEnchant(Enchantment.KNOCKBACK, 1, true).build();

    private ShopInventory shopInventory;

    public SwordInventory(ShopInventory shopInventory) {
        this.shopInventory = shopInventory;
        Bukkit.getPluginManager().registerEvents(this, shopInventory.getBedWars());
    }

    public void show(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, TITLE);
        this.shopInventory.setShopInventory(inventory, 0);

        // enchant block item
        ItemStack stack = inventory.getItem(12);
        ItemMeta meta = stack.getItemMeta();
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        stack.setItemMeta(meta);
        inventory.setItem(12, stack);

        inventory.setItem(37, STICK);
        inventory.setItem(39, GOLD_SWORD_1);
        inventory.setItem(40, GOLD_SWORD_2);
        inventory.setItem(42, IRON_SWORD);

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
                if (stack.equals(STICK)) {
                    shopInventory.buyItem(player, BRONZE.getItem(), STICK, 10, buyWholeStack);
                } else if (stack.equals(GOLD_SWORD_1)) {
                    shopInventory.buyItem(player, IRON.getItem(), GOLD_SWORD_1, 1, buyWholeStack);
                } else if (stack.equals(GOLD_SWORD_2)) {
                    shopInventory.buyItem(player, IRON.getItem(), GOLD_SWORD_2, 3, buyWholeStack);
                } else if (stack.equals(IRON_SWORD)) {
                    shopInventory.buyItem(player, GOLD.getItem(), IRON_SWORD, 5, buyWholeStack);
                }
            }

            shopInventory.checkForCategoryClick(player, stack);
        }
    }

}
