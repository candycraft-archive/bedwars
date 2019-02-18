package de.novusmc.bedwars.npc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.novusmc.bedwars.BedWars;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.*;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.PlayerInfoData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static de.novusmc.bedwars.util.ReflectionUtils.*;

/**
 * Created by Paul
 * on 04.01.2019
 *
 * @author pauhull
 */
public class NPC {

    @Getter
    private static List<NPC> npcs = new ArrayList<>();

    @Getter
    private int entityId;
    private boolean showInTablist;
    @Getter
    private Location location;
    private SkinData skin;
    private GameProfile profile;
    private PacketPlayOutPlayerInfo infoAddPacket;
    private PacketPlayOutNamedEntitySpawn spawnPacket;
    private PacketPlayOutEntityHeadRotation rotationPacket;
    private PacketPlayOutPlayerInfo infoRemovePacket;
    private PacketPlayOutEntityDestroy destroyPacket;

    public NPC(Location location, UUID uuid, String name, SkinData skin, boolean showInTablist) {
        this.entityId = findFreeEntityId();
        this.profile = new GameProfile(uuid, name);
        this.profile.getProperties().put("textures", new Property("textures", skin.getTexture(), skin.getSignature()));
        this.location = location;
        this.showInTablist = showInTablist;
        this.skin = skin;
        this.initPackets();
    }

    private static int findFreeEntityId() {
        List<Integer> currentEntityIds = new ArrayList<>();
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                currentEntityIds.add(entity.getEntityId());
            }
        }
        for (NPC npc : npcs) {
            currentEntityIds.add(npc.entityId);
        }

        for (int id = 2000; id < 10000; id++) {
            if (!currentEntityIds.contains(id)) {
                return id;
            }
        }

        return 0;
    }

    private void initPackets() {
        infoAddPacket = new PacketPlayOutPlayerInfo();
        PlayerInfoData data = infoAddPacket.new PlayerInfoData(profile, 1, WorldSettings.EnumGamemode.NOT_SET, CraftChatMessage.fromString(profile.getName())[0]);
        List<PlayerInfoData> playersToAdd = (List<PlayerInfoData>) getValue(infoAddPacket, "b");
        playersToAdd.add(data);
        setValue(infoAddPacket, "a", EnumPlayerInfoAction.ADD_PLAYER);
        setValue(infoAddPacket, "b", playersToAdd);

        infoRemovePacket = new PacketPlayOutPlayerInfo();
        List<PlayerInfoData> playersToRemove = (List<PlayerInfoData>) getValue(infoRemovePacket, "b");
        playersToRemove.add(data);
        setValue(infoRemovePacket, "a", EnumPlayerInfoAction.REMOVE_PLAYER);
        setValue(infoRemovePacket, "b", playersToRemove);

        byte yaw = (byte) ((int) (location.getYaw() * 256.0F / 360.0F));
        byte pitch = (byte) ((int) (location.getPitch() * 256.0F / 360.0F));

        spawnPacket = new PacketPlayOutNamedEntitySpawn();
        setValue(spawnPacket, "a", entityId);
        setValue(spawnPacket, "b", profile.getId());
        setValue(spawnPacket, "c", MathHelper.floor(location.getX() * 32.0D));
        setValue(spawnPacket, "d", MathHelper.floor(location.getY() * 32.0D));
        setValue(spawnPacket, "e", MathHelper.floor(location.getZ() * 32.0D));
        setValue(spawnPacket, "f", yaw);
        setValue(spawnPacket, "g", pitch);
        setValue(spawnPacket, "h", 0);
        DataWatcher watcher = new DataWatcher(null);
        watcher.a(6, (float) 20);
        watcher.a(10, (byte) 127);
        setValue(spawnPacket, "i", watcher);

        rotationPacket = new PacketPlayOutEntityHeadRotation();
        setValue(rotationPacket, "a", entityId);
        setValue(rotationPacket, "b", yaw);

        destroyPacket = new PacketPlayOutEntityDestroy(entityId);
    }

    public void spawn() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            spawn(player);
        }
        npcs.add(this);
    }

    public void spawn(Player player) {
        if (!npcs.contains(this)) return;
        if (!location.getWorld().equals(player.getWorld())) return;
        // you first have to send the tablist information to all players or the npc will be invisible
        sendPacket(player, infoAddPacket);
        // then you spawn the npc with the spawnPacket
        sendPacket(player, spawnPacket);
        // you have to rotate the npc's head by yourself (which is really weird)
        sendPacket(player, rotationPacket);
        // half a second after the npc is spawned you can safely remove it from the tablist if you want
        if (!showInTablist) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(BedWars.getInstance(), () -> {
                sendPacket(infoRemovePacket);
            }, 20L);
        }
    }

    public void respawn(Player player) {
        despawn(player);
        spawn(player);
    }

    public void despawn() {
        // if the tablist information isnt removed already you have to do it here
        if (showInTablist) {
            sendPacket(infoRemovePacket);
        }
        // destroy the entity on the client side
        sendPacket(destroyPacket);
    }

    public void despawn(Player player) {
        if (showInTablist) {
            sendPacket(player, infoRemovePacket);
        }
        sendPacket(player, destroyPacket);
    }

}
