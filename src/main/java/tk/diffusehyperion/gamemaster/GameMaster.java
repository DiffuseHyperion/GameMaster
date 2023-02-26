package tk.diffusehyperion.gamemaster;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import tk.diffusehyperion.gamemaster.Events.FirstPlayerJoinEvent.FirstPlayerJoinEventHandler;

public final class GameMaster extends JavaPlugin implements CommandExecutor {

    public static Plugin plugin;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new FirstPlayerJoinEventHandler(), this);
        Bukkit.getLogger().info("GameMaster successfully enabled!");
    }

    @Override
    public void onLoad() {
        plugin = this;
    }

    @Override
    public void onDisable() {
        getLogger().info("GameMaster successfully disabled!");
    }

}
