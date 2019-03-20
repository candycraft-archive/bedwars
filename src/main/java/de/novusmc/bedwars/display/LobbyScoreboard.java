package de.novusmc.bedwars.display;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import de.novusmc.bedwars.BedWars;
import de.novusmc.bedwars.game.Team;
import de.pauhull.friends.common.party.Party;
import de.pauhull.friends.spigot.SpigotFriends;
import de.pauhull.scoreboard.CustomScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionGroup;


/**
 * Created by Paul
 * on 07.12.2018
 *
 * @author pauhull
 */
public class LobbyScoreboard extends CustomScoreboard {

    private DisplayScore online, team;

    public LobbyScoreboard(Player player) {
        super(player, player.getName() + "_lobby", "§5§lBedwars Lobby");
        this.descending = false;
    }

    @Override
    public void show() {
        new DisplayScore(" §d§lCandyCraft§7.§dde");
        new DisplayScore("Server:");
        new DisplayScore();
        new DisplayScore(" §c" + TimoCloudAPI.getBukkitAPI().getThisServer().getMap());
        new DisplayScore("Map:");
        new DisplayScore();
        this.team = new DisplayScore("§b Lädt");
        new DisplayScore("Team:");
        new DisplayScore();
        this.online = new DisplayScore("§a Lädt");
        new DisplayScore("Online:");
        new DisplayScore();

        super.show();
    }

    @Override
    public void update() {
        String online = "§a " + Bukkit.getOnlinePlayers().size();
        if (!this.online.getScore().getEntry().equals(online)) {
            this.online.setName(online);
        }

        Team team = Team.getTeam(player);
        String teamName = "§bZufällig";
        if (team != null) {
            teamName = team.getColoredName();
        }
        String teamScore = " " + teamName;
        if (!this.team.getScore().getEntry().equals(teamScore)) {
            this.team.setName(teamScore);
        }
    }

    @Override
    public void updateTeam(Player player) {
        SpigotFriends.getInstance().getPartyManager().getAllParties(parties -> {
            Bukkit.getScheduler().runTask(BedWars.getInstance(), () -> {

                String prefix;
                String rank;
                if (player.getDisplayName().equals(player.getName())) {
                    PermissionGroup group = this.getHighestPermissionGroup(player);
                    rank = group.getRank() + "";
                    prefix = group.getPrefix();
                } else {
                    rank = "65";
                    prefix = "§a";
                }

                Team team = Team.getTeam(player);

                String name = team != null ? team.name() + player.getName() : "Z" + player.getName();
                if (name.length() > 16) {
                    name = name.substring(0, 16);
                }

                if (scoreboard.getTeam(name) != null) {
                    scoreboard.getTeam(name).unregister();
                }

                org.bukkit.scoreboard.Team scoreboardTeam = scoreboard.registerNewTeam(name);

                String suffix = "";
                String suffixColor = "";
                for (Party party : parties) {
                    if (party.getMembers().contains(player.getDisplayName()) && party.getMembers().contains(this.player.getDisplayName())) {
                        suffix = " [Party]";
                        suffixColor = "§7";
                    }
                }
                if (suffix.equals("") && team != null) {
                    suffix = " [" + team.getName() + "]";
                }
                if (team != null) {
                    suffixColor = team.getChatColor().toString();
                }

                scoreboardTeam.setSuffix(suffixColor + suffix);
                scoreboardTeam.addEntry(player.getName());
            });
        });
    }

}
