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
import me.paulbgd.blocks.commands.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class BlocksCommand extends Command {

    public BlocksCommand() {
        super(new String[]{"blocks"}, BlocksLanguage.BLOCKS_COMMAND_INFO, new Permission("blocks.help", PermissionDefault.TRUE),
                new BlocksCopyCommand(),
                new BlocksPasteCommand(),
                new BlocksSetPositionCommand(),
                new BlocksReloadCommand(),
                new BlocksSaveCommand(),
                new BlocksLoadCommand(),
                new BlocksResourceCommand()
        );
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        this.showHelp(sender);
    }
}
