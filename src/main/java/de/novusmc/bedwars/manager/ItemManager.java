package de.novusmc.bedwars.manager;

import de.novusmc.bedwars.BedWars;
import de.novusmc.bedwars.util.ItemBuilder;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Paul
 * on 04.01.2019
 *
 * @author pauhull
 */
public class ItemManager {

    public static final ItemStack LEAVE = new ItemBuilder(Material.SLIME_BALL).setDisplayName("§eZur Lobby §7§o<Rechtsklick>").build();
    public static final ItemStack TEAM_SELECT = new ItemBuilder(Material.COMPASS).setDisplayName("§bTeam auswählen §7§o<Rechtsklick>").build();
    public static final ItemStack SPECTATE = new ItemBuilder(Material.COMPASS).setDisplayName("§cSpieler zuschauen §7§o<Rechtsklick>").build();

    public static void giveLobbyItems(Player player) {
        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setAllowFlight(false);
        player.setExp(0);
        player.setLevel(0);
        player.setFireTicks(0);
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.getInventory().setHeldItemSlot(4);
        player.getInventory().setItem(1, TEAM_SELECT);
        player.getInventory().setItem(7, LEAVE);
    }

    public static void giveSpectatorItems(Player player) {
        BedWars.getInstance().getLocationManager().teleport(player, "Spectator");
        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.getInventory().setHeldItemSlot(4);
        player.getInventory().setItem(4, SPECTATE);
    }

}
