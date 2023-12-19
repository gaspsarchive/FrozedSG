package club.frozed.frozedsg.utils.board;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.utils.Symbols;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Board
{
    private final BoardAdapter adapter;
    private final Player player;
    private List<BoardEntry> entries;
    private Set<BoardTimer> timers;
    private Set<String> keys;
    private Scoreboard scoreboard;
    private Objective objective;
    private Objective healthName;
    
    public Board(final JavaPlugin plugin, final Player player, final BoardAdapter adapter) {
        this.entries = new ArrayList<BoardEntry>();
        this.timers = new HashSet<BoardTimer>();
        this.keys = new HashSet<String>();
        this.adapter = adapter;
        this.player = player;
        this.init(plugin);
    }
    
    private void init(final JavaPlugin plugin) {
        if (!this.player.getScoreboard().equals(plugin.getServer().getScoreboardManager().getMainScoreboard())) {
            this.scoreboard = this.player.getScoreboard();
        }
        else {
            this.scoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
        }
        (this.objective = this.scoreboard.registerNewObjective("Default", "dummy")).setDisplaySlot(DisplaySlot.SIDEBAR);
        this.objective.setDisplayName(this.adapter.getTitle(this.player));
        if (PotSG.getInstance().getConfiguration("config").getBoolean("BOOLEANS.HEALTH-NAMETAG")) {
            (this.healthName = this.getOrCreateObjective(this.scoreboard, "healthName", "health")).setDisplaySlot(DisplaySlot.BELOW_NAME);
            this.healthName.setDisplayName(ChatColor.RED + Symbols.HEALTH);
        }
    }
    
    public String getNewKey(final BoardEntry entry) {
        for (final ChatColor color : ChatColor.values()) {
            String colorText = color + "" + ChatColor.WHITE;
            if (entry.getText().length() > 16) {
                final String sub = entry.getText().substring(0, 16);
                colorText += ChatColor.getLastColors(sub);
            }
            if (!this.keys.contains(colorText)) {
                this.keys.add(colorText);
                return colorText;
            }
        }
        throw new IndexOutOfBoundsException("No more keys available!");
    }
    
    public Objective getOrCreateObjective(final Scoreboard scoreboard, final String objective, final String type) {
        Objective value = scoreboard.getObjective(objective);
        if (value == null) {
            value = scoreboard.registerNewObjective(objective, type);
        }
        value.setDisplayName(objective);
        return value;
    }
    
    public List<String> getBoardEntriesFormatted() {
        final List<String> toReturn = new ArrayList<String>();
        for (final BoardEntry entry : new ArrayList<BoardEntry>(this.entries)) {
            toReturn.add(entry.getText());
        }
        return toReturn;
    }
    
    public BoardEntry getByPosition(final int position) {
        for (int i = 0; i < this.entries.size(); ++i) {
            if (i == position) {
                return this.entries.get(i);
            }
        }
        return null;
    }
    
    public BoardTimer getCooldown(final String id) {
        for (final BoardTimer cooldown : this.getTimers()) {
            if (cooldown.getId().equals(id)) {
                return cooldown;
            }
        }
        return null;
    }
    
    public Set<BoardTimer> getTimers() {
        this.timers.removeIf(cooldown -> System.currentTimeMillis() >= cooldown.getEnd());
        return this.timers;
    }
    
    public BoardAdapter getAdapter() {
        return this.adapter;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public List<BoardEntry> getEntries() {
        return this.entries;
    }
    
    public Set<String> getKeys() {
        return this.keys;
    }
    
    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }
    
    public Objective getObjective() {
        return this.objective;
    }
    
    public Objective getHealthName() {
        return this.healthName;
    }
}
