package club.frozed.frozedsg.utils.leaderboards;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.managers.MongoManager;
import com.mongodb.BasicDBObject;
import org.bson.Document;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class Leaderboard
{
    private String name;
    private String mongoValue;
    private Material material;
    private List<String> formats;
    private boolean enabled;
    
    public Leaderboard(final Material material, final String name, final String mongoValue, final boolean enabled) {
        this.formats = new ArrayList<String>();
        this.name = name;
        this.mongoValue = mongoValue;
        this.material = material;
        this.enabled = enabled;
        this.load();
    }
    
    public void load() {
        this.formats.clear();
        final List<Document> documents = MongoManager.getInstance().getStatsCollection().find().limit(10).sort(new BasicDBObject(this.mongoValue, -1)).into(new ArrayList<Document>());
        int pos = 1;
        if (documents == null) {
            return;
        }
        for (final Document document : documents) {
            String format = PotSG.getInstance().getConfiguration("config").getString("LEADERBOARD-FORMAT");
            format = format.replace("<pos>", String.valueOf(pos));
            format = format.replace("<name>", (document.getString("name") == null) ? "null" : document.getString("name"));
            format = format.replace("<amount>", document.get(this.mongoValue).toString());
            if (document.getInteger(this.mongoValue) > 0) {
                this.formats.add(format);
            }
            ++pos;
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getMongoValue() {
        return this.mongoValue;
    }
    
    public Material getMaterial() {
        return this.material;
    }
    
    public List<String> getFormats() {
        return this.formats;
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setMongoValue(final String mongoValue) {
        this.mongoValue = mongoValue;
    }
    
    public void setMaterial(final Material material) {
        this.material = material;
    }
    
    public void setFormats(final List<String> formats) {
        this.formats = formats;
    }
    
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
}
