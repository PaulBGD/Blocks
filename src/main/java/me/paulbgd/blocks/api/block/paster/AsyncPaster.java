package me.paulbgd.blocks.api.block.paster;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import lombok.Data;
import me.paulbgd.blocks.BlocksLanguage;
import me.paulbgd.blocks.plugin.BlocksPlugin;
import me.paulbgd.blocks.api.block.Blocks;
import me.paulbgd.blocks.utils.BlockUtils;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Pastes blocks "async", or in chunks. Can be modified by being extended.
 */
public class AsyncPaster extends BukkitRunnable implements BlockPaster {

    private final List<QueueItem> queue = new ArrayList<>();

    protected long speed = 1l;
    protected int chunkSize = 1000;

    public AsyncPaster() {
        this.runTaskTimer(BlocksPlugin.getPlugin(), speed, speed);
    }

    @Override
    public void handle(Blocks blocks, Block location, CommandSender paster, boolean air) {
        paster.sendMessage(ChatColor.AQUA + String.format(BlocksLanguage.PROCESSING_X_BLOCKS, blocks.size()));
        List<me.paulbgd.blocks.api.block.Block> sorted = new ArrayList<>(blocks);
        Collections.sort(sorted, new Comparator<me.paulbgd.blocks.api.block.Block>() {
            @Override
            public int compare(me.paulbgd.blocks.api.block.Block o1, me.paulbgd.blocks.api.block.Block o2) {
                return Integer.compare(o1.getPosition().getRelativeY(), o2.getPosition().getRelativeY());
            }
        });
        List<List<me.paulbgd.blocks.api.block.Block>> toProcess = Lists.partition(sorted, chunkSize);
        for (int i = 0; i < toProcess.size(); i++) {
            queue.add(new QueueItem(toProcess.get(i), location, air, paster, i, toProcess.size() - 1));
        }
    }

    @Override
    public void run() {
        if (queue.isEmpty()) {
            return;
        }
        QueueItem item = queue.remove(0);
        BlockUtils.paste(item.getBlocks(), item.getLocation(), item.air);
        if (item.getPosition() == item.totalSize && (!(item.getPaster() instanceof Player) || ((Player) item.getPaster()).isOnline())) {
            item.getPaster().sendMessage(ChatColor.GREEN + BlocksLanguage.PASTED);
        }
    }

    public void finish() {
        this.cancel();
    }

    @Data
    public class QueueItem {
        private final List<me.paulbgd.blocks.api.block.Block> blocks;
        private final Block location;
        private final boolean air;
        private final CommandSender paster;
        private final double position;
        private final double totalSize;
    }

}
