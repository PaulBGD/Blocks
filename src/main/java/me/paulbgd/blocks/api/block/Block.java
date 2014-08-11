package me.paulbgd.blocks.api.block;

import lombok.Data;
import me.paulbgd.blocks.api.block.data.BlockData;

@Data
/**
 * Represents a relative block.
 * Holds all block data
 */
public class Block {

    /**
     * Relative coordinates of the block
     */
    private final BlockPosition position;
    /**
     * All block data
     */
    private final BlockData data;

    /**
     * Clones this block so we can steal it for our home planet.
     *
     * @return the clone
     */
    public Block clone() {
        return new Block(position.clone(), data.clone());
    }

}
