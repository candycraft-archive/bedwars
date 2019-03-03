package de.novusmc.bedwars.listener;

import de.novusmc.bedwars.BedWars;
import de.pauhull.npcapi.event.PlayerClickNpcEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created by Paul
 * on 23.02.2019
 *
 * @author pauhull
 */
public class PlayerClickNpcListener implements Listener {

    private BedWars bedWars;

    public PlayerClickNpcListener(BedWars bedWars) {
        this.bedWars = bedWars;
        Bukkit.getPluginManager().registerEvents(this, bedWars);
    }

    @EventHandler
    public void onPlayerClickNpc(PlayerClickNpcEvent event) {
        Player player = event.getPlayer();

        if (bedWars.getSpectators().contains(player)) {
            return;
        }

        if (event.getAction() == PlayerClickNpcEvent.Action.INTERACT && event.getNpc().getName().equals("§cShop")) {
            bedWars.getShopInventory().show(player);
        }
    }

}
