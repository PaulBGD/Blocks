package me.paulbgd.blocks.api.block;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.paulbgd.blocks.plugin.BlocksPlugin;
import me.paulbgd.blocks.api.BlocksAPI;
import me.paulbgd.blocks.api.block.data.BlockData;
import me.paulbgd.blocks.api.block.loader.BlocksLoader;
import me.paulbgd.blocks.api.block.paster.BlockPaster;
import net.minecraft.util.org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

@Data
@EqualsAndHashCode(callSuper = false)
public class Blocks extends ArrayList<Block> {

    private final HashMap<Biome, List<String>> biomes = new HashMap<>();

    private int minY;
    private int maxY;
    private int minZ;
    private int maxZ;
    private int minX;
    private int maxX;

    @Deprecated
    public Blocks(org.bukkit.block.Block start, List<org.bukkit.block.Block> blocks) {
        this();

        addBlocks(start, blocks);
    }

    public Blocks(List<Block> blocks) {
        this();

        addBlocks(blocks);
    }

    public Blocks() {
        super();
    }

    public static Blocks load(File file) throws IOException {
        return Blocks.load(file, BlocksType.BLOCKS);
    }

    public static Blocks load(File file, BlocksLoader blocksLoader) throws IOException {
        return Blocks.load(new FileInputStream(file), blocksLoader);
    }

    public static Blocks load(InputStream inputStream) throws IOException {
        return Blocks.load(inputStream, BlocksType.BLOCKS);
    }

    /**
     * @param inputStream - data containing all of the blocks
     * @return all of the blocks + their data
     */
    public static Blocks load(InputStream inputStream, BlocksLoader blocksLoader) throws IOException {
        Blocks blocks = blocksLoader.load(inputStream);
        IOUtils.closeQuietly(inputStream);
        return blocks;
    }

    public int getHeight() {
        return maxY - minY;
    }

    public int getLength() {
        return maxZ - minZ;
    }

    public int getWidth() {
        return maxX - minX;
    }

    @Deprecated
    public void addBlocks(org.bukkit.block.Block start, List<org.bukkit.block.Block> blocks) {
        List<Block> blockList = new ArrayList<>(blocks.size());
        for (org.bukkit.block.Block block : blocks) {
            if (!start.getWorld().equals(block.getWorld())) {
                throw new IllegalArgumentException("There cannot be two different worlds!");
            }
            String key = (block.getX() - start.getX()) + "!" + (block.getZ() - start.getZ());
            Biome biome = block.getBiome();
            if (!this.biomes.containsKey(biome)) {
                this.biomes.put(biome, new ArrayList<String>());
            }
            this.biomes.get(biome).add(key);
            BlockPosition position = new BlockPosition(block.getX() - start.getX(), block.getY() - start.getY(), block.getZ() - start.getZ());
            BlockData blockData = BlockData.loadData(block.getState());
            blockList.add(new Block(position, blockData));
        }
        this.addBlocks(blockList);
    }

    public void addBlocks(List<Block> blocks) {
        this.addAll(blocks);
    }

    @Override
    public boolean add(Block block) {
        BlockPosition position = block.getPosition();
        if (position.getRelativeY() < minY) {
            this.minY = position.getRelativeY();
        } else if (position.getRelativeY() > maxY) {
            this.maxY = position.getRelativeY();
        }
        if (position.getRelativeZ() < minZ) {
            this.minZ = position.getRelativeZ();
        } else if (position.getRelativeZ() > maxZ) {
            this.maxZ = position.getRelativeZ();
        }
        if (position.getRelativeX() < minX) {
            this.minX = position.getRelativeX();
        } else if (position.getRelativeX() > maxX) {
            this.maxX = position.getRelativeX();
        }
        return super.add(block);
    }

    @Override
    public boolean addAll(Collection<? extends Block> blocks) {
        for (Block block : blocks) {
            this.add(block);
        }
        return true;
    }

    public void paste(org.bukkit.block.Block block) {
        this.paste(block, Bukkit.getConsoleSender());
    }

    public void paste(org.bukkit.block.Block block, CommandSender sender) {
        this.paste(block, sender, true, false);
    }

    public void paste(org.bukkit.block.Block block, boolean air, boolean biomes) {
        this.paste(block, Bukkit.getConsoleSender(), air, biomes);
    }

    public void paste(org.bukkit.block.Block block, CommandSender sender, boolean air, boolean biomes) {
        this.paste(block, BlocksAPI.getDefaultPaster(), sender, air, biomes);
    }

    public void paste(org.bukkit.block.Block location, BlockPaster paster) {
        this.paste(location, paster, Bukkit.getConsoleSender());
    }

    public void paste(org.bukkit.block.Block location, BlockPaster paster, CommandSender sender) {
        this.paste(location, paster, sender, true, false);
    }

    public void paste(org.bukkit.block.Block location, BlockPaster paster, CommandSender sender, boolean air, boolean biomes) {
        paster.handle(this, location, sender, air);

        if (biomes && !this.biomes.isEmpty()) {
            // let's set some biomes!
            World world = location.getWorld();
            int x = location.getX(), z = location.getZ();
            for (Map.Entry<Biome, List<String>> entry : this.biomes.entrySet()) {
                for (String value : entry.getValue()) {
                    String[] position = value.split("!");
                    world.setBiome(Integer.parseInt(position[0]) + x, Integer.parseInt(position[1]) + z, entry.getKey());
                }
            }
        }
    }

    public void save(File file) throws IOException {
        this.save(new FileOutputStream(file));
    }

    public void save(File file, BlocksLoader blocksLoader) throws IOException {
        save(new FileOutputStream(file), blocksLoader);
    }

    public void save(OutputStream outputStream) throws IOException {
        this.save(outputStream, BlocksType.BLOCKS);
    }

    public void save(final OutputStream outputStream, final BlocksLoader blocksLoader) throws IOException {
        if (Bukkit.isPrimaryThread()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        save(outputStream, blocksLoader);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.runTaskAsynchronously(BlocksPlugin.getPlugin());
            return;
        }
        blocksLoader.save(this, outputStream);
        IOUtils.closeQuietly(outputStream);
    }

}
