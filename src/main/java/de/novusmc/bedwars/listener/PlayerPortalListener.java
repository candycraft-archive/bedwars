package de.novusmc.bedwars.listener;

import de.novusmc.bedwars.BedWars;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

/**
 * Created by Paul
 * on 23.02.2019
 *
 * @author pauhull
 */
public class PlayerPortalListener implements Listener {

    private BedWars bedWars;

    public PlayerPortalListener(BedWars bedWars) {
        this.bedWars = bedWars;
        Bukkit.getPluginManager().registerEvents(this, bedWars);
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        event.setCancelled(true);
    }

}
