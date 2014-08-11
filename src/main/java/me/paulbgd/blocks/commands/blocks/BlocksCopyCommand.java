package me.paulbgd.blocks.commands.blocks;

import java.util.List;
import me.paulbgd.blocks.BlocksLanguage;
import me.paulbgd.blocks.api.BlocksAPI;
import me.paulbgd.blocks.api.block.Block;
import me.paulbgd.blocks.api.block.Blocks;
import me.paulbgd.blocks.api.player.BlocksPlayerData;
import me.paulbgd.blocks.commands.Command;
import me.paulbgd.blocks.utils.BlockUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public class BlocksCopyCommand extends Command.Subcommand {

    public BlocksCopyCommand() {
        super(new String[]{"copy", "clone"}, new Permission("blocks.copy"), BlocksLanguage.COPY_COMMAND_INFO);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sendMessage(String.format("%s%s", ChatColor.RED, BlocksLanguage.NOT_PLAYER), sender);
            return;
        }
        BlocksPlayerData playerData = BlocksAPI.getPlayerData((Player) sender);
        if (playerData.getPointOne() == null) {
            sender.sendMessage(ChatColor.RED + String.format(BlocksLanguage.NOT_SET, "1"));
            return;
        } else if (playerData.getPointTwo() == null) {
            sender.sendMessage(ChatColor.RED + String.format(BlocksLanguage.NOT_SET, "2"));
            return;
        }
        List<Block> blocks = BlockUtils.getAllBlocks(((Player) sender).getLocation().getBlock(), playerData.getPointOne(), playerData.getPointTwo());
        String clipboardMessage;
        if (args.length == 1 && args[0].equalsIgnoreCase("add") && playerData.getClipboard() != null) {
            playerData.getClipboard().addBlocks(blocks);
            clipboardMessage = BlocksLanguage.ADDED_TO_CLIPBOARD;
        } else {
            playerData.setClipboard(new Blocks(blocks));
            clipboardMessage = BlocksLanguage.COPIED_TO_CLIPBOARD;
        }
        sender.sendMessage(String.format("%s%s", ChatColor.GREEN, clipboardMessage));
    }
}
