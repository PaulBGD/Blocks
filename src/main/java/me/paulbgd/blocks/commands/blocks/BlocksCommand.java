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
