package de.novusmc.bedwars.npc;

import de.novusmc.bedwars.BedWars;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.inventivetalent.packetlistener.handler.PacketHandler;
import org.inventivetalent.packetlistener.handler.ReceivedPacket;
import org.inventivetalent.packetlistener.handler.SentPacket;

import static de.novusmc.bedwars.util.ReflectionUtils.getValue;

/**
 * Created by Paul
 * on 05.01.2019
 *
 * @author pauhull
 */
public class PacketReader extends PacketHandler {

    @Override
    public void onSend(SentPacket sentPacket) {

    }

    @Override
    public void onReceive(ReceivedPacket receivedPacket) {
        Player player = receivedPacket.getPlayer();
        if (receivedPacket.getPacket() instanceof PacketPlayInUseEntity) {
            PacketPlayInUseEntity packet = (PacketPlayInUseEntity) receivedPacket.getPacket();
            int id = (int) getValue(packet, "a");
            PacketPlayInUseEntity.EnumEntityUseAction action = (PacketPlayInUseEntity.EnumEntityUseAction) getValue(packet, "action");

            Bukkit.getScheduler().runTask(BedWars.getInstance(), () -> {
                BedWars.getInstance().getNpcManager().onNpcClick(player, id, action);
            });
        }
    }

}
