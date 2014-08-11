package me.paulbgd.blocks.api.block.data;

import org.bukkit.block.BlockState;

/**
 * Represents a basic block which is not a TileEntity. Pretty much a BlockData that can be created.
 */
public class SimpleBlockData extends BlockData {

    public SimpleBlockData(int id, short data) {
        super(id, data);
    }

    @Deprecated
    public SimpleBlockData(BlockState blockState) {
        super(blockState);
    }

    @Override
    public Object getData() {
        return super.getBlockData();
    }

    @Override
    public BlockData clone() {
        return new SimpleBlockData(getId(), getBlockData());
    }

}
