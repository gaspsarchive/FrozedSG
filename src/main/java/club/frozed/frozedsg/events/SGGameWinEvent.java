package club.frozed.frozedsg.events;

import club.frozed.frozedsg.managers.PlayerDataManager;
import club.frozed.frozedsg.player.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SGGameWinEvent extends Event
{
    public Player player;
    public PlayerData playerData;
    public static HandlerList handlers;
    
    public SGGameWinEvent(final Player player) {
        this.playerData = null;
        this.player = player;
        if (PlayerDataManager.getInstance().getByUUID(player.getUniqueId()) != null) {
            this.playerData = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());
        }
    }
    
    public SGGameWinEvent() {
        this.playerData = null;
        this.player = null;
    }
    
    public HandlerList getHandlers() {
        return SGGameWinEvent.handlers;
    }
    
    public static HandlerList getHandlerList() {
        return SGGameWinEvent.handlers;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public PlayerData getPlayerData() {
        return this.playerData;
    }
    
    public void setPlayer(final Player player) {
        this.player = player;
    }
    
    public void setPlayerData(final PlayerData playerData) {
        this.playerData = playerData;
    }
    
    static {
        SGGameWinEvent.handlers = new HandlerList();
    }
}
