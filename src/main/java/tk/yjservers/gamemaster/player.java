package tk.yjservers.gamemaster;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static tk.yjservers.gamemaster.GameMaster.plugin;

public class player {

    /**
     * Play a sound to all players.
     * This will play the sound at max volume, and at 1x speed.
     * @param sound Sound to be played.
     */
    public void playSoundToAll(Sound sound) {
        playSoundToAll(sound, 1F, 1F);
    }

    /**
     * Play a sound to all players.
     * <a href="https://bukkit.org/threads/playsound-parameters-volume-and-pitch.151517/">You should look at this to figure out what volume and pitch you need!</a>
     * <p>
     * TLDR:
     * <p>
     * For volume, 1 = 100% sound. Setting it beyond 1 will increase how far away you can hear it. At 1, you can barely hear it at 15 blocks. At 10, you can hear it at 150 blocks.
     * <p>
     * For pitch, 1 = 1x speed, 0.5 = 0.5x speed, 2 = 2x speed.
     * @param sound Sound to be played.
     * @param volume Volume for the sound to be played at. Default is 1.
     * @param pitch Pitch/Speed for the sound to be played at. Default is 1.
     *
     */
    public void playSoundToAll(Sound sound, Float volume, Float pitch) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), sound, volume, pitch);
        }
    }

    /**
     * Enums for replacements in {@link #timer(Player, int, String, BarColor, BarStyle, BukkitRunnable)}.
     */
    public enum timerReplacement {
        /**
         * Add the timer's time left.
         */
        TIME_LEFT("%time_left%"),
        /**
         * Add the timer's elapsed time.
         */
        TIME_ELAPSED("%time_elapsed%"),
        /**
         * Add the name's of every Player to which the timer is visible.
         */
        PLAYERS_SHOWN("%players_shown");


        private final String string;
        timerReplacement(String str) {string = str;}
        public String getString() {return string;}
    }

    /**
     * Creates a timer using a bossbar. It will run a BukkitRunnable when completed.
     * @param p Player to give the timer to.
     * @param duration The duration of the timer.
     * @param title The title of the timer. See {@link timerReplacement} if you want to add variables from the timer to the title.
     * @param colour The colour of the bossbar.
     * @param style The style of the bossbar.
     * @param tasktorun A BukkitRunnable to run when the timer expires. This can be null.
     */
    public BossBar timer(Player p, int duration, String title, BarColor colour, BarStyle style, @Nullable BukkitRunnable tasktorun) {
        BossBar bossbar = Bukkit.createBossBar(title, colour, style, BarFlag.PLAY_BOSS_MUSIC);
        bossbar.addPlayer(p);
        double[] timer = {duration};
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                bossbar.setProgress(BigDecimal.valueOf(timer[0]).divide(BigDecimal.valueOf(duration), 5, RoundingMode.HALF_EVEN).doubleValue());

                title.replace(timerReplacement.TIME_LEFT.getString(), String.valueOf(timer[0]));
                title.replace(timerReplacement.TIME_ELAPSED.getString(), String.valueOf(duration - timer[0]));
                List<String> list = new ArrayList<>();
                for (Player pl : bossbar.getPlayers()) {
                    list.add(pl.getDisplayName());
                }
                title.replace(timerReplacement.PLAYERS_SHOWN.getString(), StringUtils.join(list, ", "));
                bossbar.setTitle(title);

                timer[0] = BigDecimal.valueOf(timer[0]).subtract(BigDecimal.valueOf(0.1)).doubleValue();
                if (timer[0] <= 0) {
                    bossbar.removeAll();
                    if (!Objects.isNull(tasktorun)) {tasktorun.run();}
                    this.cancel();
                }
            }
        };
        task.runTaskTimer(plugin, 0, 2);
        return bossbar;
    }
}
