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

package me.paulbgd.blocks.plugin;

import java.io.File;
import lombok.Getter;
import me.paulbgd.blocks.BlocksLanguage;
import me.paulbgd.blocks.commands.blocks.BlocksCommand;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Holds the JavaPlugin used by the Blocks API
 * <p/>
 * {@link org.bukkit.plugin.java.JavaPlugin}
 */
public class BlocksPlugin extends JavaPlugin {

    @Getter
    private static JavaPlugin plugin;

    @Getter
    private static File pluginFolder;
    @Getter
    private static File blocksFolder;
    private static boolean registered = false;

    /**
     * This method sets up the Blocks API to use the specified plugin.
     * If the Blocks API is not shaded in, this will be called by default.
     * Otherwise in the case of shading, do call this. It'll make things better.
     *
     * @param plugin the plugin to register for
     */
    public static void register(JavaPlugin plugin) {
        if (registered) {
            return; // no need to register if another plugin has registered is
        }
        pluginFolder = plugin.getDataFolder();
        System.out.println(pluginFolder.getAbsolutePath());
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs();
        }
        blocksFolder = new File(pluginFolder, "blocks");
        if (!blocksFolder.exists()) {
            blocksFolder.mkdirs();
        }
        BlocksPlugin.plugin = plugin;
        registered = true;
        BlocksCommand blocksCommand = new BlocksCommand();
        plugin.getCommand(blocksCommand.getNames()[0]).setExecutor(blocksCommand);

        BlocksLanguage.preload(); // not really needed, just preferred
    }

    @Override
    public void onEnable() {
        register(this);
    }

}
