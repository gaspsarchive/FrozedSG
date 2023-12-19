package club.frozed.frozedsg.managers;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.enums.GameState;
import club.frozed.frozedsg.player.PlayerData;
import club.frozed.frozedsg.utils.ItemBuilder;
import club.frozed.frozedsg.utils.Utils;
import club.frozed.frozedsg.utils.chat.Color;
import club.frozed.frozedsg.utils.countdowns.*;
import club.frozed.frozedsg.utils.runnables.GameRunnable;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameManager
{
    public static GameManager instance;
    private GameState gameState;
    private String gamePrefix;
    private String borderPrefix;
    private String tsInfo;
    private String webInfo;
    private String ipInfo;
    private String storeInfo;
    private String serverName;
    private int maxPlayers;
    private int minPlayers;
    private int startCountdownValue;
    private int prematchCountdownValue;
    private int pvpCountdownValue;
    private int feastsCountdownValue;
    private int deathMatchCountdownValue;
    private int rebootCountdownValue;
    private int pointsPerKill;
    private int pointsPerWin;
    private String rebootCommand;
    private StartCountdown startCountdown;
    private PrematchCountdown prematchCountdown;
    private PvPCountdown pvpCountdown;
    private FeastCountdown feastCountdown;
    private GameRunnable gameRunnable;
    private DeathMatchCountdown deathMatchCountdown;
    private RebootCountdown rebootCountdown;
    private boolean forceStarted;
    private boolean toUseLobby;
    private boolean serverPremium;
    private String lobbyFallbackServer;
    private String winner;
    private int winnerKills;
    private int winnerTotalKills;
    private boolean toCancelFirework;
    private static MetadataValue META_KEY;
    private boolean deathMatchArenaSpawned;
    private Map<Location, BrewingStand> activeBrewingStands;
    
    public GameManager() {
        this.gameState = GameState.LOBBY;
        this.gamePrefix = Color.translate(PotSG.getInstance().getConfiguration("config").getString("PREFIXES.GAME"));
        this.borderPrefix = Color.translate(PotSG.getInstance().getConfiguration("config").getString("PREFIXES.BORDER"));
        this.tsInfo = PotSG.getInstance().getConfiguration("config").getString("INFORMATIONS.TS");
        this.webInfo = PotSG.getInstance().getConfiguration("config").getString("INFORMATIONS.WEB");
        this.ipInfo = PotSG.getInstance().getConfiguration("config").getString("INFORMATIONS.IP");
        this.storeInfo = PotSG.getInstance().getConfiguration("config").getString("INFORMATIONS.STORE");
        this.serverName = PotSG.getInstance().getConfiguration("config").getString("INFORMATIONS.SERVER");
        this.maxPlayers = PotSG.getInstance().getConfiguration("config").getInt("MAXIMUM-PLAYERS-PER-GAME");
        this.minPlayers = PotSG.getInstance().getConfiguration("config").getInt("MINIMUM-PLAYERS-TO-START-GAME");
        this.startCountdownValue = PotSG.getInstance().getConfiguration("config").getInt("COUNTDOWNS.IN-SECONDS.START");
        this.prematchCountdownValue = PotSG.getInstance().getConfiguration("config").getInt("COUNTDOWNS.IN-SECONDS.PRE-MATCH");
        this.pvpCountdownValue = PotSG.getInstance().getConfiguration("config").getInt("COUNTDOWNS.IN-SECONDS.PVP-PROT");
        this.feastsCountdownValue = PotSG.getInstance().getConfiguration("config").getInt("COUNTDOWNS.IN-MINUTES.FEASTS-SPAWN");
        this.deathMatchCountdownValue = PotSG.getInstance().getConfiguration("config").getInt("COUNTDOWNS.IN-MINUTES.DEATH-MATCH");
        this.rebootCountdownValue = PotSG.getInstance().getConfiguration("config").getInt("COUNTDOWNS.IN-SECONDS.REBOOT");
        this.pointsPerKill = PotSG.getInstance().getConfiguration("config").getInt("POINTS.PER-KILL");
        this.pointsPerWin = PotSG.getInstance().getConfiguration("config").getInt("POINTS.PER-WIN");
        this.rebootCommand = PotSG.getInstance().getConfiguration("config").getString("REBOOT_COMMAND");
        this.startCountdown = null;
        this.prematchCountdown = null;
        this.pvpCountdown = null;
        this.feastCountdown = null;
        this.gameRunnable = null;
        this.deathMatchCountdown = null;
        this.forceStarted = false;
        this.toUseLobby = PotSG.getInstance().getConfiguration("config").getBoolean("BOOLEANS.LOBBY-ENABLED");
        this.serverPremium = PotSG.getInstance().getConfiguration("config").getBoolean("SERVER-PREMIUM");
        this.lobbyFallbackServer = PotSG.getInstance().getConfiguration("config").getString("LOBBY-FALLBACK-SERVER");
        this.winner = "";
        this.winnerKills = 0;
        this.winnerTotalKills = 0;
        this.toCancelFirework = false;
        this.deathMatchArenaSpawned = false;
        this.activeBrewingStands = new HashMap<Location, BrewingStand>();
        GameManager.instance = this;
    }
    
    public void handleLobbyItems(final Player player) {
        final ItemBuilder statistics = new ItemBuilder(Material.EMERALD);
        statistics.setNameWithArrows(PotSG.getInstance().getConfiguration("items").getString("join-inventory.leaderboard.name"));
        for (final String lore : PotSG.getInstance().getConfiguration("items").getStringList("join-inventory.leaderboard.lore")) {
            statistics.addLoreLine(lore);
        }
        final ItemBuilder players = new ItemBuilder(Material.WATCH);
        players.setNameWithArrows(PotSG.getInstance().getConfiguration("items").getString("join-inventory.player-stats.name"));
        for (final String lore2 : PotSG.getInstance().getConfiguration("items").getStringList("join-inventory.player-stats.lore")) {
            players.addLoreLine(lore2);
        }
        final ItemBuilder settings = new ItemBuilder(Material.CHEST);
        settings.setNameWithArrows(PotSG.getInstance().getConfiguration("items").getString("join-inventory.settings.name"));
        for (final String lore3 : PotSG.getInstance().getConfiguration("items").getStringList("join-inventory.settings.lore")) {
            settings.addLoreLine(lore3);
        }
        final ItemBuilder stats = new ItemBuilder(Material.SKULL_ITEM).setDurability(3);
        stats.setNameWithArrows(PotSG.getInstance().getConfiguration("items").getString("join-inventory.your-stats.name"));
        for (final String lore4 : PotSG.getInstance().getConfiguration("items").getStringList("join-inventory.your-stats.lore")) {
            stats.addLoreLine(lore4);
        }
        player.getInventory().setItem(0, statistics.toItemStack());
        player.getInventory().setItem(3, players.toItemStack());
        player.getInventory().setItem(5, stats.toItemStack());
        player.getInventory().setItem(8, settings.toItemStack());
    }
    
    public Location getLobbyLocation() {
        final Location lobbyLoc = WorldsManager.getInstance().getLobbyWorld().getSpawnLocation();
        lobbyLoc.setYaw((float)PotSG.getInstance().getConfiguration("config").getInt("LOCATIONS.LOBBY.YAW"));
        lobbyLoc.setPitch((float)PotSG.getInstance().getConfiguration("config").getInt("LOCATIONS.LOBBY.PITCH"));
        return lobbyLoc;
    }
    
    public Location getGameWorldCenterLocation() {
        if (!this.isGameCenterLocationValid()) {
            final int x = 0;
            final int z = 0;
            final double y = WorldsManager.getInstance().getGameWorld().getHighestBlockYAt(x, z);
            return new Location(WorldsManager.getInstance().getGameWorld(), (double)x, y, (double)z);
        }
        final String input = PotSG.getInstance().getConfiguration("config").getString("GAME-CENTER-LOCATION");
        final String[] coords = input.split(";");
        final int x2 = Integer.parseInt(coords[0]);
        final int y2 = Integer.parseInt(coords[1]);
        final int z2 = Integer.parseInt(coords[2]);
        return new Location(WorldsManager.getInstance().getGameWorld(), (double)x2, (double)y2, (double)z2);
    }
    
    private boolean isGameCenterLocationValid() {
        final String input = PotSG.getInstance().getConfiguration("config").getString("GAME-CENTER-LOCATION");
        final String[] split;
        final String[] coords = split = input.split(";");
        for (final String coord : split) {
            if (!Utils.isInteger(coord)) {
                return false;
            }
        }
        return coords.length >= 3;
    }
    
    public void startGame() {
        this.setStartCountdown(new StartCountdown());
    }
    
    public int getRequiredPlayersToJoin() {
        return this.getMinPlayers() - PlayerManager.getInstance().getLobbyPlayers().size();
    }
    
    public ItemStack getChestItem() {
        final ItemBuilder item = new ItemBuilder(Material.CHEST);
        item.setName("&aChest");
        item.addLoreLine("&7Place me on some random location and thats all.");
        item.addLoreLine("&7Please don't put items in me.");
        item.addLoreLine("&7I will put random chest items from &f'/game chests'");
        item.addLoreLine("&7You can edit chests there.");
        item.addLoreLine(" ");
        item.addLoreLine("&7&oNote: &4&lI MUST BE EMPTY IF YOU WANT ME TO WORK PROPERLY!");
        return item.toItemStack();
    }
    
    public List<PlayerData> getTop5GameKills() {
        return PlayerDataManager.getInstance().getPlayerDatas().values().stream().filter(playerData -> playerData.getGameKills().getAmount() > 0).limit(5L).sorted().collect(Collectors.toList());
    }
    
    public void spawnFeast() {
        final World world = WorldsManager.getInstance().getGameWorld();
        for (int x = -8; x < 8; ++x) {
            for (int z = -8; z < 8; ++z) {
                if (RandomUtils.nextInt(20) == 0) {
                    final Block block = world.getBlockAt(x + 8, world.getHighestBlockAt(x, z).getY(), z + 8);
                    if (block.getRelative(BlockFace.NORTH).getType() != Material.CHEST && block.getRelative(BlockFace.SOUTH).getType() != Material.CHEST && block.getRelative(BlockFace.EAST).getType() != Material.CHEST && block.getRelative(BlockFace.WEST).getType() != Material.CHEST) {
                        final FallingBlock fallingBlock = world.spawnFallingBlock(block.getLocation().add(0.0, 30.0, 0.0), Material.CHEST, (byte)54);
                        fallingBlock.setMetadata("fallingBlock", GameManager.META_KEY);
                    }
                }
            }
        }
        world.getBlockAt(8, world.getHighestBlockAt(0, 0).getY(), 8).setType(Material.ENCHANTMENT_TABLE);
    }
    
    public void clearFlat(final Location loc, final int r) {
        final int cx = loc.getBlockX();
        final int cy = loc.getBlockY();
        final int cz = loc.getBlockZ();
        final World w = loc.getWorld();
        for (int x = cx - r; x <= cx + r; ++x) {
            for (int y = cy; y <= cy + r / 2; ++y) {
                for (int z = cz - r; z <= cz + r; ++z) {
                    w.getBlockAt(x, y, z).setType(Material.AIR);
                }
            }
        }
    }
    
    public void spawnDeathMatchArena() {
        final List<Material> floorMaterials = new ArrayList<Material>();
        final List<Material> WallbeetweenMaterials = new ArrayList<Material>();
        final List<Material> WallupMaterials = new ArrayList<Material>();
        final List<Material> WalldownMaterials = new ArrayList<Material>();
        try {
            PotSG.getInstance().getConfiguration("config").getStringList("DEATH-MATCH.FLOOR-MATERIALS").forEach(mat -> floorMaterials.add(Material.valueOf(mat)));
            PotSG.getInstance().getConfiguration("config").getStringList("DEATH-MATCH.WALL-MATERIALS.BEETWEEN").forEach(mat -> WallbeetweenMaterials.add(Material.valueOf(mat)));
            PotSG.getInstance().getConfiguration("config").getStringList("DEATH-MATCH.WALL-MATERIALS.TOP").forEach(mat -> WallupMaterials.add(Material.valueOf(mat)));
            PotSG.getInstance().getConfiguration("config").getStringList("DEATH-MATCH.WALL-MATERIALS.BOTTOM").forEach(mat -> WalldownMaterials.add(Material.valueOf(mat)));
        }
        catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + e.getCause().getMessage());
            Bukkit.getConsoleSender().sendMessage(Color.translate("&cAn error ocured while spawning deathmatch arena. Please check your materials!"));
        }
        final Location oldloc = WorldsManager.getInstance().getGameWorld().getBlockAt(0, WorldsManager.getInstance().getGameWorld().getHighestBlockYAt(0, 0) - 2, 0).getLocation();
        this.clearFlat(oldloc, 16);
        final int r = 15;
        final int yTop = WorldsManager.getInstance().getGameWorld().getHighestBlockYAt(0, 0);
        final Location loc = WorldsManager.getInstance().getGameWorld().getBlockAt(0, yTop, 0).getLocation();
        final World w = loc.getWorld();
        final int cx = loc.getBlockX();
        final int cy = loc.getBlockY();
        final int cz = loc.getBlockZ();
        for (int x = cx - r; x <= cx + r; ++x) {
            for (int y = cy; y <= cy; ++y) {
                for (int z = cz - r; z <= cz + r; ++z) {
                    w.getBlockAt(x, y - 1, z).setType(Material.BEDROCK);
                    w.getBlockAt(x, y, z).setType(this.getRandomMaterial(floorMaterials));
                }
            }
        }
        final int size = 15;
        final Location centerLocation = new Location(w, 0.0, (double)w.getHighestBlockYAt(0, 0), 0.0);
        for (int i = 1; i < 2; ++i) {
            for (int x2 = centerLocation.getBlockX() - size; x2 <= centerLocation.getBlockX() + size; ++x2) {
                for (int y2 = 58; y2 <= 58; ++y2) {
                    for (int z2 = centerLocation.getBlockZ() - size; z2 <= centerLocation.getBlockZ() + size; ++z2) {
                        if (x2 == centerLocation.getBlockX() - size || x2 == centerLocation.getBlockX() + size || z2 == centerLocation.getBlockZ() - size || z2 == loc.getBlockZ() + size) {
                            final Location loc2 = new Location(w, (double)x2, (double)y2, (double)z2);
                            loc2.setY((double)w.getHighestBlockYAt(loc2));
                            loc2.getBlock().setType(this.getRandomMaterial(WalldownMaterials));
                        }
                    }
                }
            }
        }
        for (int i = 4; i < 8; ++i) {
            for (int x2 = centerLocation.getBlockX() - size; x2 <= centerLocation.getBlockX() + size; ++x2) {
                for (int y2 = 58; y2 <= 58; ++y2) {
                    for (int z2 = centerLocation.getBlockZ() - size; z2 <= centerLocation.getBlockZ() + size; ++z2) {
                        if (x2 == centerLocation.getBlockX() - size || x2 == centerLocation.getBlockX() + size || z2 == centerLocation.getBlockZ() - size || z2 == loc.getBlockZ() + size) {
                            final Location loc2 = new Location(w, (double)x2, (double)y2, (double)z2);
                            loc2.setY((double)w.getHighestBlockYAt(loc2));
                            loc2.getBlock().setType(this.getRandomMaterial(WallbeetweenMaterials));
                        }
                    }
                }
            }
        }
        for (int i = 1; i < 2; ++i) {
            for (int x2 = centerLocation.getBlockX() - size; x2 <= centerLocation.getBlockX() + size; ++x2) {
                for (int y2 = 58; y2 <= 58; ++y2) {
                    for (int z2 = centerLocation.getBlockZ() - size; z2 <= centerLocation.getBlockZ() + size; ++z2) {
                        if (x2 == centerLocation.getBlockX() - size || x2 == centerLocation.getBlockX() + size || z2 == centerLocation.getBlockZ() - size || z2 == loc.getBlockZ() + size) {
                            final Location loc2 = new Location(w, (double)x2, (double)y2, (double)z2);
                            loc2.setY((double)w.getHighestBlockYAt(loc2));
                            loc2.getBlock().setType(this.getRandomMaterial(WallupMaterials));
                        }
                    }
                }
            }
        }
        for (final Entity entites : w.getEntities()) {
            if (!(entites instanceof Player)) {
                entites.remove();
            }
        }
    }
    
    public Material getRandomMaterial(final List<Material> list) {
        final int r = RandomUtils.nextInt(list.size());
        return list.get(r);
    }
    
    public GameState getGameState() {
        return this.gameState;
    }
    
    public String getGamePrefix() {
        return this.gamePrefix;
    }
    
    public String getBorderPrefix() {
        return this.borderPrefix;
    }
    
    public String getTsInfo() {
        return this.tsInfo;
    }
    
    public String getWebInfo() {
        return this.webInfo;
    }
    
    public String getIpInfo() {
        return this.ipInfo;
    }
    
    public String getStoreInfo() {
        return this.storeInfo;
    }
    
    public String getServerName() {
        return this.serverName;
    }
    
    public int getMaxPlayers() {
        return this.maxPlayers;
    }
    
    public int getMinPlayers() {
        return this.minPlayers;
    }
    
    public int getStartCountdownValue() {
        return this.startCountdownValue;
    }
    
    public int getPrematchCountdownValue() {
        return this.prematchCountdownValue;
    }
    
    public int getPvpCountdownValue() {
        return this.pvpCountdownValue;
    }
    
    public int getFeastsCountdownValue() {
        return this.feastsCountdownValue;
    }
    
    public int getDeathMatchCountdownValue() {
        return this.deathMatchCountdownValue;
    }
    
    public int getRebootCountdownValue() {
        return this.rebootCountdownValue;
    }
    
    public int getPointsPerKill() {
        return this.pointsPerKill;
    }
    
    public int getPointsPerWin() {
        return this.pointsPerWin;
    }
    
    public String getRebootCommand() {
        return this.rebootCommand;
    }
    
    public StartCountdown getStartCountdown() {
        return this.startCountdown;
    }
    
    public PrematchCountdown getPrematchCountdown() {
        return this.prematchCountdown;
    }
    
    public PvPCountdown getPvpCountdown() {
        return this.pvpCountdown;
    }
    
    public FeastCountdown getFeastCountdown() {
        return this.feastCountdown;
    }
    
    public GameRunnable getGameRunnable() {
        return this.gameRunnable;
    }
    
    public DeathMatchCountdown getDeathMatchCountdown() {
        return this.deathMatchCountdown;
    }
    
    public RebootCountdown getRebootCountdown() {
        return this.rebootCountdown;
    }
    
    public boolean isForceStarted() {
        return this.forceStarted;
    }
    
    public boolean isToUseLobby() {
        return this.toUseLobby;
    }
    
    public boolean isServerPremium() {
        return this.serverPremium;
    }
    
    public String getLobbyFallbackServer() {
        return this.lobbyFallbackServer;
    }
    
    public String getWinner() {
        return this.winner;
    }
    
    public int getWinnerKills() {
        return this.winnerKills;
    }
    
    public int getWinnerTotalKills() {
        return this.winnerTotalKills;
    }
    
    public boolean isToCancelFirework() {
        return this.toCancelFirework;
    }
    
    public boolean isDeathMatchArenaSpawned() {
        return this.deathMatchArenaSpawned;
    }
    
    public Map<Location, BrewingStand> getActiveBrewingStands() {
        return this.activeBrewingStands;
    }
    
    public void setGameState(final GameState gameState) {
        this.gameState = gameState;
    }
    
    public void setGamePrefix(final String gamePrefix) {
        this.gamePrefix = gamePrefix;
    }
    
    public void setBorderPrefix(final String borderPrefix) {
        this.borderPrefix = borderPrefix;
    }
    
    public void setTsInfo(final String tsInfo) {
        this.tsInfo = tsInfo;
    }
    
    public void setWebInfo(final String webInfo) {
        this.webInfo = webInfo;
    }
    
    public void setIpInfo(final String ipInfo) {
        this.ipInfo = ipInfo;
    }
    
    public void setStoreInfo(final String storeInfo) {
        this.storeInfo = storeInfo;
    }
    
    public void setServerName(final String serverName) {
        this.serverName = serverName;
    }
    
    public void setMaxPlayers(final int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
    
    public void setMinPlayers(final int minPlayers) {
        this.minPlayers = minPlayers;
    }
    
    public void setStartCountdownValue(final int startCountdownValue) {
        this.startCountdownValue = startCountdownValue;
    }
    
    public void setPrematchCountdownValue(final int prematchCountdownValue) {
        this.prematchCountdownValue = prematchCountdownValue;
    }
    
    public void setPvpCountdownValue(final int pvpCountdownValue) {
        this.pvpCountdownValue = pvpCountdownValue;
    }
    
    public void setFeastsCountdownValue(final int feastsCountdownValue) {
        this.feastsCountdownValue = feastsCountdownValue;
    }
    
    public void setDeathMatchCountdownValue(final int deathMatchCountdownValue) {
        this.deathMatchCountdownValue = deathMatchCountdownValue;
    }
    
    public void setRebootCountdownValue(final int rebootCountdownValue) {
        this.rebootCountdownValue = rebootCountdownValue;
    }
    
    public void setPointsPerKill(final int pointsPerKill) {
        this.pointsPerKill = pointsPerKill;
    }
    
    public void setPointsPerWin(final int pointsPerWin) {
        this.pointsPerWin = pointsPerWin;
    }
    
    public void setRebootCommand(final String rebootCommand) {
        this.rebootCommand = rebootCommand;
    }
    
    public void setStartCountdown(final StartCountdown startCountdown) {
        this.startCountdown = startCountdown;
    }
    
    public void setPrematchCountdown(final PrematchCountdown prematchCountdown) {
        this.prematchCountdown = prematchCountdown;
    }
    
    public void setPvpCountdown(final PvPCountdown pvpCountdown) {
        this.pvpCountdown = pvpCountdown;
    }
    
    public void setFeastCountdown(final FeastCountdown feastCountdown) {
        this.feastCountdown = feastCountdown;
    }
    
    public void setGameRunnable(final GameRunnable gameRunnable) {
        this.gameRunnable = gameRunnable;
    }
    
    public void setDeathMatchCountdown(final DeathMatchCountdown deathMatchCountdown) {
        this.deathMatchCountdown = deathMatchCountdown;
    }
    
    public void setRebootCountdown(final RebootCountdown rebootCountdown) {
        this.rebootCountdown = rebootCountdown;
    }
    
    public void setForceStarted(final boolean forceStarted) {
        this.forceStarted = forceStarted;
    }
    
    public void setToUseLobby(final boolean toUseLobby) {
        this.toUseLobby = toUseLobby;
    }
    
    public void setServerPremium(final boolean serverPremium) {
        this.serverPremium = serverPremium;
    }
    
    public void setLobbyFallbackServer(final String lobbyFallbackServer) {
        this.lobbyFallbackServer = lobbyFallbackServer;
    }
    
    public void setWinner(final String winner) {
        this.winner = winner;
    }
    
    public void setWinnerKills(final int winnerKills) {
        this.winnerKills = winnerKills;
    }
    
    public void setWinnerTotalKills(final int winnerTotalKills) {
        this.winnerTotalKills = winnerTotalKills;
    }
    
    public void setToCancelFirework(final boolean toCancelFirework) {
        this.toCancelFirework = toCancelFirework;
    }
    
    public void setDeathMatchArenaSpawned(final boolean deathMatchArenaSpawned) {
        this.deathMatchArenaSpawned = deathMatchArenaSpawned;
    }
    
    public void setActiveBrewingStands(final Map<Location, BrewingStand> activeBrewingStands) {
        this.activeBrewingStands = activeBrewingStands;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof GameManager)) {
            return false;
        }
        final GameManager other = (GameManager)o;
        if (!other.canEqual(this)) {
            return false;
        }
        final Object this$gameState = this.getGameState();
        final Object other$gameState = other.getGameState();
        Label_0065: {
            if (this$gameState == null) {
                if (other$gameState == null) {
                    break Label_0065;
                }
            }
            else if (this$gameState.equals(other$gameState)) {
                break Label_0065;
            }
            return false;
        }
        final Object this$gamePrefix = this.getGamePrefix();
        final Object other$gamePrefix = other.getGamePrefix();
        Label_0102: {
            if (this$gamePrefix == null) {
                if (other$gamePrefix == null) {
                    break Label_0102;
                }
            }
            else if (this$gamePrefix.equals(other$gamePrefix)) {
                break Label_0102;
            }
            return false;
        }
        final Object this$borderPrefix = this.getBorderPrefix();
        final Object other$borderPrefix = other.getBorderPrefix();
        Label_0139: {
            if (this$borderPrefix == null) {
                if (other$borderPrefix == null) {
                    break Label_0139;
                }
            }
            else if (this$borderPrefix.equals(other$borderPrefix)) {
                break Label_0139;
            }
            return false;
        }
        final Object this$tsInfo = this.getTsInfo();
        final Object other$tsInfo = other.getTsInfo();
        Label_0176: {
            if (this$tsInfo == null) {
                if (other$tsInfo == null) {
                    break Label_0176;
                }
            }
            else if (this$tsInfo.equals(other$tsInfo)) {
                break Label_0176;
            }
            return false;
        }
        final Object this$webInfo = this.getWebInfo();
        final Object other$webInfo = other.getWebInfo();
        Label_0213: {
            if (this$webInfo == null) {
                if (other$webInfo == null) {
                    break Label_0213;
                }
            }
            else if (this$webInfo.equals(other$webInfo)) {
                break Label_0213;
            }
            return false;
        }
        final Object this$ipInfo = this.getIpInfo();
        final Object other$ipInfo = other.getIpInfo();
        Label_0250: {
            if (this$ipInfo == null) {
                if (other$ipInfo == null) {
                    break Label_0250;
                }
            }
            else if (this$ipInfo.equals(other$ipInfo)) {
                break Label_0250;
            }
            return false;
        }
        final Object this$storeInfo = this.getStoreInfo();
        final Object other$storeInfo = other.getStoreInfo();
        Label_0287: {
            if (this$storeInfo == null) {
                if (other$storeInfo == null) {
                    break Label_0287;
                }
            }
            else if (this$storeInfo.equals(other$storeInfo)) {
                break Label_0287;
            }
            return false;
        }
        final Object this$serverName = this.getServerName();
        final Object other$serverName = other.getServerName();
        Label_0324: {
            if (this$serverName == null) {
                if (other$serverName == null) {
                    break Label_0324;
                }
            }
            else if (this$serverName.equals(other$serverName)) {
                break Label_0324;
            }
            return false;
        }
        if (this.getMaxPlayers() != other.getMaxPlayers()) {
            return false;
        }
        if (this.getMinPlayers() != other.getMinPlayers()) {
            return false;
        }
        if (this.getStartCountdownValue() != other.getStartCountdownValue()) {
            return false;
        }
        if (this.getPrematchCountdownValue() != other.getPrematchCountdownValue()) {
            return false;
        }
        if (this.getPvpCountdownValue() != other.getPvpCountdownValue()) {
            return false;
        }
        if (this.getFeastsCountdownValue() != other.getFeastsCountdownValue()) {
            return false;
        }
        if (this.getDeathMatchCountdownValue() != other.getDeathMatchCountdownValue()) {
            return false;
        }
        if (this.getRebootCountdownValue() != other.getRebootCountdownValue()) {
            return false;
        }
        if (this.getPointsPerKill() != other.getPointsPerKill()) {
            return false;
        }
        if (this.getPointsPerWin() != other.getPointsPerWin()) {
            return false;
        }
        final Object this$rebootCommand = this.getRebootCommand();
        final Object other$rebootCommand = other.getRebootCommand();
        Label_0491: {
            if (this$rebootCommand == null) {
                if (other$rebootCommand == null) {
                    break Label_0491;
                }
            }
            else if (this$rebootCommand.equals(other$rebootCommand)) {
                break Label_0491;
            }
            return false;
        }
        final Object this$startCountdown = this.getStartCountdown();
        final Object other$startCountdown = other.getStartCountdown();
        Label_0528: {
            if (this$startCountdown == null) {
                if (other$startCountdown == null) {
                    break Label_0528;
                }
            }
            else if (this$startCountdown.equals(other$startCountdown)) {
                break Label_0528;
            }
            return false;
        }
        final Object this$prematchCountdown = this.getPrematchCountdown();
        final Object other$prematchCountdown = other.getPrematchCountdown();
        Label_0565: {
            if (this$prematchCountdown == null) {
                if (other$prematchCountdown == null) {
                    break Label_0565;
                }
            }
            else if (this$prematchCountdown.equals(other$prematchCountdown)) {
                break Label_0565;
            }
            return false;
        }
        final Object this$pvpCountdown = this.getPvpCountdown();
        final Object other$pvpCountdown = other.getPvpCountdown();
        Label_0602: {
            if (this$pvpCountdown == null) {
                if (other$pvpCountdown == null) {
                    break Label_0602;
                }
            }
            else if (this$pvpCountdown.equals(other$pvpCountdown)) {
                break Label_0602;
            }
            return false;
        }
        final Object this$feastCountdown = this.getFeastCountdown();
        final Object other$feastCountdown = other.getFeastCountdown();
        Label_0639: {
            if (this$feastCountdown == null) {
                if (other$feastCountdown == null) {
                    break Label_0639;
                }
            }
            else if (this$feastCountdown.equals(other$feastCountdown)) {
                break Label_0639;
            }
            return false;
        }
        final Object this$gameRunnable = this.getGameRunnable();
        final Object other$gameRunnable = other.getGameRunnable();
        Label_0676: {
            if (this$gameRunnable == null) {
                if (other$gameRunnable == null) {
                    break Label_0676;
                }
            }
            else if (this$gameRunnable.equals(other$gameRunnable)) {
                break Label_0676;
            }
            return false;
        }
        final Object this$deathMatchCountdown = this.getDeathMatchCountdown();
        final Object other$deathMatchCountdown = other.getDeathMatchCountdown();
        Label_0713: {
            if (this$deathMatchCountdown == null) {
                if (other$deathMatchCountdown == null) {
                    break Label_0713;
                }
            }
            else if (this$deathMatchCountdown.equals(other$deathMatchCountdown)) {
                break Label_0713;
            }
            return false;
        }
        final Object this$rebootCountdown = this.getRebootCountdown();
        final Object other$rebootCountdown = other.getRebootCountdown();
        Label_0750: {
            if (this$rebootCountdown == null) {
                if (other$rebootCountdown == null) {
                    break Label_0750;
                }
            }
            else if (this$rebootCountdown.equals(other$rebootCountdown)) {
                break Label_0750;
            }
            return false;
        }
        if (this.isForceStarted() != other.isForceStarted()) {
            return false;
        }
        if (this.isToUseLobby() != other.isToUseLobby()) {
            return false;
        }
        if (this.isServerPremium() != other.isServerPremium()) {
            return false;
        }
        final Object this$lobbyFallbackServer = this.getLobbyFallbackServer();
        final Object other$lobbyFallbackServer = other.getLobbyFallbackServer();
        Label_0826: {
            if (this$lobbyFallbackServer == null) {
                if (other$lobbyFallbackServer == null) {
                    break Label_0826;
                }
            }
            else if (this$lobbyFallbackServer.equals(other$lobbyFallbackServer)) {
                break Label_0826;
            }
            return false;
        }
        final Object this$winner = this.getWinner();
        final Object other$winner = other.getWinner();
        Label_0863: {
            if (this$winner == null) {
                if (other$winner == null) {
                    break Label_0863;
                }
            }
            else if (this$winner.equals(other$winner)) {
                break Label_0863;
            }
            return false;
        }
        if (this.getWinnerKills() != other.getWinnerKills()) {
            return false;
        }
        if (this.getWinnerTotalKills() != other.getWinnerTotalKills()) {
            return false;
        }
        if (this.isToCancelFirework() != other.isToCancelFirework()) {
            return false;
        }
        if (this.isDeathMatchArenaSpawned() != other.isDeathMatchArenaSpawned()) {
            return false;
        }
        final Object this$activeBrewingStands = this.getActiveBrewingStands();
        final Object other$activeBrewingStands = other.getActiveBrewingStands();
        if (this$activeBrewingStands == null) {
            if (other$activeBrewingStands == null) {
                return true;
            }
        }
        else if (this$activeBrewingStands.equals(other$activeBrewingStands)) {
            return true;
        }
        return false;
    }
    
    protected boolean canEqual(final Object other) {
        return other instanceof GameManager;
    }
    
    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $gameState = this.getGameState();
        result = result * 59 + (($gameState == null) ? 43 : $gameState.hashCode());
        final Object $gamePrefix = this.getGamePrefix();
        result = result * 59 + (($gamePrefix == null) ? 43 : $gamePrefix.hashCode());
        final Object $borderPrefix = this.getBorderPrefix();
        result = result * 59 + (($borderPrefix == null) ? 43 : $borderPrefix.hashCode());
        final Object $tsInfo = this.getTsInfo();
        result = result * 59 + (($tsInfo == null) ? 43 : $tsInfo.hashCode());
        final Object $webInfo = this.getWebInfo();
        result = result * 59 + (($webInfo == null) ? 43 : $webInfo.hashCode());
        final Object $ipInfo = this.getIpInfo();
        result = result * 59 + (($ipInfo == null) ? 43 : $ipInfo.hashCode());
        final Object $storeInfo = this.getStoreInfo();
        result = result * 59 + (($storeInfo == null) ? 43 : $storeInfo.hashCode());
        final Object $serverName = this.getServerName();
        result = result * 59 + (($serverName == null) ? 43 : $serverName.hashCode());
        result = result * 59 + this.getMaxPlayers();
        result = result * 59 + this.getMinPlayers();
        result = result * 59 + this.getStartCountdownValue();
        result = result * 59 + this.getPrematchCountdownValue();
        result = result * 59 + this.getPvpCountdownValue();
        result = result * 59 + this.getFeastsCountdownValue();
        result = result * 59 + this.getDeathMatchCountdownValue();
        result = result * 59 + this.getRebootCountdownValue();
        result = result * 59 + this.getPointsPerKill();
        result = result * 59 + this.getPointsPerWin();
        final Object $rebootCommand = this.getRebootCommand();
        result = result * 59 + (($rebootCommand == null) ? 43 : $rebootCommand.hashCode());
        final Object $startCountdown = this.getStartCountdown();
        result = result * 59 + (($startCountdown == null) ? 43 : $startCountdown.hashCode());
        final Object $prematchCountdown = this.getPrematchCountdown();
        result = result * 59 + (($prematchCountdown == null) ? 43 : $prematchCountdown.hashCode());
        final Object $pvpCountdown = this.getPvpCountdown();
        result = result * 59 + (($pvpCountdown == null) ? 43 : $pvpCountdown.hashCode());
        final Object $feastCountdown = this.getFeastCountdown();
        result = result * 59 + (($feastCountdown == null) ? 43 : $feastCountdown.hashCode());
        final Object $gameRunnable = this.getGameRunnable();
        result = result * 59 + (($gameRunnable == null) ? 43 : $gameRunnable.hashCode());
        final Object $deathMatchCountdown = this.getDeathMatchCountdown();
        result = result * 59 + (($deathMatchCountdown == null) ? 43 : $deathMatchCountdown.hashCode());
        final Object $rebootCountdown = this.getRebootCountdown();
        result = result * 59 + (($rebootCountdown == null) ? 43 : $rebootCountdown.hashCode());
        result = result * 59 + (this.isForceStarted() ? 79 : 97);
        result = result * 59 + (this.isToUseLobby() ? 79 : 97);
        result = result * 59 + (this.isServerPremium() ? 79 : 97);
        final Object $lobbyFallbackServer = this.getLobbyFallbackServer();
        result = result * 59 + (($lobbyFallbackServer == null) ? 43 : $lobbyFallbackServer.hashCode());
        final Object $winner = this.getWinner();
        result = result * 59 + (($winner == null) ? 43 : $winner.hashCode());
        result = result * 59 + this.getWinnerKills();
        result = result * 59 + this.getWinnerTotalKills();
        result = result * 59 + (this.isToCancelFirework() ? 79 : 97);
        result = result * 59 + (this.isDeathMatchArenaSpawned() ? 79 : 97);
        final Object $activeBrewingStands = this.getActiveBrewingStands();
        result = result * 59 + (($activeBrewingStands == null) ? 43 : $activeBrewingStands.hashCode());
        return result;
    }
    
    @Override
    public String toString() {
        return "GameManager(gameState=" + this.getGameState() + ", gamePrefix=" + this.getGamePrefix() + ", borderPrefix=" + this.getBorderPrefix() + ", tsInfo=" + this.getTsInfo() + ", webInfo=" + this.getWebInfo() + ", ipInfo=" + this.getIpInfo() + ", storeInfo=" + this.getStoreInfo() + ", serverName=" + this.getServerName() + ", maxPlayers=" + this.getMaxPlayers() + ", minPlayers=" + this.getMinPlayers() + ", startCountdownValue=" + this.getStartCountdownValue() + ", prematchCountdownValue=" + this.getPrematchCountdownValue() + ", pvpCountdownValue=" + this.getPvpCountdownValue() + ", feastsCountdownValue=" + this.getFeastsCountdownValue() + ", deathMatchCountdownValue=" + this.getDeathMatchCountdownValue() + ", rebootCountdownValue=" + this.getRebootCountdownValue() + ", pointsPerKill=" + this.getPointsPerKill() + ", pointsPerWin=" + this.getPointsPerWin() + ", rebootCommand=" + this.getRebootCommand() + ", startCountdown=" + this.getStartCountdown() + ", prematchCountdown=" + this.getPrematchCountdown() + ", pvpCountdown=" + this.getPvpCountdown() + ", feastCountdown=" + this.getFeastCountdown() + ", gameRunnable=" + this.getGameRunnable() + ", deathMatchCountdown=" + this.getDeathMatchCountdown() + ", rebootCountdown=" + this.getRebootCountdown() + ", forceStarted=" + this.isForceStarted() + ", toUseLobby=" + this.isToUseLobby() + ", serverPremium=" + this.isServerPremium() + ", lobbyFallbackServer=" + this.getLobbyFallbackServer() + ", winner=" + this.getWinner() + ", winnerKills=" + this.getWinnerKills() + ", winnerTotalKills=" + this.getWinnerTotalKills() + ", toCancelFirework=" + this.isToCancelFirework() + ", deathMatchArenaSpawned=" + this.isDeathMatchArenaSpawned() + ", activeBrewingStands=" + this.getActiveBrewingStands() + ")";
    }
    
    public static GameManager getInstance() {
        return GameManager.instance;
    }
    
    static {
        GameManager.META_KEY = (MetadataValue)new FixedMetadataValue((Plugin)PotSG.getInstance(), (Object)true);
    }
}
