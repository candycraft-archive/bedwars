package de.novusmc.bedwars.command;

import de.novusmc.bedwars.BedWars;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class StatsCommand implements CommandExecutor {

    private BedWars bedWars;

    public StatsCommand(BedWars bedWars) {
        this.bedWars = bedWars;
        bedWars.getCommand("stats").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;

        bedWars.getStatsTable().getStats(player.getUniqueId(), stats -> {
            if (stats != null) {
                player.sendMessage("§8§m---|§e Statistiken §8§m|---");
                player.sendMessage("§a");
                player.sendMessage("§eGespielte Spiele §8» §7" + stats.getPlayedGames());
                player.sendMessage("§eGewonnene Spiele §8» §7" + stats.getWins());
                player.sendMessage("§eWin-Rate §8» §7" + stats.getWinRate());
                player.sendMessage("§b");
                player.sendMessage("§eKills §8» §7" + stats.getKills());
                player.sendMessage("§eTode §8» §7" + stats.getDeaths());
                player.sendMessage("§eK/D §8» §7" + format(stats.getKD()));
                player.sendMessage("§c");
                player.sendMessage("§8§m---|§e Statistiken §8§m|---");
            }
        });

        return true;
    }

    private String format(double i) {
        DecimalFormat f = new DecimalFormat("#0.00");
        double toFormat = Math.round(i * 100.0D) / 100.0D;
        return f.format(toFormat);
    }

}
