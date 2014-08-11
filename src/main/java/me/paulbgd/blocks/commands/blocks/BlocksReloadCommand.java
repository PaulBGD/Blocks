package me.paulbgd.blocks.commands.blocks;

import me.paulbgd.blocks.BlocksLanguage;
import me.paulbgd.blocks.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

public class BlocksReloadCommand extends Command.Subcommand {

    public BlocksReloadCommand() {
        super(new String[]{"reload"}, new Permission("blocks.reload"), BlocksLanguage.RELOAD_COMMAND_INFO);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        BlocksLanguage.reload();
        sendMessage(ChatColor.GREEN + BlocksLanguage.RELOADED, sender);
    }
}
