package club.frozed.frozedsg.player;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.enums.PlayerState;
import club.frozed.frozedsg.managers.GameManager;
import club.frozed.frozedsg.managers.MongoManager;
import club.frozed.frozedsg.utils.Cooldown;
import club.frozed.frozedsg.utils.ItemBuilder;
import club.frozed.frozedsg.utils.RespawnInfo;
import club.frozed.frozedsg.utils.Setting;
import club.frozed.frozedsg.utils.chat.Color;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class PlayerData {
    private PlayerState state;
    private UUID uuid;
    private String name;
    private Stat kills;
    private Stat deaths;
    private Stat wins;
    private Stat gamesPlayed;
    private Stat points;
    private Stat goldenApplesEaten;
    private Stat bowShots;
    private Stat killStreak;
    private Stat gameKills;
    private Stat chestBroke;
    private Stat potionSplashed;
    private Stat potionDrank;
    private Location lastChestOpenedLocation;
    private Cooldown enderpearlCooldown;
    private Cooldown combatCooldown;
    private RespawnInfo respawnInfo;
    private boolean specChat;
    private List<Setting> settings;

    public PlayerData(UUID uuid) {
        this.state = PlayerState.LOBBY;
        this.kills = new Stat();
        this.deaths = new Stat();
        this.wins = new Stat();
        this.gamesPlayed = new Stat();
        this.points = new Stat();
        this.goldenApplesEaten = new Stat();
        this.bowShots = new Stat();
        this.killStreak = new Stat();
        this.gameKills = new Stat();
        this.chestBroke = new Stat();
        this.potionSplashed = new Stat();
        this.potionDrank = new Stat();
        this.lastChestOpenedLocation = null;
        this.respawnInfo = null;
        this.specChat = true;
        this.settings = new ArrayList();
        this.uuid = uuid;
        this.name = Bukkit.getOfflinePlayer(uuid).getName();
        this.settings.add(new Setting("&e&lChestClick", Material.CHEST, true, 0, new String[]{"You should not see this"}));
        this.settings.add(new Setting("&e&lGlassBorder", Material.STAINED_GLASS, true, 14, 0, new String[]{"You should not see this"}));
        this.settings.add(new Setting("&e&lScoreboard", Material.ITEM_FRAME, true, 0, new String[]{"You should not see this"}));
        this.settings.add(new Setting("&e&lLightning-On-Kill", Material.SKULL_ITEM, true, 1, 0, new String[]{"You should not see this"}));
        this.settings.add(new Setting("&e&lChest-Auto-Pickup", Material.ENDER_CHEST, false, 50, new String[]{"You should not see this"}));
    }

    public Setting getSettingByName(String name) {
        return (Setting)this.settings.stream().filter((setting) -> {
            return setting.getName().equals(name);
        }).findFirst().orElse((Setting) null);
    }

    public Inventory getSettingsInventory() {
        Inventory inv = Bukkit.createInventory((InventoryHolder)null, PotSG.getInstance().getConfiguration("inventory").getInt("settings-inventory.size"), Color.translate(PotSG.getInstance().getConfiguration("inventory").getString("settings-inventory.title")));
        inv.addItem(new ItemStack[]{new ItemStack(Material.AIR)});
        this.settings.forEach((setting) -> {
            ItemBuilder item = new ItemBuilder(setting.getMaterial());
            item.setDurability(setting.getData());
            item.setName(PotSG.getInstance().getConfiguration("inventory").getString("settings-inventory.name").replaceAll("<name>", setting.getName()));
            Iterator var4 = PotSG.getInstance().getConfiguration("inventory").getStringList("settings-inventory.settings-line").iterator();

            String description;
            while(var4.hasNext()) {
                description = (String)var4.next();
                item.addLoreLine(description);
            }

            if (setting.getDescription().length > 0) {
                if (setting.getName().equals("ChestClick")) {
                    var4 = PotSG.getInstance().getConfiguration("inventory").getStringList("settings-inventory.description.chestclick").iterator();

                    while(var4.hasNext()) {
                        description = (String)var4.next();
                        item.addLoreLine(description);
                    }

                    this.settingFiller(setting, item);
                    inv.setItem(0, item.toItemStack());
                }

                if (setting.getName().equals("GlassBorder")) {
                    var4 = PotSG.getInstance().getConfiguration("inventory").getStringList("settings-inventory.description.glass-border").iterator();

                    while(var4.hasNext()) {
                        description = (String)var4.next();
                        item.addLoreLine(description);
                    }

                    this.settingFiller(setting, item);
                    inv.setItem(2, item.toItemStack());
                }

                if (setting.getName().equals("Scoreboard")) {
                    var4 = PotSG.getInstance().getConfiguration("inventory").getStringList("settings-inventory.description.scoreboard").iterator();

                    while(var4.hasNext()) {
                        description = (String)var4.next();
                        item.addLoreLine(description);
                    }

                    this.settingFiller(setting, item);
                    inv.setItem(4, item.toItemStack());
                }

                if (setting.getName().equals("Lightning-On-Kill")) {
                    var4 = PotSG.getInstance().getConfiguration("inventory").getStringList("settings-inventory.description.lightning-on-kill").iterator();

                    while(var4.hasNext()) {
                        description = (String)var4.next();
                        item.addLoreLine(description);
                    }

                    this.settingFiller(setting, item);
                    inv.setItem(6, item.toItemStack());
                }

                if (setting.getName().equals("Chest-Auto-Pickup")) {
                    var4 = PotSG.getInstance().getConfiguration("inventory").getStringList("settings-inventory.description.chest-auto-pickup").iterator();

                    while(var4.hasNext()) {
                        description = (String)var4.next();
                        item.addLoreLine(description);
                    }

                    this.settingWithPointsFiller(setting, item);
                    inv.setItem(8, item.toItemStack());
                }
            }

        });
        return inv;
    }

    private void settingFiller(Setting setting, ItemBuilder item) {
        Iterator var3 = PotSG.getInstance().getConfiguration("inventory").getStringList("settings-inventory.info-status").iterator();

        String string;
        while(var3.hasNext()) {
            string = (String)var3.next();
            item.addLoreLine(string.replaceAll("<name>", String.valueOf(setting.getName())).replaceAll("<settings_status>", setting.isEnabled() ? PotSG.getInstance().getConfiguration("inventory").getString("enabled") : PotSG.getInstance().getConfiguration("inventory").getString("disabled")));
        }

        var3 = PotSG.getInstance().getConfiguration("inventory").getStringList("settings-inventory.settings-line").iterator();

        while(var3.hasNext()) {
            string = (String)var3.next();
            item.addLoreLine(string);
        }

    }

    private void settingWithPointsFiller(Setting setting, ItemBuilder item) {
        int required = setting.getRequiredPoints() - this.getPoints().getAmount();
        Iterator var4 = PotSG.getInstance().getConfiguration("inventory").getStringList("settings-inventory.info-status").iterator();

        String string;
        while(var4.hasNext()) {
            string = (String)var4.next();
            item.addLoreLine(string.replaceAll("<name>", String.valueOf(setting.getName())).replaceAll("<settings_status>", setting.isEnabled() ? PotSG.getInstance().getConfiguration("inventory").getString("enabled") : PotSG.getInstance().getConfiguration("inventory").getString("disabled")));
        }

        if (this.getPoints().getAmount() < setting.getRequiredPoints()) {
            var4 = PotSG.getInstance().getConfiguration("inventory").getStringList("settings-inventory.not-enough-points-lore").iterator();

            while(var4.hasNext()) {
                string = (String)var4.next();
                item.addLoreLine(string.replaceAll("<required_points>", String.valueOf(setting.getRequiredPoints())).replaceAll("<needed_points>", String.valueOf(required)));
            }
        }

        var4 = PotSG.getInstance().getConfiguration("inventory").getStringList("settings-inventory.settings-line").iterator();

        while(var4.hasNext()) {
            string = (String)var4.next();
            item.addLoreLine(string);
        }

    }

    public double getKdr() {
        double kd;
        if (this.kills.getAmount() > 0 && this.deaths.getAmount() == 0) {
            kd = (double)this.kills.getAmount();
        } else if (this.kills.getAmount() == 0 && this.deaths.getAmount() == 0) {
            kd = 0.0;
        } else {
            kd = (double)(this.kills.getAmount() / this.deaths.getAmount());
        }

        return kd;
    }

    public boolean hasData() {
        Document document = (Document)MongoManager.getInstance().getStatsCollection().find(Filters.eq("info", GameManager.getInstance().isServerPremium() ? this.uuid : this.name)).first();
        return document != null;
    }

    public void load() {
        if (this.hasData()) {
            Document document = (Document)MongoManager.getInstance().getStatsCollection().find(Filters.eq("info", GameManager.getInstance().isServerPremium() ? this.uuid : this.name)).first();
            this.kills.setAmount(document.getInteger("kills"));
            this.deaths.setAmount(document.getInteger("deaths"));
            this.wins.setAmount(document.getInteger("wins"));
            this.gamesPlayed.setAmount(document.getInteger("gamesPlayed"));
            this.points.setAmount(document.getInteger("points"));
            this.goldenApplesEaten.setAmount(document.getInteger("goldenApplesEaten"));
            this.bowShots.setAmount(document.getInteger("bowShots"));
            this.killStreak.setAmount(document.getInteger("killStreak"));
            this.chestBroke.setAmount(document.getInteger("chestBroke"));
            this.potionSplashed.setAmount(document.getInteger("potionSplashed"));
            this.potionDrank.setAmount(document.getInteger("potionDrank"));

            try {
                this.settings.forEach((setting) -> {
                    setting.setEnabled(document.getBoolean(setting.getName()));
                });
            } catch (Exception var3) {
                this.save();
            }

        }
    }

    public void save() {
        Document document = new Document("info", GameManager.getInstance().isServerPremium() ? this.uuid : this.name);
        if (!this.hasData()) {
            document.put("name", this.name);
            document.put("kills", this.kills.getAmount());
            document.put("deaths", this.deaths.getAmount());
            document.put("wins", this.wins.getAmount());
            document.put("gamesPlayed", this.gamesPlayed.getAmount());
            document.put("points", this.points.getAmount());
            document.put("goldenApplesEaten", this.goldenApplesEaten.getAmount());
            document.put("bowShots", this.bowShots.getAmount());
            document.put("killStreak", this.killStreak.getAmount());
            document.put("chestBroke", this.chestBroke.getAmount());
            document.put("potionSplashed", this.potionSplashed.getAmount());
            document.put("potionDrank", this.potionDrank.getAmount());
            this.settings.forEach((setting) -> {
                document.put(setting.getName(), setting.isEnabled());
            });
            MongoManager.getInstance().getStatsCollection().insertOne(document);
        } else {
            document.put("name", this.name);
            document.put("kills", this.kills.getAmount());
            document.put("deaths", this.deaths.getAmount());
            document.put("wins", this.wins.getAmount());
            document.put("gamesPlayed", this.gamesPlayed.getAmount());
            document.put("points", this.points.getAmount());
            document.put("goldenApplesEaten", this.goldenApplesEaten.getAmount());
            document.put("bowShots", this.bowShots.getAmount());
            document.put("killStreak", this.killStreak.getAmount());
            document.put("chestBroke", this.chestBroke.getAmount());
            document.put("potionSplashed", this.potionSplashed.getAmount());
            document.put("potionDrank", this.potionDrank.getAmount());
            this.settings.forEach((setting) -> {
                document.put(setting.getName(), setting.isEnabled());
            });
            MongoManager.getInstance().getStatsCollection().replaceOne(Filters.eq("info", GameManager.getInstance().isServerPremium() ? this.uuid : this.name), document);
        }

    }

    public PlayerState getState() {
        return this.state;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public Stat getKills() {
        return this.kills;
    }

    public Stat getDeaths() {
        return this.deaths;
    }

    public Stat getWins() {
        return this.wins;
    }

    public Stat getGamesPlayed() {
        return this.gamesPlayed;
    }

    public Stat getPoints() {
        return this.points;
    }

    public Stat getGoldenApplesEaten() {
        return this.goldenApplesEaten;
    }

    public Stat getBowShots() {
        return this.bowShots;
    }

    public Stat getKillStreak() {
        return this.killStreak;
    }

    public Stat getGameKills() {
        return this.gameKills;
    }

    public Stat getChestBroke() {
        return this.chestBroke;
    }

    public Stat getPotionSplashed() {
        return this.potionSplashed;
    }

    public Stat getPotionDrank() {
        return this.potionDrank;
    }

    public Location getLastChestOpenedLocation() {
        return this.lastChestOpenedLocation;
    }

    public Cooldown getEnderpearlCooldown() {
        return this.enderpearlCooldown;
    }

    public Cooldown getCombatCooldown() {
        return this.combatCooldown;
    }

    public RespawnInfo getRespawnInfo() {
        return this.respawnInfo;
    }

    public boolean isSpecChat() {
        return this.specChat;
    }

    public List<Setting> getSettings() {
        return this.settings;
    }

    public void setState(PlayerState state) {
        this.state = state;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setKills(Stat kills) {
        this.kills = kills;
    }

    public void setDeaths(Stat deaths) {
        this.deaths = deaths;
    }

    public void setWins(Stat wins) {
        this.wins = wins;
    }

    public void setGamesPlayed(Stat gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public void setPoints(Stat points) {
        this.points = points;
    }

    public void setGoldenApplesEaten(Stat goldenApplesEaten) {
        this.goldenApplesEaten = goldenApplesEaten;
    }

    public void setBowShots(Stat bowShots) {
        this.bowShots = bowShots;
    }

    public void setKillStreak(Stat killStreak) {
        this.killStreak = killStreak;
    }

    public void setGameKills(Stat gameKills) {
        this.gameKills = gameKills;
    }

    public void setChestBroke(Stat chestBroke) {
        this.chestBroke = chestBroke;
    }

    public void setPotionSplashed(Stat potionSplashed) {
        this.potionSplashed = potionSplashed;
    }

    public void setPotionDrank(Stat potionDrank) {
        this.potionDrank = potionDrank;
    }

    public void setLastChestOpenedLocation(Location lastChestOpenedLocation) {
        this.lastChestOpenedLocation = lastChestOpenedLocation;
    }

    public void setEnderpearlCooldown(Cooldown enderpearlCooldown) {
        this.enderpearlCooldown = enderpearlCooldown;
    }

    public void setCombatCooldown(Cooldown combatCooldown) {
        this.combatCooldown = combatCooldown;
    }

    public void setRespawnInfo(RespawnInfo respawnInfo) {
        this.respawnInfo = respawnInfo;
    }

    public void setSpecChat(boolean specChat) {
        this.specChat = specChat;
    }

    public void setSettings(List<Setting> settings) {
        this.settings = settings;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof PlayerData)) {
            return false;
        } else {
            PlayerData other = (PlayerData)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label255: {
                    Object this$state = this.getState();
                    Object other$state = other.getState();
                    if (this$state == null) {
                        if (other$state == null) {
                            break label255;
                        }
                    } else if (this$state.equals(other$state)) {
                        break label255;
                    }

                    return false;
                }

                Object this$uuid = this.getUuid();
                Object other$uuid = other.getUuid();
                if (this$uuid == null) {
                    if (other$uuid != null) {
                        return false;
                    }
                } else if (!this$uuid.equals(other$uuid)) {
                    return false;
                }

                Object this$name = this.getName();
                Object other$name = other.getName();
                if (this$name == null) {
                    if (other$name != null) {
                        return false;
                    }
                } else if (!this$name.equals(other$name)) {
                    return false;
                }

                label234: {
                    Object this$kills = this.getKills();
                    Object other$kills = other.getKills();
                    if (this$kills == null) {
                        if (other$kills == null) {
                            break label234;
                        }
                    } else if (this$kills.equals(other$kills)) {
                        break label234;
                    }

                    return false;
                }

                label227: {
                    Object this$deaths = this.getDeaths();
                    Object other$deaths = other.getDeaths();
                    if (this$deaths == null) {
                        if (other$deaths == null) {
                            break label227;
                        }
                    } else if (this$deaths.equals(other$deaths)) {
                        break label227;
                    }

                    return false;
                }

                Object this$wins = this.getWins();
                Object other$wins = other.getWins();
                if (this$wins == null) {
                    if (other$wins != null) {
                        return false;
                    }
                } else if (!this$wins.equals(other$wins)) {
                    return false;
                }

                Object this$gamesPlayed = this.getGamesPlayed();
                Object other$gamesPlayed = other.getGamesPlayed();
                if (this$gamesPlayed == null) {
                    if (other$gamesPlayed != null) {
                        return false;
                    }
                } else if (!this$gamesPlayed.equals(other$gamesPlayed)) {
                    return false;
                }

                label206: {
                    Object this$points = this.getPoints();
                    Object other$points = other.getPoints();
                    if (this$points == null) {
                        if (other$points == null) {
                            break label206;
                        }
                    } else if (this$points.equals(other$points)) {
                        break label206;
                    }

                    return false;
                }

                label199: {
                    Object this$goldenApplesEaten = this.getGoldenApplesEaten();
                    Object other$goldenApplesEaten = other.getGoldenApplesEaten();
                    if (this$goldenApplesEaten == null) {
                        if (other$goldenApplesEaten == null) {
                            break label199;
                        }
                    } else if (this$goldenApplesEaten.equals(other$goldenApplesEaten)) {
                        break label199;
                    }

                    return false;
                }

                Object this$bowShots = this.getBowShots();
                Object other$bowShots = other.getBowShots();
                if (this$bowShots == null) {
                    if (other$bowShots != null) {
                        return false;
                    }
                } else if (!this$bowShots.equals(other$bowShots)) {
                    return false;
                }

                label185: {
                    Object this$killStreak = this.getKillStreak();
                    Object other$killStreak = other.getKillStreak();
                    if (this$killStreak == null) {
                        if (other$killStreak == null) {
                            break label185;
                        }
                    } else if (this$killStreak.equals(other$killStreak)) {
                        break label185;
                    }

                    return false;
                }

                Object this$gameKills = this.getGameKills();
                Object other$gameKills = other.getGameKills();
                if (this$gameKills == null) {
                    if (other$gameKills != null) {
                        return false;
                    }
                } else if (!this$gameKills.equals(other$gameKills)) {
                    return false;
                }

                label171: {
                    Object this$chestBroke = this.getChestBroke();
                    Object other$chestBroke = other.getChestBroke();
                    if (this$chestBroke == null) {
                        if (other$chestBroke == null) {
                            break label171;
                        }
                    } else if (this$chestBroke.equals(other$chestBroke)) {
                        break label171;
                    }

                    return false;
                }

                Object this$potionSplashed = this.getPotionSplashed();
                Object other$potionSplashed = other.getPotionSplashed();
                if (this$potionSplashed == null) {
                    if (other$potionSplashed != null) {
                        return false;
                    }
                } else if (!this$potionSplashed.equals(other$potionSplashed)) {
                    return false;
                }

                Object this$potionDrank = this.getPotionDrank();
                Object other$potionDrank = other.getPotionDrank();
                if (this$potionDrank == null) {
                    if (other$potionDrank != null) {
                        return false;
                    }
                } else if (!this$potionDrank.equals(other$potionDrank)) {
                    return false;
                }

                label150: {
                    Object this$lastChestOpenedLocation = this.getLastChestOpenedLocation();
                    Object other$lastChestOpenedLocation = other.getLastChestOpenedLocation();
                    if (this$lastChestOpenedLocation == null) {
                        if (other$lastChestOpenedLocation == null) {
                            break label150;
                        }
                    } else if (this$lastChestOpenedLocation.equals(other$lastChestOpenedLocation)) {
                        break label150;
                    }

                    return false;
                }

                label143: {
                    Object this$enderpearlCooldown = this.getEnderpearlCooldown();
                    Object other$enderpearlCooldown = other.getEnderpearlCooldown();
                    if (this$enderpearlCooldown == null) {
                        if (other$enderpearlCooldown == null) {
                            break label143;
                        }
                    } else if (this$enderpearlCooldown.equals(other$enderpearlCooldown)) {
                        break label143;
                    }

                    return false;
                }

                Object this$combatCooldown = this.getCombatCooldown();
                Object other$combatCooldown = other.getCombatCooldown();
                if (this$combatCooldown == null) {
                    if (other$combatCooldown != null) {
                        return false;
                    }
                } else if (!this$combatCooldown.equals(other$combatCooldown)) {
                    return false;
                }

                Object this$respawnInfo = this.getRespawnInfo();
                Object other$respawnInfo = other.getRespawnInfo();
                if (this$respawnInfo == null) {
                    if (other$respawnInfo != null) {
                        return false;
                    }
                } else if (!this$respawnInfo.equals(other$respawnInfo)) {
                    return false;
                }

                if (this.isSpecChat() != other.isSpecChat()) {
                    return false;
                } else {
                    Object this$settings = this.getSettings();
                    Object other$settings = other.getSettings();
                    if (this$settings == null) {
                        if (other$settings != null) {
                            return false;
                        }
                    } else if (!this$settings.equals(other$settings)) {
                        return false;
                    }

                    return true;
                }
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof PlayerData;
    }

    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        Object $state = this.getState();
        result = result * 59 + ($state == null ? 43 : $state.hashCode());
        Object $uuid = this.getUuid();
        result = result * 59 + ($uuid == null ? 43 : $uuid.hashCode());
        Object $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        Object $kills = this.getKills();
        result = result * 59 + ($kills == null ? 43 : $kills.hashCode());
        Object $deaths = this.getDeaths();
        result = result * 59 + ($deaths == null ? 43 : $deaths.hashCode());
        Object $wins = this.getWins();
        result = result * 59 + ($wins == null ? 43 : $wins.hashCode());
        Object $gamesPlayed = this.getGamesPlayed();
        result = result * 59 + ($gamesPlayed == null ? 43 : $gamesPlayed.hashCode());
        Object $points = this.getPoints();
        result = result * 59 + ($points == null ? 43 : $points.hashCode());
        Object $goldenApplesEaten = this.getGoldenApplesEaten();
        result = result * 59 + ($goldenApplesEaten == null ? 43 : $goldenApplesEaten.hashCode());
        Object $bowShots = this.getBowShots();
        result = result * 59 + ($bowShots == null ? 43 : $bowShots.hashCode());
        Object $killStreak = this.getKillStreak();
        result = result * 59 + ($killStreak == null ? 43 : $killStreak.hashCode());
        Object $gameKills = this.getGameKills();
        result = result * 59 + ($gameKills == null ? 43 : $gameKills.hashCode());
        Object $chestBroke = this.getChestBroke();
        result = result * 59 + ($chestBroke == null ? 43 : $chestBroke.hashCode());
        Object $potionSplashed = this.getPotionSplashed();
        result = result * 59 + ($potionSplashed == null ? 43 : $potionSplashed.hashCode());
        Object $potionDrank = this.getPotionDrank();
        result = result * 59 + ($potionDrank == null ? 43 : $potionDrank.hashCode());
        Object $lastChestOpenedLocation = this.getLastChestOpenedLocation();
        result = result * 59 + ($lastChestOpenedLocation == null ? 43 : $lastChestOpenedLocation.hashCode());
        Object $enderpearlCooldown = this.getEnderpearlCooldown();
        result = result * 59 + ($enderpearlCooldown == null ? 43 : $enderpearlCooldown.hashCode());
        Object $combatCooldown = this.getCombatCooldown();
        result = result * 59 + ($combatCooldown == null ? 43 : $combatCooldown.hashCode());
        Object $respawnInfo = this.getRespawnInfo();
        result = result * 59 + ($respawnInfo == null ? 43 : $respawnInfo.hashCode());
        result = result * 59 + (this.isSpecChat() ? 79 : 97);
        Object $settings = this.getSettings();
        result = result * 59 + ($settings == null ? 43 : $settings.hashCode());
        return result;
    }

    public String toString() {
        return "PlayerData(state=" + this.getState() + ", uuid=" + this.getUuid() + ", name=" + this.getName() + ", kills=" + this.getKills() + ", deaths=" + this.getDeaths() + ", wins=" + this.getWins() + ", gamesPlayed=" + this.getGamesPlayed() + ", points=" + this.getPoints() + ", goldenApplesEaten=" + this.getGoldenApplesEaten() + ", bowShots=" + this.getBowShots() + ", killStreak=" + this.getKillStreak() + ", gameKills=" + this.getGameKills() + ", chestBroke=" + this.getChestBroke() + ", potionSplashed=" + this.getPotionSplashed() + ", potionDrank=" + this.getPotionDrank() + ", lastChestOpenedLocation=" + this.getLastChestOpenedLocation() + ", enderpearlCooldown=" + this.getEnderpearlCooldown() + ", combatCooldown=" + this.getCombatCooldown() + ", respawnInfo=" + this.getRespawnInfo() + ", specChat=" + this.isSpecChat() + ", settings=" + this.getSettings() + ")";
    }
}
