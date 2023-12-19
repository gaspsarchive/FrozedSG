package club.frozed.frozedsg.utils.inventories;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.utils.ItemBuilder;
import club.frozed.frozedsg.utils.chat.Color;
import club.frozed.frozedsg.utils.leaderboards.LeaderboardManager;
import club.frozed.frozedsg.utils.pagination.PaginatedMenu;
import club.frozed.frozedsg.utils.pagination.buttons.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LobbyInventoryPlayers extends PaginatedMenu {
    public LobbyInventoryPlayers() {
    }

    public String getPrePaginatedTitle(Player player) {
        return Color.translate(PotSG.getInstance().getConfiguration("inventory").getString("player-statistics-inventory.title"));
    }

    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap();
        LeaderboardManager.getInstance().getAllStatistics().forEach((document) -> {
            Button var10000 = (Button)buttons.put(buttons.size(), new Button() {
                public ItemStack getButtonItem(Player player) {
                    ItemBuilder item = new ItemBuilder(Material.INK_SACK);
                    item.setDurability(7);
                    item.setName(PotSG.getInstance().getConfiguration("inventory").getString("player-statistics-inventory.name").replaceAll("<player_name>", document.getString("name") == null ? "null" : document.getString("name")));
                    Iterator var3 = PotSG.getInstance().getConfiguration("inventory").getStringList("player-statistics-inventory.lore").iterator();

                    while(var3.hasNext()) {
                        String string = (String)var3.next();
                        item.addLoreLine(string.replaceAll("<player_kills>", String.valueOf(document.getInteger("kills"))).replaceAll("<player_deaths>", String.valueOf(document.getInteger("deaths"))).replaceAll("<player_killstreak>", String.valueOf(document.getInteger("killStreak"))).replaceAll("<player_wins>", String.valueOf(document.getInteger("wins"))).replaceAll("<player_points>", String.valueOf(document.getInteger("points"))).replaceAll("<player_games_played>", String.valueOf(document.getInteger("gamesPlayed"))).replaceAll("<player_golden_apples_eaten>", String.valueOf(document.getInteger("goldenApplesEaten"))).replaceAll("<player_bow_shots>", String.valueOf(document.getInteger("bowShots"))).replaceAll("<player_chests_opened>", String.valueOf(document.getInteger("chestBroke"))).replaceAll("<player_potion_splashed>", String.valueOf(document.getInteger("potionSplashed"))).replaceAll("<player_potion_drank>", String.valueOf(document.getInteger("potionDrank"))));
                    }

                    return item.toItemStack();
                }
            });
        });
        return buttons;
    }
}
