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

import java.io.IOException;
import java.io.InputStream;
import me.paulbgd.blocks.BlocksLanguage;
import me.paulbgd.blocks.api.BlocksAPI;
import me.paulbgd.blocks.api.block.Blocks;
import me.paulbgd.blocks.api.block.BlocksType;
import me.paulbgd.blocks.api.block.loader.BlocksLoader;
import me.paulbgd.blocks.api.player.BlocksPlayerData;
import me.paulbgd.blocks.commands.Command;
import me.paulbgd.blocks.plugin.BlocksPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public class BlocksResourceCommand extends Command.Subcommand {

    public BlocksResourceCommand() {
        super(new String[]{"resource"}, new Permission("blocks.resource"), BlocksLanguage.RESOURCE_COMMAND_INFO);
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
        InputStream resource = BlocksPlugin.getPlugin().getResource(name);
        if (resource == null) {
            sender.sendMessage(String.format("%s%s", ChatColor.RED, BlocksLanguage.BLOCKS_DOESNT_EXIST));
            return;
        }
        try {
            Blocks blocks = BlocksAPI.load(resource, blocksType);
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
