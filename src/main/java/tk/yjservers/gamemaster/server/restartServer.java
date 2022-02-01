package tk.yjservers.gamemaster.server;

import org.bukkit.Bukkit;

public class restartServer {
    public void restartForConfig() {
        Bukkit.getLogger().severe("Restarting the server now for edits to take effect. This might take a while!");
        Bukkit.spigot().restart();
    }
}
