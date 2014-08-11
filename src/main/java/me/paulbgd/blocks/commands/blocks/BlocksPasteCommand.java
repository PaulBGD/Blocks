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
