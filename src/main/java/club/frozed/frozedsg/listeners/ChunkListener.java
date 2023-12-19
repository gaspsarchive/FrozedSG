package club.frozed.frozedsg.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkListener implements Listener
{
    @EventHandler
    public void onChunkUnload(final ChunkUnloadEvent event) {
        if (event.getWorld().getName().equalsIgnoreCase("na-psg-1")) {
            event.setCancelled(true);
        }
    }
}
