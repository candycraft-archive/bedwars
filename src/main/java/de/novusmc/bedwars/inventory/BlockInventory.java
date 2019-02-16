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
public class BlockInventory implements Listener {

    /*
<00:30:35> "GodTitan": clay, endstone, glowstone, glass(je nach team farbe[wie beim clay]), iron_block,
<00:30:46> "pauhull": ok
<00:30:50> "pauhull": und die kosten jeweils
<00:30:55> "pauhull": habe bei clay jetzt 1 bronze = 1 clay
<00:31:09> "GodTitan": 1 bronze = 4 clay oder?
<00:31:17> "GodTitan": so wie auf gomme der sandstone
<00:31:26> "pauhull": ok
<00:32:23> "GodTitan": mach aber  1 iron_block = 5 eisen
<00:32:42> "pauhull": ok
     */

    private static final String TITLE = "§bBlöcke";
    private static final ItemStack CLAY = new ItemBuilder(Material.STAINED_CLAY, 2).setDisplayName("§9Blöcke").setLore(" ", "§8» §7Kosten: §c1 Bronze", " ").build();
    private static final ItemStack END_STONE = new ItemBuilder(Material.ENDER_STONE).setDisplayName("§9Endstein").setLore(" ", "§8» §7Kosten: §c7 Bronze", " ").build();
    private static final ItemStack ICE = new ItemBuilder(Material.ICE).setDisplayName("§9Eis").setLore(" ", "§8» §7Kosten: §c2 Bronze", " ").build();
    private static final ItemStack GLASS = new ItemBuilder(Material.GLASS).setDisplayName("§9Glas").setLore(" ", "§8» §7Kosten: §c4 Bronze", " ").build();
    private static final ItemStack IRON_BLOCK = new ItemBuilder(Material.IRON_BLOCK).setDisplayName("§9Eisenblock").setLore(" ", "§8» §7Kosten: §f3 Eisen", " ").build();

    private ShopInventory shopInventory;

    public BlockInventory(ShopInventory shopInventory) {
        this.shopInventory = shopInventory;
        Bukkit.getPluginManager().registerEvents(this, shopInventory.getBedWars());
    }

    public void show(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, TITLE);
        this.shopInventory.setShopInventory(inventory, 0);

        // enchant block item
        ItemStack stack = inventory.getItem(9);
        ItemMeta meta = stack.getItemMeta();
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        stack.setItemMeta(meta);
        inventory.setItem(9, stack);

        Team team = Team.getTeam(player);

        if (team != null) {
            inventory.setItem(37, new ItemBuilder(CLAY).setDurability(team.getDyeColor().getWoolData()).build());
        }

        inventory.setItem(39, ICE);
        inventory.setItem(40, GLASS);
        inventory.setItem(41, END_STONE);
        inventory.setItem(43, IRON_BLOCK);

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
                if (stack.getType() == CLAY.getType() && team != null) {
                    shopInventory.buyItem(player, BRONZE.getItem(), new ItemBuilder(CLAY).setDurability(team.getDyeColor().getWoolData()).build(), 1, buyWholeStack);
                } else if (stack.equals(END_STONE)) {
                    shopInventory.buyItem(player, BRONZE.getItem(), END_STONE, 7, buyWholeStack);
                } else if (stack.equals(ICE)) {
                    shopInventory.buyItem(player, BRONZE.getItem(), ICE, 2, buyWholeStack);
                } else if (stack.equals(GLASS)) {
                    shopInventory.buyItem(player, BRONZE.getItem(), GLASS, 4, buyWholeStack);
                } else if (stack.equals(IRON_BLOCK)) {
                    shopInventory.buyItem(player, IRON.getItem(), IRON_BLOCK, 3, buyWholeStack);
                }
            }

            shopInventory.checkForCategoryClick(player, stack);
        }
    }

}
