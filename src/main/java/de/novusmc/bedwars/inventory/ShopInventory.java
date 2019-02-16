package de.novusmc.bedwars.inventory;

import de.novusmc.bedwars.BedWars;
import de.novusmc.bedwars.util.InventoryUtils;
import de.novusmc.bedwars.util.ItemBuilder;
import lombok.Getter;
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

public class ShopInventory implements Listener {

    private static final String TITLE = "§cShop";
    private static final ItemStack GLASS_PANE = new ItemBuilder(Material.STAINED_GLASS_PANE).setDisplayName(" ").build();
    private static final ItemStack BLOCKS = new ItemBuilder(Material.SANDSTONE).setDisplayName("§bBlöcke").build();
    private static final ItemStack ARMOR = new ItemBuilder(Material.CHAINMAIL_CHESTPLATE).setDisplayName("§5Rüstung").build();
    private static final ItemStack PICKAXES = new ItemBuilder(Material.IRON_PICKAXE).setDisplayName("§3Spitzhacken").hideAttributes().build();
    private static final ItemStack SWORDS = new ItemBuilder(Material.GOLD_SWORD).setDisplayName("§4Schwerter").hideAttributes().build();
    private static final ItemStack BOWS = new ItemBuilder(Material.BOW).setDisplayName("§cBögen").build();
    private static final ItemStack FOOD = new ItemBuilder(Material.APPLE).setDisplayName("§6Essen").build();
    private static final ItemStack CHESTS = new ItemBuilder(Material.CHEST).setDisplayName("§aKisten").build();
    private static final ItemStack POTIONS = new ItemBuilder(Material.POTION).setDisplayName("§dTränke").build();
    private static final ItemStack EXTRAS = new ItemBuilder(Material.TNT).setDisplayName("§eExtras").build();

    @Getter
    private BedWars bedWars;

    private BlockInventory blockInventory;
    private ArmorInventory armorInventory;
    private PickaxeInventory pickaxeInventory;
    private SwordInventory swordInventory;
    private BowInventory bowInventory;
    private FoodInventory foodInventory;
    private ChestInventory chestInventory;
    private PotionInventory potionInventory;
    private ExtrasInventory extrasInventory;

    public ShopInventory(BedWars bedWars) {
        this.bedWars = bedWars;
        Bukkit.getPluginManager().registerEvents(this, bedWars);

        this.blockInventory = new BlockInventory(this);
        this.armorInventory = new ArmorInventory(this);
        this.pickaxeInventory = new PickaxeInventory(this);
        this.swordInventory = new SwordInventory(this);
        this.bowInventory = new BowInventory(this);
        this.foodInventory = new FoodInventory(this);
        this.chestInventory = new ChestInventory(this);
        this.potionInventory = new PotionInventory(this);
        this.extrasInventory = new ExtrasInventory(this);
    }

    public void show(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9 * 3, TITLE);
        setShopInventory(inventory, 0);
        player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
        player.openInventory(inventory);
    }

    void setShopInventory(Inventory inventory, int offset) {
        for (int i = 0; i < 27; i++) {
            if (i < 9 || i > 17) {
                inventory.setItem(i + offset, GLASS_PANE);
            }
        }
        inventory.setItem(9 + offset, BLOCKS);
        inventory.setItem(10 + offset, ARMOR);
        inventory.setItem(11 + offset, PICKAXES);
        inventory.setItem(12 + offset, SWORDS);
        inventory.setItem(13 + offset, BOWS);
        inventory.setItem(14 + offset, FOOD);
        inventory.setItem(15 + offset, CHESTS);
        inventory.setItem(16 + offset, POTIONS);
        inventory.setItem(17 + offset, EXTRAS);
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
            checkForCategoryClick(player, stack);
        }
    }

    void checkForCategoryClick(Player player, ItemStack stack) {

        if (stack.equals(BLOCKS)) {
            blockInventory.show(player);
        } else if (stack.equals(ARMOR)) {
            armorInventory.show(player);
        } else if (stack.equals(PICKAXES)) {
            pickaxeInventory.show(player);
        } else if (stack.equals(SWORDS)) {
            swordInventory.show(player);
        } else if (stack.equals(BOWS)) {
            bowInventory.show(player);
        } else if (stack.equals(FOOD)) {
            foodInventory.show(player);
        } else if (stack.equals(CHESTS)) {
            chestInventory.show(player);
        } else if (stack.equals(POTIONS)) {
            potionInventory.show(player);
        } else if (stack.equals(EXTRAS)) {
            extrasInventory.show(player);
        }
    }

    void buyItem(Player player, ItemStack buyItem, ItemStack stack, int cost, boolean buyWholeStack) {
        ItemStack strippedStack = new ItemBuilder(stack).stripLore().build();
        int freeSpace = InventoryUtils.getFreeSpace(player.getInventory(), strippedStack);
        int bronzeCount = InventoryUtils.countItems(player.getInventory(), buyItem);
        if (bronzeCount < cost || freeSpace < strippedStack.getAmount()) {
            player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
            return;
        }

        int amount = 1;
        if (buyWholeStack) {
            amount = Math.min(bronzeCount, 64) / cost; // spend 1 stack of bronze at once at most
        }
        amount = Math.min(amount, freeSpace / strippedStack.getAmount());

        InventoryUtils.removeItems(player.getInventory(), buyItem, amount * cost);
        player.getInventory().addItem(new ItemBuilder(strippedStack).setAmount(amount * strippedStack.getAmount()).build());
        player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1, 1);
    }

}
