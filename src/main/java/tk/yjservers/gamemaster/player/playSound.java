package tk.yjservers.gamemaster.player;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class playSound {
    public void playSound(Sound sound) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), sound, Float.parseFloat("0.6"), 1);
        }
    }
}
