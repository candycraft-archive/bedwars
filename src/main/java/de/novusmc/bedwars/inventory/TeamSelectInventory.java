package de.novusmc.bedwars.inventory;

import de.novusmc.bedwars.BedWars;
import de.novusmc.bedwars.Messages;
import de.novusmc.bedwars.game.Team;
import de.novusmc.bedwars.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul
 * on 07.12.2018
 *
 * @author pauhull
 */
public class TeamSelectInventory implements Listener {

    private static final String TITLE = "§cTeam auswählen";
    private static final ItemStack BLACK_GLASS = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 15).setDisplayName(" ").build();

    private BedWars bedWars;

    public TeamSelectInventory(BedWars bedWars) {
        this.bedWars = bedWars;

        Bukkit.getPluginManager().registerEvents(this, bedWars);
    }

    public void show(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, TITLE);
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, BLACK_GLASS);
        }

        placeTeam(player, inventory, 9, Team.CYAN);
        placeTeam(player, inventory, 10, Team.GREEN);
        placeTeam(player, inventory, 11, Team.ORANGE);
        placeTeam(player, inventory, 12, Team.RED);
        placeTeam(player, inventory, 14, Team.BLUE);
        placeTeam(player, inventory, 15, Team.PINK);
        placeTeam(player, inventory, 16, Team.YELLOW);
        placeTeam(player, inventory, 17, Team.WHITE);

        player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1, 1);
        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        ItemStack stack = event.getCurrentItem();

        if (inventory == null || inventory.getTitle() == null || !inventory.getTitle().equals(TITLE)) {
            return;
        } else {
            event.setCancelled(true);
        }

        if (stack != null) {
            if (stack.getType() == Material.WOOL) {
                for (Team team : Team.values()) {
                    if (stack.getDurability() == team.getDyeColor().getWoolData()) {
                        addToTeam(player, team);
                        return;
                    }
                }
            }
        }
    }

    private void placeTeam(Player player, Inventory inventory, int slot, Team team) {
        if (!team.isEnabled())
            return;

        ItemBuilder builder = new ItemBuilder(Material.WOOL, 1, team.getDyeColor().getWoolData()).setDisplayName("§8» §r" + team.getColoredName());
        if (team.getMembers().isEmpty()) {
            builder.setLore("§8➥ §7Leer");
        } else {
            List<String> lore = new ArrayList<>();
            for (Player member : team.getMembers()) {
                lore.add("§8• §7" + member.getName());
            }
            if (team.getMembers().size() >= Team.TEAM_SIZE) {
                lore.add("§c§lVoll");
            }
            builder.setLore(lore);
        }

        ItemStack glass;

        if (team.getMembers().contains(player)) {
            glass = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, team.getGlassPaneColor())
                    .setDisplayName(" ").addEnchant(Enchantment.DURABILITY, 0, true).addItemFlag(ItemFlag.HIDE_ENCHANTS).build();
            builder.addEnchant(Enchantment.DURABILITY, 0, true);
            builder.addItemFlag(ItemFlag.HIDE_ENCHANTS);
        } else {
            glass = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, team.getGlassPaneColor())
                    .setDisplayName(" ").build();
        }

        inventory.setItem(slot - 9, glass);
        inventory.setItem(slot, builder.build());
        inventory.setItem(slot + 9, glass);
    }

    private void addToTeam(Player player, Team team) {
        Team currentTeam = Team.getTeam(player);

        if (team.getMembers().size() >= Team.TEAM_SIZE) {
            player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
            player.sendMessage(Messages.PREFIX + "Dieses Team ist bereits §cvoll§7!");
            return;
        }

        if (currentTeam != null) {
            if (currentTeam == team) {
                player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                player.sendMessage(Messages.PREFIX + "Du bist bereits in Team " + team.getColoredName() + "§7!");
                return;
            }

            currentTeam.getMembers().remove(player);
        }

        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
        player.closeInventory();
        player.sendMessage(Messages.PREFIX + "Du hast das Team " + team.getColoredName() + "§7 ausgewählt!");
        team.getMembers().add(player);
        team.giveArmor(player);
        bedWars.getScoreboardManager().updateTeam(player);
    }

}
