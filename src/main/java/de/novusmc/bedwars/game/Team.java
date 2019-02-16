package de.novusmc.bedwars.game;

import de.novusmc.bedwars.manager.BedManager;
import de.novusmc.bedwars.manager.LocationManager;
import de.novusmc.bedwars.util.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Paul
 * on 04.01.2019
 *
 * @author pauhull
 */
public enum Team {

    BLUE(ChatColor.BLUE, DyeColor.BLUE, "Blau", (short) 11),
    GREEN(ChatColor.GREEN, DyeColor.LIME, "Grün", (short) 13),
    RED(ChatColor.RED, DyeColor.RED, "Rot", (short) 14),
    YELLOW(ChatColor.YELLOW, DyeColor.YELLOW, "Gelb", (short) 4),
    ORANGE(ChatColor.GOLD, DyeColor.ORANGE, "Orange", (short) 1),
    CYAN(ChatColor.DARK_AQUA, DyeColor.CYAN, "Türkis", (short) 9),
    PINK(ChatColor.LIGHT_PURPLE, DyeColor.PINK, "Pink", (short) 6),
    WHITE(ChatColor.WHITE, DyeColor.WHITE, "Weiß", (short) 0);

    public static int TEAM_SIZE;
    public static int TEAM_AMOUNT;
    public static int MAX_PLAYERS;
    public static int MIN_PLAYERS;

    @Getter
    private ChatColor chatColor;
    @Getter
    private DyeColor dyeColor;
    @Getter
    private String name;
    @Getter
    private List<Player> members;
    @Getter
    private short glassPaneColor;
    @Getter
    @Setter
    private Location spawnLocation = null, bedLocation = null;
    @Getter
    private Inventory chestInventory;
    @Getter
    @Setter
    private boolean hasBed = true;

    Team(ChatColor chatColor, DyeColor dyeColor, String name, short glassPaneColor) {
        this.chatColor = chatColor;
        this.dyeColor = dyeColor;
        this.name = name;
        this.glassPaneColor = glassPaneColor;
        this.members = new ArrayList<>();
        this.chestInventory = Bukkit.createInventory(null, 27, chatColor + "Kiste von " + name);
    }

    public static void loadLocations(LocationManager locationManager) {
        BLUE.spawnLocation = locationManager.getLocation("Blue");
        GREEN.spawnLocation = locationManager.getLocation("Green");
        RED.spawnLocation = locationManager.getLocation("Red");
        YELLOW.spawnLocation = locationManager.getLocation("Yellow");
        ORANGE.spawnLocation = locationManager.getLocation("Orange");
        CYAN.spawnLocation = locationManager.getLocation("Cyan");
        PINK.spawnLocation = locationManager.getLocation("Pink");
        WHITE.spawnLocation = locationManager.getLocation("White");
    }

    public static void loadBeds(BedManager bedManager) {
        bedManager.load();
    }

    public static Team findFreeTeam() {
        int i = 0;

        List<Team> shuffledList = Arrays.asList(values());
        Collections.shuffle(shuffledList);
        Team[] teams = shuffledList.toArray(new Team[0]);
        Team team;
        do {
            if (i >= teams.length) {
                return null;
            }

            team = teams[i++];
        } while (team.getMembers().size() >= TEAM_SIZE || !team.isEnabled());
        return team;
    }

    public static Team getTeam(Player player) {
        if (player == null) return null;

        for (Team team : values()) {
            if (team.getMembers().contains(player)) {
                return team;
            }
        }

        return null;
    }

    public static int getActiveTeamAmount() {
        int activeTeams = 0;
        for (Team team : Team.values()) {
            if (team.isEnabled()) activeTeams++;
        }
        return activeTeams;
    }

    public boolean isEnabled() {
        return spawnLocation != null;
    }

    public String getColoredName() {
        return chatColor + name;
    }

    public void giveArmor(Player player) {
        ItemStack helmet = new ItemBuilder(Material.LEATHER_HELMET).setColor(dyeColor.getColor()).setDisplayName("§8⬛ " + chatColor + "Helm §8«").setUnbreakable(true).build();
        ItemStack chestplate = new ItemBuilder(Material.LEATHER_CHESTPLATE).setColor(dyeColor.getColor()).setDisplayName("§8⬛ " + chatColor + "Brustplatte §8«").setUnbreakable(true).build();
        ItemStack leggings = new ItemBuilder(Material.LEATHER_LEGGINGS).setColor(dyeColor.getColor()).setDisplayName("§8⬛ " + chatColor + "Hose §8«").setUnbreakable(true).build();
        ItemStack boots = new ItemBuilder(Material.LEATHER_BOOTS).setColor(dyeColor.getColor()).setDisplayName("§8⬛ " + chatColor + "Schuhe §8«").setUnbreakable(true).build();
        player.getInventory().setArmorContents(new ItemStack[]{boots, leggings, chestplate, helmet});
    }

}
