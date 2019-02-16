package de.novusmc.bedwars.listener;

import de.novusmc.bedwars.BedWars;
import de.novusmc.bedwars.Messages;
import de.novusmc.bedwars.manager.SpectatorManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

/**
 * Created by Paul
 * on 19.01.2019
 *
 * @author pauhull
 */
public class PlayerInteractAtEntityListener implements Listener {

    private BedWars bedWars;

    public PlayerInteractAtEntityListener(BedWars bedWars) {
        this.bedWars = bedWars;
        Bukkit.getPluginManager().registerEvents(this, bedWars);
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();

        if (!bedWars.getSpectators().contains(player)) {
            return;
        }

        if (event.getRightClicked() instanceof Player) {
            Player clicked = (Player) event.getRightClicked();

            if (bedWars.getSpectators().contains(clicked)) {
                return;
            }

            SpectatorManager.setSpectating(player, clicked);
            player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1, 1);
            player.sendMessage(Messages.PREFIX + "Du schaust nun §e" + clicked.getName() + "§7 zu.");
        }
    }

}
