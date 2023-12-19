package club.frozed.frozedsg.listeners;

import club.frozed.frozedsg.border.BorderManager;
import club.frozed.frozedsg.managers.PlayerDataManager;
import club.frozed.frozedsg.player.PlayerData;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;

public class GlassListener implements Listener
{
    private final Map<UUID, Set<Location>> glassLocations;
    
    public GlassListener() {
        this.glassLocations = new HashMap<UUID, Set<Location>>();
    }
    
    public int closest(final int n, final int... array) {
        int n2 = array[0];
        for (final int j : array) {
            if (Math.abs(n - j) < Math.abs(n - n2)) {
                n2 = j;
            }
        }
        return n2;
    }
    
    public void update(final Player player) {
        if (BorderManager.getInstance().getBorder() == null) {
            return;
        }
        final PlayerData data = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());
        if (data != null && data.getSettingByName("GlassBorder") != null && !data.getSettingByName("GlassBorder").isEnabled()) {
            this.removeGlass(player);
            return;
        }
        final HashSet<Location> set = new HashSet<Location>();
        final int firstClosest = this.closest(player.getLocation().getBlockX(), -BorderManager.getInstance().getBorder().getSize(), BorderManager.getInstance().getBorder().getSize());
        final int secondClosest = this.closest(player.getLocation().getBlockZ(), -BorderManager.getInstance().getBorder().getSize(), BorderManager.getInstance().getBorder().getSize());
        final boolean first = Math.abs(player.getLocation().getX() - firstClosest) < 6.0;
        final boolean second = Math.abs(player.getLocation().getZ() - secondClosest) < 6.0;
        if (!first && !second) {
            this.removeGlass(player);
            return;
        }
        if (first) {
            for (int i = -4; i < 5; ++i) {
                for (int j = -5; j < 6; ++j) {
                    final Location location = new Location(player.getLocation().getWorld(), (double)firstClosest, (double)(player.getLocation().getBlockY() + i), (double)(player.getLocation().getBlockZ() + j));
                    if (!set.contains(location) && !location.getBlock().getType().isOccluding()) {
                        set.add(location);
                    }
                }
            }
        }
        if (second) {
            for (int k = -4; k < 5; ++k) {
                for (int l = -5; l < 6; ++l) {
                    final Location location2 = new Location(player.getLocation().getWorld(), (double)(player.getLocation().getBlockX() + l), (double)(player.getLocation().getBlockY() + k), (double)secondClosest);
                    if (!set.contains(location2) && !location2.getBlock().getType().isOccluding()) {
                        set.add(location2);
                    }
                }
            }
        }
        this.render(player, set);
    }
    
    @Deprecated
    public void render(final Player player, final Set<Location> set) {
        if (BorderManager.getInstance().getBorder() == null) {
            return;
        }
        final PlayerData data = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());
        if (data != null && data.getSettingByName("GlassBorder") != null && !data.getSettingByName("GlassBorder").isEnabled()) {
            return;
        }
        if (this.glassLocations.containsKey(player.getUniqueId())) {
            this.glassLocations.get(player.getUniqueId()).addAll(set);
            for (final Location location : this.glassLocations.get(player.getUniqueId())) {
                if (!set.contains(location)) {
                    final Block block = location.getBlock();
                    player.sendBlockChange(location, block.getTypeId(), block.getData());
                }
            }
            final Iterator<Location> iterator2 = set.iterator();
            while (iterator2.hasNext()) {
                player.sendBlockChange((Location)iterator2.next(), 95, (byte)14);
            }
        }
        else {
            final Iterator<Location> iterator3 = set.iterator();
            while (iterator3.hasNext()) {
                player.sendBlockChange((Location)iterator3.next(), 95, (byte)14);
            }
        }
        this.glassLocations.put(player.getUniqueId(), set);
    }
    
    public void removeGlass(final Player player) {
        if (this.glassLocations.containsKey(player.getUniqueId())) {
            for (final Location location : this.glassLocations.get(player.getUniqueId())) {
                final Block block = location.getBlock();
                player.sendBlockChange(location, block.getTypeId(), block.getData());
            }
            this.glassLocations.remove(player.getUniqueId());
        }
    }
    
    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        this.update(player);
    }
    
    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        this.update(player);
    }
}
