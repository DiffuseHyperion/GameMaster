package tk.diffusehyperion.gamemaster;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;
import tk.diffusehyperion.gamemaster.Util.CompletableStringBuffer;
import tk.diffusehyperion.gamemaster.Util.Pair;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Objects;

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
    public static void playSoundToAll(Sound sound) {
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
    public static void playSoundToAll(Sound sound, Float volume, Float pitch) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), sound, volume, pitch);
        }
    }


    /**
     * Creates a CompletableStringBuffer that is continuously updated.
     * @param duration The duration of the timer.
     * @param title The title of the timer. See {@link timerReplacement} if you want to add variables from the timer to the title.
     * @param tasktorun A BukkitRunnable to run when the timer expires.
     * @param replaceList A Hashmap of any custom placeholders to their string. Null this if there is none.
     * @param timerNotches How many notches will the timer have. Null this to set it to 10.
     * @param colours The colour palette of the timer. Null this for the default colour palette.
     * @see TimerColours
     * @see tk.diffusehyperion.gamemaster.ActionBars.ActionBarSender#sendUpdatingActionBar(Player, CompletableStringBuffer, int)
     */
    public static Pair<CompletableStringBuffer, BukkitRunnable> timer(float duration, String title, @Nullable BukkitRunnable tasktorun, @Nullable HashMap<String, String> replaceList, @Nullable Integer timerNotches, @Nullable TimerColours colours) {
        BigDecimal[] timer = {BigDecimal.valueOf(duration).setScale(1, RoundingMode.HALF_UP)};
        CompletableStringBuffer buffer = new CompletableStringBuffer();
        StringBuffer stringBuffer = buffer.stringBuffer;

        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                timer[0] = timer[0].subtract(BigDecimal.valueOf(0.1));
                if (timer[0].floatValue() <= 0) {
                    if (tasktorun != null) {
                        tasktorun.run();
                    }
                    buffer.complete();
                    this.cancel();
                }

                stringBuffer.delete(0, stringBuffer.length());

                stringBuffer.append(getTimerStringWLogic(timer[0], duration, timerNotches, colours));
                stringBuffer.append(ChatColor.RESET);
                stringBuffer.append(" / ");
                if (Objects.isNull(colours)) {
                    TimerColours tc = new TimerColours(null, null, null, null);
                    for (ChatColor color : tc.delimeterColour) {
                        stringBuffer.append(color);
                    }
                } else {
                    for (ChatColor color : colours.delimeterColour) {
                        stringBuffer.append(color);
                    }
                }
                stringBuffer.append(ChatColor.RESET);
                if (Objects.isNull(replaceList)) {
                    stringBuffer.append(replaceTitle(title, timer[0], BigDecimal.valueOf(duration).subtract(timer[0])));
                } else {
                    stringBuffer.append(
                            customBossbarReplaceTitle(
                                    replaceTitle(title, timer[0], BigDecimal.valueOf(duration).subtract(timer[0])), replaceList));
                }
            }
        };
        task.runTaskTimer(plugin, 0, 2);
        return new Pair<>(buffer, task);
    }

    public static String getTimerStringWLogic(BigDecimal timer, float duration, @javax.annotation.Nullable Integer timerNotches, @javax.annotation.Nullable TimerColours colours) {
        if (Objects.isNull(colours)) {
            // no colour
            if (Objects.isNull(timerNotches)) {
                // no colour nor notches
                return getTimerString(timer.floatValue(), duration);
            } else {
                // no colour got notches
                return getTimerString(timer.floatValue(), duration, timerNotches);
            }
        } else {
            // got colour
            if (Objects.isNull(timerNotches)) {
                // got colour not notches
                return getTimerString(timer.floatValue(), duration, colours);
            } else {
                // got colour and notches
                return getTimerString(timer.floatValue(), duration, timerNotches, colours);
            }
        }
    }
    public static String getTimerString(float elapsed, float max, TimerColours colours) {
        return getTimerString(elapsed, max, 10, colours);
    }
    public static String getTimerString(float elapsed, float max) {
        return getTimerString(elapsed, max, 10, new TimerColours(null, null, null, null));
    }

    public static String getTimerString(float elapsed, float max, int notches) {
        return getTimerString(elapsed, max, notches, new TimerColours(null, null, null, null));
    }

    /**
     * Convenience method of getting a colour array.
     * @param chatColours Chat colours to convert into an array.
     * @return Array of inputted chat colours.
     */
    public static ChatColor[] asColourArray(ChatColor... chatColours) {
        return chatColours;
    }

    /**
     * A class defining the colour palettes of a timer string.
     * Defaults: 
     * Border => DARK_RED + BOLD
     * Empty Notches => GRAY
     * Filled Notches => RED
     * @see #asColourArray(ChatColor...) 
     */
    public static class TimerColours {
        public ChatColor[] borderColour;
        public ChatColor[] emptyColour;
        public ChatColor[] filledColour;

        public ChatColor[] delimeterColour;

        public TimerColours(@Nullable ChatColor[] borderColour, @Nullable ChatColor[] delimeterColour, @Nullable ChatColor[] emptyColour, @Nullable ChatColor[] filledColour) {
            if (Objects.nonNull(borderColour)) {
                this.borderColour = borderColour;
            } else {
                this.borderColour = asColourArray(ChatColor.DARK_RED, ChatColor.BOLD);
            }
            if (Objects.nonNull(delimeterColour)) {
                this.delimeterColour = delimeterColour;
            } else {
                this.delimeterColour = asColourArray(ChatColor.GRAY);
            }
            if (Objects.nonNull(emptyColour)) {
                this.emptyColour = emptyColour;
            } else {
                this.emptyColour = asColourArray(ChatColor.GRAY);
            }
            if (Objects.nonNull(filledColour)) {
                this.filledColour = filledColour;
            } else {
                this.filledColour = asColourArray(ChatColor.RED);
            }

        }
    }
    public static String getTimerString(float elapsed, float max, int notches, TimerColours colours) {
        float percentage = elapsed / max;
        int filledNotches = Math.round(percentage * notches);
        StringBuilder builder = new StringBuilder();

        for (ChatColor bc : colours.borderColour) {
            builder.append(bc.toString());
        }
        builder.append("[");
        builder.append(ChatColor.RESET);

        for (ChatColor fc : colours.filledColour) {
            builder.append(fc.toString());
        }
        for (int i = 0; i < filledNotches; i++) {
            builder.append("|");
        }
        builder.append(ChatColor.RESET);

        for (ChatColor ec : colours.emptyColour) {
            builder.append(ec.toString());
        }
        for (int i = 0; i < max - filledNotches; i++) {
            builder.append("|");
        }
        builder.append(ChatColor.RESET);

        for (ChatColor bc : colours.borderColour) {
            builder.append(bc.toString());
        }
        builder.append("]");

        return builder.toString();
    }

    public static String replaceTitle(String title, BigDecimal timeLeft, BigDecimal timeElapsed) {
        String replacementTitle = title;
        replacementTitle = replacementTitle.replace(timerReplacement.TIME_LEFT.toString(), timeLeft.toString());
        replacementTitle = replacementTitle.replace(timerReplacement.TIME_ELAPSED.toString(), timeElapsed.toString());
        return replacementTitle;
    }

    public static String customBossbarReplaceTitle(String title, HashMap<String, String> replaceList) {
        String replacementTitle = title;
        for (String s : replaceList.keySet()) {
            replacementTitle = replacementTitle.replace(s, replaceList.get(s));
        }
        return replacementTitle;
    }
}
