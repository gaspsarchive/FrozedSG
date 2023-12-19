package club.frozed.frozedsg.utils.countdowns;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.border.BorderManager;
import club.frozed.frozedsg.enums.GameState;
import club.frozed.frozedsg.enums.PlayerState;
import club.frozed.frozedsg.managers.GameManager;
import club.frozed.frozedsg.managers.PlayerDataManager;
import club.frozed.frozedsg.managers.PlayerManager;
import club.frozed.frozedsg.managers.WorldsManager;
import club.frozed.frozedsg.player.PlayerData;
import club.frozed.frozedsg.utils.ItemBuilder;
import club.frozed.frozedsg.utils.Utils;
import club.frozed.frozedsg.utils.runnables.GameRunnable;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

public class PrematchCountdown extends BukkitRunnable {
    public int seconds = GameManager.getInstance().getPrematchCountdownValue();
    private long expire;

    public PrematchCountdown() {
        long duration = (long)(1000 * this.seconds);
        this.expire = System.currentTimeMillis() + duration;
        this.runTaskTimer(PotSG.getInstance(), 0L, 20L);
    }

    public void execute() {
        Utils.broadcastMessage(PotSG.getInstance().getConfiguration("messages").getString("game-started"), true);
        PlayerManager.getInstance().getPrematchPlayers().forEach((player) -> {
            PlayerData data = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());
            Utils.clearPlayer(player);
            player.teleport(GameManager.getInstance().getGameWorldCenterLocation());
            data.setState(PlayerState.INGAME);
            data.getGamesPlayed().increaseAmount(1);
            player.setAllowFlight(false);
            player.setFlying(false);
            player.setGameMode(GameMode.SURVIVAL);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 980, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 300, 14));
            player.getInventory().addItem(new ItemStack[]{(new ItemBuilder(Material.COMPASS)).setNameWithArrows(PotSG.getInstance().getConfiguration("items").getString("player-tracker.name")).toItemStack()});
        });
        WorldsManager.getInstance().getGameWorld().setPVP(false);
        GameManager.getInstance().setGameState(GameState.INGAME);
        BorderManager.getInstance().startBorderShrink();
        GameManager.getInstance().setGameRunnable(new GameRunnable());
        Bukkit.getScheduler().scheduleSyncDelayedTask(PotSG.getInstance(), () -> {
            GameManager.getInstance().setPvpCountdown(new PvPCountdown());
            GameManager.getInstance().setFeastCountdown(new FeastCountdown());
            GameManager.getInstance().setDeathMatchCountdown(new DeathMatchCountdown());
        }, 20L);
    }

    public List<Integer> seconds() {
        return Arrays.asList(300, 240, 180, 120, 60, 30, 20, 10, 5, 4, 3, 2, 1);
    }

    public void everySeconds() {
        if (this.seconds > 60) {
            Utils.broadcastMessage(PotSG.getInstance().getConfiguration("messages").getString("game-begin-countdown-minutes").replaceAll("<minutes>", String.valueOf(this.seconds / 60)), true);
        } else {
            Utils.broadcastMessage(PotSG.getInstance().getConfiguration("messages").getString("game-begin-countdown-seconds").replaceAll("<seconds>", String.valueOf(this.seconds)), true);
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
