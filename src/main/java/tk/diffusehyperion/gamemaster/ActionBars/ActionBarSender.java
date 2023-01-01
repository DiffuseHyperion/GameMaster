package tk.diffusehyperion.gamemaster.ActionBars;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import tk.diffusehyperion.gamemaster.Util.CompletableStringBuffer;

import static tk.diffusehyperion.gamemaster.GameMaster.plugin;

public class ActionBarSender {

    private static final int refreshTime = 40;

    /**
     * Send a static action bar to a player for a set duration.
     * @param p Player to send the action bar to.
     * @param text Text to be sent. Chat formatting is accepted.
     * @param duration The total duration to send the action bar for. The action bar will fade out completely at the end of this duration.
     */
    public static void sendActionBar(Player p, String text, int duration) {
        PacketPlayOutChat packet = getPacket(text);
        PlayerConnection conn = ((CraftPlayer) p).getHandle().playerConnection;
        sendPacket(conn, packet);

        //               total duration, update countdown
        int[] timerTicks = {duration * 20, refreshTime};
        new BukkitRunnable(){
            @Override
            public void run() {
                if (timerTicks[0] <= 60) {
                    sendPacket(conn, packet);
                    // send packet one final time, will fade out right around when duration ends
                    this.cancel();
                }
                if (timerTicks[1] <= 0) {
                    sendPacket(conn, packet);
                    timerTicks[1] = refreshTime;
                }

                timerTicks[0] -= 20;
                timerTicks[1] -= 20;
            }
        }.runTaskTimer(plugin, 0L, 20);
    }

    /**
     * Send an updating action bar to a player, until the CompletableStringBuffer is marked as complete.
     * @param p Player to send the action bar to.
     * @param buffer Text to be sent.
     * @param refreshTimeTicks The frequency to refresh the action bar.
     */
    public static void sendUpdatingActionBar(Player p, CompletableStringBuffer buffer, int refreshTimeTicks) {
        PlayerConnection conn = ((CraftPlayer) p).getHandle().playerConnection;
        sendPacket(conn, getPacket(buffer.toString()));

        //               update countdown
        int[] timerTicks = {refreshTimeTicks};
        new BukkitRunnable(){
            @Override
            public void run() {
                if (buffer.completed) {
                    this.cancel();
                }
                if (timerTicks[0] <= 0) {
                    if (p.isOnline()) {
                        sendPacket(conn, getPacket(buffer.toString()));
                    }
                    timerTicks[0] = refreshTimeTicks;
                }

                timerTicks[0] -= 2;
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    private static PacketPlayOutChat getPacket(String text) {
        return new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + text + "\"}"), (byte) 2);
    }

    // safer way to send packets
    private static void sendPacket(PlayerConnection conn, PacketPlayOutChat packet) {
        if (conn.getPlayer().isOnline()) {
            conn.sendPacket(packet);
        }
    }
}
