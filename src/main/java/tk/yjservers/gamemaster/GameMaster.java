package tk.yjservers.gamemaster;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class GameMaster extends JavaPlugin {

    public player player;
    public server server;
    public world world;

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("GameMaster successfully enabled!");
        player = new player();
        server = new server();
        world = new world();
    }

    @Override
    public void onDisable() {
        getLogger().info("GameMaster successfully disabled!");
    }
}
