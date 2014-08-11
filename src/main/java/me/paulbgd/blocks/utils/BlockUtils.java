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

package me.paulbgd.blocks.utils;

import com.google.common.base.Joiner;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.paulbgd.blocks.BlocksLanguage;
import me.paulbgd.blocks.api.block.BlockPosition;
import me.paulbgd.blocks.api.block.data.BlockData;
import me.paulbgd.blocks.api.block.data.ComplexBlockData;
import me.paulbgd.blocks.api.block.data.SimpleBlockData;
import me.paulbgd.blocks.utils.reflection.BlocksReflection;
import me.paulbgd.blocks.utils.reflection.Reflection;
import net.minecraft.server.v1_7_R3.TileEntity;
import net.minecraft.util.org.apache.commons.lang3.Validate;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jnbt.CompoundTag;
import org.jnbt.IntTag;
import org.jnbt.Tag;

public class BlockUtils {

    /**
     * Gets a list of all the blocks between two points.
     *
     * @param from   where they're being added from
     * @param block1 point 1
     * @param block2 point 2
     * @return a list of all blocks between the points
     */
    public static List<me.paulbgd.blocks.api.block.Block> getAllBlocks(Block from, Block block1, Block block2) {
        if (!block1.getWorld().equals(block2.getWorld())) {
            throw new IllegalArgumentException(BlocksLanguage.DIFFERENT_WORLDS);
        }
        List<me.paulbgd.blocks.api.block.Block> blocks = new ArrayList<>();
        Reflection.ReflectionObject nmsWorld = BlocksReflection.getWorldHandle(block1.getWorld());
        int maxHeight = block1.getWorld().getMaxHeight(), minY = lowestInteger(block1.getY(), block2.getY());
        if (minY > maxHeight) {
            return blocks;
        }
        int maxY = highestInteger(block1.getY(), block2.getY());
        if (maxY > maxHeight) {
            maxY = maxHeight;
        }
        int minX = lowestInteger(block1.getX(), block2.getX()), minZ = lowestInteger(block1.getZ(), block2.getZ());
        int maxX = highestInteger(block1.getX(), block2.getX()), maxZ = highestInteger(block1.getZ(), block2.getZ());
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPosition blockPosition = new BlockPosition(x - from.getX(), y - from.getY(), z - from.getZ());
                    BlockData blockData;
                    Reflection.ReflectionClass world = new Reflection.ReflectionClass(BlocksReflection.getNmsWorld());
                    Object tileEntity = world.getMethod("getTileEntity", BlocksReflection.getTileEntityClass(), nmsWorld.getObject()).invoke(x, y, z);
                    short data = ((Integer) world.getMethod("getData", int.class, nmsWorld.getObject()).invoke(x, y, z)).shortValue();
                    if (tileEntity == null) {
                        // normal block I suppose
                        blockData = new SimpleBlockData(BlocksReflection.getId(world.getMethod("getType", BlocksReflection.getNmsBlock(), 3, nmsWorld.getObject()).invoke(x, y, z)), data);
                    } else {
                        blockData = new ComplexBlockData(tileEntity, data);
                    }
                    blocks.add(new me.paulbgd.blocks.api.block.Block(blockPosition, blockData));
                }
            }
        }
        return blocks;
    }

    /**
     * Pastes a list of blocks at a specified location. There is the option to paste with air.
     *
     * @param blocks   a list of blocks to paste
     * @param location the location to paste at
     * @param air      if or if not to paste the air
     */
    public static void paste(Collection<me.paulbgd.blocks.api.block.Block> blocks, Block location, boolean air) {
        World craftWorld = location.getWorld();
        Object world = BlocksReflection.getWorldHandle(location.getWorld()).getObject();
        Reflection.ReflectionClass worldClass = new Reflection.ReflectionClass(BlocksReflection.getNmsWorld());
        int x = location.getX(), y = location.getY(), z = location.getZ();
        Reflection.ReflectionMethod getType = worldClass.getMethod("getType", BlocksReflection.getNmsBlock(), 3, world);
        Reflection.ReflectionMethod getBlock = BlocksReflection.getCraftMagicNumbers().getStaticMethod("getBlock", BlocksReflection.getNmsBlock(), new Class<?>[]{int.class});
        Reflection.ReflectionMethod setTypeAndData = worldClass.getMethod("setTypeAndData", boolean.class, 6, world);
        Reflection.ReflectionMethod getTileEntity = worldClass.getMethod("getTileEntity", BlocksReflection.getTileEntityClass(), 3, world);
        Reflection.ReflectionMethod setTileEntity = worldClass.getMethod("setTileEntity", null, 4, world);
        Validate.noNullElements(new Object[]{getType, getBlock, setTypeAndData, getTileEntity, setTileEntity});
        for (me.paulbgd.blocks.api.block.Block block : blocks) {
            BlockPosition position = block.getPosition();
            BlockData data = block.getData();
            int j = x + position.getRelativeX(), k = y + position.getRelativeY(), l = z + position.getRelativeZ();
            Chunk chunk = craftWorld.getChunkAt(j >> 4, l >> 4);
            if (!chunk.isLoaded()) {
                chunk.load(true);
            }
            if (data.getId() == 0 && (!air || getType.invoke(j, k, l) == Reflection.getClass("Blocks", Reflection.PackageType.NMS).getStaticField("AIR").getValue())) {
                continue; // no need to do air.. again
            }
            try {
                Object id = getBlock.invoke(data.getId());
                setTypeAndData.invoke(j, k, l, id, (int) data.getBlockData(), 2);
                if (data instanceof ComplexBlockData) {
                    ComplexBlockData complexBlockData = (ComplexBlockData) data;
                    CompoundTag nbt = complexBlockData.getNBT();
                    Map<String, Tag> value = new HashMap<>(nbt.getValue());
                    value.put("x", new IntTag("x", j));
                    value.put("y", new IntTag("y", k));
                    value.put("z", new IntTag("z", l));
                    CompoundTag newTag = new CompoundTag(nbt.getName(), value);
                    Reflection.ReflectionObject nbtTagCompound = NBTUtils.newToOld(newTag);
                    Reflection.ReflectionObject tileEntity = Reflection.getObject(getTileEntity.invoke(j, k, l));
                    Reflection.ReflectionMethod method = tileEntity.getMethod("a", null, 1);
                    System.out.println("Method: " + method.getMethod().getName());
                    method.invoke(nbtTagCompound.getObject());
                    setTileEntity.invoke(j, k, l, tileEntity.getObject());
                    tileEntity.getMethod("update", null).invoke();
                } else if (!(data instanceof SimpleBlockData)) {
                    throw new IllegalArgumentException(String.format(BlocksLanguage.INVALID_DATA_TYPE, data.getClass()));
                }
            } catch (NullPointerException e) {
                System.out.println("Invalid ID: " + data.getId());
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Gets the lowest integer between two numbers
     *
     * @param i1 number 1
     * @param i2 number 2
     * @return lowest integer
     */
    private static int lowestInteger(int i1, int i2) {
        return i1 < i2 ? i1 : i2;
    }


    /**
     * Gets the highest integer between two numbers
     *
     * @param i1 number 1
     * @param i2 number 2
     * @return highest integer
     */
    private static int highestInteger(int i1, int i2) {
        return i1 > i2 ? i1 : i2;
    }

}
