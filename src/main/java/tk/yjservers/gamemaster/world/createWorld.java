package tk.yjservers.gamemaster.world;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

public class createWorld {
    public World createWorld(String worldName) {
        WorldCreator worldcreator = new WorldCreator(worldName);
        return Bukkit.createWorld(worldcreator);
    }
}
