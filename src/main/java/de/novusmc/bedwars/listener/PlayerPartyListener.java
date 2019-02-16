package de.novusmc.bedwars.listener;

import de.novusmc.bedwars.BedWars;
import de.pauhull.friends.spigot.event.PlayerJoinPartyEvent;
import de.pauhull.friends.spigot.event.PlayerLeavePartyEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created by Paul
 * on 16.02.2019
 *
 * @author pauhull
 */
public class PlayerPartyListener implements Listener {

    private BedWars bedWars;

    public PlayerPartyListener(BedWars bedWars) {
        this.bedWars = bedWars;
        Bukkit.getPluginManager().registerEvents(this, bedWars);
    }

    @EventHandler
    public void onPlayerJoinParty(PlayerJoinPartyEvent event) {
        bedWars.getScoreboardManager().updateTeam(event.getPlayer());
    }

    @EventHandler
    public void onPlayerLeaveParty(PlayerLeavePartyEvent event) {
        this.bedWars.getScoreboardManager().updateTeam(event.getPlayer());
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (event.getParty().getMembers().contains(player.getName())) {
                this.bedWars.getScoreboardManager().updateTeam(player);
            }
        }
    }

}
