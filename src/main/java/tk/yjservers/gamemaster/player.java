package tk.yjservers.gamemaster;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Objects;

public class player {

    public void playSoundToAll(Sound sound) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), sound, Float.parseFloat("0.6"), 1);
        }
    }

    public void timer(int duration, BukkitRunnable tasktorun, String name, BarColor colour, BarStyle style, HashMap<String, String> replacewords) {
        BossBar bossbar = Bukkit.createBossBar(name, colour, style, BarFlag.PLAY_BOSS_MUSIC);
        for (Player p : Bukkit.getOnlinePlayers()) {
            bossbar.addPlayer(p);
        }
        double[] timer = {duration};
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                timer[0] = BigDecimal.valueOf(timer[0]).subtract(BigDecimal.valueOf(0.1)).doubleValue();
                if (timer[0] <= 0) {
                    bossbar.removeAll();
                    tasktorun.run();
                    this.cancel();
                }
                bossbar.setProgress(BigDecimal.valueOf(timer[0]).divide(BigDecimal.valueOf(duration), 5, RoundingMode.HALF_EVEN).doubleValue());
                String namereplace = name;
                for (HashMap.Entry<String, String> entry : replacewords.entrySet()) {
                    namereplace = namereplace.replace(entry.getKey(), entry.getValue());
                }
                bossbar.setTitle(namereplace);
            }
        };
        task.runTaskTimer(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin("GameMaster")), 0, 2);
    }
}
