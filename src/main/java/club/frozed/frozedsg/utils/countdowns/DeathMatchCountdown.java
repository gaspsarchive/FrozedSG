package club.frozed.frozedsg.utils.countdowns;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.border.BorderManager;
import club.frozed.frozedsg.managers.GameManager;
import club.frozed.frozedsg.managers.PlayerManager;
import club.frozed.frozedsg.managers.WorldsManager;
import club.frozed.frozedsg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;
public class DeathMatchCountdown extends BukkitRunnable
{
    public int seconds;
    private long expire;
    
    public DeathMatchCountdown() {
        this.seconds = GameManager.getInstance().getDeathMatchCountdownValue() * 60;
        final long duration = 1000 * this.seconds;
        this.expire = System.currentTimeMillis() + duration;
        this.runTaskTimer((Plugin)PotSG.getInstance(), 0L, 20L);
    }
    
    public void execute() {
        GameManager.getInstance().spawnDeathMatchArena();
        GameManager.getInstance().setDeathMatchArenaSpawned(true);
        Utils.broadcastMessage(PotSG.getInstance().getConfiguration("messages").getString("deathmatch-started"), true);
        PlayerManager.getInstance().getGamePlayers().forEach(player -> player.teleport(this.getDLocation()));
        BorderManager.getInstance().getBorder().setSize(15);
        if (PotSG.getInstance().getConfiguration("config").getBoolean("DEBUG")) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Deathmatch has been started");
        }
    }
    
    public Location getDLocation() {
        final int x = 0;
        final int z = 0;
        final double y = WorldsManager.getInstance().getGameWorld().getHighestBlockYAt(x, z);
        return new Location(WorldsManager.getInstance().getGameWorld(), (double)x, y + 0.5, (double)z);
    }
    
    public List<Integer> seconds() {
        return Arrays.asList(420, 480, 540, 360, 600, 300, 240, 180, 120, 60, 30, 20, 10, 5, 4, 3, 2, 1);
    }
    
    public void everySeconds() {
        if (this.seconds > 60) {
            Utils.broadcastMessage(PotSG.getInstance().getConfiguration("messages").getString("deathmatch-countdown-minutes").replaceAll("<minutes>", String.valueOf(this.seconds / 60)), true);
        }
        else {
            Utils.broadcastMessage(PotSG.getInstance().getConfiguration("messages").getString("deathmatch-countdown-seconds").replaceAll("<seconds>", String.valueOf(this.seconds)), true);
        }
    }
    
    public Sound playSound() {
        return Sound.NOTE_PLING;
    }
    
    public boolean hasExpired() {
        return System.currentTimeMillis() - this.expire > 1L;
    }
    
    public long getRemaining() {
        return this.expire - System.currentTimeMillis();
    }
    
    public int getSecondsLeft() {
        return (int)this.getRemaining() / 1000;
    }
    
    public String getTimeLeft() {
        return Utils.formatTime(this.getSecondsLeft());
    }
    
    public void cancelCountdown() {
        this.cancel();
        this.expire = 0L;
    }
    
    public void run() {
        --this.seconds;
        if (this.seconds().contains(this.seconds)) {
            this.everySeconds();
        }
        if (this.seconds == 0) {
            this.execute();
            this.cancel();
        }
    }
}
