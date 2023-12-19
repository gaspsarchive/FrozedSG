package club.frozed.frozedsg.border;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.managers.GameManager;
import club.frozed.frozedsg.utils.Utils;
import club.frozed.frozedsg.utils.chat.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class BorderManager
{
    public static BorderManager instance;
    private Border border;
    private BorderTask borderTask;
    private int startBorder;
    private int shrinkUntil;
    private int shrinkEvery;
    
    public BorderManager() {
        this.startBorder = PotSG.getInstance().getConfiguration("config").getInt("BORDER.START");
        this.shrinkUntil = PotSG.getInstance().getConfiguration("config").getInt("BORDER.SHRINK-UNTIL");
        this.shrinkEvery = PotSG.getInstance().getConfiguration("config").getInt("BORDER.SHRINK-EVERY-SECONDS");
        BorderManager.instance = this;
    }
    
    public void startBorderShrink() {
        this.border = new Border();
        (this.borderTask = new BorderTask()).runTaskTimer((Plugin)PotSG.getInstance(), 20L, 20L);
        Utils.broadcastMessage(GameManager.getInstance().getBorderPrefix() + PotSG.getInstance().getConfiguration("messages").getString("start-border-strink").replaceAll("<border_minutes>", String.valueOf(this.border.getSeconds() / 60)).replaceAll("<border_seconds>", String.valueOf(this.border.getSeconds())).replaceAll("<last_border>", String.valueOf(this.border.getLastBorder())));
    }
    
    public String getBorderInfo() {
        if (this.border == null) {
            return "";
        }
        if (this.border.getSize() == this.border.getLastBorder()) {
            return "";
        }
        if (this.border.getSeconds() <= 60) {
            return Color.translate(PotSG.getInstance().getConfiguration("scoreboard").getString("border-info.seconds").replaceAll("<seconds>", String.valueOf(this.border.getSeconds())));
        }
        return Color.translate(PotSG.getInstance().getConfiguration("scoreboard").getString("border-info.minutes").replaceAll("<minutes>", String.valueOf(this.border.getSeconds() / 60 + 1)));
    }
    
    public void checkBorder(final Player player) {
        final Border border = this.getBorder();
        if (border == null) {
            return;
        }
        final int size = border.getSize();
        final World w = player.getWorld();
        if (w.getName().equals("world")) {
            if (w.getEnvironment().equals((Object) Environment.NETHER)) {
                return;
            }
            if (player.getLocation().getBlockX() > size) {
                player.teleport(new Location(w, (double)(size - 2), (double)player.getLocation().getBlockY(), (double)player.getLocation().getBlockZ()));
                player.setVelocity(player.getLocation().getDirection().multiply(1.0));
                if (player.getLocation().getBlockY() < w.getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ())) {
                    player.teleport(new Location(w, (double)player.getLocation().getBlockX(), (double)(w.getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ()) + 2), (double)player.getLocation().getBlockZ()));
                }
                player.sendMessage(GameManager.getInstance().getBorderPrefix() + Color.translate(PotSG.getInstance().getConfiguration("messages").getString("stay-in-border")));
                player.playSound(player.getLocation(), Sound.EXPLODE, 1.0f, 1.0f);
                player.spigot().setCollidesWithEntities(true);
            }
            if (player.getLocation().getBlockZ() > size) {
                player.teleport(new Location(w, (double)player.getLocation().getBlockX(), (double)player.getLocation().getBlockY(), (double)(size - 2)));
                if (player.getLocation().getBlockY() < w.getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ())) {
                    player.teleport(new Location(w, (double)player.getLocation().getBlockX(), (double)(w.getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ()) + 2), (double)player.getLocation().getBlockZ()));
                }
                player.sendMessage(GameManager.getInstance().getBorderPrefix() + Color.translate(PotSG.getInstance().getConfiguration("messages").getString("stay-in-border")));
                player.playSound(player.getLocation(), Sound.EXPLODE, 1.0f, 1.0f);
                player.spigot().setCollidesWithEntities(true);
            }
            if (player.getLocation().getBlockX() < -size) {
                player.teleport(new Location(w, (double)(-size + 2), (double)player.getLocation().getBlockY(), (double)player.getLocation().getBlockZ()));
                if (player.getLocation().getBlockY() < w.getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ())) {
                    player.teleport(new Location(w, (double)player.getLocation().getBlockX(), (double)(w.getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ()) + 2), (double)player.getLocation().getBlockZ()));
                }
                player.sendMessage(GameManager.getInstance().getBorderPrefix() + Color.translate(PotSG.getInstance().getConfiguration("messages").getString("stay-in-border")));
                player.playSound(player.getLocation(), Sound.EXPLODE, 1.0f, 1.0f);
                player.spigot().setCollidesWithEntities(true);
            }
            if (player.getLocation().getBlockZ() < -size) {
                player.teleport(new Location(w, (double)player.getLocation().getBlockX(), (double)player.getLocation().getBlockY(), (double)(-size + 2)));
                if (player.getLocation().getBlockY() < w.getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ())) {
                    player.teleport(new Location(w, (double)player.getLocation().getBlockX(), (double)(w.getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ()) + 2), (double)player.getLocation().getBlockZ()));
                }
                player.sendMessage(GameManager.getInstance().getBorderPrefix() + Color.translate(PotSG.getInstance().getConfiguration("messages").getString("stay-in-border")));
                player.playSound(player.getLocation(), Sound.EXPLODE, 1.0f, 1.0f);
                player.spigot().setCollidesWithEntities(true);
            }
        }
    }
    
    public Border getBorder() {
        return this.border;
    }
    
    public BorderTask getBorderTask() {
        return this.borderTask;
    }
    
    public int getStartBorder() {
        return this.startBorder;
    }
    
    public int getShrinkUntil() {
        return this.shrinkUntil;
    }
    
    public int getShrinkEvery() {
        return this.shrinkEvery;
    }
    
    public void setBorder(final Border border) {
        this.border = border;
    }
    
    public void setBorderTask(final BorderTask borderTask) {
        this.borderTask = borderTask;
    }
    
    public void setStartBorder(final int startBorder) {
        this.startBorder = startBorder;
    }
    
    public void setShrinkUntil(final int shrinkUntil) {
        this.shrinkUntil = shrinkUntil;
    }
    
    public void setShrinkEvery(final int shrinkEvery) {
        this.shrinkEvery = shrinkEvery;
    }
    
    public static BorderManager getInstance() {
        return BorderManager.instance;
    }
}
