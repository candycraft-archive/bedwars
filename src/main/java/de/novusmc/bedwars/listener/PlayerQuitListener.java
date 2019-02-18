package de.novusmc.bedwars.listener;

import de.novusmc.bedwars.BedWars;
import de.novusmc.bedwars.Messages;
import de.novusmc.bedwars.game.Team;
import de.novusmc.bedwars.manager.SpectatorManager;
import de.novusmc.bedwars.phase.GamePhase;
import de.novusmc.bedwars.phase.type.IngamePhase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by Paul
 * on 04.01.2019
 *
 * @author pauhull
 */
public class PlayerQuitListener implements Listener {

    private BedWars bedWars;

    public PlayerQuitListener(BedWars bedWars) {
        this.bedWars = bedWars;
        Bukkit.getPluginManager().registerEvents(this, bedWars);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        SpectatorManager.getSpectating().remove(player);
        bedWars.getSpectators().remove(player);

        Team team = Team.getTeam(player);
        if (team != null) {
            team.getMembers().remove(player);
        }

        if (bedWars.getPhaseHandler().getActivePhaseType() == GamePhase.Type.LOBBY) {
            event.setQuitMessage(Messages.PREFIX + "§e" + player.getName() + "§7 hat das Spiel §cverlassen§7! §8[§e"
                    + (Bukkit.getOnlinePlayers().size() - 1) + "§8/§e" + Team.MAX_PLAYERS + "§8]");
        } else {
            if (team != null) {
                event.setQuitMessage(Messages.PREFIX + team.getChatColor() + player.getName() + "§7 hat das Spiel §cverlassen§7!");
                ((IngamePhase) bedWars.getPhaseHandler().getActivePhase()).checkForWin();
            } else {
                event.setQuitMessage(null);
            }
        }
    }

}
