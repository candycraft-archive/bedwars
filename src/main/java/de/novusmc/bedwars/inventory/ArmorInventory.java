package de.novusmc.bedwars.inventory;

import de.novusmc.bedwars.game.Team;
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
public class ArmorInventory implements Listener {

    /*
        armor:
         leather -> boots, helmet = 1 bronze
                    chestplate, leggings = 2 bronze
         chainmail -> unbreaking 1 = 1 iron
                      unbreaking 1, protection 1 = 3 iron
                      unbreaking 1, protection 2 = 7 iron
     */

    private static final String TITLE = "§5Rüstung";
    private static final ItemStack LEATHER_HELMET = new ItemBuilder(Material.LEATHER_HELMET).setDisplayName("§9Lederhelm").setLore(" ", "§8» §7Kosten: §c1 Bronze", " ").addEnchant(Enchantment.DURABILITY, 1, true).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true).build();
    private static final ItemStack LEATHER_CHESTPLATE = new ItemBuilder(Material.LEATHER_CHESTPLATE).setDisplayName("§9Lederbrustplatte").setLore(" ", "§8» §7Kosten: §c2 Bronze", " ").addEnchant(Enchantment.DURABILITY, 1, true).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true).build();
    private static final ItemStack LEATHER_LEGGINGS = new ItemBuilder(Material.LEATHER_LEGGINGS).setDisplayName("§9Röhrenjeans").setLore(" ", "§8» §7Kosten: §c2 Bronze", " ").addEnchant(Enchantment.DURABILITY, 1, true).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true).build();
    private static final ItemStack LEATHER_BOOTS = new ItemBuilder(Material.LEATHER_BOOTS).setDisplayName("§9Lederstiefel").setLore(" ", "§8» §7Kosten: §c1 Bronze", " ").addEnchant(Enchantment.DURABILITY, 1, true).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true).build();
    private static final ItemStack CHESTPLATE_1 = new ItemBuilder(Material.CHAINMAIL_CHESTPLATE).setDisplayName("§9Brustplatte I").setLore(" ", "§8» §7Kosten: §f1 Eisen", " ").addEnchant(Enchantment.DURABILITY, 1, true).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true).build();
    private static final ItemStack CHESTPLATE_2 = new ItemBuilder(Material.CHAINMAIL_CHESTPLATE).setDisplayName("§9Brustplatte II").setLore(" ", "§8» §7Kosten: §f3 Eisen", " ").addEnchant(Enchantment.DURABILITY, 1, true).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true).build();
    private static final ItemStack CHESTPLATE_3 = new ItemBuilder(Material.CHAINMAIL_CHESTPLATE).setDisplayName("§9Brustplatte III").setLore(" ", "§8» §7Kosten: §f7 Eisen", " ").addEnchant(Enchantment.DURABILITY, 1, true).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3, true).build();

    private ShopInventory shopInventory;

    public ArmorInventory(ShopInventory shopInventory) {
        this.shopInventory = shopInventory;
        Bukkit.getPluginManager().registerEvents(this, shopInventory.getBedWars());
    }

    public void show(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, TITLE);
        this.shopInventory.setShopInventory(inventory, 0);

        // enchant block item
        ItemStack stack = inventory.getItem(10);
        ItemMeta meta = stack.getItemMeta();
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        stack.setItemMeta(meta);
        inventory.setItem(10, stack);

        Team team = Team.getTeam(player);

        if (team != null) {
            inventory.setItem(37, new ItemBuilder(LEATHER_HELMET).setColor(team.getDyeColor().getColor()).build());
            inventory.setItem(38, new ItemBuilder(LEATHER_CHESTPLATE).setColor(team.getDyeColor().getColor()).build());
            inventory.setItem(39, new ItemBuilder(LEATHER_LEGGINGS).setColor(team.getDyeColor().getColor()).build());
            inventory.setItem(40, new ItemBuilder(LEATHER_BOOTS).setColor(team.getDyeColor().getColor()).build());
        }

        inventory.setItem(41, CHESTPLATE_1);
        inventory.setItem(42, CHESTPLATE_2);
        inventory.setItem(43, CHESTPLATE_3);

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
                Team team = Team.getTeam(player);
                if (stack.getType() == LEATHER_HELMET.getType() && team != null) {
                    shopInventory.buyItem(player, BRONZE.getItem(), new ItemBuilder(LEATHER_HELMET).setColor(team.getDyeColor().getColor()).build(), 1, buyWholeStack);
                } else if (stack.getType() == LEATHER_CHESTPLATE.getType() && team != null) {
                    shopInventory.buyItem(player, BRONZE.getItem(), new ItemBuilder(LEATHER_CHESTPLATE).setColor(team.getDyeColor().getColor()).build(), 2, buyWholeStack);
                } else if (stack.getType() == LEATHER_LEGGINGS.getType() && team != null) {
                    shopInventory.buyItem(player, BRONZE.getItem(), new ItemBuilder(LEATHER_LEGGINGS).setColor(team.getDyeColor().getColor()).build(), 2, buyWholeStack);
                } else if (stack.getType() == LEATHER_BOOTS.getType() && team != null) {
                    shopInventory.buyItem(player, BRONZE.getItem(), new ItemBuilder(LEATHER_BOOTS).setColor(team.getDyeColor().getColor()).build(), 1, buyWholeStack);
                } else if (stack.equals(CHESTPLATE_1)) {
                    shopInventory.buyItem(player, IRON.getItem(), CHESTPLATE_1, 1, buyWholeStack);
                } else if (stack.equals(CHESTPLATE_2)) {
                    shopInventory.buyItem(player, IRON.getItem(), CHESTPLATE_2, 3, buyWholeStack);
                } else if (stack.equals(CHESTPLATE_3)) {
                    shopInventory.buyItem(player, IRON.getItem(), CHESTPLATE_3, 7, buyWholeStack);
                }
            }

            shopInventory.checkForCategoryClick(player, stack);
        }
    }

}
