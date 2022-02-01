package tk.yjservers.gamemaster.world;

import org.bukkit.Bukkit;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.concurrent.CountDownLatch;

public class deleteWorld {
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
}
