package de.novusmc.bedwars.manager;

import net.minecraft.server.v1_8_R3.PacketPlayOutCamera;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Created by Paul
 * on 19.01.2019
 *
 * @author pauhull
 */
public class SpectatorManager {

    public static void setSpectating(Player spectator, Player spectated) {
        spectator.setGameMode(GameMode.SPECTATOR);
        PacketPlayOutCamera packet = new PacketPlayOutCamera(((CraftPlayer) spectated).getHandle());
        ((CraftPlayer) spectator).getHandle().playerConnection.sendPacket(packet);
    }

}
