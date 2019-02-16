package de.novusmc.bedwars.command;

import de.novusmc.bedwars.BedWars;
import de.novusmc.bedwars.Messages;
import de.novusmc.bedwars.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Paul
 * on 04.01.2019
 *
 * @author pauhull
 */
public class SetLocationCommand implements CommandExecutor {

    private BedWars bedWars;

    public SetLocationCommand(BedWars bedWars) {
        this.bedWars = bedWars;
        bedWars.getCommand("setlocation").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permissions.SET_LOCATION)) {
            sender.sendMessage(Messages.PREFIX + Messages.NO_PERMISSIONS);
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.PREFIX + Messages.ONLY_PLAYERS);
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Messages.PREFIX + "§c/setlocation <Name>");
            return true;
        }

        Player player = (Player) sender;
        bedWars.getLocationManager().setLocation(args[0], player.getLocation());
        player.sendMessage(Messages.PREFIX + "Deine Position wurde erfolgreich unter \"" + args[0] + "\" gespeichert!");

        return true;
    }
}
