package club.frozed.frozedsg.utils.countdowns;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.managers.GameManager;
import club.frozed.frozedsg.managers.PlayerDataManager;
import club.frozed.frozedsg.player.PlayerData;
import club.frozed.frozedsg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

public class RebootCountdown extends BukkitRunnable {
    public int seconds = GameManager.getInstance().getRebootCountdownValue();
    private long expire;

    public RebootCountdown() {
        long duration = (long)(1000 * this.seconds);
        this.expire = System.currentTimeMillis() + duration;
        this.runTaskTimer(PotSG.getInstance(), 0L, 20L);
    }

    public void execute() {
        PlayerDataManager.getInstance().getPlayerDatas().values().forEach(PlayerData::save);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), GameManager.getInstance().getRebootCommand());
    }

    public void everySecond() {
    }

    public List<Integer> seconds() {
        return Arrays.asList(300, 240, 180, 120, 60, 30, 20, 10, 5, 4, 3, 2, 1);
    }

    public void everySeconds() {
        if (this.seconds > 60) {
            Utils.broadcastMessage(PotSG.getInstance().getConfiguration("messages").getString("reboot-countdown-minutes").replaceAll("<minutes>", String.valueOf(this.seconds / 60)), true);
        } else {
            Utils.broadcastMessage(PotSG.getInstance().getConfiguration("messages").getString("reboot-countdown-seconds").replaceAll("<seconds>", String.valueOf(this.seconds)), true);
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
