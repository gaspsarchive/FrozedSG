package club.frozed.frozedsg.utils.runnables;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.enums.GameState;
import club.frozed.frozedsg.events.SGGameWinEvent;
import club.frozed.frozedsg.managers.GameManager;
import club.frozed.frozedsg.managers.PlayerManager;
import club.frozed.frozedsg.utils.Utils;
import club.frozed.frozedsg.utils.chat.Color;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameRunnable implements Runnable {
    private int seconds = 0;
    private boolean announced = false;

    public GameRunnable() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(PotSG.getInstance(), this, 20L, 20L);
    }

    public void run() {
        if (GameManager.getInstance().getGameState().equals(GameState.INGAME)) {
            ++this.seconds;
            if (PlayerManager.getInstance().getGamePlayers().size() == 1) {
                if (!this.announced) {
                    Player winer = (Player)PlayerManager.getInstance().getGamePlayers().get(0);
                    this.setAnnounced(true);
                    Bukkit.getServer().getPluginManager().callEvent(new SGGameWinEvent(winer));
                }
            } else if ((PlayerManager.getInstance().getGamePlayers().size() <= 0 || PlayerManager.getInstance().getGamePlayers().isEmpty()) && !this.announced) {
                Bukkit.broadcastMessage(Color.translate("&cSystem has detected the game has no players left, system will automatically restart in 10 seconds..."));
                this.setAnnounced(true);
                (new BukkitRunnable() {
                    public void run() {
                        Bukkit.shutdown();
                    }
                }).runTaskLaterAsynchronously(PotSG.getInstance(), 200L);
            }
        }

    }

    public String getTime() {
        return Utils.formatTime(this.seconds);
    }

    public int getSeconds() {
        return this.seconds;
    }

    public boolean isAnnounced() {
        return this.announced;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public void setAnnounced(boolean announced) {
        this.announced = announced;
    }
}
