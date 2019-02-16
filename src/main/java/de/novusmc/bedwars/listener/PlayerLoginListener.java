package de.novusmc.bedwars.listener;

import de.novusmc.bedwars.BedWars;
import de.novusmc.bedwars.Messages;
import de.novusmc.bedwars.game.Team;
import de.novusmc.bedwars.phase.GamePhase;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 * Created by Paul
 * on 05.01.2019
 *
 * @author pauhull
 */
public class PlayerLoginListener implements Listener {

    private BedWars bedWars;

    public PlayerLoginListener(BedWars bedWars) {
        this.bedWars = bedWars;
        Bukkit.getPluginManager().registerEvents(this, bedWars);
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        if (bedWars.getPhaseHandler().getActivePhaseType() == GamePhase.Type.LOBBY && Bukkit.getOnlinePlayers().size() >= Team.MAX_PLAYERS) {
            event.setResult(PlayerLoginEvent.Result.KICK_FULL);
            event.setKickMessage(Messages.PREFIX + "Der Server ist §cvoll§7!");
        }
    }

}
