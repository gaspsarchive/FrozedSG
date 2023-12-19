package club.frozed.frozedsg.utils.board;

import club.frozed.frozedsg.PotSG;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public class BoardManager implements Runnable, Listener
{
    private final JavaPlugin plugin;
    private final Map<UUID, Board> playerBoards;
    private final BoardAdapter adapter;

    @Override
    public void run() {
        this.adapter.preLoop();
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            final Board board = this.playerBoards.get(player.getUniqueId());
            if (board == null) {
                continue;
            }
            try {
                final Scoreboard scoreboard = board.getScoreboard();
                final List<String> scores = this.adapter.getScoreboard(player, board);
                if (scores != null) {
                    Collections.reverse(scores);
                    final Objective objective = board.getObjective();
                    if (!objective.getDisplayName().equals(this.adapter.getTitle(player))) {
                        objective.setDisplayName(this.adapter.getTitle(player));
                    }
                    if (scores.isEmpty()) {
                        final Iterator<BoardEntry> iter = board.getEntries().iterator();
                        while (iter.hasNext()) {
                            final BoardEntry boardEntry = iter.next();
                            boardEntry.remove();
                            iter.remove();
                        }
                        continue;
                    }
                    int i = 0;
                Label_0211:
                    while (i < scores.size()) {
                        final String text = scores.get(i);
                        final int position = i + 1;
                        while (true) {
                            for (final BoardEntry boardEntry2 : new LinkedList<BoardEntry>(board.getEntries())) {
                                final Score score = objective.getScore(boardEntry2.getKey());
                                if (score != null && boardEntry2.getText().equals(text) && score.getScore() == position) {
                                    ++i;
                                    continue Label_0211;
                                }
                            }
                            Iterator<BoardEntry> iter2 = board.getEntries().iterator();
                            while (iter2.hasNext()) {
                                final BoardEntry boardEntry2 = iter2.next();
                                final int entryPosition = scoreboard.getObjective(DisplaySlot.SIDEBAR).getScore(boardEntry2.getKey()).getScore();
                                if (entryPosition > scores.size()) {
                                    boardEntry2.remove();
                                    iter2.remove();
                                }
                            }
                            final int positionToSearch = position - 1;
                            final BoardEntry entry = board.getByPosition(positionToSearch);
                            if (entry == null) {
                                new BoardEntry(board, text).send(position);
                            }
                            else {
                                entry.setText(text).setup().send(position);
                            }
                            if (board.getEntries().size() > scores.size()) {
                                iter2 = board.getEntries().iterator();
                                while (iter2.hasNext()) {
                                    final BoardEntry boardEntry3 = iter2.next();
                                    if (!scores.contains(boardEntry3.getText()) || Collections.frequency(board.getBoardEntriesFormatted(), boardEntry3.getText()) > 1) {
                                        boardEntry3.remove();
                                        iter2.remove();
                                    }
                                }
                            }
                            continue;
                        }
                    }
                }
                else if (!board.getEntries().isEmpty()) {
                    board.getEntries().forEach(BoardEntry::remove);
                    board.getEntries().clear();
                }
                this.adapter.onScoreboardCreate(player, scoreboard);
                player.setScoreboard(scoreboard);
            }
            catch (Exception ex) {}
        }
    }

    public void addPlayer(final Player player) {
        if (!this.getPlayerBoards().containsKey(player.getUniqueId())) {
            this.getPlayerBoards().put(player.getUniqueId(), new Board(PotSG.getInstance(), player, this.getAdapter()));
        }
    }

    public Board getBoardByPlayer(final Player player) {
        if (this.getPlayerBoards().containsKey(player.getUniqueId())) {
            return this.getPlayerBoards().get(player.getUniqueId());
        }
        return null;
    }

    public void removePlayer(final Player player) {
        if (this.getPlayerBoards().containsKey(player.getUniqueId())) {
            this.getPlayerBoards().remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        if (PotSG.getInstance().isPluginLoading()) {
            return;
        }
        this.addPlayer(event.getPlayer());
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        this.removePlayer(event.getPlayer());
    }

    @EventHandler
    public void onQuit(final PlayerKickEvent event) {
        this.removePlayer(event.getPlayer());
    }

    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    public Map<UUID, Board> getPlayerBoards() {
        return this.playerBoards;
    }

    public BoardAdapter getAdapter() {
        return this.adapter;
    }

    public BoardManager(final JavaPlugin plugin, final BoardAdapter adapter) {
        this.playerBoards = new HashMap<UUID, Board>();
        this.plugin = plugin;
        this.adapter = adapter;
    }
}
