package de.novusmc.bedwars.phase.type;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import de.godtitan.coins.CoinAPI;
import de.novusmc.bedwars.game.Team;
import de.novusmc.bedwars.phase.GamePhase;
import de.novusmc.bedwars.phase.GamePhaseHandler;
import de.novusmc.bedwars.util.Title;
import de.pauhull.utils.misc.RandomFireworkGenerator;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Created by Paul
 * on 08.12.2018
 *
 * @author pauhull
 */
public class EndPhase extends GamePhase {

    @Getter
    private Type type = Type.ENDING;

    @Setter
    private Team winningTeam;

    private int seconds = 0;

    public EndPhase(GamePhaseHandler handler) {
        super(handler);
    }

    @Override
    public void run() {
        if (seconds >= 10) {
            this.end();
            return;
        }

        seconds++;
    }

    @Override
    public void start() {
        super.start();

        int seconds = (int) (System.currentTimeMillis() - startTime) / 1000;
        int minutes = Math.floorDiv(seconds, 60);
        seconds %= 60;

        if (winningTeam != null) {
            for (Player player : winningTeam.getMembers()) {
                RandomFireworkGenerator.shootRandomFirework(player.getLocation(), 10);
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 1);

                Title.sendTitle(player, winningTeam.getColoredName() + "§r hat §agewonnen§r!",
                        winningTeam.getMembers().contains(player) ? "§a+§r 75 Coins" : "", 0, 40, 20);

                if (winningTeam.getMembers().contains(player)) {
                    CoinAPI.getInstance().addCoins(player.getUniqueId(), 75);
                }
            }

            Bukkit.broadcastMessage("§8§m                                 ");
            Bukkit.broadcastMessage("§7Team " + winningTeam.getColoredName() + "§7 hat das Spiel §agewonnen§7!");
            Bukkit.broadcastMessage("§7Das Spiel hat §e" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds) + "§7 gedauert.");
            Bukkit.broadcastMessage("§8§m                                 ");
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 1);

                Title.sendTitle(player, "Das Spiel ist vorbei", "", 0, 40, 20);
            }

            Bukkit.broadcastMessage("§8§m                                 ");
            Bukkit.broadcastMessage("§7Das Spiel ist vorbei und §cuntentschieden§7!");
            Bukkit.broadcastMessage("§7Das Spiel hat §e" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds) + "§7 gedauert.");
            Bukkit.broadcastMessage("§8§m                                 ");
        }


    }

    @Override
    public void end() {
        super.end();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer("");
        }

        TimoCloudAPI.getBukkitAPI().getThisServer().stop();
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

}
