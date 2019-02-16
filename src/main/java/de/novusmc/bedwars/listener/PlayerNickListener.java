package de.novusmc.bedwars.listener;

import de.novusmc.bedwars.BedWars;
import de.pauhull.nickapi.event.PostPlayerNickEvent;
import de.pauhull.nickapi.event.PostPlayerUnnickEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created by Paul
 * on 13.12.2018
 *
 * @author pauhull
 */
public class PlayerNickListener implements Listener {

    private BedWars bedWars;

    public PlayerNickListener(BedWars bedWars) {
        this.bedWars = bedWars;
        Bukkit.getPluginManager().registerEvents(this, bedWars);
    }

    @EventHandler
    public void onPostPlayerNick(PostPlayerNickEvent event) {
        bedWars.getScoreboardManager().updateTeam(event.getPlayer());
    }

    @EventHandler
    public void onPostPlayerUnnick(PostPlayerUnnickEvent event) {
        bedWars.getScoreboardManager().updateTeam(event.getPlayer());
    }

}
