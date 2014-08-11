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

package me.paulbgd.blocks.commands;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import me.paulbgd.blocks.BlocksLanguage;
import me.paulbgd.blocks.plugin.BlocksPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * A basic command class util thingy. Wrote specifically for Blocks.
 *
 * @author PaulBGD
 */
public abstract class Command implements TabExecutor {

    private static final JavaPlugin blocksPlugin = BlocksPlugin.getPlugin();

    @Getter
    private final String[] names;
    @Getter
    private final String usage;
    @Getter
    private final Permission permission;
    @Getter
    private final Subcommand[] subcommands;

    public Command(String[] names, String info, Permission permission, Subcommand... subcommands) {
        this.names = names;
        this.usage = info;
        this.permission = permission;
        this.subcommands = subcommands;

        registerCommand(names);
    }

    private static boolean registerCommand(String[] aliases) {
        PluginCommand command = getCommand(aliases[0], blocksPlugin);
        command.setAliases(Arrays.asList(aliases));
        if (blocksPlugin == null || getCommandMap() == null) {
            return false;
        }
        getCommandMap().register(aliases[0], command);
        return true;
    }

    private static PluginCommand getCommand(String name, JavaPlugin plugin) {
        PluginCommand command = null;
        try {
            Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            c.setAccessible(true);

            command = c.newInstance(name, plugin);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return command;
    }

    private static CommandMap getCommandMap() {
        CommandMap commandMap = null;
        try {
            if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
                Field f = SimplePluginManager.class.getDeclaredField("commandMap");
                f.setAccessible(true);

                commandMap = (CommandMap) f.get(Bukkit.getPluginManager());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return commandMap;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (!sender.hasPermission(this.permission)) {
            sendMessage(String.format("%s%s", ChatColor.RED, BlocksLanguage.NO_PERMISSION), sender);
            return true;
        }
        if (args.length > 0) {
            for (Subcommand subcommand : subcommands) {
                for(String name : subcommand.names) {
                    if(name.equalsIgnoreCase(args[0].toLowerCase())) {
                        if (sender.hasPermission(subcommand.permission)) {
                            subcommand.onCommand(sender, args.length == 1 ? new String[0] : Arrays.copyOfRange(args, 1, args.length));
                        } else {
                            sendMessage(String.format("%s%s", ChatColor.RED, BlocksLanguage.NO_PERMISSION), sender);
                        }
                        return true;
                    }
                }
            }
        }
        this.onCommand(sender, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        List<String> toReturn = new ArrayList<>();

        if (args.length == 0) {
            for (Subcommand subcommand : subcommands) {
                toReturn.addAll(Arrays.asList(subcommand.names));
            }
        }
        // not much more we can do, I see no reason to go much further

        return toReturn;
    }

    public abstract void onCommand(CommandSender sender, String[] args);

    protected final void showHelp(CommandSender sender) {
        sendMessage(String.format("%s%s%s: /%s", ChatColor.AQUA, ChatColor.BOLD, BlocksLanguage.HELP_FOR, this.names[0]), sender);
        sendMessage(String.format("%s%s /%s - %s%s", ChatColor.GRAY, ChatColor.BOLD, this.names[0], ChatColor.DARK_GRAY, this.usage), sender);
        for (Subcommand subcommand : subcommands) {
            sendMessage(String.format("%s%s /%s %s - %s%s", ChatColor.GRAY, ChatColor.BOLD, this.names[0], subcommand.names[0], ChatColor.DARK_GRAY, subcommand.info), sender);
        }
    }

    protected final void sendMessage(String message, CommandSender sender) {
        if (!(sender instanceof Player)) {
            // for safety, remove colors
            message = ChatColor.stripColor(message);
        }
        sender.sendMessage(message);
    }

    public static abstract class Subcommand extends Command {
        private final String[] names;
        private final Permission permission;
        private final String info;

        public Subcommand(String[] names, Permission permission, String info) {
            super(names, info, permission);

            this.names = names;
            this.permission = permission;
            this.info = info;
        }
    }

}
