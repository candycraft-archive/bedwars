package de.novusmc.bedwars.listener;

import de.novusmc.bedwars.BedWars;
import de.novusmc.bedwars.Messages;
import de.novusmc.bedwars.data.table.StatsTable;
import de.novusmc.bedwars.game.Team;
import de.novusmc.bedwars.manager.ItemManager;
import de.novusmc.bedwars.phase.GamePhase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by Paul
 * on 04.01.2019
 *
 * @author pauhull
 */
public class PlayerJoinListener implements Listener {

    private BedWars bedWars;

    public PlayerJoinListener(BedWars bedWars) {
        this.bedWars = bedWars;
        Bukkit.getPluginManager().registerEvents(this, bedWars);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        BedWars.getInstance().getStatsTable().getStats(player.getUniqueId(), stats -> {
            if (stats == null) {
                BedWars.getInstance().getStatsTable().setStats(player.getUniqueId(), new StatsTable.Stats(player.getUniqueId(), 0, 0, 0, 0));
            }
        });
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (BedWars.getInstance().getSpectators().contains(online)) {
                player.hidePlayer(online);
            } else {
                player.showPlayer(online);
            }
        }

        if (bedWars.getPhaseHandler().getActivePhaseType() == GamePhase.Type.LOBBY) {
            event.setJoinMessage(Messages.PREFIX + "§e" + player.getName() + "§7 ist dem Spiel §abeigetreten§7! §8[§e"
                    + Bukkit.getOnlinePlayers().size() + "§8/§e" + Team.MAX_PLAYERS + "§8]");
            bedWars.getLocationManager().teleport(player, "Lobby");
            ItemManager.giveLobbyItems(player);
        } else {
            event.setJoinMessage(null);
            ItemManager.giveSpectatorItems(player);
            for (Player all : Bukkit.getOnlinePlayers()) {
                all.hidePlayer(player);
            }
            bedWars.getSpectators().add(player);
        }


    }

}
