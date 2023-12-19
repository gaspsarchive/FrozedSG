package club.frozed.frozedsg.listeners;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.utils.pagination.Menu;
import club.frozed.frozedsg.utils.pagination.PaginatedMenu;
import club.frozed.frozedsg.utils.pagination.buttons.Button;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class ButtonListener implements Listener
{
    @EventHandler(priority = EventPriority.MONITOR)
    public void onButtonPress(final InventoryClickEvent event) {
        final Player player = (Player)event.getWhoClicked();
        final Menu openMenu = Menu.currentlyOpenedMenus.get(player.getName());
        if (openMenu != null) {
            if (event.getSlot() != event.getRawSlot()) {
                if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                    event.setCancelled(true);
                }
                return;
            }
            if (openMenu.getButtons().containsKey(event.getSlot())) {
                final Button button = openMenu.getButtons().get(event.getSlot());
                final boolean cancel = button.shouldCancel(player, event.getSlot(), event.getClick());
                if (!cancel && (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) {
                    event.setCancelled(true);
                    if (event.getCurrentItem() != null) {
                        player.getInventory().addItem(new ItemStack[] { event.getCurrentItem() });
                    }
                }
                else {
                    player.playSound(player.getLocation(), Sound.BAT_TAKEOFF, 1.0f, 1.0f);
                    event.setCancelled(cancel);
                }
                button.clicked(player, event.getSlot(), event.getClick(), event.getHotbarButton());
                if (Menu.currentlyOpenedMenus.containsKey(player.getName())) {
                    final Menu newMenu = Menu.currentlyOpenedMenus.get(player.getName());
                    if (newMenu == openMenu) {
                        final boolean buttonUpdate = button.shouldUpdate(player, event.getSlot(), event.getClick());
                        if ((newMenu.isUpdateAfterClick() && buttonUpdate) || buttonUpdate) {
                            openMenu.setClosedByMenu(true);
                            newMenu.openMenu(player);
                        }
                    }
                }
                else if (button.shouldUpdate(player, event.getSlot(), event.getClick())) {
                    openMenu.setClosedByMenu(true);
                    openMenu.openMenu(player);
                }
                if (event.isCancelled()) {
                    Bukkit.getScheduler().runTaskLater((Plugin)PotSG.getInstance(), player::updateInventory, 1L);
                }
            }
            else if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClose(final InventoryCloseEvent event) {
        final Player player = (Player)event.getPlayer();
        final Menu openMenu = Menu.currentlyOpenedMenus.get(player.getName());
        if (openMenu != null) {
            openMenu.onClose(player);
            Menu.currentlyOpenedMenus.remove(player.getName());
            if (openMenu instanceof PaginatedMenu) {
                return;
            }
        }
        player.setMetadata("scanglitch", (MetadataValue)new FixedMetadataValue((Plugin)PotSG.getInstance(), (Object)true));
    }
    
    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        if (player.hasMetadata("scanglitch")) {
            player.removeMetadata("scanglitch", (Plugin)PotSG.getInstance());
            for (final ItemStack it : player.getInventory().getContents()) {
                if (it != null) {
                    final ItemMeta meta = it.getItemMeta();
                    if (meta != null && meta.hasDisplayName() && meta.getDisplayName().contains(StringEscapeUtils.unescapeJava("&d&b&c"))) {
                        player.getInventory().remove(it);
                    }
                }
            }
        }
    }
}
