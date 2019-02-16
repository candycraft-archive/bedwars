package de.novusmc.bedwars.inventory;

import de.novusmc.bedwars.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import static de.novusmc.bedwars.manager.SpawnerManager.SpawnerType.GOLD;
import static de.novusmc.bedwars.manager.SpawnerManager.SpawnerType.IRON;

/**
 * Created by Paul
 * on 03.01.2019
 *
 * @author pauhull
 */
public class PotionInventory implements Listener {

    private static final String TITLE = "§dTränke";
    private static final ItemStack REGENERATION = new ItemBuilder(Material.POTION).setDisplayName("§9Regeneration").setLore(" ", "§8» §7Kosten: §f5 Eisen").setPotion(PotionType.REGEN, false, 1, false).build();
    private static final ItemStack STRENGTH = new ItemBuilder(Material.POTION).setDisplayName("§9Stärke").setLore(" ", "§8» §7Kosten: §66 Gold").setPotion(PotionType.STRENGTH, false, 1, false).build();
    private static final ItemStack JUMP_BOOST = new ItemBuilder(Material.POTION).setDisplayName("§9Sprungkraft").setLore(" ", "§8» §7Kosten: §f3 Eisen").setPotion(PotionType.JUMP, false, 1, false).build();
    private static final ItemStack INSTANT_HEALTH = new ItemBuilder(Material.POTION).setDisplayName("§9Heilung").setLore(" ", "§8» §7Kosten: §f4 Eisen").setPotion(PotionType.INSTANT_HEAL, false, 1, true).build();

    private ShopInventory shopInventory;

    public PotionInventory(ShopInventory shopInventory) {
        this.shopInventory = shopInventory;
        Bukkit.getPluginManager().registerEvents(this, shopInventory.getBedWars());
    }

    public void show(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, TITLE);
        this.shopInventory.setShopInventory(inventory, 0);

        // replace potion
        ItemStack stack = new ItemBuilder(Material.POTION)
                .setPotion(PotionType.STRENGTH, false, 1, false)
                .setDisplayName("§dTränke")
                .hidePotionEffects()
                .build();
        inventory.setItem(16, stack);

        inventory.setItem(37, REGENERATION);
        inventory.setItem(39, STRENGTH);
        inventory.setItem(41, JUMP_BOOST);
        inventory.setItem(43, INSTANT_HEALTH);

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
                if (stack.equals(REGENERATION)) {
                    shopInventory.buyItem(player, IRON.getItem(), REGENERATION, 5, buyWholeStack);
                } else if (stack.equals(STRENGTH)) {
                    shopInventory.buyItem(player, GOLD.getItem(), STRENGTH, 6, buyWholeStack);
                } else if (stack.equals(JUMP_BOOST)) {
                    shopInventory.buyItem(player, IRON.getItem(), JUMP_BOOST, 3, buyWholeStack);
                } else if (stack.equals(INSTANT_HEALTH)) {
                    shopInventory.buyItem(player, IRON.getItem(), INSTANT_HEALTH, 4, buyWholeStack);
                }
            }

            shopInventory.checkForCategoryClick(player, stack);
        }
    }

}
