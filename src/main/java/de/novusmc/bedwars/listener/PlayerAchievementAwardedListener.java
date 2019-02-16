package de.novusmc.bedwars.listener;

import de.novusmc.bedwars.BedWars;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;

/**
 * Created by Paul
 * on 04.01.2019
 *
 * @author pauhull
 */
public class PlayerAchievementAwardedListener implements Listener {

    private BedWars bedWars;

    public PlayerAchievementAwardedListener(BedWars bedWars) {
        this.bedWars = bedWars;
        Bukkit.getPluginManager().registerEvents(this, bedWars);
    }

    @EventHandler
    public void onPlayerAchievementAwarded(PlayerAchievementAwardedEvent event) {
        event.setCancelled(true);
    }

}
