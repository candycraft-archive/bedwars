package de.novusmc.bedwars.listener;

import de.novusmc.bedwars.BedWars;
import de.novusmc.bedwars.phase.GamePhase;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

/**********************************************************************************
 *  Uhreberrechtshinweis                                                          *
 *  Copyright © Jason H. 2019                                                  *
 *  Erstellt: 03.01.2019 / 18:16                                                   *
 *                                                                                *
 *  Alle Inhalte dieses Quelltextes sind urheberrechtlich geschützt.              *
 *  Das Urheberrecht liegt, soweit nicht ausdrücklich anders gekennzeichnet,      *
 *  bei Jason H. Alle Rechte vorbehalten.                                         *
 *                                                                                *
 *  Jede Art der Vervielfältigung, Verbreitung, Vermietung, Verleihung,           *
 *  öffentlich Zugänglichmachung oder anderer Nutzung,                            *
 *  bedarf der ausdrücklichen, schriftlichen Zustimmung von Jason H.              *
 *********************************************************************************/

public class PlayerDropItemListener implements Listener {

    private BedWars bedWars;

    public PlayerDropItemListener(BedWars bedWars) {
        this.bedWars = bedWars;
        Bukkit.getPluginManager().registerEvents(this, bedWars);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (bedWars.getSpectators().contains(event.getPlayer())) {
            event.setCancelled(true);
            return;
        }

        if (bedWars.getPhaseHandler().getActivePhaseType() != GamePhase.Type.INGAME) {
            event.setCancelled(true);
        }
    }
}
