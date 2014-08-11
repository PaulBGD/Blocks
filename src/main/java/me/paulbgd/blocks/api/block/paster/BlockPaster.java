package me.paulbgd.blocks.api.block.paster;

import me.paulbgd.blocks.api.block.Blocks;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;

/**
 * Represents a way to paste a set of blocks at a specific location.
 */
public interface BlockPaster {

    public void handle(Blocks blocks, Block location, CommandSender paster, boolean air);

}
