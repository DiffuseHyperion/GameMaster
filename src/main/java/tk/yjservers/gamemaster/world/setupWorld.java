package tk.yjservers.gamemaster.world;

import org.bukkit.*;

import java.util.Objects;

public class setupWorld {
    public void setupworld(World world, boolean setupSpawnPlatform, Double borderSize, int warningTime, int warningDist, int spawnRadius) {
        WorldBorder border = world.getWorldBorder();
        border.setCenter(0, 0);
        border.setSize(borderSize);
        for (int i = 255; true; i = i - 1) {
            if (!Objects.equals(world.getBlockAt(0, i, 0).getType(), Material.AIR)) {
                if (setupSpawnPlatform && Objects.equals(world.getBlockAt(0, i, 0).getType(), Material.WATER)) {
                    new fillBlocks().fillBlocks(new Location(world, -1, i, -1), new Location(world, 1, i, 1), Material.DIRT);
                }
                world.setSpawnLocation(0, i + 1, 0);
                break;
            }
        }
        world.setGameRule(GameRule.SPAWN_RADIUS, spawnRadius);
        world.getWorldBorder().setWarningTime(warningTime);
        world.getWorldBorder().setWarningDistance(warningDist);
        world.setPVP(false);
    }
}
