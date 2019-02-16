package de.novusmc.bedwars.manager;

import com.darkblade12.particleeffect.ParticleEffect;
import de.novusmc.bedwars.BedWars;
import de.novusmc.bedwars.util.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Paul
 * on 05.01.2019
 *
 * @author pauhull
 */
public class SpawnerManager {

    @Getter
    private static HashMap<Location, SpawnerType> spawners = new HashMap<>();

    private int goldSpawns = 0;
    private int spawnerTask = -1;
    private BedWars bedWars;
    private File file;
    private FileConfiguration config;

    public SpawnerManager(BedWars bedWars) {
        this.bedWars = bedWars;
        this.file = new File(bedWars.getDataFolder(), "spawners.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
        this.load();
    }

    public void load() {
        spawners.clear();
        for (SpawnerType type : SpawnerType.values()) {
            if (config.isSet(type.name())) {
                @SuppressWarnings("unchecked")
                List<Location> locations = (List<Location>) config.getList(type.name());
                for (Location location : locations) {
                    spawners.put(location, type);
                }
            }
        }
    }

    public void save() {
        List<Location> bronzeList = new ArrayList<>();
        List<Location> ironList = new ArrayList<>();
        List<Location> goldList = new ArrayList<>();

        for (Location location : spawners.keySet()) {
            switch (spawners.get(location)) {
                case BRONZE:
                    bronzeList.add(location);
                    break;
                case IRON:
                    ironList.add(location);
                    break;
                case GOLD:
                    goldList.add(location);
                    break;
            }
        }

        config.set("BRONZE", bronzeList);
        config.set("IRON", ironList);
        config.set("GOLD", goldList);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void add(Location location, SpawnerType type) {
        spawners.put(location, type);
        save();
    }

    public void clear() {
        spawners.clear();
        save();
    }

    public void startSpawning() {

        goldSpawns = 0;
        if (this.spawnerTask != -1) {
            this.stopSpawning();
        }

        final AtomicInteger passedSeconds = new AtomicInteger(0);
        this.spawnerTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(bedWars, () -> {
            for (Map.Entry<Location, SpawnerType> entry : spawners.entrySet()) {
                Location location = entry.getKey();
                SpawnerType type = entry.getValue();

                if (type == SpawnerType.BRONZE
                        || (type == SpawnerType.IRON && passedSeconds.get() % 10 == 0)
                        || (type == SpawnerType.GOLD && passedSeconds.get() % 30 == 0)) {

                    if (type == SpawnerType.GOLD && goldSpawns++ < 3) { // first 3 spawns are skipped
                        break;
                    }

                    location.getWorld().dropItem(location, type.item);

                    for (int i = 0; i < 25; i++) {
                        ParticleEffect.SPELL_MOB.display(type.particleColor, location, 50);
                    }
                }
            }

            passedSeconds.getAndIncrement();
        }, 0, 20);
    }

    public void stopSpawning() {
        if (this.spawnerTask == -1) {
            throw new RuntimeException("No spawner task is running");
        }

        Bukkit.getScheduler().cancelTask(spawnerTask);
        spawnerTask = -1;
    }

    public enum SpawnerType {

        BRONZE(new ItemBuilder(Material.CLAY_BRICK).setDisplayName("§cBronze").build(), new ParticleEffect.OrdinaryColor(255, 127, 64)),
        IRON(new ItemBuilder(Material.IRON_INGOT).setDisplayName("§7Eisen").build(), new ParticleEffect.OrdinaryColor(193, 193, 193)),
        GOLD(new ItemBuilder(Material.GOLD_INGOT).setDisplayName("§6Gold").build(), new ParticleEffect.OrdinaryColor(255, 233, 0));

        @Getter
        private ItemStack item;

        @Getter
        private ParticleEffect.OrdinaryColor particleColor;

        SpawnerType(ItemStack item, ParticleEffect.OrdinaryColor particleColor) {
            this.item = item;
            this.particleColor = particleColor;
        }

    }

}
