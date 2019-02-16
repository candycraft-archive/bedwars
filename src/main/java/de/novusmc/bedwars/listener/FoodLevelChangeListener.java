package de.novusmc.bedwars.listener;

import de.novusmc.bedwars.BedWars;
import de.novusmc.bedwars.phase.GamePhase;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**********************************************************************************
 *  Uhreberrechtshinweis                                                          *
 *  Copyright © Jason H. 2019                                                  *
 *  Erstellt: 03.01.2019 / 17:57                                                   *
 *                                                                                *
 *  Alle Inhalte dieses Quelltextes sind urheberrechtlich geschützt.              *
 *  Das Urheberrecht liegt, soweit nicht ausdrücklich anders gekennzeichnet,      *
 *  bei Jason H. Alle Rechte vorbehalten.                                         *
 *                                                                                *
 *  Jede Art der Vervielfältigung, Verbreitung, Vermietung, Verleihung,           *
 *  öffentlich Zugänglichmachung oder anderer Nutzung,                            *
 *  bedarf der ausdrücklichen, schriftlichen Zustimmung von Jason H.              *
 *********************************************************************************/

public class FoodLevelChangeListener implements Listener {

    private BedWars bedWars;

    public FoodLevelChangeListener(BedWars bedWars) {
        this.bedWars = bedWars;
        Bukkit.getPluginManager().registerEvents(this, bedWars);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (bedWars.getSpectators().contains(event.getEntity())) {
            event.setFoodLevel(20);
            event.setCancelled(true);
            return;
        }

        if (bedWars.getPhaseHandler().getActivePhaseType() != GamePhase.Type.INGAME) {
            event.setFoodLevel(20);
            event.setCancelled(true);
        }
    }
}
