package de.novusmc.bedwars.npc;

import de.novusmc.bedwars.BedWars;
import de.pauhull.nickapi.event.PostPlayerRefreshEvent;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity.EnumEntityUseAction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Paul
 * on 03.01.2019
 *
 * @author pauhull
 */
public class NPCManager implements Listener {

    // get the data from https://sessionserver.mojang.com/session/minecraft/profile/{UUID without dashes}?unsigned=false
    // this is the skin of leStylex (https://sessionserver.mojang.com/session/minecraft/profile/58e970278ca84888a75626e719f21de0?unsigned=false)
    private static final SkinData NPC_SKIN = new SkinData("eyJ0aW1lc3RhbXAiOjE1NTAzMjMzMzU1MTgsInByb2ZpbGVJZCI6IjU4ZTk3MDI3OGNhODQ4ODhhNzU2MjZlNzE5ZjIxZGUwIiwicHJvZmlsZU5hbWUiOiJsZVN0eWxleCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQ3ZDYxNjMwMDRlNzlmMjU5Y2Q1Y2NjZjg2NzU1Mjc1ZmE3Yzg1MmY4NzhjMDY2OWIyZjJkNTA5Zjk2MDUwNiJ9fX0=", "Fl/xfTQxvtOPY4qA9QCdJwprXzJuV2SdAM6OVXSHc6yR4zlcjByrVoV9MdwZBK+9I3A4FNOybFuhH3ShwR3qweH2pmMZBf3s/SiX2E9FCzlHrZpaUV6+RTnMyguR/UnqUfW7+iX/Vi3ynveCC5BVOOBQdzrV1oY59quqYm3sJ7uGWXgCJySgefkH2rIoZMpBxTH+als5WLPDZwH26uzbzvkABFPEEyr5GVMulKuCcwVrIiFdWVrG8TyQqa/U5eFFdah5ZGRA4DpSJfBwPruKWrZTUnnNZRVg5t4eUNyrW4QqUsED1MVrH9yTY9T0YlkFyefNseTclZbd9zd+xjNUmRh1vXyqrqbTmd6Qqe/ZGfNMD4u3eeq08KI019gquIz3amEIy9NRbJq1PlXKWzUtdK64oj+PwRz9sMfXYA+eCFQIyyb9PfF7EafKbE87VP1Banh7lpYYlQOP2ga4Bl9QYnesPUbLBLkUpuV3c9ZWwmMoXeFB6ME9eimvzaay5/Kh7/5E1bXxwnmuzJ6HhYUEazA4HB23Kl6W6/5nh5OEBFaVEFW68QppRy+3YUruQ5BZWtc1emGa+hdAmG+sZ0NPU5cxLgh2ecN1FdQ8AtRBaFg/3L8gAcg8vPCCQb8VvapP5lvqge98k7pJB6d1bPSEi3caDebXcQelxpxYOxL5rVM=");

    // some random uuid that has no real player linked to it
    private static final UUID NPC_UUID = UUID.fromString("5b5fefa4-27ad-11e9-ab14-d663bd873d93");

    private BedWars bedWars;
    private File file;
    private FileConfiguration config;
    private Map<Player, List<NPC>> npcsInSight = new HashMap<>();

    public NPCManager(BedWars bedWars) {
        this.bedWars = bedWars;
        this.file = new File(bedWars.getDataFolder(), "npcs.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
        this.load();

        Bukkit.getPluginManager().registerEvents(this, bedWars);
    }

    public void load() {
        clear();
        if (config.isList("locations")) {
            for (Location location : (List<Location>) config.getList("locations")) {
                spawn(location);
            }
        }
    }

    public void save() {
        List<Location> locations = new ArrayList<>();
        for (NPC npc : NPC.getNpcs()) {
            locations.add(npc.getLocation());
        }
        config.set("locations", locations);
        saveConfig();
    }

    private void saveConfig() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void create(Location location) {
        spawn(location);
        save();
    }

    public void removeAll() {
        clear();
        save();
    }

    public void clear() {
        Iterator<NPC> iterator = NPC.getNpcs().iterator();
        while (iterator.hasNext()) {
            iterator.next().despawn();
            iterator.remove();
        }
    }

    private void spawn(Location location) {
        NPC npc = new NPC(location, NPC_UUID, "§cShop", NPC_SKIN, false);
        npc.spawn();
    }

    public void onNpcClick(Player player, int entityId, EnumEntityUseAction action) {
        if (player == null || bedWars.getSpectators().contains(player)) {
            return;
        }

        if (action == EnumEntityUseAction.INTERACT) {
            for (NPC npc : NPC.getNpcs()) {
                if (npc.getEntityId() == entityId) {
                    bedWars.getShopInventory().show(player);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // when you join the world and the npcs get reloaded and a npc has a certain distance from you (48 blocks), the
        // skin won't be loaded so you have to respawn it when the player gets near it. after that the skin is loaded
        // forever, until the npc gets respawned (e.g. world switch)
        Player player = event.getPlayer();
        if (npcsInSight.containsKey(player)) {
            List<NPC> npcsInSight = this.npcsInSight.get(player);
            for (NPC npc : NPC.getNpcs()) {
                if (!npc.getLocation().getWorld().equals(player.getWorld())) {
                    continue;
                }

                if (!npcsInSight.contains(npc) && npc.getLocation().distanceSquared(player.getLocation()) <= 48 * 48) {
                    npc.respawn(player);
                    npcsInSight.add(npc);
                }
            }
            this.npcsInSight.put(player, npcsInSight);
        } else {
            this.npcsInSight.put(player, new ArrayList<>());
            this.onPlayerMove(event);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.npcsInSight.remove(event.getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        for (NPC npc : NPC.getNpcs()) {
            npc.spawn(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (!event.getFrom().getWorld().equals(event.getTo().getWorld())) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(bedWars, () -> {
                for (NPC npc : NPC.getNpcs()) {
                    npc.spawn(event.getPlayer());
                }
            }, 15);
            npcsInSight.remove(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(bedWars, () -> {
            for (NPC npc : NPC.getNpcs()) {
                npc.spawn(event.getPlayer());
            }
        }, 5);
        npcsInSight.remove(event.getPlayer());
    }

    @EventHandler
    public void onPostPlayerRefresh(PostPlayerRefreshEvent event) {
        for (NPC npc : NPC.getNpcs()) {
            npc.spawn(event.getPlayer());
        }
        npcsInSight.remove(event.getPlayer());
    }

}
