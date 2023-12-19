package club.frozed.frozedsg.utils.inventories;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.managers.PlayerManager;
import club.frozed.frozedsg.utils.ItemBuilder;
import club.frozed.frozedsg.utils.chat.Color;
import club.frozed.frozedsg.utils.pagination.PaginatedMenu;
import club.frozed.frozedsg.utils.pagination.buttons.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AlivePlayersInventory extends PaginatedMenu {
    public AlivePlayersInventory() {
    }

    public String getPrePaginatedTitle(Player player) {
        return Color.translate(PotSG.getInstance().getConfiguration("inventory").getString("player-alive-inventory.title"));
    }

    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap();
        PlayerManager.getInstance().getGamePlayers().forEach((online) -> {
            buttons.put(buttons.size(), new Button() {
                public ItemStack getButtonItem(Player player) {
                    ItemBuilder item = new ItemBuilder(Material.SKULL_ITEM);
                    item.setDurability(3);
                    item.setName(PotSG.getInstance().getConfiguration("inventory").getString("player-alive-inventory.name").replaceAll("<player_name>", online.getName()));
                    Iterator var3 = PotSG.getInstance().getConfiguration("inventory").getStringList("player-alive-inventory.lore").iterator();

                    while(var3.hasNext()) {
                        String lore = (String)var3.next();
                        item.addLoreLine(lore.replaceAll("<player_name>", online.getName()));
                    }

                    return item.toItemStack();
                }
            });
        });
        return buttons;
    }
}
