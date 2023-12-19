package club.frozed.frozedsg.utils.pagination.buttons;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.utils.chat.Color;
import club.frozed.frozedsg.utils.pagination.PaginatedMenu;
import club.frozed.frozedsg.utils.pagination.ViewAllPagesMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
public class PageButton extends Button
{
    private int mod;
    private PaginatedMenu menu;
    
    @Override
    public ItemStack getButtonItem(final Player player) {
        final ItemStack itemStack = new ItemStack(Material.ARROW);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        if (this.hasNext(player)) {
            itemMeta.setDisplayName(Color.translate((this.mod > 0) ? PotSG.getInstance().getConfiguration("inventory").getString("player-statistics-inventory.page-button.next-page-name") : PotSG.getInstance().getConfiguration("inventory").getString("player-statistics-inventory.page-button.previous-page-name")));
        }
        else {
            itemMeta.setDisplayName(Color.translate((this.mod > 0) ? PotSG.getInstance().getConfiguration("inventory").getString("player-statistics-inventory.page-button.last-page-name") : PotSG.getInstance().getConfiguration("inventory").getString("player-statistics-inventory.page-button.first-page-name")));
        }
        final List<String> lore = new ArrayList<String>();
        for (final String string : PotSG.getInstance().getConfiguration("inventory").getStringList("player-statistics-inventory.page-button.lore")) {
            lore.add(string);
        }
        itemMeta.setLore((List)Color.translate(lore));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    
    @Override
    public void clicked(final Player player, final int i, final ClickType clickType, final int hb) {
        if (clickType == ClickType.RIGHT) {
            new ViewAllPagesMenu(this.menu).openMenu(player);
            Button.playNeutral(player);
        }
        else if (this.hasNext(player)) {
            this.menu.modPage(player, this.mod);
            Button.playNeutral(player);
        }
        else {
            Button.playFail(player);
        }
    }
    
    private boolean hasNext(final Player player) {
        final int pg = this.menu.getPage() + this.mod;
        return pg > 0 && this.menu.getPages(player) >= pg;
    }
    
    public PageButton(final int mod, final PaginatedMenu menu) {
        this.mod = mod;
        this.menu = menu;
    }
}
