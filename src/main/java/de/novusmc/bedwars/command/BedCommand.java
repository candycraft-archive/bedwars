package de.novusmc.bedwars.command;

import de.novusmc.bedwars.BedWars;
import de.novusmc.bedwars.Messages;
import de.novusmc.bedwars.Permissions;
import de.novusmc.bedwars.game.Team;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.material.Bed;

import java.util.HashSet;

/**
 * Created by Paul
 * on 05.01.2019
 *
 * @author pauhull
 */
public class BedCommand implements CommandExecutor {

    private BedWars bedWars;

    public BedCommand(BedWars bedWars) {
        this.bedWars = bedWars;
        bedWars.getCommand("bed").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permissions.BED)) {
            sender.sendMessage(Messages.PREFIX + Messages.NO_PERMISSIONS);
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.PREFIX + Messages.ONLY_PLAYERS);
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Messages.PREFIX + "§c/bed <BLUE|GREEN|RED|YELLOW|ORANGE|CYAN|PINK|WHITE|clear>");
            return true;
        }

        Player player = (Player) sender;
        Team team = null;

        try {
            team = Team.valueOf(args[0].toUpperCase());
        } catch (Exception ignored) {
        }
        if (team == null && !args[0].equalsIgnoreCase("clear")) {
            sender.sendMessage(Messages.PREFIX + "§c/bed <BLUE|GREEN|RED|YELLOW|ORANGE|CYAN|PINK|WHITE|clear>");
            return true;
        }

        if (args[0].equalsIgnoreCase("clear")) {
            bedWars.getBedManager().clear();
            player.sendMessage(Messages.PREFIX + "Alle Bettpositionen wurden gelöscht.");
        } else if (team != null) {
            Block target = player.getTargetBlock((HashSet<Byte>) null, 5);

            if (target.getType() != Material.BED_BLOCK) {
                player.sendMessage(Messages.PREFIX + "Du musst ein Bett angucken.");
                return true;
            }

            Location location = target.getLocation();
            Bed bed = (Bed) target.getState().getData();
            if (!bed.isHeadOfBed()) {
                location = target.getRelative(bed.getFacing()).getLocation();
            }

            bedWars.getBedManager().add(location, team);
            player.sendMessage(Messages.PREFIX + "Bett für Team " + team.getColoredName() + "§7 gesetzt.");
        }
        return true;
    }

}
