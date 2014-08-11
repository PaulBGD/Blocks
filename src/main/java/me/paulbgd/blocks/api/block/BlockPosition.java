package me.paulbgd.blocks.api.block;

import lombok.Data;

@Data
/**
 * Represents a relative position. Used for storing {@link me.paulbgd.blocks.api.block.Block} positions.
 */
public class BlockPosition {

    /**
     * The relative x
     */
    private final int relativeX;
    private final int relativeY;
    /**
     * The relative y
     */
    private final int relativeZ;
    /**
     * The relative z
     */

    /**
     * Returns a stringed version of this, to be used in storage
     *
     * @return stringed version
     */
    @Override
    public String toString() {
        return relativeX + "!" + relativeY + "!" + relativeZ;
    }

    /**
     * Checks if another position is the same relatively
     *
     * @param object to check again
     * @return true of they're at the same spot
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof BlockPosition)) {
            return false;
        }
        BlockPosition other = (BlockPosition) object;
        return other.relativeZ == relativeZ && other.relativeX == relativeX && other.getRelativeY() == relativeY;
    }

    /**
     * A quick way to check coordinates.
     *
     * @param x the x coord
     * @param y the y coord
     * @param z the z coord
     * @return if the coordinates are the same
     */
    public boolean equals(int x, int y, int z) {
        return x == this.relativeX && y == this.relativeY && z == this.relativeZ;
    }

    /**
     * Returns a clone of this for {@link me.paulbgd.blocks.api.block.Block#clone()}
     *
     * @return the clone
     */
    public BlockPosition clone() {
        return new BlockPosition(relativeX, relativeY, relativeZ);
    }

}
