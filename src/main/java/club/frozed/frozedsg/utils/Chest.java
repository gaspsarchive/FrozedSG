package club.frozed.frozedsg.utils;

import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
public class Chest
{
    private ItemStack[] items;
    private int number;
    
    public Chest(final ItemStack[] items, final int number) {
        this.items = items;
        this.number = number;
    }
    
    public ItemStack[] getItems() {
        return this.items;
    }
    
    public int getNumber() {
        return this.number;
    }
    
    public void setItems(final ItemStack[] items) {
        this.items = items;
    }
    
    public void setNumber(final int number) {
        this.number = number;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Chest)) {
            return false;
        }
        final Chest other = (Chest)o;
        return other.canEqual(this) && Arrays.deepEquals(this.getItems(), other.getItems()) && this.getNumber() == other.getNumber();
    }
    
    protected boolean canEqual(final Object other) {
        return other instanceof Chest;
    }
    
    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * 59 + Arrays.deepHashCode(this.getItems());
        result = result * 59 + this.getNumber();
        return result;
    }
    
    @Override
    public String toString() {
        return "Chest(items=" + Arrays.deepToString(this.getItems()) + ", number=" + this.getNumber() + ")";
    }
}
