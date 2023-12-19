package club.frozed.frozedsg.managers;

import club.frozed.frozedsg.player.PlayerData;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager
{
    public static PlayerDataManager instance;
    private Map<UUID, PlayerData> playerDatas;
    
    public PlayerDataManager() {
        this.playerDatas = new HashMap<UUID, PlayerData>();
        PlayerDataManager.instance = this;
    }
    
    public PlayerData getByUUID(final UUID uuid) {
        return this.playerDatas.getOrDefault(uuid, null);
    }
    
    public void handleCreateData(final UUID uuid) {
        if (!this.playerDatas.containsKey(uuid)) {
            this.playerDatas.put(uuid, new PlayerData(uuid));
        }
    }
    
    public void saveData(final PlayerData data, final String info, final String value, final int amount) {
        if (!this.hasData(info)) {
            return;
        }
        final Document document = MongoManager.getInstance().getStatsCollection().find(Filters.eq("info", info)).first();
        document.put(value, (Object)amount);
        MongoManager.getInstance().getStatsCollection().replaceOne(Filters.eq("info", info), document);
        data.load();
    }
    
    private boolean hasData(final String info) {
        final Document document = MongoManager.getInstance().getStatsCollection().find(Filters.eq("info", info)).first();
        return document != null;
    }
    
    public Map<UUID, PlayerData> getPlayerDatas() {
        return this.playerDatas;
    }
    
    public void setPlayerDatas(final Map<UUID, PlayerData> playerDatas) {
        this.playerDatas = playerDatas;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PlayerDataManager)) {
            return false;
        }
        final PlayerDataManager other = (PlayerDataManager)o;
        if (!other.canEqual(this)) {
            return false;
        }
        final Object this$playerDatas = this.getPlayerDatas();
        final Object other$playerDatas = other.getPlayerDatas();
        if (this$playerDatas == null) {
            if (other$playerDatas == null) {
                return true;
            }
        }
        else if (this$playerDatas.equals(other$playerDatas)) {
            return true;
        }
        return false;
    }
    
    protected boolean canEqual(final Object other) {
        return other instanceof PlayerDataManager;
    }
    
    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $playerDatas = this.getPlayerDatas();
        result = result * 59 + (($playerDatas == null) ? 43 : $playerDatas.hashCode());
        return result;
    }
    
    @Override
    public String toString() {
        return "PlayerDataManager(playerDatas=" + this.getPlayerDatas() + ")";
    }
    
    public static PlayerDataManager getInstance() {
        return PlayerDataManager.instance;
    }
}
