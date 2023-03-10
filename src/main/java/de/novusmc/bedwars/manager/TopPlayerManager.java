package de.novusmc.bedwars.manager;

import de.novusmc.bedwars.BedWars;
import de.pauhull.npcapi.npc.Npc;
import de.pauhull.npcapi.npc.SkinData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Paul
 * on 23.02.2019
 *
 * @author pauhull
 */
public class TopPlayerManager {

    private BedWars bedWars;
    private File skinCacheFile;
    private FileConfiguration skinCache;

    public TopPlayerManager(BedWars bedWars) {
        this.bedWars = bedWars;
        this.skinCacheFile = new File(System.getProperty("user.home"), "skinCache.yml");
        if (!this.skinCacheFile.exists()) {
            try {
                skinCacheFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.skinCache = YamlConfiguration.loadConfiguration(skinCacheFile);
    }

    public void init() {
        if (!bedWars.getLocationManager().isSet("top1")
                || !bedWars.getLocationManager().isSet("top2")
                || !bedWars.getLocationManager().isSet("top3")
                || !bedWars.getLocationManager().isSet("sign1")
                || !bedWars.getLocationManager().isSet("sign2")
                || !bedWars.getLocationManager().isSet("sign3"))
            return;

        bedWars.getStatsTable().getTopPlayers(3, topPlayers -> {
            if (topPlayers.size() != 3) return;

            for (int i = 0; i < 3; i++) {
                final Location location = bedWars.getLocationManager().getLocation("top" + (i + 1));
                final Location signLocation = bedWars.getLocationManager().getLocation("sign" + (i + 1));
                final int index = i;
                bedWars.getUuidFetcher().fetchProfileAsync(topPlayers.get(i), profile -> {
                    Npc npc = new Npc(location, UUID.randomUUID(), "??e" + profile.getPlayerName(), getSkinData(profile.getUuid()), false);
                    npc.spawn();

                    final DecimalFormat format = new DecimalFormat("#.##");
                    bedWars.getStatsTable().getStats(profile.getUuid(), stats -> Bukkit.getScheduler().runTask(bedWars, () -> {
                        Block block = signLocation.getBlock();
                        Sign sign = (Sign) block.getState();
                        sign.setLine(0, "??2" + (index + 1) + ".");
                        sign.setLine(1, "??1" + profile.getPlayerName());
                        sign.setLine(2, "??4" + stats.getWins() + " Wins");
                        sign.setLine(3, "??5K/D: " + format.format(stats.getKD()));
                        sign.update();
                    }));
                });
            }
        });
    }

    public SkinData getSkinData(UUID uuid) { // Cache skin for a hour
        if (skinCache.isSet(uuid.toString())) {
            if (System.currentTimeMillis() - skinCache.getLong(uuid + ".Timestamp") > TimeUnit.HOURS.toMillis(1)) {
                skinCache.set(uuid.toString(), null);
                try {
                    skinCache.save(skinCacheFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return getSkinData(uuid);
            } else {
                String texture = skinCache.getString(uuid + ".Texture");
                String signature = skinCache.getString(uuid + ".Signature");
                return new SkinData(texture, signature);
            }
        }

        SkinData data = SkinData.getSkinSync(uuid);
        if (data == null) return null;

        skinCache.set(uuid + ".Timestamp", System.currentTimeMillis());
        skinCache.set(uuid + ".Texture", data.getTexture());
        skinCache.set(uuid + ".Signature", data.getSignature());
        try {
            skinCache.save(skinCacheFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

}
