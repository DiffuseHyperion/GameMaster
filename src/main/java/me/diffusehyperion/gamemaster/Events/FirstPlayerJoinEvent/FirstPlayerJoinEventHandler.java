package me.diffusehyperion.gamemaster.Events.FirstPlayerJoinEvent;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import me.diffusehyperion.gamemaster.Components.GameServer;

public class FirstPlayerJoinEventHandler implements Listener {
    public static boolean playerJoined = false;

    @EventHandler
    public void playerJoined(PlayerJoinEvent e) {
        if (!playerJoined) {
            playerJoined = true;
            GameServer.playersJoinedBefore = true;
            FirstPlayerJoinEvent event = new FirstPlayerJoinEvent(e.getPlayer(), e.getJoinMessage());
            Bukkit.getPluginManager().callEvent(event);
        }
    }
}
