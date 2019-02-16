package de.novusmc.bedwars.command;

import de.novusmc.bedwars.BedWars;
import de.novusmc.bedwars.Messages;
import de.novusmc.bedwars.Permissions;
import de.novusmc.bedwars.manager.SpawnerManager.SpawnerType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Paul
 * on 05.01.2019
 *
 * @author pauhull
 */
public class SpawnerCommand implements CommandExecutor {

    private BedWars bedWars;

    public SpawnerCommand(BedWars bedWars) {
        this.bedWars = bedWars;
        bedWars.getCommand("spawner").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permissions.SPAWNER)) {
            sender.sendMessage(Messages.PREFIX + Messages.NO_PERMISSIONS);
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.PREFIX + Messages.ONLY_PLAYERS);
            return true;
        }

        if (args.length == 0 || (!args[0].equalsIgnoreCase("bronze") && !args[0].equalsIgnoreCase("gold")
                && !args[0].equalsIgnoreCase("iron") && !args[0].equalsIgnoreCase("clearall"))) {
            sender.sendMessage(Messages.PREFIX + "§c/spawner <BRONZE|GOLD|IRON|clearall>");
            return true;
        }

        Player player = (Player) sender;

        if (args[0].equalsIgnoreCase("clearall")) {
            bedWars.getSpawnerManager().clear();
            player.sendMessage(Messages.PREFIX + "Alle Spawner entfernt.");
            return true;
        }

        SpawnerType type = SpawnerType.valueOf(args[0].toUpperCase());
        bedWars.getSpawnerManager().add(player.getLocation(), type);
        player.sendMessage(Messages.PREFIX + "Erfolgreich Spawner hinzugefügt: " + type.getItem().getItemMeta().getDisplayName());

        return true;
    }

}
