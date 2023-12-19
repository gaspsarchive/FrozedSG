package club.frozed.frozedsg.utils.leaderboards;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.managers.MongoManager;
import club.frozed.frozedsg.managers.PlayerDataManager;
import club.frozed.frozedsg.player.PlayerData;
import club.frozed.frozedsg.utils.ItemBuilder;
import club.frozed.frozedsg.utils.chat.Color;
import com.mongodb.BasicDBObject;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LeaderboardManager
{
    public static LeaderboardManager instance;
    private List<Leaderboard> leaderboardList;
    private List<Document> top5Wins;
    private List<Document> allStatistics;

    public LeaderboardManager() {
        this.leaderboardList = new ArrayList<Leaderboard>();
        this.top5Wins = MongoManager.getInstance().getStatsCollection().find().limit(5).sort(new BasicDBObject("wins", -1)).into(new ArrayList<Document>());
        this.allStatistics = MongoManager.getInstance().getStatsCollection().find().into(new ArrayList<Document>());
        LeaderboardManager.instance = this;
        this.leaderboardList.addAll(Arrays.asList(new Leaderboard(Material.EMERALD, "&eKills", "kills", PotSG.getInstance().getConfiguration("inventory").getBoolean("leaderboard.kills.enabled")), new Leaderboard(Material.CHEST, "&eDeaths", "deaths", PotSG.getInstance().getConfiguration("inventory").getBoolean("leaderboard.deaths.enabled")), new Leaderboard(Material.NETHER_STAR, "&eWins", "wins", PotSG.getInstance().getConfiguration("inventory").getBoolean("leaderboard.wins.enabled")), new Leaderboard(Material.GOLD_NUGGET, "&ePoints", "points", PotSG.getInstance().getConfiguration("inventory").getBoolean("leaderboard.points.enabled")), new Leaderboard(Material.BOOK, "&eGames Played", "gamesPlayed", PotSG.getInstance().getConfiguration("inventory").getBoolean("leaderboard.games-played.enabled")), new Leaderboard(Material.DIAMOND, "&eKillStreak", "killStreak", PotSG.getInstance().getConfiguration("inventory").getBoolean("leaderboard.kill-streak.enabled")), new Leaderboard(Material.GOLDEN_APPLE, "&eGolden Apple Eaten", "goldenApplesEaten", PotSG.getInstance().getConfiguration("inventory").getBoolean("leaderboard.golden-apple-eaten.enabled")), new Leaderboard(Material.BOW, "&eBow Shots", "bowShots", PotSG.getInstance().getConfiguration("inventory").getBoolean("leaderboard.bow-shots.enabled")), new Leaderboard(Material.CHEST, "&eChest Broke", "chestBroke", PotSG.getInstance().getConfiguration("inventory").getBoolean("leaderboard.chest-broke.enabled")), new Leaderboard(Material.POTION, "&ePotion Splashed", "potionSplashed", PotSG.getInstance().getConfiguration("inventory").getBoolean("leaderboard.potion-splashed.enabled")), new Leaderboard(Material.POTION, "&ePotion Drank", "potionDrank", PotSG.getInstance().getConfiguration("inventory").getBoolean("leaderboard.potion-drank.enabled"))));
    }

    public void updateAllLeaderboards() {
        new BukkitRunnable() {
            public void run() {
                for (final Leaderboard leaderboard : LeaderboardManager.this.getLeaderboardList()) {
                    leaderboard.load();
                }
                LeaderboardManager.this.allStatistics = (List<Document>)MongoManager.getInstance().getStatsCollection().find().into(new ArrayList());
                LeaderboardManager.this.top5Wins = (List<Document>)MongoManager.getInstance().getStatsCollection().find().limit(5).sort(new BasicDBObject("wins", -1)).into(new ArrayList());
            }
        }.runTaskAsynchronously((Plugin)PotSG.getInstance());
    }

    public Inventory getInventory(final Player player) {
        final Object inv = Bukkit.createInventory((InventoryHolder)null, 54, Color.translate(PotSG.getInstance().getConfiguration("inventory").getString("leaderboard-inventory.title")));
        final Object data = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());
        final Object stats = new ItemBuilder(Material.SKULL_ITEM);
        ((ItemBuilder)stats).setDurability(3);
        ((ItemBuilder)stats).setName("&eYour Statistics");
        ((ItemBuilder)stats).addLoreLine("");
        ((ItemBuilder)stats).addLoreLine("&eKills: &d" + ((PlayerData)data).getKills().getAmount());
        ((ItemBuilder)stats).addLoreLine("&eDeaths: &d" + ((PlayerData)data).getDeaths().getAmount());
        ((ItemBuilder)stats).addLoreLine("&eWins: &d" + ((PlayerData)data).getWins().getAmount());
        ((ItemBuilder)stats).addLoreLine("&ePoints: &d" + ((PlayerData)data).getPoints().getAmount());
        ((ItemBuilder)stats).addLoreLine("&eGames Played: &d" + ((PlayerData)data).getGamesPlayed().getAmount());
        ((ItemBuilder)stats).addLoreLine("&eKDR: &d" + ((PlayerData)data).getKdr());
        ((Inventory)inv).setItem(13, ((ItemBuilder)stats).toItemStack());
        int i = 27;
        for (final Object leaderboards : this.getLeaderboardList()) {
            if (((Leaderboard)leaderboards).isEnabled()) {
                Object item;
                if (((Leaderboard)leaderboards).getName().equals("Potion Splashed")) {
                    item = new ItemBuilder(Material.POTION).setDurability(16421);
                }
                else {
                    item = new ItemBuilder(((Leaderboard)leaderboards).getMaterial());
                }
                ((ItemBuilder)item).setName(PotSG.getInstance().getConfiguration("inventory").getString("leaderboard-inventory.name").replaceAll("<name>", ((Leaderboard)leaderboards).getName()));
                if (((Leaderboard)leaderboards).getFormats().isEmpty()) {
                    for (final Object string : PotSG.getInstance().getConfiguration("inventory").getStringList("leaderboard-inventory.empty-leaderboard")) {
                        ((ItemBuilder)item).addLoreLine((String)string);
                    }
                }
                else {
                    ((ItemBuilder)item).addLoreLine("");
                    for (final Object string : ((Leaderboard)leaderboards).getFormats()) {
                        ((ItemBuilder)item).addLoreLine((String)string);
                    }
                }
                ((Inventory)inv).setItem(i, ((ItemBuilder)item).toItemStack());
                i += 2;
                if (i != 45) {
                    continue;
                }
                i += 3;
            }
        }
        return (Inventory)inv;
    }

    public List<Leaderboard> getLeaderboardList() {
        return this.leaderboardList;
    }

    public List<Document> getTop5Wins() {
        return this.top5Wins;
    }

    public List<Document> getAllStatistics() {
        return this.allStatistics;
    }

    public void setLeaderboardList(final List<Leaderboard> leaderboardList) {
        this.leaderboardList = leaderboardList;
    }

    public void setTop5Wins(final List<Document> top5Wins) {
        this.top5Wins = top5Wins;
    }

    public void setAllStatistics(final List<Document> allStatistics) {
        this.allStatistics = allStatistics;
    }

    public static LeaderboardManager getInstance() {
        return LeaderboardManager.instance;
    }
}
