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
        NPC_SKIN = new SkinData("eyJ0aW1lc3RhbXAiOjE1NTIzMzUxMjc3NDAsInByb2ZpbGVJZCI6IjU4ZTk3MDI3OGNhODQ4ODhhNzU2MjZlNzE5ZjIxZGUwIiwicHJvZmlsZU5hbWUiOiJsZVN0eWxleCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGYxN2JmMjFmZTNkYjRjZTg5ZWY1OWM2MTE0Mjk0OTA1NTgxOGVlNDg3YzI1ODhlMmI4YWEyOTI5YjRjMjQ0NSJ9fX0=", "IJ0Ey6aTczZUNOBdcrAHZ9Ao+ARG5dkwvUMC6/LhNX8FD+DKznWNZEnhZWuMDvcPIApfbFGhSERtEqbEKrIOZd2uYTsleA2UA4xwBtqw6FApxNe47R2HvkdnH6CnY78YIe13LVECsnkymj67UkZr6nTchlB8H/eW78D+B5unn+pssLnhBtdzDHs7ppjvBG1S4pzQbTGUBKQGgZnKYAib335O+yf3gwLARrzeTQ7NPm83ZzBPKtupaWDn8VntBwXHUWCzbtFR3aQ/IrdOLOTqbuNwqYELGy+9Uh8HIyZjoyZrNXx27y0YSkCU8dB2xed5oZVR5neBKtxk+m824cV/ZfrJlGCc8fzlFRyyVOy5tF6TAosXCvFzOGhqpTrOEhmWZGdRmMko/g0aU22kiIOj6izC2saI37dShMdUBJZ+bFlYlWXSkWhQQ4vvzHtE4p8hhJgnewCiOd8Qi1z5nAV+9xacr8tg2WHWqnhBF6mpfGMoljeF7yWipb+ZKZrWJ0OQ0yRtDj2UAVkLmgzFnUZBt09l26JbPM+jlRj6KA9EIwpJrF0TdDwsdHvcT5WAKgHJrySQKOXOKjpVka6TJD1F3XxLxAHOhqK8W8MRzg/Xi63I3rq84vXnEbp7+azgomjc43KjfO8TPNrgFx2bQFVjliF2ELAba+xiluNazB/zl1Q="); // leStylex
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
