package club.frozed.frozedsg.utils.tasks;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.utils.chat.Color;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.List;

public class SplitedRoadProcessor extends BukkitRunnable {
    private Location center;
    private Processor processor;
    private JavaPlugin plugin;
    private int delay;

    public SplitedRoadProcessor(JavaPlugin plugin, Location center, int maxPerTick, int delay) {
        this.center = center;
        this.processor = new Processor(center, maxPerTick);
        this.delay = delay;
        this.plugin = plugin;
    }

    private SplitedRoadProcessor(JavaPlugin plugin, Location center, int delay, Processor procesor) {
        this.center = center;
        this.processor = procesor;
        this.delay = delay;
        this.plugin = plugin;
    }

    public void run() {
        if (!this.processor.run()) {
            (new SplitedRoadProcessor(this.plugin, this.center, this.delay, this.processor)).runTaskLater(this.plugin, (long)this.delay);
        }
    }

    public static class Processor {
        private World world;
        private Location center;
        private int phase = 0;
        private int processedBlockThisTick;
        private int processingZ;
        private int processingX;
        private int maxPerTick;
        private int length = 600;
        private int y = 63;

        public Processor(Location center, int maxPerTick) {
            this.world = center.getWorld();
            this.center = center;
            this.maxPerTick = maxPerTick;
        }

        public boolean run() {
            Chunk centerChunk = this.center.getChunk();
            Location centerLocation = new Location(centerChunk.getWorld(), 8.0, (double)this.y, 8.0);
            this.processedBlockThisTick = 0;
            if (this.phase == 0) {
                this.processingZ = -1 * this.length;
                this.processingX = 0;
                this.phase = 1;
                Bukkit.getConsoleSender().sendMessage(Color.translate("&b[FrozedSG] &aPhase 1 of road process has begun."));
            }

            Chunk chunk;
            if (this.phase == 1) {
                chunk = centerChunk;

                int x;
                Block block1;
                Block block2;
                for(x = 0; x < 16; ++x) {
                    block1 = this.world.getBlockAt(x, chunk.getX() + 1, chunk.getZ() - 1);
                    block2 = this.world.getBlockAt(x, chunk.getX() + 1, chunk.getZ() + 16);
                    if (!block1.getChunk().isLoaded()) {
                        block1.getChunk().load(true);
                    }

                    if (!block2.getChunk().isLoaded()) {
                        block2.getChunk().load(true);
                    }
                }

                for(x = 0; x < 16; ++x) {
                    block1 = this.world.getBlockAt(chunk.getX() - 1, 1, chunk.getZ() + x);
                    block2 = this.world.getBlockAt(chunk.getX() + 16, 1, chunk.getZ() + x);
                    if (!block1.getChunk().isLoaded()) {
                        block1.getChunk().load(true);
                    }

                    if (!block2.getChunk().isLoaded()) {
                        block2.getChunk().load(true);
                    }
                }

                for(x = -32; x < 32; ++x) {
                    for(int z2 = -32; z2 < 32; ++z2) {
                        ++this.processedBlockThisTick;
                        Location newlocation = new Location(centerChunk.getWorld(), (double)(8 + x), (double)this.y, (double)(8 + z2));
                        int diff = (int)(newlocation.distance(centerLocation) - 24.0);
                        if (diff < 0) {
                            this.clearAbove(chunk.getWorld(), x + 8, this.y, z2 + 8);
                            this.setRoad(chunk.getWorld().getBlockAt(x + 8, this.y, z2 + 8));
                        } else {
                            this.clearAbove(chunk.getWorld(), x + 8, this.y + diff, z2 + 8);
                        }
                    }
                }

                this.phase = 2;
                Bukkit.getConsoleSender().sendMessage(Color.translate("&b[FrozedSG] &aPhase 2 of road process has begun."));
            }

            Iterator var9;
            Entity entity;
            if (this.phase == 2) {
                while(true) {
                    if (this.processingZ >= this.length) {
                        this.phase = 3;
                        this.processingZ = 0;
                        this.processingX = -1 * this.length;
                        Bukkit.getConsoleSender().sendMessage(Color.translate("&b[FrozedSG] &aPhase 3 of road process has begun."));
                        break;
                    }

                    chunk = this.world.getChunkAt(new Location(this.world, this.center.getX(), 100.0, (double)this.processingZ + this.center.getZ()));
                    if (chunk.getZ() != centerChunk.getZ()) {
                        this.fillRow(this.y, Type.Z, this.processingZ);
                        if (this.processedBlockThisTick > this.maxPerTick) {
                            return false;
                        }
                    }

                    var9 = chunk.getWorld().getEntities().iterator();

                    while(var9.hasNext()) {
                        entity = (Entity)var9.next();
                        if (entity instanceof Item) {
                            entity.remove();
                        }
                    }

                    ++this.processingZ;
                }
            }

            if (this.phase == 3) {
                while(true) {
                    if (this.processingX >= this.length) {
                        this.phase = 4;
                        Bukkit.getConsoleSender().sendMessage(Color.translate("&b[FrozedSG] &aRoad process has been finished. &bPLAYERS ARE NOW ABLE TO JOIN THE SERVER."));
                        PotSG.getInstance().setPluginLoading(false);
                        break;
                    }

                    chunk = this.world.getChunkAt(new Location(this.world, this.center.getX() + (double)this.processingX, 100.0, this.center.getZ()));
                    if (chunk.getX() != centerChunk.getX()) {
                        this.fillRow(this.y, Type.X, this.processingX);
                        if (this.processedBlockThisTick > this.maxPerTick) {
                            return false;
                        }
                    }

                    var9 = chunk.getWorld().getEntities().iterator();

                    while(var9.hasNext()) {
                        entity = (Entity)var9.next();
                        if (entity instanceof Item) {
                            entity.remove();
                        }
                    }

                    ++this.processingX;
                }
            }

            return true;
        }

