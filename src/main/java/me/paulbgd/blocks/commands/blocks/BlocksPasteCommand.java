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

import me.paulbgd.blocks.BlocksLanguage;
import me.paulbgd.blocks.api.BlocksAPI;
import me.paulbgd.blocks.api.player.BlocksPlayerData;
import me.paulbgd.blocks.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public class BlocksPasteCommand extends Command.Subcommand {

    public BlocksPasteCommand() {
        super(new String[]{"paste"}, new Permission("blocks.paste"), BlocksLanguage.PASTE_COMMAND_INFO);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sendMessage(String.format("%s%s", ChatColor.RED, BlocksLanguage.NOT_PLAYER), sender);
            return;
        }
        BlocksPlayerData playerData = BlocksAPI.getPlayerData((Player) sender);
        if (playerData.getClipboard() == null) {
            sender.sendMessage(String.format("%s%s", ChatColor.RED, BlocksLanguage.EMPTY_CLIPBOARD));
            return;
        }
        boolean air = true;
        boolean biomes = false;
        for (String arg : args) {
            switch (arg.toLowerCase()) {
                case "biome":
                    biomes = true;
                    break;
                case "noair":
                    air = false;
                    break;
            }
        }
        sender.sendMessage(ChatColor.AQUA + BlocksLanguage.PASTING);
        playerData.getClipboard().paste(((Player) sender).getLocation().getBlock(), sender, air, biomes);
    }
}
