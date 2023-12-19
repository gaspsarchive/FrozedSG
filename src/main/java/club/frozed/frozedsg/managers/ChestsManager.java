package club.frozed.frozedsg.managers;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.utils.Chest;
import club.frozed.frozedsg.utils.InventoryToBase64;
import club.frozed.frozedsg.utils.ItemBuilder;
import club.frozed.frozedsg.utils.Utils;
import club.frozed.frozedsg.utils.chat.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ChestsManager implements Listener {
    public static ChestsManager instance;
    private List<Chest> chests = new ArrayList();

    public ChestsManager() {
        instance = this;
        PotSG.getInstance().getServer().getPluginManager().registerEvents(this, PotSG.getInstance());

        for(int i = 1; i <= 54; ++i) {
            if (PotSG.getInstance().getConfiguration("chests").getConfiguration().getConfigurationSection("CHESTS") != null) {
                if (!PotSG.getInstance().getConfiguration("chests").getConfiguration().getConfigurationSection("CHESTS").contains(String.valueOf(i))) {
                    this.chests.add(new Chest((ItemStack[])null, i));
                }
            } else {
                this.chests.add(new Chest((ItemStack[])null, i));
            }
        }

        this.loadChestsFromConfig();
    }

    public void updateChestItems(int chestNumber, ItemStack[] items) {
        Chest chest = (Chest)this.chests.get(chestNumber - 1);
        chest.setItems(items);
    }

    public Chest getChest(int chestNumber) {
        return (Chest)this.chests.get(chestNumber - 1);
    }

    public void saveChestsToConfig() {
        PotSG.getInstance().getConfiguration("chests").getConfiguration().createSection("CHESTS");
        Iterator var1 = this.getChests().iterator();

        while(var1.hasNext()) {
            Chest chest = (Chest)var1.next();
            if (chest.getItems() != null) {
                PotSG.getInstance().getConfiguration("chests").getConfiguration().set("CHESTS." + chest.getNumber(), InventoryToBase64.itemToBase64(chest.getItems()));
            }
        }

        PotSG.getInstance().getConfiguration("chests").save();
    }

    public void loadChestsFromConfig() {
        if (PotSG.getInstance().getConfiguration("chests").getConfiguration().getConfigurationSection("CHESTS") != null) {
            PotSG.getInstance().getConfiguration("chests").getConfiguration().getConfigurationSection("CHESTS").getKeys(false).forEach((key) -> {
                if (Utils.isInteger(key)) {
                    ItemStack[] items = new ItemStack[0];

                    try {
                        items = InventoryToBase64.itemFromBase64(PotSG.getInstance().getConfiguration("chests").getString("CHESTS." + key));
                    } catch (IOException var4) {
                        var4.printStackTrace();
                    }

                    this.chests.add(new Chest(items, Integer.parseInt(key)));
                }

            });
        }
    }

    public Inventory chestsInventory() {
        this.fixOrder();
        Inventory inv = Bukkit.createInventory((InventoryHolder)null, 54, Color.translate("Chests"));
        this.chests.forEach((key) -> {
            ItemBuilder item = new ItemBuilder(Material.CHEST);
            item.setName("&a" + key.getNumber());
            item.addLoreLine("&7Click this to");
            item.addLoreLine("&7to edit items for this chest");
            if (key.getNumber() >= 46) {
                item.addLoreLine("&cNOTE: This is feast chest");
            }

            inv.addItem(new ItemStack[]{item.toItemStack()});
        });
        return inv;
    }

    public ItemStack[] getRandomItemsFromChests(boolean feast) {
        if (PotSG.getInstance().getConfiguration("chests").getConfiguration().getConfigurationSection("CHESTS") == null) {
            return null;
        } else {
            List<Chest> availableChests = new ArrayList();
            PotSG.getInstance().getConfiguration("chests").getConfiguration().getConfigurationSection("CHESTS").getKeys(false).forEach((key) -> {
                if (Utils.isInteger(key)) {
                    ItemStack[] items = new ItemStack[0];

                    try {
                        items = InventoryToBase64.itemFromBase64(PotSG.getInstance().getConfiguration("chests").getString("CHESTS." + key));
                    } catch (IOException var4) {
                        var4.printStackTrace();
                    }

                    if (items != null && items.length > 0) {
                        availableChests.add(new Chest(items, 2));
                    }
                }

            });
            if (availableChests.size() == 0) {
                return null;
            } else {
                int random;
                if (feast) {
                    random = (new Random()).nextInt(9);
                    return ((Chest)availableChests.get(45 + random)).getItems();
                } else {
                    random = (new Random()).nextInt(45);
                    return ((Chest)availableChests.get(random)).getItems();
                }
            }
        }
    }

    public void fixOrder() {
        List<Chest> fixed = (List)this.chests.stream().sorted(Comparator.comparing(Chest::getNumber)).collect(Collectors.toList());
        this.chests.clear();
        this.chests.addAll(fixed);
    }

    public Inventory chestInventory(Chest chest) {
        Inventory inv = Bukkit.createInventory((InventoryHolder)null, 27, Color.translate("&bChest " + chest.getNumber()));
        if (chest.getItems() != null) {
            inv.setContents(chest.getItems());
        }

        return inv;
    }

    @EventHandler(
            priority = EventPriority.LOWEST
    )
    public void handleInventoryClose(InventoryCloseEvent event) {
        Player player = (Player)event.getPlayer();
        if (event.getInventory().getTitle().contains(Color.translate("&bChest "))) {
            if (event.getInventory().getContents() != null) {
                ItemStack[] items = event.getInventory().getContents();
                String number = ChatColor.stripColor(event.getInventory().getTitle().split(" ")[1]);
                this.updateChestItems(Integer.parseInt(number), items);
                this.saveChestsToConfig();
                player.sendMessage(Color.translate("&bYou have successfully saved items for &f'Chest " + number + "'&b."));
            }
        }
    }

    public List<Chest> getChests() {
        return this.chests;
    }

    public void setChests(List<Chest> chests) {
        this.chests = chests;
    }

    public static ChestsManager getInstance() {
        return instance;
    }
}
