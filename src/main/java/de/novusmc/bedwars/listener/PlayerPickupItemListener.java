package de.novusmc.bedwars.listener;

import de.novusmc.bedwars.BedWars;
import de.novusmc.bedwars.game.Team;
import de.novusmc.bedwars.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Paul
 * on 19.01.2019
 *
 * @author pauhull
 */
public class PlayerPickupItemListener implements Listener {

    private BedWars bedWars;

    public PlayerPickupItemListener(BedWars bedWars) {
        this.bedWars = bedWars;
        Bukkit.getPluginManager().registerEvents(this, bedWars);
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = event.getItem().getItemStack();

        if (stack.getType() == Material.STAINED_CLAY) {
            ItemBuilder builder = new ItemBuilder(stack);
            Team team = Team.getTeam(player);

            if (team != null) {
                builder.setDurability(team.getDyeColor().getWoolData());
            }

            builder.setDisplayName("§9Blöcke");
            event.getItem().setItemStack(builder.build());
        }

        if (bedWars.getSpectators().contains(player)) {
            event.setCancelled(true);
        }
    }

}
