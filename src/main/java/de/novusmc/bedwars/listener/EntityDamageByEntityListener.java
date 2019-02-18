package de.novusmc.bedwars.listener;

import de.novusmc.bedwars.BedWars;
import de.novusmc.bedwars.game.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
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
        Player damager = null;
        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getDamager();

            if (arrow.getShooter() instanceof Player) {
                damager = (Player) arrow.getShooter();
            }
        }

        if (damager == null)
            return;

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
