package de.novusmc.bedwars.manager;

import de.novusmc.bedwars.BedWars;
import de.novusmc.bedwars.game.Team;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Created by Paul
 * on 03.01.2019
 *
 * @author pauhull
 */
public class BedManager {

    private File file;
    private FileConfiguration config;
    private BedWars bedWars;

    public BedManager(BedWars bedWars) {
        this.bedWars = bedWars;
        this.file = new File(bedWars.getDataFolder(), "beds.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void load() {
        for (Team team : Team.values()) {
            team.setBedLocation((Location) config.get(team.name()));
        }
    }

    public void save() {
        for (Team team : Team.values()) {
            config.set(team.name(), team.getBedLocation());
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        for (Team team : Team.values()) {
            team.setBedLocation(null);
        }
        save();
    }

    public void add(Location location, Team team) {
        team.setBedLocation(location);
        save();
    }

}
