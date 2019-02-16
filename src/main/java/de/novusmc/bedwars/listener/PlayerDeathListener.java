package de.novusmc.bedwars.listener;

import de.novusmc.bedwars.BedWars;
import de.novusmc.bedwars.Messages;
import de.novusmc.bedwars.game.Team;
import de.novusmc.bedwars.phase.GamePhase;
import de.novusmc.bedwars.phase.type.IngamePhase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Created by Paul
 * on 12.01.2019
 *
 * @author pauhull
 */
public class PlayerDeathListener implements Listener {

    private BedWars bedWars;

    public PlayerDeathListener(BedWars bedWars) {
        this.bedWars = bedWars;
        Bukkit.getPluginManager().registerEvents(this, bedWars);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.getDrops().clear();

        if (bedWars.getPhaseHandler().getActivePhaseType() != GamePhase.Type.INGAME) {
            event.setDeathMessage(null);
            event.setKeepInventory(true);
            return;
        }

        Player player = event.getEntity();
        Team team = Team.getTeam(player);
        if (team != null) {
            Player killer = player.getKiller();
            Team killerTeam = Team.getTeam(killer);
            if (killer == null || killer.getName().equals(player.getName())) {
                event.setDeathMessage(Messages.PREFIX + team.getChatColor() + player.getName() + "§7 ist gestorben!");
            } else if (killerTeam != null) {
                event.setDeathMessage(Messages.PREFIX + team.getChatColor() + player.getName() + "§7 wurde von " + killerTeam.getChatColor() + killer.getName() + "§7 getötet!");
            }

            final boolean bed = team.isHasBed();
            if (!bed) {
                if (killer != null && !killer.getName().equals(player.getName())) {
                    BedWars.getInstance().getStatsTable().getStats(killer.getUniqueId(), stats -> {
                        stats.setKills(stats.getKills() + 1);
                        BedWars.getInstance().getStatsTable().setStats(killer.getUniqueId(), stats);
                    });
                }
                BedWars.getInstance().getStatsTable().getStats(player.getUniqueId(), stats -> {
                    stats.setDeaths(stats.getDeaths() + 1);
                    BedWars.getInstance().getStatsTable().setStats(player.getUniqueId(), stats);
                });
            }
            Bukkit.getScheduler().scheduleSyncDelayedTask(bedWars, () -> {
                if (player.isDead()) {
                    player.spigot().respawn();
                }
            }, 10);

            if (!team.isHasBed()) {
                team.getMembers().remove(player);
                bedWars.getSpectators().add(player);
                bedWars.getScoreboardManager().updateTeam(player);

                ((IngamePhase) bedWars.getPhaseHandler().getActivePhase()).checkForWin();
            }
        } else {
            event.setDeathMessage(null);
            event.setKeepInventory(true);
        }

    }

}
