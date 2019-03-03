package de.novusmc.bedwars.listener;

import de.novusmc.bedwars.BedWars;
import de.novusmc.bedwars.game.Team;
import de.novusmc.bedwars.inventory.ExtrasInventory;
import de.novusmc.bedwars.manager.ItemManager;
import de.novusmc.bedwars.phase.GamePhase;
import de.novusmc.bedwars.util.InventoryUtils;
import de.novusmc.bedwars.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Paul
 * on 04.01.2019
 *
 * @author pauhull
 */
public class PlayerInteractListener implements Listener {

    private BedWars bedWars;

    public PlayerInteractListener(BedWars bedWars) {
        this.bedWars = bedWars;
        Bukkit.getPluginManager().registerEvents(this, bedWars);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = event.getItem();
        Block block = event.getClickedBlock();

        if (bedWars.getSpectators().contains(player)) {
            event.setCancelled(true);

            if (stack != null && stack.equals(ItemManager.SPECTATE)) {
                bedWars.getSpectatorInventory().show(player);
            }

            return;
        }

        if (bedWars.getPhaseHandler().getActivePhaseType() == GamePhase.Type.LOBBY) {
            event.setCancelled(true);

            if (stack != null) {
                if (stack.equals(ItemManager.LEAVE)) {
                    player.kickPlayer("");
                } else if (stack.equals(ItemManager.TEAM_SELECT)) {
                    bedWars.getTeamSelectInventory().show(player);
                }
            }
        } else {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (block.getType() == Material.ENDER_CHEST) {
                    event.setCancelled(true);
                    Team team = Team.getTeam(player);
                    if (team != null) {
                        player.openInventory(team.getChestInventory());
                    }
                    return;
                }
            }

            if (stack != null) {
                stack = new ItemBuilder(stack).setAmount(1).build();

                if (stack.equals(ExtrasInventory.PLATFORM_STRIPPED)) {

                    InventoryUtils.removeItems(player.getInventory(), ExtrasInventory.PLATFORM_STRIPPED, 1);

                    final Location location = player.getLocation();
                    final Location frontBlock = new Location(location.getWorld(), location.getX() + 1, location.getY() - 1, location.getZ());
                    final Location backBlock = new Location(location.getWorld(), location.getX() - 1, location.getY() - 1, location.getZ());
                    final Location middleBlock = new Location(location.getWorld(), location.getX(), location.getY() - 1, location.getZ());
                    final Location rightBlock = new Location(location.getWorld(), location.getX(), location.getY() - 1, location.getZ() - 1);
                    final Location leftBlock = new Location(location.getWorld(), location.getX(), location.getY() - 1, location.getZ() + 1);

                    if (frontBlock.getBlock().getType().equals(Material.AIR))
                        frontBlock.getBlock().setType(Material.SLIME_BLOCK);
                    if (backBlock.getBlock().getType().equals(Material.AIR))
                        backBlock.getBlock().setType(Material.SLIME_BLOCK);
                    if (middleBlock.getBlock().getType().equals(Material.AIR))
                        middleBlock.getBlock().setType(Material.SLIME_BLOCK);
                    if (rightBlock.getBlock().getType().equals(Material.AIR))
                        rightBlock.getBlock().setType(Material.SLIME_BLOCK);
                    if (leftBlock.getBlock().getType().equals(Material.AIR))
                        leftBlock.getBlock().setType(Material.SLIME_BLOCK);

                    Bukkit.getScheduler().runTaskLater(bedWars, () -> {
                        if (frontBlock.getBlock().getType().equals(Material.SLIME_BLOCK))
                            frontBlock.getBlock().setType(Material.AIR);
                        if (backBlock.getBlock().getType().equals(Material.SLIME_BLOCK))
                            backBlock.getBlock().setType(Material.AIR);
                        if (middleBlock.getBlock().getType().equals(Material.SLIME_BLOCK))
                            middleBlock.getBlock().setType(Material.AIR);
                        if (rightBlock.getBlock().getType().equals(Material.SLIME_BLOCK))
                            rightBlock.getBlock().setType(Material.AIR);
                        if (leftBlock.getBlock().getType().equals(Material.SLIME_BLOCK))
                            leftBlock.getBlock().setType(Material.AIR);
                    }, 5 * 20L);
                } else if (stack.equals(ExtrasInventory.SHOP_STRIPPED)) {
                    bedWars.getShopInventory().show(player);
                } else if (stack.equals(ExtrasInventory.BASE_TELEPORT_STRIPPED)) {
                    Team team = Team.getTeam(player);
                    if (team != null) {
                        player.setFallDistance(0);
                        player.teleport(team.getSpawnLocation());
                        player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 10, 1);
                        InventoryUtils.removeItems(player.getInventory(), ExtrasInventory.BASE_TELEPORT_STRIPPED, 1);
                    }
                }
            }

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (event.getClickedBlock().getType() == Material.BED_BLOCK && !player.isSneaking()) {
                    event.setCancelled(true);
                }
            }
        }
    }

}
