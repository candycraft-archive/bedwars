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

/**
 * Created by Paul
 * on 03.01.2019
 *
 * @author pauhull
 */
public class BowInventory implements Listener {

    private static final String TITLE = "§cBögen";
    private static final ItemStack BOW_1 = new ItemBuilder(Material.BOW).setDisplayName("§9Bogen I").setLore(" ", "§8» §7Kosten: §63 Gold", " ").addEnchant(Enchantment.ARROW_INFINITE, 1, true).build();
    private static final ItemStack BOW_2 = new ItemBuilder(Material.BOW).setDisplayName("§9Bogen II").setLore(" ", "§8» §7Kosten: §67 Gold", " ").addEnchant(Enchantment.ARROW_DAMAGE, 1, true).addEnchant(Enchantment.ARROW_INFINITE, 1, true).build();
    private static final ItemStack BOW_3 = new ItemBuilder(Material.BOW).setDisplayName("§9Bogen III").setLore(" ", "§8» §7Kosten: §613 Gold", " ").addEnchant(Enchantment.ARROW_DAMAGE, 1, true).addEnchant(Enchantment.ARROW_KNOCKBACK, 1, true).addEnchant(Enchantment.ARROW_INFINITE, 1, true).build();
    private static final ItemStack ARROW = new ItemBuilder(Material.ARROW).setDisplayName("§9Pfeil").setLore(" ", "§8» §7Kosten: §61 Gold", " ").build();

    private ShopInventory shopInventory;

    public BowInventory(ShopInventory shopInventory) {
        this.shopInventory = shopInventory;
        Bukkit.getPluginManager().registerEvents(this, shopInventory.getBedWars());
    }

    public void show(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, TITLE);
        this.shopInventory.setShopInventory(inventory, 0);

        // enchant block item
        ItemStack stack = inventory.getItem(13);
        ItemMeta meta = stack.getItemMeta();
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        stack.setItemMeta(meta);
        inventory.setItem(13, stack);

        inventory.setItem(37, BOW_1);
        inventory.setItem(39, BOW_2);
        inventory.setItem(41, BOW_3);
        inventory.setItem(43, ARROW);

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
                if (stack.equals(BOW_1)) {
                    shopInventory.buyItem(player, GOLD.getItem(), BOW_1, 3, buyWholeStack);
                } else if (stack.equals(BOW_2)) {
                    shopInventory.buyItem(player, GOLD.getItem(), BOW_2, 7, buyWholeStack);
                } else if (stack.equals(BOW_3)) {
                    shopInventory.buyItem(player, GOLD.getItem(), BOW_3, 13, buyWholeStack);
                } else if (stack.equals(ARROW)) {
                    shopInventory.buyItem(player, GOLD.getItem(), ARROW, 1, buyWholeStack);
                }
            }

            shopInventory.checkForCategoryClick(player, stack);
        }
    }

}
