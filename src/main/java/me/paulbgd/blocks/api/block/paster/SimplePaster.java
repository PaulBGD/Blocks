package me.paulbgd.blocks.api.block.paster;

import me.paulbgd.blocks.BlocksLanguage;
import me.paulbgd.blocks.api.block.Blocks;
import me.paulbgd.blocks.utils.BlockUtils;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;

/**
 * A simple paster, just for pasting blocks without anything special.
 */
public class SimplePaster implements BlockPaster {
    @Override
    public void handle(Blocks blocks, Block location, CommandSender paster, boolean air) {
        BlockUtils.paste(blocks, location, air);
        paster.sendMessage(ChatColor.GREEN + BlocksLanguage.PASTED);
    }
}
