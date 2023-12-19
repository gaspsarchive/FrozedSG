package club.frozed.frozedsg;

import club.frozed.frozedsg.border.BorderManager;
import club.frozed.frozedsg.commands.*;
import club.frozed.frozedsg.commands.staff.*;
import club.frozed.frozedsg.layout.BoardLayout;
import club.frozed.frozedsg.layout.TablistLayout;
import club.frozed.frozedsg.listeners.*;
import club.frozed.frozedsg.managers.*;
import club.frozed.frozedsg.other.PlayerListener;
import club.frozed.frozedsg.utils.Cooldown;
import club.frozed.frozedsg.utils.Utils;
import club.frozed.frozedsg.utils.board.BoardManager;
import club.frozed.frozedsg.utils.chat.Color;
import club.frozed.frozedsg.utils.command.CommandFramework;
import club.frozed.frozedsg.utils.configurations.ConfigFile;
import club.frozed.frozedsg.utils.leaderboards.LeaderboardManager;
import club.frozed.frozedsg.utils.runnables.DataRunnable;
import club.frozed.frozedsg.utils.tasks.BrewingTask;
import club.frozed.frozedsg.utils.tasks.DataSaveTask;
import club.frozed.frozedsg.utils.tasks.LobbyTask;
import club.frozed.frozedsg.utils.tasks.PlayerTask;
import me.allen.ziggurat.Ziggurat;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PotSG extends JavaPlugin
{
    public static PotSG instance;
    private CommandFramework framework;
    private List<ConfigFile> files;
    private BoardManager boardManager;
    private boolean pluginLoading;
    private Cooldown announceCooldown;
    
    public PotSG() {
        this.files = new ArrayList<ConfigFile>();
    }
    
    public void onEnable() {
        PotSG.instance = this;
        this.pluginLoading = true;
        this.registerConfigurations();
        this.framework = new CommandFramework(this);
        this.setBoardManager(new BoardManager(this, new BoardLayout()));
        new Ziggurat(this, new TablistLayout());
        if (!this.isBorderShrinksStreamValid()) {
            this.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "[FrozedSG]" + ChatColor.RED + " The plugin could not be enabled. Please check your configuration for " + ChatColor.AQUA + "Border Shrinks Stream.");
            this.getServer().getPluginManager().disablePlugin((Plugin)this);
            return;
        }
        this.getServer().getPluginManager().registerEvents((Listener)new ButtonListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new ChunkListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new GlassListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new InventoryHandler(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new DeathMessagesListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new PlayerListener(), (Plugin)this);
        new ChestsManager();
        new GameManager();
        new InventoryManager();
        new MongoManager();
        new PlayerDataManager();
        new PlayerManager();
        new WorldsManager();
        new DataCommand();
        new GameCommand();
        new ReloadConfigCommand();
        new RespawnCommand();
        new SpectatorCommand();
        new AnnounceCommand();
        new FrozedSGCommand();
        new SettingsCommand();
        new SpecChatCommand();
        new StatsCommand();
        this.getServer().getMessenger().registerOutgoingPluginChannel((Plugin)this, "Broadcast");
        this.getServer().getMessenger().registerOutgoingPluginChannel((Plugin)this, "BungeeCord");
        new BorderManager();
        this.getServer().getScheduler().scheduleAsyncRepeatingTask((Plugin)this, (Runnable)new LobbyTask(), 20L, 20L);
        this.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)this, (Runnable)new PlayerTask(), 2L, 2L);
        this.getServer().getScheduler().scheduleAsyncRepeatingTask((Plugin)this, (Runnable)new DataSaveTask(), 200L, 200L);
        this.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)this, (Runnable)new BrewingTask(), 1L, 1L);
        this.getServer().getConsoleSender().sendMessage(Color.translate("&7&m--------------------------------------------------------------"));
        this.getServer().getConsoleSender().sendMessage(Color.translate("&7This server is using &bFrozedSG"));
        this.getServer().getConsoleSender().sendMessage(Color.translate("&7Authors&8: &b" + this.getDescription().getAuthors()));
        this.getServer().getConsoleSender().sendMessage(Color.translate("&7Discord: &bhttps://dsc.gg/meteorlol"));
        this.getServer().getConsoleSender().sendMessage(Color.translate("&7&m--------------------------------------------------------------"));
        new LeaderboardManager();
        new DataRunnable();
    }
    
    public ConfigFile getConfiguration(final String name) {
        return this.files.stream().filter(config -> config.getName().equals(name)).findFirst().orElse(null);
    }
    
    public boolean isBorderShrinksStreamValid() {
        final String shrinkStream = getInstance().getConfiguration("config").getString("BORDER.SHRINK-STREAM");
        final String[] split;
        final String[] shrinksStream = split = shrinkStream.split(";");
        for (final String shrink : split) {
            if (!Utils.isInteger(shrink)) {
                return false;
            }
        }
        return true;
    }
    
    public void registerConfigurations() {
        this.files.addAll(Arrays.asList(new ConfigFile("config"), new ConfigFile("messages"), new ConfigFile("items"), new ConfigFile("inventory"), new ConfigFile("chests"), new ConfigFile("scoreboard"), new ConfigFile("tablist")));
    }
    
    public void setBoardManager(final BoardManager boardManager) {
        this.boardManager = boardManager;
        final long interval = this.boardManager.getAdapter().getInterval();
        this.getServer().getScheduler().runTaskTimerAsynchronously((Plugin)this, (Runnable)this.boardManager, interval, interval);
        this.getServer().getPluginManager().registerEvents((Listener)this.boardManager, (Plugin)this);
    }
    
    public CommandFramework getFramework() {
        return this.framework;
    }
    
    public List<ConfigFile> getFiles() {
        return this.files;
    }
    
    public BoardManager getBoardManager() {
        return this.boardManager;
    }
    
    public boolean isPluginLoading() {
        return this.pluginLoading;
    }
    
    public Cooldown getAnnounceCooldown() {
        return this.announceCooldown;
    }
    
    public void setFramework(final CommandFramework framework) {
        this.framework = framework;
    }
    
    public void setFiles(final List<ConfigFile> files) {
        this.files = files;
    }
    
    public void setPluginLoading(final boolean pluginLoading) {
        this.pluginLoading = pluginLoading;
    }
    
    public void setAnnounceCooldown(final Cooldown announceCooldown) {
        this.announceCooldown = announceCooldown;
    }
    
    public static PotSG getInstance() {
        return PotSG.instance;
    }
}
