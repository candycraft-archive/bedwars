package de.novusmc.bedwars.phase.type;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import de.novusmc.bedwars.BedWars;
import de.novusmc.bedwars.display.IngameScoreboard;
import de.novusmc.bedwars.game.Team;
import de.novusmc.bedwars.phase.GamePhase;
import de.novusmc.bedwars.phase.GamePhaseHandler;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.DedicatedPlayerList;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Paul
 * on 07.12.2018
 *
 * @author pauhull
 */
public class IngamePhase extends GamePhase {

    private static final int GAME_LENGTH = 30 * 60; // 30 minutes

    @Getter
    private static Map<String, Double> percentages = new HashMap<>();

    @Getter
    private Type type = Type.INGAME;

    @Getter
    private int time = 0;

    private Team winningTeam = null;

    public IngamePhase(GamePhaseHandler handler) {
        super(handler);
    }

    @Override
    public void run() {
        time++;

        if (time >= GAME_LENGTH) {
            this.end();
        }
    }

    @Override
    public void start() {
        MinecraftServer.getServer().setMotd(Long.toString(System.currentTimeMillis()));
        try {
            DedicatedPlayerList server = ((CraftServer) Bukkit.getServer()).getHandle();
            Field maxPlayers = server.getClass().getSuperclass().getDeclaredField("maxPlayers");
            maxPlayers.setAccessible(true);
            maxPlayers.set(server, Team.MAX_PLAYERS + 10);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

        TimoCloudAPI.getBukkitAPI().getThisServer().setState("INGAME");
        BedWars.getInstance().getSpawnerManager().startSpawning();
        BedWars.getInstance().getScoreboardManager().setScoreboard(IngameScoreboard.class);
        for (Player all : Bukkit.getOnlinePlayers()) {
            BedWars.getInstance().getStatsTable().getStats(all.getUniqueId(), stats -> {
                stats.setPlayedGames(stats.getPlayedGames() + 1);
                BedWars.getInstance().getStatsTable().setStats(all.getUniqueId(), stats);
            });
        }
        super.start();
    }

    @Override
    public void end() {
        super.end();
        EndPhase endPhase = new EndPhase(handler);
        endPhase.setStartTime(startTime);
        endPhase.setWinningTeam(winningTeam);
        handler.startPhase(endPhase);
    }

    public void checkForWin() {
        Team winningTeam = null;
        for (Team team : Team.values()) {
            if (!team.isEnabled())
                continue;

            if (team.getMembers().size() > 0) {
                if (winningTeam == null) {
                    winningTeam = team;
                } else {
                    winningTeam = null;
                    break;
                }
            }
        }

        if (winningTeam != null) {
            this.winningTeam = winningTeam;
            for (Player all : winningTeam.getMembers()) {
                BedWars.getInstance().getStatsTable().getStats(all.getUniqueId(), stats -> {
                    stats.setWins(stats.getWins() + 1);
                    BedWars.getInstance().getStatsTable().setStats(all.getUniqueId(), stats);
                });
            }
            this.end();
        }
    }

}
