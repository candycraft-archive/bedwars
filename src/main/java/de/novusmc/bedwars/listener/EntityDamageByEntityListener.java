package de.novusmc.bedwars.listener;

import de.novusmc.bedwars.BedWars;
import de.novusmc.bedwars.game.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Created by Paul
 * on 20.01.2019
 *
 * @author pauhull
 */
public class EntityDamageByEntityListener implements Listener {

    private BedWars bedWars;

    public EntityDamageByEntityListener(BedWars bedWars) {
        this.bedWars = bedWars;
        Bukkit.getPluginManager().registerEvents(this, bedWars);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();

            Team damagerTeam = Team.getTeam(damager);
            if (damagerTeam == null) {
                event.setDamage(0);
                event.setCancelled(true);
                return;
            }

            if (event.getEntity() instanceof Player) {
                Player damaged = (Player) event.getEntity();
                Team damagedTeam = Team.getTeam(damaged);
                if (damagedTeam == null || damagerTeam == damagedTeam) {
                    event.setDamage(0);
                    event.setCancelled(true);
                }
            }
        }
    }

}
