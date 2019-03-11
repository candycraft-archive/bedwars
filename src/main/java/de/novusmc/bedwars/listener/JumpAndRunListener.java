package de.novusmc.bedwars.listener;

import de.novusmc.bedwars.BedWars;
import de.novusmc.bedwars.Messages;
import de.novusmc.bedwars.game.Team;
import de.novusmc.bedwars.manager.ItemManager;
import de.novusmc.bedwars.phase.GamePhase;
import de.pauhull.utils.misc.RandomFireworkGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Created by Paul
 * on 10.03.2019
 *
 * @author pauhull
 */
public class JumpAndRunListener implements Listener {

    private Map<Player, Integer> jumpAndRunCheckpoints = new HashMap<>();
    private List<Player> jumpAndRunFinished = new ArrayList<>();
    private Map<Player, LinkedList<Block>> lobbyBlocks = new HashMap<>();
    private BedWars bedWars;

    public JumpAndRunListener(BedWars bedWars) {
        Bukkit.getPluginManager().registerEvents(this, bedWars);
        this.bedWars = bedWars;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();
        Block block = player.getLocation().getBlock();

        if (bedWars.getPhaseHandler().getActivePhaseType() == GamePhase.Type.LOBBY) {

            if (block.getType() == Material.IRON_PLATE) {
                if (lobbyBlocks.containsKey(player)) {
                    for (Block replace : lobbyBlocks.get(player)) {
                        replace.setType(Material.BARRIER);
                        replace.setData((byte) 0);
                    }
                    lobbyBlocks.get(player).clear();
                }

                if (!jumpAndRunCheckpoints.containsKey(player)) {
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
                    player.sendMessage(Messages.PREFIX + "Du hast das §eJump and Run §7begonnen!");
                    ItemManager.giveJumpAndRunItems(player);
                    jumpAndRunCheckpoints.put(player, 0);
                    jumpAndRunFinished.remove(player);
                }
            }

            if (block.getType() == Material.GOLD_PLATE && !jumpAndRunFinished.contains(player)) {
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
                player.sendMessage(Messages.PREFIX + "Du hast das Jump and Run §ageschafft§7!");
                RandomFireworkGenerator.shootRandomFirework(player.getLocation(), 5);
                ItemManager.giveLobbyItems(player);
                jumpAndRunFinished.add(player);
                jumpAndRunCheckpoints.remove(player);
            }

            if (block.getType() == Material.WOOD_PLATE && jumpAndRunCheckpoints.containsKey(player)) {
                Block signBlock = block.getLocation().subtract(0, 2, 0).getBlock();
                if (signBlock.getType() == Material.SIGN_POST || signBlock.getType() == Material.WALL_SIGN) {
                    Sign sign = (Sign) signBlock.getState();
                    int checkpointId = Integer.parseInt(sign.getLine(0).replace("[CP-", "").replace("]", ""));
                    int currentCheckpointId = jumpAndRunCheckpoints.get(player);
                    if (checkpointId != currentCheckpointId) {
                        jumpAndRunCheckpoints.put(player, checkpointId);
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
                        player.sendMessage(Messages.PREFIX + "Du hast §aCheckpoint " + checkpointId + "§7 erreicht!");
                    }
                }
            }

            Block barrierBlock = null;
            Block blockUnder = player.getLocation().subtract(0, 1, 0).getBlock();
            Block blockUnderBlockUnder = player.getLocation().subtract(0, 1.3, 0).getBlock();
            if (blockUnder.getType() == Material.BARRIER) {
                barrierBlock = blockUnder;
            } else if (blockUnderBlockUnder.getType() == Material.BARRIER) {
                barrierBlock = blockUnderBlockUnder;
            }

            if (barrierBlock != null && !jumpAndRunCheckpoints.containsKey(player)) {
                LinkedList<Block> blocks;
                if (!lobbyBlocks.containsKey(player)) {
                    blocks = new LinkedList<>();
                    lobbyBlocks.put(player, blocks);
                } else {
                    blocks = lobbyBlocks.get(player);
                }

                if (blocks.size() >= 3) {
                    blocks.getFirst().setType(Material.BARRIER);
                    blocks.getFirst().setData((byte) 0);
                    blocks.removeFirst();
                }

                barrierBlock.setType(Material.WOOL);
                Team team = Team.getTeam(player);
                if (team != null) {
                    barrierBlock.setData(team.getDyeColor().getWoolData());
                }
                blocks.add(barrierBlock);
                final Block finalBarrierBlock = barrierBlock;
                Bukkit.getScheduler().scheduleSyncDelayedTask(bedWars, () -> {
                    if (finalBarrierBlock.getType() == Material.WOOL && player.getLocation().distanceSquared(finalBarrierBlock.getLocation().add(0.5, 1, 0.5)) > 0.75 * 0.75) {
                        finalBarrierBlock.setType(Material.BARRIER);
                        finalBarrierBlock.setData((byte) 0);
                    }
                }, 15);

                lobbyBlocks.put(player, blocks);
            }

        }

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        jumpAndRunCheckpoints.remove(player);
        jumpAndRunFinished.remove(player);
    }

    public BlockFace yawToFace(float yaw) {
        return new BlockFace[]{BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST}[(Math.round(yaw / 90.0F) & 0x3)];
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }
        ItemStack stack = event.getItem();

        if (bedWars.getPhaseHandler().getActivePhaseType() == GamePhase.Type.LOBBY && event.getItem() != null) {
            if (stack.equals(ItemManager.LEAVE_JAR)) {
                player.sendMessage(Messages.PREFIX + "Du hast das Jump and Run §cverlassen§7.");
                ItemManager.giveLobbyItems(player);
                bedWars.getLocationManager().teleport(player, "Lobby");
                player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                jumpAndRunCheckpoints.remove(player);
                jumpAndRunFinished.remove(player);
            } else if (stack.equals(ItemManager.BACK) && jumpAndRunCheckpoints.containsKey(player)) {
                int checkpointId = jumpAndRunCheckpoints.get(player);
                if (checkpointId == 0) {
                    bedWars.getLocationManager().teleport(player, "JumpAndRun");
                } else {
                    bedWars.getLocationManager().teleport(player, "cp" + checkpointId);
                }
                player.playSound(player.getLocation(), Sound.BAT_DEATH, 1, 1);
                player.getInventory().setItem(4, ItemManager.WAIT);
                Bukkit.getScheduler().scheduleSyncDelayedTask(bedWars, () -> {
                    if (player.getInventory().getItem(4) != null && player.getInventory().getItem(4).equals(ItemManager.WAIT)) {
                        player.getInventory().setItem(4, ItemManager.BACK);
                    }
                }, 20);
            }
        }
    }

}
