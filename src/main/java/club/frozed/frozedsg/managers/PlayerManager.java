package club.frozed.frozedsg.managers;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.enums.PlayerState;
import club.frozed.frozedsg.player.PlayerData;
import club.frozed.frozedsg.utils.ItemBuilder;
import club.frozed.frozedsg.utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerManager {
    public static PlayerManager instance;

    public PlayerManager() {
        instance = this;
    }

    public List<Player> getGamePlayers() {
        return (List)Utils.getOnlinePlayers().stream().filter((player) -> {
            return PlayerDataManager.getInstance().getByUUID(player.getUniqueId()) != null && PlayerDataManager.getInstance().getByUUID(player.getUniqueId()).getState() == PlayerState.INGAME;
        }).collect(Collectors.toList());
    }

    public List<Player> getOnlinePlayers() {
        return new ArrayList(Utils.getOnlinePlayers());
    }

    public List<Player> getLobbyPlayers() {
        return (List)Utils.getOnlinePlayers().stream().filter((player) -> {
            return PlayerDataManager.getInstance().getByUUID(player.getUniqueId()) != null && PlayerDataManager.getInstance().getByUUID(player.getUniqueId()).getState() == PlayerState.LOBBY;
        }).collect(Collectors.toList());
    }

    public boolean isSpectator(Player player) {
        return PlayerDataManager.getInstance().getByUUID(player.getUniqueId()) != null && (PlayerDataManager.getInstance().getByUUID(player.getUniqueId()).getState().equals(PlayerState.PREMATCH) || PlayerDataManager.getInstance().getByUUID(player.getUniqueId()).getState().equals(PlayerState.SPECTATING));
    }

    public List<Player> getSpectatorPlayers() {
        return (List)Utils.getOnlinePlayers().stream().filter((player) -> {
            return PlayerDataManager.getInstance().getByUUID(player.getUniqueId()) != null && PlayerDataManager.getInstance().getByUUID(player.getUniqueId()).getState() == PlayerState.SPECTATING;
        }).collect(Collectors.toList());
    }

    public List<Player> getPrematchPlayers() {
        return (List)Utils.getOnlinePlayers().stream().filter((player) -> {
            return PlayerDataManager.getInstance().getByUUID(player.getUniqueId()) != null && PlayerDataManager.getInstance().getByUUID(player.getUniqueId()).getState() == PlayerState.PREMATCH;
        }).collect(Collectors.toList());
    }

    public void setSpectating(Player player) {
        PlayerData data = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());
        Utils.clearPlayer(player);
        data.setState(PlayerState.SPECTATING);
        data.setSpecChat(true);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(GameManager.getInstance().getGameWorldCenterLocation().add(0.0, 10.0, 0.0));
        ItemBuilder alivePlayers = new ItemBuilder(Material.ITEM_FRAME);
        alivePlayers.setNameWithArrows(PotSG.getInstance().getConfiguration("items").getString("spectator-inventory.alive-players.name"));
        Iterator var4 = PotSG.getInstance().getConfiguration("items").getStringList("spectator-inventory.alive-players.lore").iterator();

        while(var4.hasNext()) {
            String lore = (String)var4.next();
            alivePlayers.addLoreLine(lore);
        }

        ItemBuilder lobby = new ItemBuilder(Material.REDSTONE);
        String lore;
        Iterator var10;
        if (GameManager.getInstance().isToUseLobby()) {
            lobby.setNameWithArrows(PotSG.getInstance().getConfiguration("items").getString("spectator-inventory.quit-sg.lobby-enabled.name"));
            var10 = PotSG.getInstance().getConfiguration("items").getStringList("spectator-inventory.quit-sg.lobby-enabled.lore").iterator();

            while(var10.hasNext()) {
                lore = (String)var10.next();
                lobby.addLoreLine(lore);
            }
        } else {
            lobby.setNameWithArrows(PotSG.getInstance().getConfiguration("items").getString("spectator-inventory.quit-sg.lobby-disabled.name"));
            var10 = PotSG.getInstance().getConfiguration("items").getStringList("spectator-inventory.quit-sg.lobby-disabled.lore").iterator();

            while(var10.hasNext()) {
                lore = (String)var10.next();
                lobby.addLoreLine(lore);
            }
        }

        ItemBuilder inv = new ItemBuilder(Material.BOOK);
        inv.setNameWithArrows(PotSG.getInstance().getConfiguration("items").getString("spectator-inventory.inspect-inventory.name"));
        Iterator var12 = PotSG.getInstance().getConfiguration("items").getStringList("spectator-inventory.inspect-inventory.lore").iterator();

        while(var12.hasNext()) {
            lore = (String)var12.next();
            inv.addLoreLine(lore);
        }

        ItemBuilder random = new ItemBuilder(Material.WATCH);
        random.setNameWithArrows(PotSG.getInstance().getConfiguration("items").getString("spectator-inventory.random-teleport.name"));
        Iterator var14 = PotSG.getInstance().getConfiguration("items").getStringList("spectator-inventory.random-teleport.lore").iterator();

        while(var14.hasNext()) {
            lore = (String)var14.next();
            random.addLoreLine(lore);
        }

        player.getInventory().setItem(0, alivePlayers.toItemStack());
        player.getInventory().setItem(1, inv.toItemStack());
        player.getInventory().setItem(7, random.toItemStack());
        player.getInventory().setItem(8, lobby.toItemStack());
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof PlayerManager)) {
            return false;
        } else {
            PlayerManager other = (PlayerManager)o;
            return other.canEqual(this);
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof PlayerManager;
    }

    public int hashCode() {
        boolean result = true;
        return 1;
    }

    public String toString() {
        return "PlayerManager()";
    }

    public static PlayerManager getInstance() {
        return instance;
    }
}
