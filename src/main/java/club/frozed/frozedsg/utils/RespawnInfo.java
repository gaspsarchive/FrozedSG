package club.frozed.frozedsg.utils;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class RespawnInfo
{
    private Location location;
    private ItemStack[] inventory;
    private ItemStack[] armor;

    public RespawnInfo(Location location, ItemStack[] inventory, ItemStack[] armor) {
        this.location = location;
        this.inventory = inventory;
        this.armor = armor;
    }

    public Location getLocation() {
        return this.location;
    }

    public ItemStack[] getInventory() {
        return this.inventory;
    }

    public ItemStack[] getArmor() {
        return this.armor;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setInventory(ItemStack[] inventory) {
        this.inventory = inventory;
    }

    public void setArmor(ItemStack[] armor) {
        this.armor = armor;
    }
}
