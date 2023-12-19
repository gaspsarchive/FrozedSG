package club.frozed.frozedsg.utils.board;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;

public interface BoardAdapter
{
    String getTitle(final Player p0);
    
    List<String> getScoreboard(final Player p0, final Board p1);
    
    long getInterval();
    
    void onScoreboardCreate(final Player p0, final Scoreboard p1);
    
    void preLoop();
}
