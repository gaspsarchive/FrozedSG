package club.frozed.frozedsg.listeners;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.enums.PlayerState;
import club.frozed.frozedsg.managers.*;
import club.frozed.frozedsg.player.PlayerData;
import club.frozed.frozedsg.utils.Chest;
import club.frozed.frozedsg.utils.Setting;
import club.frozed.frozedsg.utils.Utils;
import club.frozed.frozedsg.utils.chat.Color;
import club.frozed.frozedsg.utils.inventories.AlivePlayersInventory;
import club.frozed.frozedsg.utils.inventories.LobbyInventoryPlayers;
import club.frozed.frozedsg.utils.leaderboards.LeaderboardManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryHandler implements Listener
{
    @EventHandler
    public void onClick(final InventoryClickEvent event) {
        final Player player = (Player)event.getWhoClicked();
        final ItemStack item = event.getCurrentItem();
        final PlayerData data = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());
        if (item == null) {
            return;
        }
        if (event.getClickedInventory().getTitle().contains(Color.translate(PotSG.getInstance().getConfiguration("inventory").getString("player-alive-inventory.title"))) && item.getType().equals((Object)Material.SKULL_ITEM)) {
            final String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
            if (Bukkit.getPlayer(name) != null) {
                player.teleport((Entity)Bukkit.getPlayer(name));
                player.sendMessage(Color.translate(GameManager.getInstance().getGamePrefix() + PotSG.getInstance().getConfiguration("messages").getString("teleport-success")).replaceAll("<target>", name));
            }
        }
        if (data != null && event.getInventory().getTitle().equalsIgnoreCase(data.getSettingsInventory().getTitle())) {
            event.setCancelled(true);
            if (!event.getClickedInventory().getTitle().equalsIgnoreCase(data.getSettingsInventory().getTitle())) {
                return;
            }
            if (!item.hasItemMeta()) {
                return;
            }
            if (!item.getItemMeta().hasDisplayName()) {
                return;
            }
            final Setting setting = data.getSettingByName(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
            if (setting != null) {
                if (data.getPoints().getAmount() < setting.getRequiredPoints()) {
                    player.closeInventory();
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0f, 1.0f);
                    player.sendMessage(GameManager.getInstance().getGamePrefix() + Color.translate(PotSG.getInstance().getConfiguration("messages").getString("not-enough-point")));
                    return;
                }
                setting.setEnabled(!setting.isEnabled());
                player.getOpenInventory().getTopInventory().setContents(data.getSettingsInventory().getContents());
                player.updateInventory();
                player.playSound(player.getLocation(), Sound.BAT_TAKEOFF, 1.0f, 1.0f);
            }
        }
        if (!data.getState().equals(PlayerState.INGAME)) {
            if (event.getAction().equals((Object)InventoryAction.SWAP_WITH_CURSOR) || event.getAction().equals((Object)InventoryAction.HOTBAR_SWAP)) {
                event.setCancelled(true);
            }
            if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                event.setCancelled(true);
            }
        }
        if (PlayerManager.getInstance().isSpectator(player)) {
            event.setCancelled(true);
        }
        if (event.getInventory().getTitle().equalsIgnoreCase(ChestsManager.getInstance().chestsInventory().getTitle())) {
            event.setCancelled(true);
        }
        if (event.getInventory().getTitle().equalsIgnoreCase(LeaderboardManager.getInstance().getInventory(player).getTitle())) {
            event.setCancelled(true);
        }
        if (event.getInventory().getTitle().contains(Color.translate(PotSG.getInstance().getConfiguration("inventory").getString("stats-inventory.title").replaceAll("<target>", "")))) {
            event.setCancelled(true);
        }
        if (event.getClickedInventory().getTitle().equalsIgnoreCase(ChestsManager.getInstance().chestsInventory().getTitle()) && item.getType().equals((Object)Material.CHEST)) {
            final int number = Integer.parseInt(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
            final Chest chest = ChestsManager.getInstance().getChest(number);
            player.openInventory(ChestsManager.getInstance().chestInventory(chest));
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void handlePlayerInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final ItemStack item = event.getItem();
        final PlayerData data = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());
        if (item == null) {
            return;
        }
        if (!item.hasItemMeta() && !item.getItemMeta().hasDisplayName()) {
            return;
        }
        if (!event.getAction().equals((Object)Action.RIGHT_CLICK_AIR) && !event.getAction().equals((Object)Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (PlayerManager.getInstance().isSpectator(player) && item.getType().equals((Object)Material.ITEM_FRAME)) {
            event.setCancelled(true);
        }
        if (data.getState().equals(PlayerState.LOBBY)) {
            if (item.getType().equals((Object)Material.EMERALD)) {
                player.openInventory(LeaderboardManager.getInstance().getInventory(player));
            }
            if (item.getType().equals((Object)Material.CHEST)) {
                player.performCommand("gamesettings");
            }
            if (item.getType().equals((Object)Material.WATCH)) {
                new LobbyInventoryPlayers().openMenu(player);
            }
            if (item.getType().equals((Object)Material.SKULL_ITEM)) {
                player.openInventory(InventoryManager.getInstance().getStatsInventory(data));
            }
        }
        if (PlayerManager.getInstance().isSpectator(player)) {
            if (item.getType().equals((Object)Material.ITEM_FRAME)) {
                if (PlayerManager.getInstance().getGamePlayers().size() > 0) {
                    new AlivePlayersInventory().openMenu(player);
                }
                else {
                    player.sendMessage(Color.translate(GameManager.getInstance().getGamePrefix() + PotSG.getInstance().getConfiguration("messages").getString("no-alive-players")));
                }
            }
            if (item.getType().equals((Object)Material.REDSTONE)) {
                if (!GameManager.getInstance().isToUseLobby()) {
                    player.kickPlayer(Color.translate(GameManager.getInstance().getGamePrefix() + PotSG.getInstance().getConfiguration("messages").getString("kicked-from-server")).replaceAll("<server_name>", GameManager.getInstance().getServerName()));
                }
                else {
                    Utils.connectPlayer(player, GameManager.getInstance().getLobbyFallbackServer());
                    player.sendMessage(Color.translate(GameManager.getInstance().getGamePrefix() + PotSG.getInstance().getConfiguration("messages").getString("bungeecord-send-to-lobby")).replaceAll("<lobby_server_name>", GameManager.getInstance().getLobbyFallbackServer()));
                }
            }
            if (item.getType().equals((Object)Material.WATCH)) {
                Utils.randomPlayer(player);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleInspectInventory(final PlayerInteractEntityEvent event) {
        final Player player = event.getPlayer();
        final Entity entity = event.getRightClicked();
        final ItemStack item = player.getItemInHand();
        if (item == null) {
            return;
        }
        if (!item.hasItemMeta()) {
            return;
        }
        if (!item.getItemMeta().hasDisplayName()) {
            return;
        }
        if (entity instanceof Player && PlayerManager.getInstance().isSpectator(player)) {
            final Player rightClicked = (Player)entity;
            if (item.getType() == Material.BOOK) {
                player.sendMessage(GameManager.getInstance().getGamePrefix() + Color.translate(PotSG.getInstance().getConfiguration("messages").getString("open-player-inventory")).replaceAll("<target>", rightClicked.getName()));
                player.openInventory(Utils.createInventoryOther(rightClicked));
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleSpectatorInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final Action action = event.getAction();
        if (PlayerManager.getInstance().isSpectator(player)) {
            if (action.equals((Object)Action.RIGHT_CLICK_BLOCK)) {
                final Block block = event.getClickedBlock();
                if ((block.getType().equals((Object)Material.CHEST) || block.getType().equals((Object)Material.TRAPPED_CHEST)) && !player.isSneaking()) {
                    event.setCancelled(true);
                    player.sendMessage(GameManager.getInstance().getGamePrefix() + Color.translate(PotSG.getInstance().getConfiguration("messages").getString("cannot-open-chest")));
                }
            }
            if (PlayerManager.getInstance().isSpectator(player)) {
                if (action.equals((Object)Action.PHYSICAL)) {
                    event.setCancelled(true);
                }
                if (action.equals((Object)Action.RIGHT_CLICK_BLOCK)) {
                    final Block block = event.getClickedBlock();
                    if (block.getType().equals((Object)Material.LEVER) || block.getType().equals((Object)Material.WOOD_BUTTON) || block.getType().equals((Object)Material.STONE_BUTTON)) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
