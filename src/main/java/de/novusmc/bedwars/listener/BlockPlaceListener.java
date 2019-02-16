package de.novusmc.bedwars.listener;

import de.novusmc.bedwars.BedWars;
import de.novusmc.bedwars.phase.GamePhase;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Created by Paul
 * on 04.01.2019
 *
 * @author pauhull
 */
public class BlockPlaceListener implements Listener {

    private BedWars bedWars;

    public BlockPlaceListener(BedWars bedWars) {
        this.bedWars = bedWars;
        Bukkit.getPluginManager().registerEvents(this, bedWars);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (bedWars.getSpectators().contains(event.getPlayer())) {
            event.setCancelled(true);
            return;
        }

        if (bedWars.getPhaseHandler().getActivePhaseType() != GamePhase.Type.INGAME) {
            event.setCancelled(true);
            return;
        }

        bedWars.getPlacedBlocks().add(event.getBlock());
    }

}
