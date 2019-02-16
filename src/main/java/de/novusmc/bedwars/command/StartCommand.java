package de.novusmc.bedwars.command;

import de.novusmc.bedwars.BedWars;
import de.novusmc.bedwars.Messages;
import de.novusmc.bedwars.Permissions;
import de.novusmc.bedwars.game.Team;
import de.novusmc.bedwars.phase.GamePhase;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by Paul
 * on 07.12.2018
 *
 * @author pauhull
 */
public class StartCommand implements CommandExecutor {

    private BedWars bedWars;

    public StartCommand(BedWars bedWars) {
        this.bedWars = bedWars;
        bedWars.getCommand("start").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permissions.START)) {
            sender.sendMessage(Messages.PREFIX + "Nur §ePremium+ §7Spieler dürfen diesen Befehl benutzen!");
            return true;
        }

        if (bedWars.getPhaseHandler().getActivePhaseType() != GamePhase.Type.LOBBY) {
            sender.sendMessage(Messages.PREFIX + "Das Spiel ist §cbereits §7gestartet!");
            return true;
        }

        if (Bukkit.getOnlinePlayers().size() < Team.MIN_PLAYERS) {
            sender.sendMessage(Messages.PREFIX + "Es sind §cnicht §7genug Spieler vorhanden, um das Spiel zu starten!");
            return true;
        }

        bedWars.getPhaseHandler().getActivePhase().end();
        return true;
    }


}
