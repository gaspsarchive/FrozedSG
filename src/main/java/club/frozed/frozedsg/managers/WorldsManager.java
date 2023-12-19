//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package club.frozed.frozedsg.managers;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.events.SGWorldLoad;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.World.Environment;

import java.io.File;
import java.io.IOException;

public class WorldsManager {
    public static WorldsManager instance;
    private World lobbyWorld = null;
    private World gameWorld = null;
    private World gameWorldForEdit = null;

    public WorldsManager() {
        instance = this;
        this.handleWorldsCopy();
        if (PotSG.getInstance().getConfiguration("config").getBoolean("DEBUG")) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "registered WorldsManager");
        }

    }

    private void registerWorlds(boolean makeRoads) {
        String lobbyWorldName = PotSG.getInstance().getConfiguration("config").getString("WORLDS.LOBBY");
        String gameWorldName = PotSG.getInstance().getConfiguration("config").getString("WORLDS.GAME");
        this.lobbyWorld = Bukkit.createWorld((new WorldCreator(lobbyWorldName)).environment(Environment.NORMAL).type(WorldType.NORMAL));
        this.lobbyWorld.setGameRuleValue("doFireTick", "false");
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PotSG.getInstance(), () -> {
            this.gameWorld = Bukkit.createWorld((new WorldCreator(gameWorldName + "_FOR_USE")).environment(Environment.NORMAL).type(WorldType.NORMAL));
            this.gameWorld.setGameRuleValue("doFireTick", "false");
            Bukkit.getServer().getPluginManager().callEvent(new SGWorldLoad(this.gameWorld));
        }, 20L);
    }

    private void handleWorldsCopy() {
        if (PotSG.getInstance().getConfiguration("config").getBoolean("DEBUG")) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "WorldsManager handleWorldsCopy running");
        }

        String gameWorldName = PotSG.getInstance().getConfiguration("config").getString("WORLDS.GAME");
        this.handleWorldDelete(gameWorldName + "_FOR_USE");
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PotSG.getInstance(), () -> {
            this.handleWorldCopy(gameWorldName, gameWorldName + "_FOR_USE");
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PotSG.getInstance(), () -> {
                this.registerWorlds(true);
            }, 20L);
        }, 20L);
    }

    public void saveCurrentWorld() {
        this.getGameWorld().save();
        Bukkit.unloadWorld(this.getGameWorld().getName(), false);
        String gameWorldName = PotSG.getInstance().getConfiguration("config").getString("WORLDS.GAME");
        this.handleWorldDelete(gameWorldName);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PotSG.getInstance(), () -> {
            this.handleWorldCopy(this.getGameWorld().getName(), gameWorldName);
        }, 40L);
        this.registerWorlds(false);
    }

    private void handleWorldCopy(String worldToCopy, String worldToPaste) {
        File srcDir = new File(worldToCopy);
        File destDir = new File(worldToPaste);

        try {
            FileUtils.copyDirectory(srcDir, destDir);
        } catch (IOException var6) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "World '" + worldToCopy + "' doesn't exists!");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Brand new world will be created!");
        }

    }

    private void handleWorldDelete(String world) {
        Bukkit.unloadWorld(world, true);

        try {
            FileUtils.deleteDirectory(new File(world));
        } catch (IOException var3) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "World '" + world + "' doesn't exists!");
        }

    }

    public World getLobbyWorld() {
        return this.lobbyWorld;
    }

    public World getGameWorld() {
        return this.gameWorld;
    }

    public World getGameWorldForEdit() {
        return this.gameWorldForEdit;
    }

    public void setLobbyWorld(World lobbyWorld) {
        this.lobbyWorld = lobbyWorld;
    }

    public void setGameWorld(World gameWorld) {
        this.gameWorld = gameWorld;
    }

    public void setGameWorldForEdit(World gameWorldForEdit) {
        this.gameWorldForEdit = gameWorldForEdit;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof WorldsManager)) {
            return false;
        } else {
            WorldsManager other = (WorldsManager)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label47: {
                    Object this$lobbyWorld = this.getLobbyWorld();
                    Object other$lobbyWorld = other.getLobbyWorld();
                    if (this$lobbyWorld == null) {
                        if (other$lobbyWorld == null) {
                            break label47;
                        }
                    } else if (this$lobbyWorld.equals(other$lobbyWorld)) {
                        break label47;
                    }

                    return false;
                }

                Object this$gameWorld = this.getGameWorld();
                Object other$gameWorld = other.getGameWorld();
                if (this$gameWorld == null) {
                    if (other$gameWorld != null) {
                        return false;
                    }
                } else if (!this$gameWorld.equals(other$gameWorld)) {
                    return false;
                }

                Object this$gameWorldForEdit = this.getGameWorldForEdit();
                Object other$gameWorldForEdit = other.getGameWorldForEdit();
                if (this$gameWorldForEdit == null) {
                    if (other$gameWorldForEdit != null) {
                        return false;
                    }
                } else if (!this$gameWorldForEdit.equals(other$gameWorldForEdit)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof WorldsManager;
    }

    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        Object $lobbyWorld = this.getLobbyWorld();
        result = result * 59 + ($lobbyWorld == null ? 43 : $lobbyWorld.hashCode());
        Object $gameWorld = this.getGameWorld();
        result = result * 59 + ($gameWorld == null ? 43 : $gameWorld.hashCode());
        Object $gameWorldForEdit = this.getGameWorldForEdit();
        result = result * 59 + ($gameWorldForEdit == null ? 43 : $gameWorldForEdit.hashCode());
        return result;
    }

    public String toString() {
        return "WorldsManager(lobbyWorld=" + this.getLobbyWorld() + ", gameWorld=" + this.getGameWorld() + ", gameWorldForEdit=" + this.getGameWorldForEdit() + ")";
    }

    public static WorldsManager getInstance() {
        return instance;
    }
}
