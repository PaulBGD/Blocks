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

package me.paulbgd.blocks.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import me.paulbgd.blocks.api.block.Blocks;
import me.paulbgd.blocks.api.block.loader.BlocksLoader;
import me.paulbgd.blocks.api.block.paster.BlockPaster;
import me.paulbgd.blocks.api.block.paster.Paster;
import me.paulbgd.blocks.api.player.BlocksPlayerData;
import net.minidev.json.JSONStyle;
import net.minidev.json.JSONValue;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main class to be used for the BlocksAPI.
 * Contains shorthand functions for loading the Blocks in a variety of ways.
 * Also contains playerdata.
 */
public class BlocksAPI {

    // fact is, we won't ever delete this even when a player relogs. Or should we?
    private final static HashMap<UUID, BlocksPlayerData> playerData = new HashMap<>();

    @Getter
    @Setter
    private static BlockPaster defaultPaster = Paster.ASYNC_PASTER;

    static {
        // this will save space when storing
        JSONValue.COMPRESSION = JSONStyle.MAX_COMPRESS;
    }

    /**
     * Loads a block object from a InputStream
     *
     * @param stream the stream to load from
     * @return Blocks object
     * @throws IOException if there was an error loading it
     */
    public static Blocks load(InputStream stream) throws IOException {
        return Blocks.load(stream);
    }

    /**
     * Loads a block object from a File
     *
     * @param file to load from
     * @return Blocks object
     * @throws IOException if there was an error loading it
     */
    public static Blocks loadFile(File file) throws IOException {
        return Blocks.load(file);
    }

    /**
     * Loads a file using a specific BlocksLoader. Useful for Schematics.
     *
     * @param file         to load from
     * @param blocksLoader type of file to load {@link me.paulbgd.blocks.api.block.BlocksType}
     * @return Blocks object
     * @throws IOException if there was an error loading it
     */
    public static Blocks loadFile(File file, BlocksLoader blocksLoader) throws IOException {
        return Blocks.load(file, blocksLoader);
    }

    /**
     * Loads a block object from a InputStream using a specific BlocksLoader. Useful for schematics
     *
     * @param inputStream  to load from
     * @param blocksLoader type of file to load {@link me.paulbgd.blocks.api.block.BlocksType}
     * @return Blocks object
     * @throws IOException if there was an error loading it
     */
    public static Blocks load(InputStream inputStream, BlocksLoader blocksLoader) throws IOException {
        return Blocks.load(inputStream, blocksLoader);
    }

    /**
     * Loads a block object from the specified resource
     *
     * @param name   resource name
     * @param plugin plugin instance
     * @return Blocks object
     * @throws IOException                        if there was an error loading it
     * @throws java.lang.IllegalArgumentException if the resource does not exist
     */
    public static Blocks loadResource(String name, JavaPlugin plugin) throws IOException {
        InputStream resource = plugin.getResource(name);
        if (resource == null) {
            throw new IllegalArgumentException("The resource '" + name + "' does not exist in plugin " + plugin.getName() + "!");
        }
        return Blocks.load(resource);
    }

    /**
     * Loads a block object from the specific resource using a specific BlocksLoader. Useful for schematics.
     *
     * @param name         resource name
     * @param blocksLoader type of file to load {@link me.paulbgd.blocks.api.block.BlocksType}
     * @param plugin       plugin instance
     * @return Blocks object
     * @throws IOException                        if there was an error loading it
     * @throws java.lang.IllegalArgumentException if the resource does not exist
     */
    public static Blocks loadResource(String name, BlocksLoader blocksLoader, JavaPlugin plugin) throws IOException {
        InputStream resource = plugin.getResource(name);
        if (resource == null) {
            throw new IllegalArgumentException("The resource '" + name + "' does not exist in plugin " + plugin.getName() + "!");
        }
        return Blocks.load(resource, blocksLoader);
    }

    /**
     * Gets the BlocksPlayerData object for the specified player. Will create one if needed.
     *
     * @param player to get BlocksPlayerData for
     * @return the player data {@link me.paulbgd.blocks.api.player.BlocksPlayerData}
     */
    public static BlocksPlayerData getPlayerData(Player player) {
        if (!playerData.containsKey(player.getUniqueId())) {
            playerData.put(player.getUniqueId(), new BlocksPlayerData(player.getUniqueId(), player.getName()));
        }
        return playerData.get(player.getUniqueId());
    }

}
