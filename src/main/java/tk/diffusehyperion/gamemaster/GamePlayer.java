package tk.diffusehyperion.gamemaster;

import me.tigerhix.lib.bossbar.Bossbar;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.javatuples.Pair;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

import static tk.diffusehyperion.gamemaster.GameMaster.barLib;
import static tk.diffusehyperion.gamemaster.GameMaster.plugin;

public class GamePlayer {

    public enum timerReplacement {
        /**
         * Add the timer's time left.
         */
        TIME_LEFT("%time_left%"),
        /**
         * Add the timer's elapsed time.
         */
        TIME_ELAPSED("%time_elapsed%");


        private final String string;
        timerReplacement(String str) {string = str;}
        public String toString() {return string;}
    }

    /**
     * Play a sound to all players.
     * <p>
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
     * Creates a timer using a bossbar for a player. It will run a BukkitRunnable when completed.
     * @param p Player to show the bossbar to.
     * @param duration The duration of the timer.
     * @param title The title of the timer. See {@link timerReplacement} if you want to add variables from the timer to the title.
     * @param tasktorun A BukkitRunnable to run when the timer expires.
     */
    public Pair<Bossbar, BukkitRunnable> timer(Player p, int duration, String title, @Nullable BukkitRunnable tasktorun) {
        BigDecimal[] timer = {BigDecimal.valueOf(duration)};
        Bossbar bossbar = barLib.getBossbar(p);
        bossbar.setPercentage(1f);
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                bossbar.setPercentage(timer[0].divide(BigDecimal.valueOf(duration), 5, RoundingMode.HALF_EVEN).floatValue());

                bossbar.setMessage(bossbarReplaceTitle(title, timer[0].doubleValue(), duration - timer[0].doubleValue()));

                timer[0] = timer[0].subtract(BigDecimal.valueOf(0.1));
                if (timer[0].doubleValue() <= 0) {
                    barLib.clearBossbar(p);
                    if (tasktorun != null) {
                        tasktorun.run();
                    }
                    this.cancel();
                }
            }
        };
        task.runTaskTimer(plugin, 0, 2);

        return new Pair<>(bossbar, task);
    }

    /**
     * Creates a timer using a bossbar.
     * @param p Player to show the bossbar to.
     * @param duration The duration of the timer.
     * @param title The title of the timer. See {@link timerReplacement} if you want to add variables from the timer to the title.
     */
    public Pair<Bossbar, BukkitRunnable> timer(Player p, int duration, String title) {
        return timer(p, duration, title, null);
    }

    /**
     * Creates a timer using a bossbar. This timer allows for custom variables. It will run a BukkitRunnable when completed.
     * @param p Player to show the bossbar to.
     * @param duration The duration of the timer.
     * @param title The title of the timer. See {@link timerReplacement} if you want to add variables from the timer to the title.
     * @param replaceList Hashmap of placeholders to their value.
     * @see #timer(Player, int, String, BukkitRunnable)
     */
    public Pair<Bossbar, BukkitRunnable> customTimer(Player p, int duration, String title, HashMap<String, String> replaceList, BukkitRunnable tasktorun) {
        BigDecimal[] timer = {BigDecimal.valueOf(duration)};
        Bossbar bossbar = barLib.getBossbar(p);
        bossbar.setPercentage(1);
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                bossbar.setPercentage(timer[0].divide(BigDecimal.valueOf(duration), 5, RoundingMode.HALF_EVEN).floatValue());

                String tempTitle;
                tempTitle = bossbarReplaceTitle(title, timer[0].doubleValue(), duration - timer[0].doubleValue());
                bossbar.setMessage(customBossbarReplaceTitle(tempTitle, replaceList));

                timer[0] = timer[0].subtract(BigDecimal.valueOf(0.1));
                if (timer[0].doubleValue() <= 0) {
                    barLib.clearBossbar(p);
                    if (tasktorun != null) {
                        tasktorun.run();
                    }
                    this.cancel();
                }
            }
        };
        task.runTaskTimer(plugin, 0, 2);
        return new Pair<>(bossbar, task);
    }

    /**
     * Creates a timer using a bossbar. This timer allows for custom variables.
     * @param p Player to show the bossbar to.
     * @param duration The duration of the timer.
     * @param title The title of the timer. See {@link timerReplacement} if you want to add variables from the timer to the title.
     * @param replaceList Hashmap of placeholders to their value.
     * @see #customTimer(Player, int, String, HashMap, BukkitRunnable) 
     */
    public Pair<Bossbar, BukkitRunnable> customTimer(Player p, int duration, String title, HashMap<String, String> replaceList) {
        return customTimer(p, duration, title, replaceList, null);
    }

    private String bossbarReplaceTitle(String title, Double timeLeft, Double timeElapsed) {
        String replacementTitle = title;
        replacementTitle = replacementTitle.replace(timerReplacement.TIME_LEFT.toString(), String.valueOf(timeLeft));
        replacementTitle = replacementTitle.replace(timerReplacement.TIME_ELAPSED.toString(), String.valueOf(timeElapsed));
        return replacementTitle;
    }

    private String customBossbarReplaceTitle(String title, HashMap<String, String> replaceList) {
        String replacementTitle = title;
        for (String s : replaceList.keySet()) {
            replacementTitle = replacementTitle.replace(s, replaceList.get(s));
        }
        return replacementTitle;
    }
}
