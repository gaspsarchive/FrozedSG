package club.frozed.frozedsg.utils.runnables;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.border.BorderManager;
import club.frozed.frozedsg.managers.GameManager;
import club.frozed.frozedsg.managers.MongoManager;
import club.frozed.frozedsg.managers.PlayerManager;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class DataRunnable extends BukkitRunnable {
    public static DataRunnable instance;
    public String name;
    public DBCollection informationCollection = MongoManager.getInstance().getMongoClient().getDB(PotSG.getInstance().getConfiguration("config").getString("MONGODB.DATABASE")).getCollection("FrozedSGStats");

    public DataRunnable() {
        instance = this;
        this.name = Bukkit.getServerName();
        this.runTaskTimerAsynchronously(PotSG.getInstance(), 2L, 2L);
        this.loadDataIfExists();
    }

    public void run() {
        this.saveData();
    }

    public boolean hasData() {
        DBObject r = new BasicDBObject("name", this.name);
        DBObject found = this.informationCollection.findOne(r);
        return found != null;
    }

    public void saveData() {
        DBObject r = new BasicDBObject("name", this.name);
        DBObject found = this.informationCollection.findOne(r);
        if (this.hasData()) {
            DBObject obj = new BasicDBObject("name", this.name);
            obj.put("map", PotSG.getInstance().isPluginLoading());
            if (GameManager.getInstance().getGameRunnable() != null) {
                obj.put("gametime", GameManager.getInstance().getGameRunnable().getTime());
            } else {
                obj.put("gametime", "00:00");
            }

            if (BorderManager.getInstance().getBorder() != null) {
                obj.put("border", BorderManager.getInstance().getBorder().getSize() + " " + BorderManager.getInstance().getBorderInfo());
            }

            obj.put("players", PlayerManager.getInstance().getGamePlayers().size());
            this.informationCollection.update(found, obj);
        }

    }

    public int getInt(Object o) {
        return Integer.parseInt(String.valueOf(o));
    }

    public void createData() {
        DBObject obj = new BasicDBObject("name", this.name);
        obj.put("map", PotSG.getInstance().isPluginLoading());
        if (GameManager.getInstance().getGameRunnable() != null) {
            obj.put("gametime", GameManager.getInstance().getGameRunnable().getTime());
        } else {
            obj.put("gametime", "00:00");
        }

        if (BorderManager.getInstance().getBorder() != null) {
            obj.put("border", BorderManager.getInstance().getBorder().getSize() + " " + BorderManager.getInstance().getBorderInfo());
        }

        obj.put("players", PlayerManager.getInstance().getGamePlayers().size());
        this.informationCollection.insert(new DBObject[]{obj});
    }

    public void loadDataIfExists() {
        if (!this.hasData()) {
            this.createData();
        }

    }

    public String getName() {
        return this.name;
    }

    public DBCollection getInformationCollection() {
        return this.informationCollection;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInformationCollection(DBCollection informationCollection) {
        this.informationCollection = informationCollection;
    }

    public static DataRunnable getInstance() {
        return instance;
    }
}
