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

package me.paulbgd.blocks;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import me.paulbgd.blocks.plugin.BlocksPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Holds all of the messages used by the Blocks API. A tad messy at the moment.
 */
public class BlocksLanguage {

    private static final File languageFile;
    public static String NO_PERMISSION = "You do not have permission!";
    public static String HELP_FOR = "Showing help for";
    public static String NOT_PLAYER = "You are not a player!";
    public static String NOT_ONLINE = "Player %s (%s) is not online!";
    public static String NOT_SET = "You have not set point %s!";
    public static String COPIED_TO_CLIPBOARD = "Copied to your clipboard!";
    public static String ADDED_TO_CLIPBOARD = "Added to your clipboard!";
    public static String EMPTY_CLIPBOARD = "Your clipboard is empty!";
    public static String PASTING = "Pasting your current clipboard.. this may take a while";
    public static String PASTED = "Your clipboard has been pasted at your current location.";
    public static String USAGE = "Usage";
    public static String SET_POS = "Set position %s!";
    public static String RELOADED = "Reloaded configuration files.";
    public static String BLOCKS_ALREADY_EXISTS = "A .blocks file with that name already exists!";
    public static String FAILED_TO_SAVE = "Failed to save! Check console for more info.";
    public static String SAVED_AS = "You have saved your clipboard to /plugins/Blocks/blocks/%s!";
    public static String BLOCKS_DOESNT_EXIST = "A .blocks file with that name does not exist!";
    public static String FAILED_TO_LOAD = "Failed to load! Check console for more info.";
    public static String LOADED = "You have loaded %s into your clipboard.";
    public static String PROCESSING_X_BLOCKS = "Processing %s blocks...";
    public static String DIFFERENT_WORLDS = "Worlds do not match!";
    public static String INVALID_DATA_TYPE = "Invalid data type '%s'!";
    // command info
    public static String BLOCKS_COMMAND_INFO = "Shows help";
    public static String COPY_COMMAND_INFO = "Copies your current selection to your clipboard";
    public static String PASTE_COMMAND_INFO = "Pastes your current clipboard, can use 'noair' or 'biome'";
    public static String POS_COMMAND_INFO = "Sets a corner of your selection";
    public static String RELOAD_COMMAND_INFO = "Reloads all configuration files";
    public static String SAVE_COMMAND_INFO = "Saves your clipboard as a .blocks file";
    public static String LOAD_COMMAND_INFO = "Loads a .blocks file onto your clipboard!";
    public static String RESOURCE_COMMAND_INFO = "Loads a .blocks file from your resources onto your clipboard!";

    static {
        languageFile = new File(BlocksPlugin.getPluginFolder(), "language.byml"); // we're so swag, let's add a "b"
        language = YamlConfiguration.loadConfiguration(languageFile);

        reload();
        save();
    }

    private static YamlConfiguration language;

    /**
     * Only to be called by the Blocks plugin
     */
    public static void preload() {

    }

    /**
     * Checks against the newest state of the language file for changes
     */
    public static void reload() {
        if (!languageFile.exists()) {
            // deleted or something, i dun know
            save();
        }
        language = YamlConfiguration.loadConfiguration(languageFile);

        for (Field field : BlocksLanguage.class.getFields()) {
            if (Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
                if (language.isSet(field.getName())) {
                    try {
                        Object fieldObject = field.get(null);
                        Object languageObject = language.get(field.getName());
                        if (!fieldObject.equals(languageObject)) {
                            field.set(null, languageObject);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Saves all changes to the language file.
     */
    public static void save() {
        for (Field field : BlocksLanguage.class.getFields()) {
            if (Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
                try {
                    Object fieldData = field.get(null);
                    if (!language.isSet(field.getName()) || !language.get(field.getName()).equals(fieldData)) {
                        language.set(field.getName(), fieldData);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            language.save(languageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
