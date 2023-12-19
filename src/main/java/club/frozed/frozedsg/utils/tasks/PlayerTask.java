package club.frozed.frozedsg.utils.tasks;

import club.frozed.frozedsg.managers.PlayerManager;
import club.frozed.frozedsg.utils.Utils;
import org.bukkit.entity.Player;

import java.util.Iterator;

public class PlayerTask implements Runnable {
    public PlayerTask() {
    }

    public void run() {
        Iterator var1 = Utils.getOnlinePlayers().iterator();

        Player online;
        while(var1.hasNext()) {
            online = (Player)var1.next();
            PlayerManager.getInstance().getSpectatorPlayers().forEach(online::hidePlayer);
        }

        var1 = Utils.getOnlinePlayers().iterator();

        while(var1.hasNext()) {
            online = (Player)var1.next();
            PlayerManager.getInstance().getGamePlayers().forEach(online::showPlayer);
        }

    }
}
