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
public class NpcCommand implements CommandExecutor {

    private BedWars bedWars;

    public NpcCommand(BedWars bedWars) {
        this.bedWars = bedWars;
        bedWars.getCommand("npc").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permissions.NPC)) {
            sender.sendMessage(Messages.PREFIX + Messages.NO_PERMISSIONS);
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.PREFIX + Messages.ONLY_PLAYERS);
            return true;
        }

        if (args.length == 0 || (!args[0].equalsIgnoreCase("spawn") && !args[0].equalsIgnoreCase("removeall"))) {
            sender.sendMessage(Messages.PREFIX + "§c/npc <spawn|removeall>");
            return true;
        }

        Player player = (Player) sender;
        if (args[0].equalsIgnoreCase("spawn")) {
            bedWars.getNpcManager().create(player.getLocation());
            player.sendMessage(Messages.PREFIX + "NPC erfolgreich erstellt.");
        } else if (args[0].equalsIgnoreCase("removeall")) {
            bedWars.getNpcManager().removeAll();
            player.sendMessage(Messages.PREFIX + "Alle NPCs entfernt.");
        }

        return true;
    }

}
