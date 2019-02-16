package de.novusmc.bedwars.listener;

import de.novusmc.bedwars.BedWars;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

/**
 * Created by Paul
 * on 04.01.2019
 *
 * @author pauhull
 */
public class PrepareItemCraftListener implements Listener {

    public PrepareItemCraftListener(BedWars bedWars) {
        Bukkit.getPluginManager().registerEvents(this, bedWars);
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        if (event.getRecipe().getResult() != null) {
            event.getInventory().setResult(null);
        }
    }

}
