package club.frozed.frozedsg.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SGCombatTagEvent extends Event
{
    public Player target;
    public Player player;
    public static HandlerList handlers;
    
    public SGCombatTagEvent(final Player player, final Player target) {
        this.player = player;
        this.target = target;
    }
    
    public HandlerList getHandlers() {
        return SGCombatTagEvent.handlers;
    }
    
    public static HandlerList getHandlerList() {
        return SGCombatTagEvent.handlers;
    }
    
    public Player getTarget() {
        return this.target;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public void setTarget(final Player target) {
        this.target = target;
    }
    
    public void setPlayer(final Player player) {
        this.player = player;
    }
    
    static {
        SGCombatTagEvent.handlers = new HandlerList();
    }
}
