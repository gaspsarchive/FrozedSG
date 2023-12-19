package club.frozed.frozedsg.events;

import club.frozed.frozedsg.player.PlayerData;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SGWorldLoad extends Event
{
    public World world;
    public PlayerData playerData;
    public static HandlerList handlers;
    
    public SGWorldLoad(final World world) {
        this.playerData = null;
        this.world = world;
    }
    
    public Location getCenterLocation() {
        final int x = 0;
        final int z = 0;
        final double y = this.world.getHighestBlockYAt(x, z);
        return new Location(this.world, (double)x, y, (double)z);
    }
    
    public HandlerList getHandlers() {
        return SGWorldLoad.handlers;
    }
    
    public static HandlerList getHandlerList() {
        return SGWorldLoad.handlers;
    }
    
    public World getWorld() {
        return this.world;
    }
    
    public PlayerData getPlayerData() {
        return this.playerData;
    }
    
    public void setWorld(final World world) {
        this.world = world;
    }
    
    public void setPlayerData(final PlayerData playerData) {
        this.playerData = playerData;
    }
    
    static {
        SGWorldLoad.handlers = new HandlerList();
    }
}
