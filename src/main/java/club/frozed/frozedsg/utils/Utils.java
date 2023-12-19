package club.frozed.frozedsg.utils;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.border.Border;
import club.frozed.frozedsg.border.BorderManager;
import club.frozed.frozedsg.enums.PlayerState;
import club.frozed.frozedsg.managers.GameManager;
import club.frozed.frozedsg.managers.PlayerDataManager;
import club.frozed.frozedsg.managers.PlayerManager;
import club.frozed.frozedsg.utils.chat.Color;
import club.frozed.frozedsg.utils.chat.JSONMessage;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Utils {
    public Utils() {
    }

    public static List<Player> getOnlinePlayers() {
        return new ArrayList(Bukkit.getServer().getOnlinePlayers());
    }

    public static void clearPlayer(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents((ItemStack[])null);
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setLevel(0);
        player.setGameMode(GameMode.SURVIVAL);
        player.getActivePotionEffects().forEach((potionEffect) -> {
            player.removePotionEffect(potionEffect.getType());
        });
    }

    public static String formatTime(int timer) {
        int hours = timer / 3600;
        int secondsLeft = timer - hours * 3600;
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft - minutes * 60;
        String formattedTime = "";
        if (hours > 0) {
            if (hours < 10) {
                formattedTime = formattedTime + "0";
            }

            formattedTime = formattedTime + hours + ":";
        }

        if (minutes < 10) {
            formattedTime = formattedTime + "0";
        }

        formattedTime = formattedTime + minutes + ":";
        if (seconds < 10) {
            formattedTime = formattedTime + "0";
        }

        formattedTime = formattedTime + seconds;
        return formattedTime;
    }

    public static void broadcastMessage(String message) {
        getOnlinePlayers().forEach((player) -> {
            player.sendMessage(Color.translate(message));
        });
    }

    public static void broadcastMessage(String message, boolean prefix) {
        getOnlinePlayers().forEach((player) -> {
            player.sendMessage(prefix ? GameManager.getInstance().getGamePrefix() + Color.translate(message) : Color.translate(message));
        });
    }

    public static void broadcastMessageToSpectators(String message) {
        getOnlinePlayers().stream().filter((player) -> {
            return PlayerManager.getInstance().isSpectator(player);
        }).forEach((player) -> {
            player.sendMessage(Color.translate(message));
        });
    }

    public static void broadcastMessageToStaff(String message) {
        getOnlinePlayers().stream().filter((player) -> {
            return player.hasPermission("frozedsg.staff");
        }).forEach((player) -> {
            player.sendMessage(Color.translate(message));
        });
    }

    public static void playSound(Sound sound) {
        getOnlinePlayers().forEach((player) -> {
            player.playSound(player.getLocation(), sound, 2.0F, 2.0F);
        });
    }

    public static boolean isInteger(String in) {
        try {
            Integer.parseInt(in);
            return true;
        } catch (NumberFormatException var2) {
            return false;
        }
    }

    public static void connectPlayer(Player player, String server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (IOException var5) {
            var5.printStackTrace();
        }

        player.sendPluginMessage(PotSG.getInstance(), "BungeeCord", b.toByteArray());
    }

    public static void randomPlayer(Player player) {
        List<Player> online = (List)getOnlinePlayers().stream().filter((player1) -> {
            return PlayerDataManager.getInstance().getByUUID(player1.getUniqueId()) != null && PlayerDataManager.getInstance().getByUUID(player1.getUniqueId()).getState().equals(PlayerState.INGAME) && player1 != player;
        }).collect(Collectors.toList());
        if (online.size() != 0) {
            Player target = (Player)online.get((new Random()).nextInt(online.size()));
            player.teleport(target);
            player.sendMessage(GameManager.getInstance().getGamePrefix() + Color.translate("&bYou've been teleported to &f'" + target.getName() + "'&b."));
        } else {
            player.sendMessage(GameManager.getInstance().getGamePrefix() + Color.translate("&bNo players found who are playing this game."));
        }

    }

    public static boolean isInventoryEmpty(Inventory inv) {
        ItemStack[] contents;
        int length = (contents = inv.getContents()).length;

        for(int i = 0; i < length; ++i) {
            ItemStack item = contents[i];
            if (item != null && item.getType() != Material.AIR) {
                return false;
            }
        }

        return true;
    }

    public static Inventory createInventoryOther(Player player) {
        Inventory inv = Bukkit.createInventory((InventoryHolder)null, 54, "Inventory inspect");
        ItemStack[] contents = player.getInventory().getContents();
        ItemStack[] armor = player.getInventory().getArmorContents();
        inv.setContents(contents);
        inv.setItem(45, armor[3]);
        inv.setItem(46, armor[2]);
        inv.setItem(47, armor[1]);
        inv.setItem(48, armor[0]);

        for(int i = 36; i <= 49; ++i) {
            if (!Arrays.asList(45, 46, 47, 48).contains(i)) {
                inv.setItem(i, (new ItemBuilder(Material.STAINED_GLASS_PANE)).setDurability(5).setName("&7Inventory (" + player.getName() + ")").toItemStack());
            }
        }

        return inv;
    }

    public static ItemStack createGlass(String name) {
        ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)5);
        ItemMeta itemmeta = item.getItemMeta();
        itemmeta.setDisplayName(name);
        item.setItemMeta(itemmeta);
        return item;
    }

    public static void sendGlobalMessage(Player player, String message) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("Message");
        output.writeUTF("ALL");
        output.writeUTF(message);
        player.sendPluginMessage(PotSG.getInstance(), "BungeeCord", output.toByteArray());
    }

    public static void sendGlobalClickableMessage(Player player, String message, String command) {
        ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();
        dataOutput.writeUTF("MessageRaw");
        dataOutput.writeUTF("ALL");
        dataOutput.writeUTF(JSONMessage.create(Color.translate(message)).tooltip(Color.translate(PotSG.getInstance().getConfiguration("config").getString("ANNOUNCE.HOVER"))).runCommand("/" + PotSG.getInstance().getConfiguration("config").getString("ANNOUNCE.CMD")).toString());
        player = (Player)Iterables.getFirst(Bukkit.getOnlinePlayers(), (Object)null);
        if (player != null) {
            player.sendPluginMessage(PotSG.getInstance(), "BungeeCord", dataOutput.toByteArray());
        }

    }

    public static int getNextBorderDefault() {
        Border border = BorderManager.getInstance().getBorder();
        if (border == null) {
            return 25;
        } else {
            int size = border.getSize();
            int nextsize = 0;
            if (size > 500) {
                nextsize = size - 500;
            } else if (size <= 500 && size > 100) {
                nextsize = 100;
            } else if (size == 100) {
                nextsize = 50;
            } else if (size == 50) {
                nextsize = 25;
            } else if (size == 50) {
                nextsize = 25;
            } else if (size == 25) {
                nextsize = 10;
            }

            return nextsize;
        }
    }
}
