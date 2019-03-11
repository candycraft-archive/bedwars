package de.novusmc.bedwars;

import de.novusmc.bedwars.command.*;
import de.novusmc.bedwars.data.MySQL;
import de.novusmc.bedwars.data.table.StatsTable;
import de.novusmc.bedwars.display.LobbyScoreboard;
import de.novusmc.bedwars.game.Team;
import de.novusmc.bedwars.inventory.ShopInventory;
import de.novusmc.bedwars.inventory.SpectatorInventory;
import de.novusmc.bedwars.inventory.TeamSelectInventory;
import de.novusmc.bedwars.listener.*;
import de.novusmc.bedwars.manager.*;
import de.novusmc.bedwars.phase.GamePhaseHandler;
import de.novusmc.bedwars.phase.type.LobbyPhase;
import de.novusmc.bedwars.util.SkullCache;
import de.pauhull.scoreboard.ScoreboardManager;
import de.pauhull.uuidfetcher.common.fetcher.UUIDFetcher;
import de.pauhull.uuidfetcher.spigot.SpigotUUIDFetcher;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.DedicatedPlayerList;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Paul
 * on 03.01.2019
 *
 * @author pauhull
 */
public class BedWars extends JavaPlugin {

    @Getter
    private static BedWars instance = null;
    @Getter
    private ExecutorService executorService;
    @Getter
    private GamePhaseHandler phaseHandler;
    @Getter
    private NpcManager npcManager;
    @Getter
    private ShopInventory shopInventory;
    @Getter
    private TeamSelectInventory teamSelectInventory;
    @Getter
    private SpectatorInventory spectatorInventory;
    @Getter
    private LocationManager locationManager;
    @Getter
    private BedManager bedManager;
    @Getter
    private SpawnerManager spawnerManager;
    @Getter
    private ScoreboardManager scoreboardManager;
    @Getter
    private File configFile;
    @Getter
    private FileConfiguration config;
    @Getter
    private boolean pluginEnabled;
    @Getter
    private List<Player> spectators;
    @Getter
    private List<Block> placedBlocks;
    @Getter
    private SkullCache skullCache;
    @Getter
    private MySQL mySQL;
    @Getter
    private StatsTable statsTable;
    @Getter
    private FileConfiguration gameSettings;
    @Getter
    private UUIDFetcher uuidFetcher;
    @Getter
    private TopPlayerManager topPlayerManager;

    @Override
    public void onEnable() {
        instance = this;

        this.topPlayerManager = new TopPlayerManager(this);
        this.uuidFetcher = SpigotUUIDFetcher.getInstance();
        this.skullCache = new SkullCache();
        this.spectators = new ArrayList<>();
        this.placedBlocks = new ArrayList<>();
        this.configFile = new File(getDataFolder(), "config.yml");
        this.config = copyAndLoad("config.yml", configFile);
        this.gameSettings = copyAndLoad("gameSettings.yml", new File(getDataFolder(), "gameSettings.yml"));
        this.pluginEnabled = config.getBoolean("Enabled");
        this.executorService = Executors.newSingleThreadExecutor();
        this.phaseHandler = new GamePhaseHandler();
        this.npcManager = new NpcManager(this);
        this.shopInventory = new ShopInventory(this);
        this.teamSelectInventory = new TeamSelectInventory(this);
        this.spectatorInventory = new SpectatorInventory(this);
        this.locationManager = new LocationManager(this);
        this.spawnerManager = new SpawnerManager(this);
        this.bedManager = new BedManager(this);
        this.scoreboardManager = new ScoreboardManager(this, LobbyScoreboard.class);
        this.mySQL = new MySQL(config.getString("MySQL.Host"),
                config.getString("MySQL.Port"),
                config.getString("MySQL.Database"),
                config.getString("MySQL.User"),
                config.getString("MySQL.Password"),
                config.getBoolean("MySQL.SSL"));
        if (!this.mySQL.connect()) {
            return;
        }
        this.statsTable = new StatsTable(mySQL, executorService);

        Team.loadLocations(locationManager);
        Team.loadBeds(bedManager);
        Team.TEAM_SIZE = gameSettings.getInt("PlayersPerTeam");
        Team.TEAM_AMOUNT = Team.getActiveTeamAmount();
        Team.MIN_PLAYERS = Team.TEAM_SIZE + 1;
        Team.MAX_PLAYERS = Team.TEAM_SIZE * Team.TEAM_AMOUNT;

        try {
            DedicatedPlayerList server = ((CraftServer) Bukkit.getServer()).getHandle();
            Field maxPlayers = server.getClass().getSuperclass().getDeclaredField("maxPlayers");
            maxPlayers.setAccessible(true);
            maxPlayers.set(server, Team.MAX_PLAYERS);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

        if (pluginEnabled) {
            this.phaseHandler.startPhase(LobbyPhase.class);

            new AsyncPlayerChatListener(this);
            new BlockBreakListener(this);
            new BlockPlaceListener(this);
            new EntityDamageByEntityListener(this);
            new EntityDamageListener(this);
            new FoodLevelChangeListener(this);
            new InventoryClickListener(this);
            new PlayerAchievementAwardedListener(this);
            new PlayerPortalListener(this);
            new PlayerDeathListener(this);
            new PlayerDropItemListener(this);
            new PlayerInteractAtEntityListener(this);
            new PlayerInteractListener(this);
            new PlayerJoinListener(this);
            new PlayerLoginListener(this);
            new PlayerMoveListener(this);
            new PlayerNickListener(this);
            new PlayerPartyListener(this);
            new PlayerPickupItemListener(this);
            new PlayerQuitListener(this);
            new PlayerRespawnListener(this);
            new PlayerToggleSneakListener(this);
            new PrepareItemCraftListener(this);
            new PlayerClickNpcListener(this);
            new WeatherChangeListener(this);
        }

        new JumpAndRunListener(this);
        new NpcCommand(this);
        new SetLocationCommand(this);
        new BedCommand(this);
        new StartCommand(this);
        new SpawnerCommand(this);
        new StatsCommand(this);

        topPlayerManager.init();
    }

    @Override
    public void onDisable() {
        instance = null;

        this.mySQL.close();
        this.executorService.shutdown();
    }

    private void copy(String resource, File file, boolean override) {
        if (!file.exists() || override) {
            file.getParentFile().mkdirs();

            if (file.exists()) {
                file.delete();
            }

            try {
                Files.copy(getResource(resource), file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private YamlConfiguration copyAndLoad(String resource, File file) {
        copy(resource, file, false);
        return YamlConfiguration.loadConfiguration(file);
    }

}
