package club.frozed.frozedsg.managers;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.player.PlayerData;
import club.frozed.frozedsg.utils.ItemBuilder;
import club.frozed.frozedsg.utils.chat.Color;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class InventoryManager
{
    public static InventoryManager instance;
    
    public InventoryManager() {
        InventoryManager.instance = this;
    }
    
    public Inventory getStatsInventory(final PlayerData data) {
        final Inventory inv = Bukkit.createInventory((InventoryHolder)null, 9, Color.translate(PotSG.getInstance().getConfiguration("inventory").getString("stats-inventory.title").replaceAll("<target>", data.getName())));
        final ItemBuilder combat = new ItemBuilder(Material.DIAMOND_SWORD);
        combat.setName(PotSG.getInstance().getConfiguration("inventory").getString("stats-inventory.combat.name"));
        for (final String string : PotSG.getInstance().getConfiguration("inventory").getStringList("stats-inventory.combat.lore")) {
            combat.addLoreLine(this.replace(string, data));
        }
        final ItemBuilder misc = new ItemBuilder(Material.NETHER_STAR);
        misc.setName(PotSG.getInstance().getConfiguration("inventory").getString("stats-inventory.misc.name"));
        for (final String string2 : PotSG.getInstance().getConfiguration("inventory").getStringList("stats-inventory.misc.lore")) {
            misc.addLoreLine(this.replace(string2, data));
        }
        final ItemBuilder potion = new ItemBuilder(Material.POTION);
        potion.setName(PotSG.getInstance().getConfiguration("inventory").getString("stats-inventory.potion.name"));
        for (final String string3 : PotSG.getInstance().getConfiguration("inventory").getStringList("stats-inventory.potion.lore")) {
            potion.addLoreLine(this.replace(string3, data));
        }
        final ItemBuilder other = new ItemBuilder(Material.DIAMOND);
        other.setName(PotSG.getInstance().getConfiguration("inventory").getString("stats-inventory.other.name"));
        for (final String string4 : PotSG.getInstance().getConfiguration("inventory").getStringList("stats-inventory.other.lore")) {
            other.addLoreLine(this.replace(string4, data));
        }
        inv.setItem(1, combat.toItemStack());
        inv.setItem(3, misc.toItemStack());
        inv.setItem(5, potion.toItemStack());
        inv.setItem(7, other.toItemStack());
        return inv;
    }
    
    public String replace(final String s, final PlayerData data) {
        return s.replaceAll("<target_kills>", String.valueOf(data.getKills().getAmount())).replaceAll("<target_deaths>", String.valueOf(data.getDeaths().getAmount())).replaceAll("<target_kdr>", String.valueOf(data.getKdr())).replaceAll("<target_games_played>", String.valueOf(data.getGamesPlayed().getAmount())).replaceAll("<target_wins>", String.valueOf(data.getWins().getAmount())).replaceAll("<target_points>", String.valueOf(data.getPoints().getAmount())).replaceAll("<target_golden_apples_eaten>", String.valueOf(data.getGoldenApplesEaten().getAmount())).replaceAll("<target_bow_shots>", String.valueOf(data.getBowShots().getAmount())).replaceAll("<target_chest_opened>", String.valueOf(data.getChestBroke().getAmount())).replaceAll("<target_potion_splashed>", String.valueOf(data.getPotionSplashed().getAmount())).replaceAll("<target_potion_drank>", String.valueOf(data.getPotionDrank().getAmount()));
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof InventoryManager)) {
            return false;
        }
        final InventoryManager other = (InventoryManager)o;
        return other.canEqual(this);
    }
    
    protected boolean canEqual(final Object other) {
        return other instanceof InventoryManager;
    }
    
    @Override
    public int hashCode() {
        final int result = 1;
        return 1;
    }
    
    @Override
    public String toString() {
        return "InventoryManager()";
    }
    
    public static InventoryManager getInstance() {
        return InventoryManager.instance;
    }
}
