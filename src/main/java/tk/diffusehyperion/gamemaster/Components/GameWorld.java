package tk.diffusehyperion.gamemaster.Components;

import org.apache.commons.io.FileUtils;
import org.bukkit.*;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

public class GameWorld {

    private final GameServer GameServer = new GameServer();

    /**
     * Creates a world.
     * <p>
     * The world should not exist, or this method will return that world.
     * <p>
     * Use PlayerJoinEvent to teleport people into the world, using p.teleport(World.getSpawnLocation());
     * @param worldName The name of the created world.
     * @param seed The seed for the world.
     * @return Returns the created world, or if a world already exists with the provided name, the existing world.
     */
    public World createWorld(String worldName, Long seed, World.Environment env, WorldType type) {
        WorldCreator worldcreator = new WorldCreator(worldName);
        worldcreator.seed(seed);
        worldcreator.environment(env);
        worldcreator.type(type);
        return worldcreator.createWorld();
    }

    /**
     * Creates a world. The world will be a normal overworld.
     * <p>
     * The world should not exist, or this method will return that world.
     * <p>
     * Use PlayerJoinEvent to teleport people into the world, using p.teleport(World.getSpawnLocation());
     * @see #createWorld(String, Long, World.Environment, WorldType)
     * @param worldName The name of the created world.
     * @return Returns the created world, or if a world already exists with the provided name, the existing world.
     */
    public World createWorld(String worldName, Long seed) {
        return createWorld(worldName, seed, World.Environment.NORMAL, WorldType.NORMAL);
    }

    /**
     * Creates a world. It will use a random seed.
     * <p>
     * The world should not exist, or this method will return that world.
     * <p>
     * Use PlayerJoinEvent to teleport people into the world, using p.teleport(World.getSpawnLocation());
     * @see #createWorld(String, Long, World.Environment, WorldType)
     * @param worldName The name of the created world.
     * @return Returns the created world, or if a world already exists with the provided name, the existing world.
     */
    public World createWorld(String worldName) {
        return createWorld(worldName, new Random().nextLong());
    }

    /**
     * Creates a world. It's name will be the one specified under `level-name` in server.properties. It will use a random seed.
     * <p>
     * The world should not exist, or this method will return that world.
     * <p>
     * Use PlayerJoinEvent to teleport people into the world, using p.teleport(World.getSpawnLocation());
     * @see #createWorld(String, Long, World.Environment, WorldType)
     * @return Returns the created world, or if a world already exists with the provided name, the existing world.
     */
    public World createWorld() throws IOException {
        return createWorld(GameServer.readServerProperties("level-name"), new Random().nextLong());
    }

    /**
     * Deletes a world. This should be in onLoad().
     * @param worldName The name of the deleted world.
     */
    public void deleteWorld(String worldName) {
        assert worldName != null;
        File oldworld = new File(Bukkit.getWorldContainer().getAbsolutePath() + "/" + worldName);
        do {
            Bukkit.unloadWorld(worldName, false);
            FileUtils.deleteQuietly(oldworld);
        } while (oldworld.exists());
    }

    /**
     * Deletes a world. It's name will be the one specified under `level-name` in server.properties. This should be in onLoad().
     */
    public void deleteWorld() throws IOException {
        deleteWorld(GameServer.readServerProperties("level-name"));
    }

    /**
     * Sets an area with blocks.
     * <p>
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
     * <p>
     * The world spawn will be set at X: 0 Z: 0, and its Y will be at the highest non-air block.
     * <p>
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
                    fillBlocks(new Location(world, -2, i, -2), new Location(world, 2, i, 2), Material.DIRT);
                }
                world.setSpawnLocation(new Location(world, 0.5, i + 1, 0.5));
                break;
            }
        }
        world.setGameRule(GameRule.SPAWN_RADIUS, spawnRadius);
        world.getWorldBorder().setWarningTime(warningTime);
        world.getWorldBorder().setWarningDistance(warningDist);
        world.setPVP(false);
    }

    /**
     * Deletes and resets a world.
     * <p>
     * <a href="https://github.com/Duckulus/Bingo/blob/master/src/main/java/de/amin/bingo/BingoPlugin.java#L94">Code seen here!</a>
     * @apiNote This should be done in onLoad()! The plugin does not need to be started at STARTUP.
     * @param name Name of the world being reset.
     */
    public void resetWorld(String name) throws IOException {
        // Getting and deleting the main world
        File world = new File(Bukkit.getWorldContainer(), name);
        FileUtils.deleteDirectory(world);

        // Creating needed directories
        world.mkdirs();
        new File(world, "data").mkdirs();
        new File(world, "datapacks").mkdirs();
        new File(world, "entities").mkdirs();
        new File(world, "playerdata").mkdirs();
        new File(world, "poi").mkdirs();
        new File(world, "region").mkdirs();
    }

    /**
     * Deletes and resets a world.
     * <p>
     * The name of the world being reset will be the one specified under `level-name` in server.properties.
     * <p>
     * <a href="https://github.com/Duckulus/Bingo/blob/master/src/main/java/de/amin/bingo/BingoPlugin.java#L94">Code seen here!</a>
     * @apiNote This should be done in onLoad()! The plugin does not need to be started at STARTUP.
     */
    public void resetWorld() throws IOException {
        resetWorld(GameServer.readServerProperties("level-name"));
    }
}
