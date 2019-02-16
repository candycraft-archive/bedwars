package de.novusmc.bedwars.listener;

import de.novusmc.bedwars.BedWars;
import de.novusmc.bedwars.phase.GamePhase;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Created by Paul
 * on 04.01.2019
 *
 * @author pauhull
 */
public class InventoryClickListener implements Listener {

    private BedWars bedWars;

    public InventoryClickListener(BedWars bedWars) {
        this.bedWars = bedWars;
        Bukkit.getPluginManager().registerEvents(this, bedWars);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (bedWars.getSpectators().contains(event.getWhoClicked())) {
            event.setCancelled(true);
            return;
        }

        if (bedWars.getPhaseHandler().getActivePhaseType() == GamePhase.Type.LOBBY) {
            event.setCancelled(true);
        }
    }

}
