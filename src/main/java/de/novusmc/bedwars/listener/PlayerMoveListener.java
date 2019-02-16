package de.novusmc.bedwars.listener;

import de.novusmc.bedwars.BedWars;
import de.novusmc.bedwars.game.Team;
import de.novusmc.bedwars.phase.GamePhase;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Created by Paul
 * on 13.01.2019
 *
 * @author pauhull
 */
public class PlayerMoveListener implements Listener {

    private BedWars bedWars;
    private Location spectatorSpawn;

    public PlayerMoveListener(BedWars bedWars) {
        this.bedWars = bedWars;
        Bukkit.getPluginManager().registerEvents(this, bedWars);
        this.spectatorSpawn = bedWars.getLocationManager().getLocation("Spectator");
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        /* npc push away
        double distance = 1;
        for(NPC npc : NPC.getNpcs()) {
            if(npc.getLocation().getWorld().equals(player.getWorld())) {
                if(npc.getLocation().distanceSquared(player.getLocation()) < distance * distance) {
                    Vector vel = new Vector(npc.getLocation().getX() - player.getLocation().getX(),
                            0, npc.getLocation().getZ() - player.getLocation().getZ())
                            .normalize().multiply(-0.75);
                    player.setVelocity(vel);
                }
            }
        }
        */

        if (player.getLocation().getY() <= 0) {
            if (bedWars.getPhaseHandler().getActivePhaseType() == GamePhase.Type.LOBBY) {
                bedWars.getLocationManager().teleport(player, "Lobby");
            } else if (bedWars.getPhaseHandler().getActivePhaseType() == GamePhase.Type.INGAME) {
                if (bedWars.getSpectators().contains(player)) {
                    if (spectatorSpawn != null) {
                        player.teleport(spectatorSpawn);
                    }
                    return;
                }

                if (!player.isDead()) {
                    player.setHealth(0.0);
                }
            } else {
                Team team = Team.getTeam(player);
                if (team != null) {
                    player.teleport(team.getBedLocation());
                    player.setFallDistance(0);
                }
            }
        }
    }

}
