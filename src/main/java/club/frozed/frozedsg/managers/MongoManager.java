package club.frozed.frozedsg.managers;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.utils.chat.Color;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Collections;

public class MongoManager
{
    public static MongoManager instance;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> statsCollection;
    
    public MongoManager() {
        MongoManager.instance = this;
        try {
            if (PotSG.getInstance().getConfiguration("config").getBoolean("MONGODB.AUTHENTICATION.ENABLED")) {
                final MongoCredential credential = MongoCredential.createCredential(PotSG.getInstance().getConfiguration("config").getString("MONGODB.AUTHENTICATION.USERNAME"), PotSG.getInstance().getConfiguration("config").getString("MONGODB.AUTHENTICATION.DATABASE"), PotSG.getInstance().getConfiguration("config").getString("MONGODB.AUTHENTICATION.PASSWORD").toCharArray());
                this.mongoClient = new MongoClient(new ServerAddress(PotSG.getInstance().getConfiguration("config").getString("MONGODB.ADDRESS"), PotSG.getInstance().getConfiguration("config").getInt("MONGODB.PORT")), Collections.singletonList(credential));
            }
            else {
                this.mongoClient = new MongoClient(PotSG.getInstance().getConfiguration("config").getString("MONGODB.ADDRESS"), PotSG.getInstance().getConfiguration("config").getInt("MONGODB.PORT"));
            }
            this.mongoDatabase = this.mongoClient.getDatabase(PotSG.getInstance().getConfiguration("config").getString("MONGODB.DATABASE"));
            this.statsCollection = this.mongoDatabase.getCollection("Statistics");
        }
        catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(Color.translate("&b[FrozedSG] &cFailed to connect to MongoDB"));
            Bukkit.getServer().getPluginManager().disablePlugin((Plugin)PotSG.getInstance());
        }
    }
    
    public MongoClient getMongoClient() {
        return this.mongoClient;
    }
    
    public MongoDatabase getMongoDatabase() {
        return this.mongoDatabase;
    }
    
    public MongoCollection<Document> getStatsCollection() {
        return this.statsCollection;
    }
    
    public static MongoManager getInstance() {
        return MongoManager.instance;
    }
}
