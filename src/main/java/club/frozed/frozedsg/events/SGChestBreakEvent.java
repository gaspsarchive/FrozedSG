package club.frozed.frozedsg.events;

import club.frozed.frozedsg.managers.PlayerDataManager;
import club.frozed.frozedsg.player.PlayerData;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;

public class SGChestBreakEvent extends Event {
    public Player player;
    public PlayerData playerData = null;
    public Location chestLocation;
    public Inventory inventory;
    public static HandlerList handlers = new HandlerList();

    public SGChestBreakEvent(Player player, Inventory inventory, Location location) {
        this.player = player;
        this.chestLocation = location;
        this.inventory = inventory;
        if (PlayerDataManager.getInstance().getByUUID(player.getUniqueId()) != null) {
            this.playerData = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());
        }

    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return this.player;
    }

    public PlayerData getPlayerData() {
        return this.playerData;
    }

    public Location getChestLocation() {
        return this.chestLocation;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setPlayerData(PlayerData playerData) {
        this.playerData = playerData;
    }

    public void setChestLocation(Location chestLocation) {
        this.chestLocation = chestLocation;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
}