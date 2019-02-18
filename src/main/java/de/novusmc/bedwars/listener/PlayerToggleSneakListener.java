package de.novusmc.bedwars.listener;

import de.novusmc.bedwars.BedWars;
import de.novusmc.bedwars.manager.SpectatorManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

/**
 * Created by Paul
 * on 20.01.2019
 *
 * @author pauhull
 */
public class PlayerToggleSneakListener implements Listener {

    private BedWars bedWars;

    public PlayerToggleSneakListener(BedWars bedWars) {
        this.bedWars = bedWars;
        Bukkit.getPluginManager().registerEvents(this, bedWars);
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        if (SpectatorManager.getSpectating().contains(player)) {
            SpectatorManager.unspectate(player);
        }
    }

}
