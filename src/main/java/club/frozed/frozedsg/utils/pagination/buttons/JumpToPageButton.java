package club.frozed.frozedsg.utils.pagination.buttons;

import club.frozed.frozedsg.utils.chat.Color;
import club.frozed.frozedsg.utils.pagination.PaginatedMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;


public class JumpToPageButton extends Button
{
    private int page;
    private PaginatedMenu menu;
    private boolean current;
    
    @Override
    public ItemStack getButtonItem(final Player player) {
        final ItemStack itemStack = new ItemStack(this.current ? Material.ENCHANTED_BOOK : Material.BOOK, this.page);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(Color.translate("&bPage " + this.page));
        if (this.current) {
            itemMeta.setLore((List)Color.translate(Arrays.asList("", "&aCurrent page")));
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    
    @Override
    public void clicked(final Player player, final int i, final ClickType clickType, final int hb) {
        this.menu.modPage(player, this.page - this.menu.getPage());
        Button.playNeutral(player);
    }
    
    public JumpToPageButton(final int page, final PaginatedMenu menu, final boolean current) {
        this.page = page;
        this.menu = menu;
        this.current = current;
    }
}
