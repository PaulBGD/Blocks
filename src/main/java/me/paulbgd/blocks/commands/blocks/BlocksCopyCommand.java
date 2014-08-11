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
