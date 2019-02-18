package de.novusmc.bedwars.display;

import com.google.common.collect.Lists;
import de.novusmc.bedwars.BedWars;
import de.novusmc.bedwars.game.Team;
import de.novusmc.bedwars.phase.GamePhase;
import de.novusmc.bedwars.phase.type.IngamePhase;
import de.pauhull.friends.common.party.Party;
import de.pauhull.friends.spigot.SpigotFriends;
import de.pauhull.scoreboard.NovusScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Paul
 * on 07.12.2018
 *
 * @author pauhull
 */
public class IngameScoreboard extends NovusScoreboard {

    // ✔ ✘

    private static List<Team> playingTeams = null;
    private NovusScore currentTeam;
    private Map<Team, NovusScore> scores = new HashMap<>();

    public IngameScoreboard(Player player) {
        super(player, player.getName() + "_ingame", "§e§lBedWars§8 | §730:00");
        this.descending = false;

        if (playingTeams == null) {
            playingTeams = new ArrayList<>();
            for (Team team : Team.values()) {
                if (team.isEnabled() && !team.getMembers().isEmpty()) {
                    playingTeams.add(team);
                }
            }
        }
    }

    @Override
    public void show() {
        new NovusScore(" §d§lCandyCraft§7.§dde");
        new NovusScore("Server:");
        new NovusScore();
        for (Team team : Lists.reverse(playingTeams)) {
            scores.put(team, new NovusScore("§a§l✔§r " + team.getColoredName()));
        }
        new NovusScore();
        this.currentTeam = new NovusScore(" §aLädt...");
        new NovusScore("Dein Team:");
        new NovusScore();

        super.show();
    }

    @Override
    public void update() {
        if (BedWars.getInstance().getPhaseHandler().getActivePhaseType() == GamePhase.Type.INGAME) {
            IngamePhase phase = (IngamePhase) BedWars.getInstance().getPhaseHandler().getActivePhase();
            int minutes = Math.floorDiv(phase.getTime(), 60);
            int seconds = phase.getTime() - minutes * 60;
            this.updateTitle("§e§lBedWars§8 | §7" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
        }

        Team team = Team.getTeam(player);
        String currentTeam = team != null ? " " + team.getColoredName() : " §bSpectator";
        if (!this.currentTeam.getScore().getEntry().equals(currentTeam)) {
            this.currentTeam.setName(currentTeam);
        }

        for (Team allTeams : Team.values()) {
            NovusScore score;
            if ((score = scores.get(allTeams)) != null) {
                String entry = (allTeams.isHasBed() && !allTeams.getMembers().isEmpty() ? "§a§l✔§r " : "§c§l✘§r ") + allTeams.getColoredName() + "§8 (" + allTeams.getMembers().size() + "/" + Team.TEAM_SIZE + ")";
                if (!score.getScore().getEntry().equals(entry)) {
                    score.setName(entry);
                }
            }
        }
    }

    @Override
    public void updateTeam(Player player) {
        SpigotFriends.getInstance().getPartyManager().getAllParties(parties -> {
            Bukkit.getScheduler().runTask(BedWars.getInstance(), () -> {

                Team team = Team.getTeam(player);

                String name = team != null ? team.name() + player.getName() : "Z" + player.getName();
                if (name.length() > 16) {
                    name = name.substring(0, 16);
                }

                if (scoreboard.getTeam(name) != null) {
                    scoreboard.getTeam(name).unregister();
                }

                org.bukkit.scoreboard.Team scoreboardTeam = scoreboard.registerNewTeam(name);

                StringBuilder suffix = new StringBuilder();
                for (Party party : parties) {
                    if (party.getMembers().contains(player.getDisplayName()) && party.getMembers().contains(this.player.getDisplayName())) {
                        suffix.append("§7 [§5Party§7]");
                    }
                }
                scoreboardTeam.setSuffix(suffix.toString());

                if (team == null) {
                    scoreboardTeam.setPrefix(ChatColor.GRAY.toString());
                } else {
                    scoreboardTeam.setPrefix(team.getChatColor().toString());
                }

                scoreboardTeam.addEntry(player.getName());
            });
        });
    }

}
