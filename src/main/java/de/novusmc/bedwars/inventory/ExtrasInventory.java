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
public class ExtrasInventory implements Listener {

    private static final String TITLE = "§eExtras";
    private static final ItemStack LADDER = new ItemBuilder(Material.LADDER).setDisplayName("§9Leiter").setLore(" ", "§8» §7Kosten: §c1 Bronze", " ").build();
    private static final ItemStack WEB = new ItemBuilder(Material.WEB).setDisplayName("§9Spinnennetz").setLore(" ", "§8» §7Kosten: §c16 Bronze", " ").build();
    private static final ItemStack BASE_TELEPORT = new ItemBuilder(Material.SULPHUR).setDisplayName("§9Baseteleport").setLore(" ", "§8» §7Kosten: §f3 Eisen", " ").build();
    public static final ItemStack BASE_TELEPORT_STRIPPED = new ItemBuilder(BASE_TELEPORT).stripLore().build();
    private static final ItemStack ROD = new ItemBuilder(Material.FISHING_ROD).setDisplayName("§9Angel").setLore(" ", "§8» §7Kosten: §f10 Eisen", " ").build();
    private static final ItemStack SHOP = new ItemBuilder(Material.EMERALD).setDisplayName("§9Mobiler Shop").setLore(" ", "§8» §7Kosten: §62 Gold", " ").build();
    public static final ItemStack SHOP_STRIPPED = new ItemBuilder(SHOP).stripLore().build();
    private static final ItemStack PLATFORM = new ItemBuilder(Material.BLAZE_ROD).setDisplayName("§9Rettungsplatform").setLore(" ", "§8» §7Kosten: §63 Gold", " ").build();
    public static final ItemStack PLATFORM_STRIPPED = new ItemBuilder(PLATFORM).stripLore().build();
    private static final ItemStack SNOWBALLS = new ItemBuilder(Material.SNOW_BALL, 8).setDisplayName("§9Schneebälle").setLore(" ", "§8» §7Kosten: §62 Gold", " ").build();
    private static final ItemStack BOOTS = new ItemBuilder(Material.CHAINMAIL_BOOTS).setDisplayName("§9Federfallschuhe").setLore(" ", "§8» §7Kosten: §f7 Eisen", " ").addEnchant(Enchantment.PROTECTION_FALL, 1, true).build();
    private static final ItemStack ENDER_PEARL = new ItemBuilder(Material.ENDER_PEARL).setDisplayName("§9Enderperle").setLore(" ", "§8» §7Kosten: §613 Gold", " ").build();
    private ShopInventory shopInventory;

    public ExtrasInventory(ShopInventory shopInventory) {
        this.shopInventory = shopInventory;
        Bukkit.getPluginManager().registerEvents(this, shopInventory.getBedWars());
    }

    public void show(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, TITLE);
        this.shopInventory.setShopInventory(inventory, 0);

        // enchant block item
        ItemStack stack = inventory.getItem(17);
        ItemMeta meta = stack.getItemMeta();
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        stack.setItemMeta(meta);
        inventory.setItem(17, stack);

        inventory.setItem(36, LADDER);
        inventory.setItem(37, WEB);
        inventory.setItem(38, BASE_TELEPORT);
        inventory.setItem(39, ROD);
        inventory.setItem(40, SHOP);
        inventory.setItem(41, PLATFORM);
        inventory.setItem(42, SNOWBALLS);
        inventory.setItem(43, BOOTS);
        inventory.setItem(44, ENDER_PEARL);

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
                if (stack.equals(LADDER)) {
                    shopInventory.buyItem(player, BRONZE.getItem(), LADDER, 1, buyWholeStack);
                } else if (stack.equals(WEB)) {
                    shopInventory.buyItem(player, BRONZE.getItem(), WEB, 16, buyWholeStack);
                } else if (stack.equals(BASE_TELEPORT)) {
                    shopInventory.buyItem(player, IRON.getItem(), BASE_TELEPORT, 3, buyWholeStack);
                } else if (stack.equals(ROD)) {
                    shopInventory.buyItem(player, IRON.getItem(), ROD, 10, buyWholeStack);
                } else if (stack.equals(SHOP)) {
                    shopInventory.buyItem(player, GOLD.getItem(), SHOP, 2, buyWholeStack);
                } else if (stack.equals(PLATFORM)) {
                    shopInventory.buyItem(player, GOLD.getItem(), PLATFORM, 3, buyWholeStack);
                } else if (stack.equals(SNOWBALLS)) {
                    shopInventory.buyItem(player, GOLD.getItem(), SNOWBALLS, 2, buyWholeStack);
                } else if (stack.equals(BOOTS)) {
                    shopInventory.buyItem(player, IRON.getItem(), BOOTS, 7, buyWholeStack);
                } else if (stack.equals(ENDER_PEARL)) {
                    shopInventory.buyItem(player, GOLD.getItem(), ENDER_PEARL, 13, buyWholeStack);
                }
            }

            shopInventory.checkForCategoryClick(player, stack);
        }
    }

}
