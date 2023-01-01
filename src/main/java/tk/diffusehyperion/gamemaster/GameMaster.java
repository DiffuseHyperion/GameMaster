package tk.diffusehyperion.gamemaster;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class GameMaster extends JavaPlugin implements CommandExecutor {

    public GamePlayer GamePlayer;
    public GameServer GameServer;
    public GameWorld GameWorld;

    public static Plugin plugin;

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("GameMaster successfully enabled!");
    }

    @Override
    public void onLoad() {
        this.GamePlayer = new GamePlayer();
        this.GameServer = new GameServer();
        this.GameWorld = new GameWorld();
        plugin = this;
    }

    @Override
    public void onDisable() {
        getLogger().info("GameMaster successfully disabled!");
    }

}
