package net.pl3x.bukkit.lobby.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class BukkitListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        event.setJoinMessage(null);

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.GOLD + "+ " + ChatColor.RESET + player.getDisplayName() + " joined the game!"));
    }
}
