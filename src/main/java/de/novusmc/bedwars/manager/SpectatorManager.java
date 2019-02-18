package de.novusmc.bedwars.manager;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.PacketPlayOutCamera;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul
 * on 19.01.2019
 *
 * @author pauhull
 */
public class SpectatorManager {

    @Getter
    private static List<Player> spectating = new ArrayList<>();

    public static void setSpectating(Player spectator, Player spectated) {
        spectating.add(spectator);
        spectator.setGameMode(GameMode.SPECTATOR);
        PacketPlayOutCamera packet = new PacketPlayOutCamera(((CraftPlayer) spectated).getHandle());
        ((CraftPlayer) spectator).getHandle().playerConnection.sendPacket(packet);
    }

    public static void unspectate(Player spectator) {
        spectating.remove(spectator);
        PacketPlayOutCamera packet = new PacketPlayOutCamera(((CraftPlayer) spectator).getHandle());
        ((CraftPlayer) spectator).getHandle().playerConnection.sendPacket(packet);
        spectator.setGameMode(GameMode.ADVENTURE);
        spectator.setAllowFlight(true);
        spectator.setFlying(true);
    }

}
