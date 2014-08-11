package me.paulbgd.blocks.api.block;

import me.paulbgd.blocks.api.block.loader.BlocksFormat;
import me.paulbgd.blocks.api.block.loader.BlocksLoader;
import me.paulbgd.blocks.api.block.loader.SchematicFormat;

/**
 * Holds the official BlockLoaders. Switch from an enum allowing plugins to create their own format.
 */
public class BlocksType {

    /**
     * The Blocks format saver/loader. Used for the official format.
     */
    public static final BlocksLoader BLOCKS = new BlocksFormat();
    /**
     * The schematic saver/loader. Used for conversion.
     */
    public static final BlocksLoader SCHEMATIC = new SchematicFormat();

}
