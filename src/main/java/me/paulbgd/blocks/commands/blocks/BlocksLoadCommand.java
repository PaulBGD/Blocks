package me.paulbgd.blocks.commands.blocks;

import java.io.File;
import java.io.IOException;
import me.paulbgd.blocks.BlocksLanguage;
import me.paulbgd.blocks.plugin.BlocksPlugin;
import me.paulbgd.blocks.api.BlocksAPI;
import me.paulbgd.blocks.api.block.Blocks;
import me.paulbgd.blocks.api.block.BlocksType;
import me.paulbgd.blocks.api.block.loader.BlocksLoader;
import me.paulbgd.blocks.api.player.BlocksPlayerData;
import me.paulbgd.blocks.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public class BlocksLoadCommand extends Command.Subcommand {

    public BlocksLoadCommand() {
        super(new String[]{"load"}, new Permission("blocks.load"), BlocksLanguage.LOAD_COMMAND_INFO);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sendMessage(String.format("%s%s", ChatColor.RED, BlocksLanguage.NOT_PLAYER), sender);
            return;
        }
        BlocksPlayerData playerData = BlocksAPI.getPlayerData((Player) sender);
        if (args.length < 1) {
            sender.sendMessage(String.format("%s%s: /blocks load <name> [add]", ChatColor.RED, BlocksLanguage.USAGE));
            return;
        }
        boolean add = false;
        BlocksLoader blocksType = BlocksType.BLOCKS;
        for (int i = 0, argsLength = args.length; i < argsLength; i++) {
            String arg = args[i];
            if (i != 0) {
                // is name
                if (arg.equals("add")) {
                    add = true;
                } else if (arg.startsWith("type:")) {
                    switch (arg.split(":")[1]) {
                        case "blocks":
                            blocksType = BlocksType.BLOCKS;
                            break;
                        case "schematic":
                        case "schem":
                            blocksType = BlocksType.SCHEMATIC;
                    }
                }
            }
        }
        String name = args[0];
        if (!name.toLowerCase().endsWith("." + blocksType.getName())) {
            name = name + "." + blocksType.getName();
        }
        File file = new File(BlocksPlugin.getBlocksFolder(), name);
        if (!file.exists()) {
            sender.sendMessage(String.format("%s%s", ChatColor.RED, BlocksLanguage.BLOCKS_DOESNT_EXIST));
            return;
        }
        try {
            Blocks blocks = BlocksAPI.loadFile(file, blocksType);
            if (add && playerData.getClipboard() != null) {
                playerData.getClipboard().addBlocks(blocks);
            } else {
                playerData.setClipboard(blocks);
            }
            sender.sendMessage(ChatColor.GREEN + String.format(BlocksLanguage.LOADED, name));
        } catch (IOException e) {
            e.printStackTrace();
            sender.sendMessage(String.format("%s%s", ChatColor.RED, BlocksLanguage.FAILED_TO_LOAD));
        }
    }
}
