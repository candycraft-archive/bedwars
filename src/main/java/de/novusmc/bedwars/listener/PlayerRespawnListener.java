package de.novusmc.bedwars.listener;

import de.novusmc.bedwars.BedWars;
import de.novusmc.bedwars.game.Team;
import de.novusmc.bedwars.manager.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * Created by Paul
 * on 12.01.2019
 *
 * @author pauhull
 */
public class PlayerRespawnListener implements Listener {

    private BedWars bedWars;

    public PlayerRespawnListener(BedWars bedWars) {
        this.bedWars = bedWars;
        Bukkit.getPluginManager().registerEvents(this, bedWars);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Team team = Team.getTeam(player);

        if (team != null) {
            event.setRespawnLocation(team.getSpawnLocation());
        } else {
            event.setRespawnLocation(bedWars.getLocationManager().getLocation("Spectator"));
            ItemManager.giveSpectatorItems(player);
            for (Player all : Bukkit.getOnlinePlayers()) {
                all.hidePlayer(player);
            }
        }
    }

}
