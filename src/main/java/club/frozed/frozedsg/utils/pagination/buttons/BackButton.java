package club.frozed.frozedsg.utils.pagination.buttons;

import club.frozed.frozedsg.utils.chat.Color;
import club.frozed.frozedsg.utils.pagination.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BackButton extends Button {
    private Menu back;

    public ItemStack getButtonItem(Player player) {
        ItemStack itemStack = new ItemStack(Material.BED);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(Color.translate("&4Go back"));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public void clicked(Player player, int i, ClickType clickType, int hb) {
        playNeutral(player);
        this.back.openMenu(player);
    }

    public BackButton(Menu back) {
        this.back = back;
    }
}
