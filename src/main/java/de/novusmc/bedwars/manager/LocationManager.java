package de.novusmc.bedwars.manager;

import de.novusmc.bedwars.BedWars;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

/**
 * Created by Paul
 * on 03.01.2019
 *
 * @author pauhull
 */
public class LocationManager {

    private File file;
    private FileConfiguration config;
    private BedWars bedWars;

    public LocationManager(BedWars bedWars) {
        this.bedWars = bedWars;
        this.file = new File(bedWars.getDataFolder(), "locations.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Location getLocation(String name) {
        return (Location) config.get(name);
    }

    public void setLocation(String name, Location location) {
        config.set(name, location);
        save();
    }

    public boolean isSet(String name) {
        return getLocation(name) != null;
    }

    public void teleport(Player player, String name) {
        Location location = getLocation(name);
        if (location != null) {
            player.teleport(location);
        }
    }

}
