package tk.yjservers.gamemaster;

import org.bukkit.plugin.java.JavaPlugin;

public final class GameMaster extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("GameMaster successfully enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("GameMaster successfully disabled!");
    }
}
