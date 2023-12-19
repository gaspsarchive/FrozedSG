package club.frozed.frozedsg.layout;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.border.BorderManager;
import club.frozed.frozedsg.enums.GameState;
import club.frozed.frozedsg.managers.GameManager;
import club.frozed.frozedsg.managers.PlayerDataManager;
import club.frozed.frozedsg.managers.PlayerManager;
import club.frozed.frozedsg.player.PlayerData;
import club.frozed.frozedsg.utils.Symbols;
import club.frozed.frozedsg.utils.board.Board;
import club.frozed.frozedsg.utils.board.BoardAdapter;
import club.frozed.frozedsg.utils.chat.Color;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

public class BoardLayout implements BoardAdapter
{
    @Override
    public String getTitle(final Player player) {
        String title = PotSG.getInstance().getConfiguration("scoreboard").getString("title");
        title = title.replace("<I>", StringEscapeUtils.unescapeJava("\u2503"));
        return Color.translate(title);
    }
    
    @Override
    public List<String> getScoreboard(final Player player, final Board board) {
        return Color.translate(this.getPlayerScoreboard(player));
    }
    
    private List<String> getPlayerScoreboard(final Player player) {
        final List<String> board = new ArrayList<String>();
        final PlayerData data = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());
        final GameManager gameManager = GameManager.getInstance();
        final PlayerManager playerManager = PlayerManager.getInstance();
        if (data.getSettingByName("Scoreboard") != null && !data.getSettingByName("Scoreboard").isEnabled()) {
            return board;
        }
        if (gameManager.getGameState() == GameState.LOBBY) {
            if (PlayerManager.getInstance().getLobbyPlayers().size() < GameManager.getInstance().getMinPlayers()) {
                for (final String string : PotSG.getInstance().getConfiguration("scoreboard").getStringList("lobby-scoreboard")) {
                    board.add(this.replace(string, player));
                }
            }
            else if (gameManager.getStartCountdown() != null && !gameManager.getStartCountdown().hasExpired()) {
                for (final String string : PotSG.getInstance().getConfiguration("scoreboard").getStringList("lobby-countdown-scoreboard")) {
                    board.add(this.replace(string, player));
                }
            }
        }
        if (gameManager.getGameState() == GameState.PREMATCH) {
            for (final String string : PotSG.getInstance().getConfiguration("scoreboard").getStringList("pre-match-scoreboard")) {
                board.add(this.replace(string, player));
            }
        }
        if (gameManager.getGameState() == GameState.INGAME) {
            if (gameManager.getPvpCountdown() != null && !gameManager.getPvpCountdown().hasExpired()) {
                for (final String string : PotSG.getInstance().getConfiguration("scoreboard").getStringList("pvp-countdown-scoreboard")) {
                    board.add(this.borderReplace(this.replace(string, player)));
                }
            }
            else if (gameManager.getFeastCountdown() != null && !gameManager.getFeastCountdown().hasExpired()) {
                for (final String string : PotSG.getInstance().getConfiguration("scoreboard").getStringList("feast-countdown-scoreboard")) {
                    board.add(this.borderReplace(this.replace(string, player)));
                }
            }
            else if (gameManager.getDeathMatchCountdown() != null && !gameManager.getDeathMatchCountdown().hasExpired()) {
                for (final String string : PotSG.getInstance().getConfiguration("scoreboard").getStringList("deathmatch-countdown-scoreboard")) {
                    board.add(this.borderReplace(this.replace(string, player)));
                }
            }
            else if (gameManager.getDeathMatchCountdown().hasExpired()) {
                for (final String string : PotSG.getInstance().getConfiguration("scoreboard").getStringList("deathmatch-scoreboard")) {
                    board.add(this.borderReplace(this.replace(string, player)));
                }
            }
        }
        if (gameManager.getGameState() == GameState.ENDING) {
            for (final String string : PotSG.getInstance().getConfiguration("scoreboard").getStringList("winner-scoreboard")) {
                board.add(this.replace(string, player));
            }
        }
        return board;
    }
    
    @Override
    public long getInterval() {
        return 2L;
    }
    
    @Override
    public void onScoreboardCreate(final Player player, final Scoreboard board) {
    }
    
    public String replace(final String string, final Player player) {
        final PlayerData data = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());
        final GameManager gameManager = GameManager.getInstance();
        final PlayerManager playerManager = PlayerManager.getInstance();
        return string.replaceAll("<server_name>", gameManager.getServerName()).replaceAll("<spectator_chat_boolean>", data.isSpecChat() ? PotSG.getInstance().getConfiguration("scoreboard").getString("enabled") : PotSG.getInstance().getConfiguration("scoreboard").getString("disabled")).replaceAll("<require_players>", String.valueOf(gameManager.getRequiredPlayersToJoin())).replaceAll("<prematch_players_size>", String.valueOf(playerManager.getPrematchPlayers().size())).replaceAll("<game_players>", String.valueOf(playerManager.getGamePlayers().size())).replaceAll("<max_players>", String.valueOf(gameManager.getMaxPlayers())).replaceAll("<countdown_players>", String.valueOf(playerManager.getLobbyPlayers().size())).replaceAll("<player_kills>", String.valueOf(data.getGameKills().getAmount())).replaceAll("<player_ping>", String.valueOf(((CraftPlayer)player).getHandle().ping)).replaceAll("<server_ip>", gameManager.getIpInfo()).replaceAll("<winner>", gameManager.getWinner()).replaceAll("<winner_kills>", String.valueOf(gameManager.getWinnerKills())).replaceAll("<game_time>", (GameManager.getInstance().getGameRunnable() != null) ? String.valueOf(GameManager.getInstance().getGameRunnable().getTime()) : "null").replaceAll("<start_countdown>", (gameManager.getStartCountdown() != null) ? String.valueOf(gameManager.getStartCountdown().getTimeLeft()) : "null").replaceAll("<prematch_countdown>", (gameManager.getPrematchCountdown() != null) ? String.valueOf(gameManager.getPrematchCountdown().getTimeLeft()) : "null").replaceAll("<enderpearl_countdown>", (data.getEnderpearlCooldown() != null) ? String.valueOf(data.getEnderpearlCooldown().getMiliSecondsLeft()) : "null").replaceAll("<combat_countdown>", (data.getCombatCooldown() != null) ? String.valueOf(data.getCombatCooldown().getMiliSecondsLeft()) : "null").replaceAll("<pvp_protection_countdown>", (gameManager.getPvpCountdown() != null) ? String.valueOf(gameManager.getPvpCountdown().getTimeLeft()) : "null").replaceAll("<feast_countdown>", (gameManager.getFeastCountdown() != null) ? String.valueOf(gameManager.getFeastCountdown().getTimeLeft()) : "null").replaceAll("<deathmatch_countdown>", (gameManager.getDeathMatchCountdown() != null) ? String.valueOf(gameManager.getDeathMatchCountdown().getTimeLeft()) : "null").replaceAll("<reboot_countdown>", (gameManager.getRebootCountdown() != null) ? String.valueOf(gameManager.getRebootCountdown().getTimeLeft()) : "null").replaceAll("<arrow_left>", Symbols.ARROW_LEFT).replaceAll("<arrow_right>", Symbols.ARROW_RIGHT);
    }
    
    public String borderReplace(final String string) {
        return string.replaceAll("<border_size>", String.valueOf(BorderManager.getInstance().getBorder().getSize())).replaceAll("<border_info>", BorderManager.getInstance().getBorderInfo());
    }
    
    @Override
    public void preLoop() {
    }
}
