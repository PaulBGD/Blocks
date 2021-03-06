/*
 * COPYRIGHT AND PERMISSION NOTICE
 *
 * Copyright (c) 2014, PaulBGD, <paul@paulbgd.me>.
 *
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software for any purpose
 * with or without fee is hereby granted, provided that the above copyright
 * notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT OF THIRD PARTY RIGHTS. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Except as contained in this notice, the name of a copyright holder shall not
 * be used in advertising or otherwise to promote the sale, use or other dealings
 * in this Software without prior written authorization of the copyright holder.
 */

package me.paulbgd.blocks.commands.blocks;

import java.io.File;
import java.io.IOException;
import me.paulbgd.blocks.BlocksLanguage;
import me.paulbgd.blocks.api.block.loader.BlocksLoader;
import me.paulbgd.blocks.plugin.BlocksPlugin;
import me.paulbgd.blocks.api.BlocksAPI;
import me.paulbgd.blocks.api.block.BlocksType;
import me.paulbgd.blocks.api.player.BlocksPlayerData;
import me.paulbgd.blocks.commands.Command;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public class BlocksSaveCommand extends Command.Subcommand {

    public BlocksSaveCommand() {
        super(new String[]{"save"}, new Permission("blocks.save"), BlocksLanguage.SAVE_COMMAND_INFO);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sendMessage(String.format("%s%s", ChatColor.RED, BlocksLanguage.NOT_PLAYER), sender);
            return;
        }
        BlocksPlayerData playerData = BlocksAPI.getPlayerData((Player) sender);
        if (playerData.getClipboard() == null) {
            sender.sendMessage(ChatColor.RED + BlocksLanguage.EMPTY_CLIPBOARD);
            return;
        }
        if (args.length == 0) {
            sender.sendMessage(String.format("%s%s: /blocks save <name>", ChatColor.RED, BlocksLanguage.USAGE));
            return;
        }
        String name = args[0];
        BlocksLoader blocksType = BlocksType.BLOCKS;
        for (int i = 0, argsLength = args.length; i < argsLength; i++) {
            String arg = args[i];
            if (i != 0) {
                // is name
                if (arg.startsWith("type:")) {
                    System.out.println("Type");
                    switch (StringUtils.split(arg, ':')[1]) {
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
        if (!name.toLowerCase().endsWith("." + blocksType.getName())) {
            name = name + "." + blocksType.getName();
        }
        File file = new File(BlocksPlugin.getBlocksFolder(), name);
        if (file.exists()) {
            sender.sendMessage(String.format("%s%s", ChatColor.RED, BlocksLanguage.BLOCKS_ALREADY_EXISTS));
            return;
        }
        try {
            playerData.getClipboard().save(file, blocksType);
            sender.sendMessage(ChatColor.GREEN + String.format(BlocksLanguage.SAVED_AS, name));
        } catch (IOException e) {
            e.printStackTrace();
            sender.sendMessage(String.format("%s%s", ChatColor.RED, BlocksLanguage.FAILED_TO_SAVE));
        }
    }
}
