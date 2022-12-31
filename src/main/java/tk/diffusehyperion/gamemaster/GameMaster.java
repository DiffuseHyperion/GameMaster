package tk.diffusehyperion.gamemaster;

import me.tigerhix.lib.bossbar.BossbarLib;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class GameMaster extends JavaPlugin implements CommandExecutor {

    public GamePlayer GamePlayer;
    public GameServer GameServer;
    public GameWorld GameWorld;

    static Plugin plugin;

    static BossbarLib barLib;

    @Override
    public void onEnable() {
        barLib = BossbarLib.createFor(this, 2);
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
