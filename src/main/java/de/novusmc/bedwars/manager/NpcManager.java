package de.novusmc.bedwars.manager;

import de.novusmc.bedwars.BedWars;
import de.pauhull.npcapi.npc.Npc;
import de.pauhull.npcapi.npc.SkinData;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Created by Paul
 * on 23.02.2019
 *
 * @author pauhull
 */
public class NpcManager {

    private static final SkinData NPC_SKIN;
    private static final UUID NPC_UUID;

    static {
        NPC_SKIN = SkinData.getSkinSync(UUID.fromString("58e97027-8ca8-4888-a756-26e719f21de0")); // leStylex
        NPC_UUID = UUID.fromString("5b5fefa4-27ad-11e9-ab14-d663bd873d93");    // some random uuid that has no real player linked to it
    }

    private BedWars bedWars;
    private File file;
    private FileConfiguration config;

    public NpcManager(BedWars bedWars) {
        this.bedWars = bedWars;
        this.file = new File(bedWars.getDataFolder(), "npcs.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
        this.load();
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
        for (Npc npc : Npc.getNpcs()) {
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
        Iterator<Npc> iterator = Npc.getNpcs().iterator();
        while (iterator.hasNext()) {
            iterator.next().despawn();
            iterator.remove();
        }
    }

    private void spawn(Location location) {
        Npc npc = new Npc(location, NPC_UUID, "§cShop", NPC_SKIN, false);
        npc.spawn();
    }

}
