package me.paulbgd.blocks.api.block.data;

import lombok.Data;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;

/**
 * Represents the data a block can contain.
 * <p/>
 * {@link SimpleBlockData}
 * {@link ComplexBlockData}
 */
@Data
public abstract class BlockData {

    protected final int id;
    protected final short blockData;

    /**
     * Creates a simple BlockData instance using the block id and data
     *
     * @param id        the block id
     * @param blockData the block data
     */
    public BlockData(int id, short blockData) {
        this.id = id;
        this.blockData = blockData;
    }

    /**
     * Creates a BlockData instance using a BlockState.
     * Deprecated for use of Bukkit object BlockState.
     * <p/>
     * {@link org.bukkit.block.BlockState}
     *
     * @param blockState the block state
     */
    @Deprecated
    public BlockData(BlockState blockState) {
        this.id = blockState.getTypeId();
        this.blockData = blockState.getRawData();
    }

    /**
     * Loads a new BlockData using the id and some sort of object
     *
     * @param id   the block id
     * @param data either a JSON string, JSON object, or short
     * @return new BlockData
     */
    public static BlockData loadData(int id, Object data) {
        if (data instanceof String) {
            String stringedData = (String) data;
            if (JSONValue.isValidJson(stringedData)) {
                data = JSONValue.parse(stringedData);
            }
        }
        if (data instanceof JSONObject) {
            // load complex data!
            return new ComplexBlockData(id, (JSONObject) data);
        } else {
            // normal block, not an issue
            return new SimpleBlockData(id, Short.valueOf(data.toString()));
        }
    }

    /**
     * Loads a new BlockData using a BlockState.
     * Deprecated for use of Bukkit object BlockState.
     *
     * @param blockState the block state to use
     * @return new BlockData
     */
    @Deprecated
    public static BlockData loadData(BlockState blockState) {
        if (blockState.getClass().getSimpleName().equals("CraftBlockState")) {
            // normal data value, bleh
            return new SimpleBlockData(blockState);
        } else {
            return new ComplexBlockData(((CraftWorld) blockState.getWorld()).getHandle().getTileEntity(blockState.getX(), blockState.getY(), blockState.getZ()), blockState.getRawData());
        }
    }

    /**
     * Gets the data that can be easily stored in JSON
     *
     * @return the data
     */
    public abstract Object getData();

    /**
     * Clones the object
     * {@link me.paulbgd.blocks.api.block.Block#clone}
     *
     * @return a clone
     */
    public abstract BlockData clone();

}
