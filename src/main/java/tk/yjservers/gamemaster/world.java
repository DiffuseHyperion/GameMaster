package tk.yjservers.gamemaster;

import org.apache.commons.io.FileUtils;
import org.bukkit.*;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class world {

    /**
     * Creates a world.
     * @param worldName The name of the created world.
     * @return Returns the created world, or if a world already exists with the provided name, the existing world.
     */
    public World createWorld(String worldName) {
        WorldCreator worldcreator = new WorldCreator(worldName);
        return Bukkit.createWorld(worldcreator);
    }

    /**
     * Deletes a world.
     * @param worldName The name of the deleted world.
     * @param latch A latch for the method to countdown when the world is deleted.
     */
    public void deleteWorld(String worldName, CountDownLatch latch) {
        assert worldName != null;
        for (; true; ) {
            Bukkit.unloadWorld(worldName, false);
            File oldworld = new File(Bukkit.getWorldContainer().getAbsolutePath() + "/" + worldName);
            FileUtils.deleteQuietly(oldworld);
            if (!oldworld.exists()) {
                latch.countDown();
                break;
            }
        }
    }

    /**
     * Sets an area with blocks.
     * loc1 and loc2 must have the same world.
     * @param loc1 A corner of the area being filled.
     * @param loc2 Another corner of the area being filled.
     * @param blocktype The type of block to fill the area with.
     */
    public void fillBlocks(Location loc1, Location loc2, Material blocktype) {
        if (loc1.getWorld() != loc2.getWorld()) {
            throw new IllegalArgumentException("Location 1 and location 2's worlds must be the same!");
        }
        World world = loc1.getWorld();
        int X1 = loc1.getBlockX();
        int Y1 = loc1.getBlockY();
        int Z1 = loc1.getBlockZ();
        int X2 = loc2.getBlockX();
        int Y2 = loc2.getBlockY();
        int Z2 = loc2.getBlockZ();
        int startX;
        int endX;
        int startY;
        int endY;
        int startZ;
        int endZ;
        if (X1 > X2) {
            startX = X2;
            endX = X1;
        } else {
            startX = X1;
            endX = X2;
        }
        if (Y1 > Y2) {
            startY = Y2;
            endY = Y1;
        } else {
            startY = Y1;
            endY = Y2;
        }
        if (Z1 > Z2) {
            startZ = Z2;
            endZ = Z1;
        } else {
            startZ = Z1;
            endZ = Z2;
        }
        for (int y = startY; y <= endY; y++) {
            for (int z = startZ; z <= endZ; z++) {
                for (int x = startX; x <= endX; x++) {
                    assert world != null;
                    world.getBlockAt(x, y, z).setType(blocktype);
                }
            }
        }
    }

    /**
     * Prepares a world for a pregame scenario.
     * The world spawn will be set at X: 0 Z: 0, and its Y will be at the highest non-air block.
     * PVP will be off.
     * @param world The affected world.
     * @param setupSpawnPlatform Whether to create a 3x3 dirt platform at 0, 0.
     * @param borderSize The diameter of the border.
     * @param warningTime Warning time for the border.
     * @param warningDist Warning distance for the border.
     * @param spawnRadius How much blocks you can spawn away from 0, 0.
     */
    public void setupWorld(World world, boolean setupSpawnPlatform, Double borderSize, int warningTime, int warningDist, int spawnRadius) {
        WorldBorder border = world.getWorldBorder();
        border.setCenter(0, 0);
        border.setSize(borderSize);
        for (int i = 255; true; i = i - 1) {
            if (!Objects.equals(world.getBlockAt(0, i, 0).getType(), Material.AIR)) {
                if (setupSpawnPlatform && Objects.equals(world.getBlockAt(0, i, 0).getType(), Material.WATER)) {
                    fillBlocks(new Location(world, -1, i, -1), new Location(world, 1, i, 1), Material.DIRT);
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
