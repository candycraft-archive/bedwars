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
public class PickaxeInventory implements Listener {

    /*
         pickaxes:
            wooden -> eff. 1, unbreaking 1 = 2 bronze
            stone -> eff. 1, unbreaking 1 = 2 iron
            iron -> eff. 1, unbreaking 1 = 1     gold
     */

    private static final String TITLE = "§3Spitzhacken";
    private static final ItemStack WOODEN_PICKAXE = new ItemBuilder(Material.WOOD_PICKAXE).setDisplayName("§9Holzspitzhacke").setLore(" ", "§8» §7Kosten: §c4 Bronze", " ").addEnchant(Enchantment.DURABILITY, 1, true).addEnchant(Enchantment.DIG_SPEED, 1, true).hideAttributes().build();
    private static final ItemStack STONE_PICKAXE = new ItemBuilder(Material.STONE_PICKAXE).setDisplayName("§9Steinspitzhacke").setLore(" ", "§8» §7Kosten: §f2 Eisen", " ").addEnchant(Enchantment.DURABILITY, 1, true).addEnchant(Enchantment.DIG_SPEED, 1, true).hideAttributes().build();
    private static final ItemStack IRON_PICKAXE = new ItemBuilder(Material.IRON_PICKAXE).setDisplayName("§9Eisenspitzhacke").setLore(" ", "§8» §7Kosten: §61 Gold", " ").addEnchant(Enchantment.DURABILITY, 1, true).addEnchant(Enchantment.DIG_SPEED, 1, true).hideAttributes().build();

    private ShopInventory shopInventory;

    public PickaxeInventory(ShopInventory shopInventory) {
        this.shopInventory = shopInventory;
        Bukkit.getPluginManager().registerEvents(this, shopInventory.getBedWars());
    }

    public void show(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, TITLE);
        this.shopInventory.setShopInventory(inventory, 0);

        // enchant block item
        ItemStack stack = inventory.getItem(11);
        ItemMeta meta = stack.getItemMeta();
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        stack.setItemMeta(meta);
        inventory.setItem(11, stack);

        inventory.setItem(38, WOODEN_PICKAXE);
        inventory.setItem(40, STONE_PICKAXE);
        inventory.setItem(42, IRON_PICKAXE);

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
                if (stack.equals(WOODEN_PICKAXE)) {
                    shopInventory.buyItem(player, BRONZE.getItem(), WOODEN_PICKAXE, 4, buyWholeStack);
                } else if (stack.equals(STONE_PICKAXE)) {
                    shopInventory.buyItem(player, IRON.getItem(), STONE_PICKAXE, 2, buyWholeStack);
                } else if (stack.equals(IRON_PICKAXE)) {
                    shopInventory.buyItem(player, GOLD.getItem(), IRON_PICKAXE, 1, buyWholeStack);
                }
            }

            shopInventory.checkForCategoryClick(player, stack);
        }
    }

}