        public void fillRow(int y, Type modifierType, int modifier) {
            int i;
            Block one;
            Block two;
            if (modifierType == Type.X) {
                for(i = 0; i < 16; ++i) {
                    ++this.processedBlockThisTick;
                    this.clearAbove(this.world, modifier + this.center.getBlockX(), y, this.center.getBlockZ() + i);
                    this.setRoad(this.world.getBlockAt(modifier + this.center.getBlockX(), y, this.center.getBlockZ() + i));
                }

                for(i = 1; i < 6; ++i) {
                    one = this.world.getBlockAt(modifier + this.center.getBlockX(), y + i - 1, this.center.getBlockZ() + 15 + i);
                    two = this.world.getBlockAt(modifier + this.center.getBlockX(), y + i - 1, this.center.getBlockZ() - i);
                    if (!one.getRelative(BlockFace.UP).isEmpty() && one.getRelative(BlockFace.UP).getType().isOccluding()) {
                        one.setType(this.getBiomeMaterial(one.getBiome()));
                    }

                    if (!two.getRelative(BlockFace.UP).isEmpty() && two.getRelative(BlockFace.UP).getType().isOccluding()) {
                        two.setType(this.getBiomeMaterial(two.getBiome()));
                    }

                    this.clearAbove(this.world, modifier + this.center.getBlockX(), y + i - 1, this.center.getBlockZ() + 15 + i);
                    this.clearAbove(this.world, modifier + this.center.getBlockX(), y + i - 1, this.center.getBlockZ() - i);
                }
            } else {
                for(i = 0; i < 16; ++i) {
                    ++this.processedBlockThisTick;
                    this.clearAbove(this.world, this.center.getBlockX() + i, y, modifier + this.center.getBlockZ());
                    this.setRoad(this.world.getBlockAt(this.center.getBlockX() + i, y, modifier + this.center.getBlockZ()));
                }

                for(i = 1; i < 6; ++i) {
                    one = this.world.getBlockAt(this.center.getBlockX() + 15 + i, y + i - 1, modifier + this.center.getBlockZ());
                    two = this.world.getBlockAt(this.center.getBlockX() - i, y + i - 1, modifier + this.center.getBlockZ());
                    if (!one.getRelative(BlockFace.UP).isEmpty() && one.getRelative(BlockFace.UP).getType().isOccluding()) {
                        one.setType(this.getBiomeMaterial(one.getBiome()));
                    }

                    if (!two.getRelative(BlockFace.UP).isEmpty() && two.getRelative(BlockFace.UP).getType().isOccluding()) {
                        two.setType(this.getBiomeMaterial(two.getBiome()));
                    }

                    this.clearAbove(this.world, this.center.getBlockX() + 15 + i, y + i - 1, modifier + this.center.getBlockZ());
                    this.clearAbove(this.world, this.center.getBlockX() - i, y + i - 1, modifier + this.center.getBlockZ());
                }
            }

        }

        public void setRoad(Block block) {
            block.setType(this.getRoadMaterial());

            for(int i = 0; i < 10; ++i) {
                (block = block.getRelative(BlockFace.DOWN)).setType(Material.BEDROCK);
            }

        }

        public void clearAbove(World world, int bx, int by, int bz) {
            for(int y = by + 1; y < 256; ++y) {
                ++this.processedBlockThisTick;
                world.getBlockAt(bx, y, bz).setType(Material.AIR);
            }

        }

        public Material getBiomeMaterial(Biome biome) {
            switch (biome) {
                case DESERT:
                    return Material.SAND;
                default:
                    return Material.GRASS;
            }
        }

        public Material getRoadMaterial() {
            List<String> materials = PotSG.getInstance().getConfiguration("config").getStringList("ROADS.ROAD-MATERIALS");
            int r = RandomUtils.nextInt(materials.size());
            return Material.valueOf((String)materials.get(r));
        }
    }

    public static enum Type {
        X("X", 0),
        Z("Z", 1);

        private Type(String s, int n) {
        }
    }
}
