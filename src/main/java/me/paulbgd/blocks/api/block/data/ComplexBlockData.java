package me.paulbgd.blocks.api.block.data;

import java.util.HashMap;
import me.paulbgd.blocks.utils.NBTUtils;
import net.minecraft.server.v1_7_R3.NBTTagCompound;
import net.minecraft.server.v1_7_R3.TileEntity;
import net.minidev.json.JSONObject;
import org.bukkit.craftbukkit.v1_7_R3.util.CraftMagicNumbers;
import org.jnbt.CompoundTag;
import org.jnbt.Tag;

/**
 * Represents a TileEntity. Stores data as JSON and NBT.
 */
public class ComplexBlockData extends BlockData {

    private final JSONObject data;
    private final CompoundTag nbt;

    /**
     * Creates a ComplexBlockData using the block id and JSON string
     *
     * @param id   the block id
     * @param data the json string
     */
    public ComplexBlockData(int id, JSONObject data) {
        super(id, Short.valueOf((String) data.get("e")));
        this.data = data;

        this.nbt = data.containsKey("n") ? NBTUtils.jsonToNewNBT((JSONObject) data.get("n")) : new CompoundTag("", new HashMap<String, Tag>());
    }

    /**
     * Creates a ComplexBlockData using a TileEntity.
     * {@link net.minecraft.server.v1_7_R3.TileEntity}
     *
     * @param tileEntity the TileEntity
     * @param data       block data
     */
    public ComplexBlockData(TileEntity tileEntity, int data) {
        super(CraftMagicNumbers.getId(tileEntity.q()), (short) data);
        this.data = new JSONObject();
        this.data.put("e", Short.toString(this.blockData));

        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        tileEntity.b(nbtTagCompound);
        JSONObject nbtData = NBTUtils.nbtToJSON(nbtTagCompound);
        this.nbt = NBTUtils.jsonToNewNBT(nbtData);
        this.data.put("n", nbtData);
    }

    @Override
    public Object getData() {
        return this.data;
    }

    @Override
    public BlockData clone() {
        return new ComplexBlockData(getId(), this.data);
    }

    /**
     * Returns the NBT of the TileEntity
     *
     * @return the NBT
     */
    public CompoundTag getNBT() {
        return this.nbt;
    }

}
