package de.novusmc.bedwars.listener;

import de.novusmc.bedwars.BedWars;
import net.minecraft.server.v1_8_R3.PacketPlayOutCamera;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

/**
 * Created by Paul
 * on 20.01.2019
 *
 * @author pauhull
 */
public class PlayerToggleSneakListener implements Listener {

    private BedWars bedWars;

    public PlayerToggleSneakListener(BedWars bedWars) {
        this.bedWars = bedWars;
        Bukkit.getPluginManager().registerEvents(this, bedWars);
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.SPECTATOR) {
            PacketPlayOutCamera packet = new PacketPlayOutCamera(((CraftPlayer) player).getHandle());
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(true);
            player.setFlying(true);
        }
    }

}
