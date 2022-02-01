package tk.yjservers.gamemaster.world;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class fillBlocks {
    public void fillBlocks(Location loc1, Location loc2, Material blocktype) {
        if (loc1.getWorld() != loc2.getWorld()) {
            Bukkit.getLogger().severe("bruh loc1 and loc2 worlds not the same");
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
}
