package club.frozed.frozedsg.border;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.managers.WorldsManager;
import club.frozed.frozedsg.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Border
{
    private int size;
    private int seconds;
    private int lastBorder;
    private int startBorder;
    
    public Border() {
        this.startBorder = BorderManager.getInstance().getStartBorder();
        this.size = this.startBorder;
        this.seconds = BorderManager.getInstance().getShrinkEvery();
        this.lastBorder = BorderManager.getInstance().getShrinkUntil();
    }
    
    public void increaseSeconds() {
        --this.seconds;
    }

    public int getNextBorder() {
        String shrinkStream = PotSG.getInstance().getConfiguration("config").getString("BORDER.SHRINK-STREAM");
        String[] shrinksStream = shrinkStream.split(";");
        if (((List)Arrays.stream(shrinksStream).collect(Collectors.toList())).contains(String.valueOf(this.size))) {
            int current = ((List)Arrays.stream(shrinksStream).collect(Collectors.toList())).indexOf(String.valueOf(this.size));
            return current == shrinksStream.length - 1 ? Integer.parseInt(shrinksStream[shrinksStream.length - 1]) : Integer.parseInt(shrinksStream[current + 1]);
        } else {
            return Utils.getNextBorderDefault();
        }
    }
    
    public void shrinkBorder(final int size) {
        this.size = size;
        final World w = WorldsManager.getInstance().getGameWorld();
        this.buildWalls(size, Material.BEDROCK, 4, w);
        for (final Player player : w.getPlayers()) {
            if (player.getLocation().getBlockX() > size) {
                player.setNoDamageTicks(100);
                player.setFallDistance(0.0f);
                player.teleport(new Location(w, (double)(size - 4), w.getHighestBlockYAt(size - 4, player.getLocation().getBlockZ()) + 0.5, (double)player.getLocation().getBlockZ()));
                player.setFallDistance(0.0f);
                player.getLocation().add(0.0, 2.0, 0.0).getBlock().setType(Material.AIR);
                player.getLocation().add(0.0, 3.0, 0.0).getBlock().setType(Material.AIR);
                player.getLocation().add(0.0, 4.0, 0.0).getBlock().setType(Material.AIR);
                player.teleport(new Location(w, (double)player.getLocation().getBlockX(), w.getHighestBlockAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ()).getLocation().getBlockY() + 0.5, (double)player.getLocation().getBlockZ()));
            }
            if (player.getLocation().getBlockZ() > size) {
                player.setNoDamageTicks(100);
                player.setFallDistance(0.0f);
                player.teleport(new Location(w, (double)player.getLocation().getBlockX(), w.getHighestBlockYAt(player.getLocation().getBlockX(), size - 4) + 0.5, (double)(size - 4)));
                player.setFallDistance(0.0f);
                player.getLocation().add(0.0, 2.0, 0.0).getBlock().setType(Material.AIR);
                player.getLocation().add(0.0, 3.0, 0.0).getBlock().setType(Material.AIR);
                player.getLocation().add(0.0, 4.0, 0.0).getBlock().setType(Material.AIR);
                player.teleport(new Location(w, (double)player.getLocation().getBlockX(), w.getHighestBlockAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ()).getLocation().getBlockY() + 0.5, (double)player.getLocation().getBlockZ()));
            }
            if (player.getLocation().getBlockX() < -size) {
                player.setNoDamageTicks(100);
                player.setFallDistance(0.0f);
                player.teleport(new Location(w, (double)(-size + 4), w.getHighestBlockYAt(-size + 4, player.getLocation().getBlockZ()) + 0.5, (double)player.getLocation().getBlockZ()));
                player.setFallDistance(0.0f);
                player.getLocation().add(0.0, 2.0, 0.0).getBlock().setType(Material.AIR);
                player.getLocation().add(0.0, 3.0, 0.0).getBlock().setType(Material.AIR);
                player.getLocation().add(0.0, 4.0, 0.0).getBlock().setType(Material.AIR);
                player.teleport(new Location(w, (double)player.getLocation().getBlockX(), w.getHighestBlockAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ()).getLocation().getBlockY() + 0.5, (double)player.getLocation().getBlockZ()));
            }
            if (player.getLocation().getBlockZ() < -size) {
                player.setNoDamageTicks(100);
                player.setFallDistance(0.0f);
                player.teleport(new Location(w, (double)player.getLocation().getBlockX(), w.getHighestBlockYAt(player.getLocation().getBlockX(), -size + 4) + 0.5, (double)(-size + 4)));
                player.setFallDistance(0.0f);
                player.getLocation().add(0.0, 2.0, 0.0).getBlock().setType(Material.AIR);
                player.getLocation().add(0.0, 3.0, 0.0).getBlock().setType(Material.AIR);
                player.getLocation().add(0.0, 4.0, 0.0).getBlock().setType(Material.AIR);
                player.teleport(new Location(w, (double)player.getLocation().getBlockX(), w.getHighestBlockAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ()).getLocation().getBlockY() + 0.5, (double)player.getLocation().getBlockZ()));
            }
        }
    }
    
    public void buildWalls(final int size, final Material mat, final int h, final World w) {
        final Location loc = new Location(w, 0.0, 59.0, 0.0);
        for (int i = h; i < h + h; ++i) {
            for (int x = loc.getBlockX() - size; x <= loc.getBlockX() + size; ++x) {
                for (int y = 58; y <= 58; ++y) {
                    for (int z = loc.getBlockZ() - size; z <= loc.getBlockZ() + size; ++z) {
                        if (x == loc.getBlockX() - size || x == loc.getBlockX() + size || z == loc.getBlockZ() - size || z == loc.getBlockZ() + size) {
                            final Location loc2 = new Location(w, (double)x, (double)y, (double)z);
                            loc2.setY((double)w.getHighestBlockYAt(loc2));
                            loc2.getBlock().setType(mat);
                        }
                    }
                }
            }
        }
    }
    
    public int getSize() {
        return this.size;
    }
    
    public int getSeconds() {
        return this.seconds;
    }
    
    public int getLastBorder() {
        return this.lastBorder;
    }
    
    public int getStartBorder() {
        return this.startBorder;
    }
    
    public void setSize(final int size) {
        this.size = size;
    }
    
    public void setSeconds(final int seconds) {
        this.seconds = seconds;
    }
    
    public void setLastBorder(final int lastBorder) {
        this.lastBorder = lastBorder;
    }
    
    public void setStartBorder(final int startBorder) {
        this.startBorder = startBorder;
    }
}
