package tk.yjservers.gamemaster;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class GameMaster extends JavaPlugin implements CommandExecutor {

    public player player;
    public server server;
    public world world;

    static Plugin plugin;

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("GameMaster successfully enabled!");
        player = new player();
        server = new server();
        world = new world();
        plugin = this;
    }

    @Override
    public void onDisable() {
        getLogger().info("GameMaster successfully disabled!");
    }

}
