package de.novusmc.bedwars.listener;

import de.novusmc.bedwars.BedWars;
import de.novusmc.bedwars.Messages;
import de.novusmc.bedwars.game.Team;
import de.novusmc.bedwars.phase.GamePhase;
import de.novusmc.bedwars.util.Title;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.material.Bed;

/**
 * Created by Paul
 * on 04.01.2019
 *
 * @author pauhull
 */
public class BlockBreakListener implements Listener {

    private BedWars bedWars;

    public BlockBreakListener(BedWars bedWars) {
        this.bedWars = bedWars;
        Bukkit.getPluginManager().registerEvents(this, bedWars);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (bedWars.getSpectators().contains(player)) {
            event.setCancelled(true);
            return;
        }

        if (bedWars.getPhaseHandler().getActivePhaseType() != GamePhase.Type.INGAME) {
            event.setCancelled(true);
            return;
        }

        if (!bedWars.getPlacedBlocks().contains(block)) {
            event.setCancelled(true);
        } else {
            bedWars.getPlacedBlocks().remove(block);
        }

        if (block.getType() == Material.WEB) {
            event.setCancelled(true);
            event.getBlock().setType(Material.AIR);
            return;
        }

        if (block.getType() == Material.BED_BLOCK) {

            Location bedHead = block.getLocation();
            Bed bed = (Bed) block.getState().getData();
            if (!bed.isHeadOfBed()) {
                bedHead = block.getRelative(bed.getFacing()).getLocation();
            }

            Team team = Team.getTeam(player);
            if (team == null) {
                event.setCancelled(true);
                return;
            }

            Team brokenTeam = null;
            for (Team check : Team.values()) {
                if (check.isHasBed() && check.getBedLocation() != null && check.getBedLocation().equals(bedHead)) {
                    brokenTeam = check;
                }
            }

            if (brokenTeam != null) {
                if (team.equals(brokenTeam)) {
                    player.playEffect(block.getLocation().add(0, 1, 0), Effect.SMOKE, 4);
                    event.setCancelled(true);
                    return;
                }

                if (brokenTeam.getMembers().isEmpty()) {
                    player.sendMessage(Messages.PREFIX + "Dieses Team ist bereits §causgeschieden§7!");
                    return;
                }

                brokenTeam.setHasBed(false);
                Bukkit.broadcastMessage(Messages.PREFIX + "Das Bett von Team " + brokenTeam.getColoredName() + "§7 wurde von " + team.getChatColor() + player.getName() + "§7 zerstört!");
                for (Player all : Bukkit.getOnlinePlayers()) {
                    all.playSound(all.getLocation(), Sound.WITHER_DEATH, 10, 1);
                    if (brokenTeam.getMembers().contains(all)) {
                        Title.sendTitle(all, "Dein Bett", "wurde §czerstört§r!", 10, 40, 10);
                    }
                }

                // set both blocks to air and use false as second parameter so that no block update occurs and no item drops
                event.setCancelled(true);
                Block headBlock = bedHead.getBlock();
                Block baseBlock = headBlock.getRelative(bed.getFacing().getOppositeFace());
                headBlock.setType(Material.AIR, false);
                baseBlock.setType(Material.AIR, false);

            }
        }

    }

}
