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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import me.paulbgd.blocks.BlocksLanguage;
import me.paulbgd.blocks.api.block.BlockPosition;
import me.paulbgd.blocks.api.block.data.BlockData;
import me.paulbgd.blocks.api.block.data.ComplexBlockData;
import me.paulbgd.blocks.api.block.data.SimpleBlockData;
import net.minecraft.server.v1_7_R3.Blocks;
import net.minecraft.server.v1_7_R3.NBTTagCompound;
import net.minecraft.server.v1_7_R3.TileEntity;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.util.CraftMagicNumbers;

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
        net.minecraft.server.v1_7_R3.World world = ((CraftWorld) block1.getWorld()).getHandle();
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
                    TileEntity tileEntity = world.getTileEntity(x, y, z);
                    short data = (short) world.getData(x, y, z);
                    if (tileEntity == null) {
                        // normal block I suppose
                        blockData = new SimpleBlockData(CraftMagicNumbers.getId(world.getType(x, y, z)), data);
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
        CraftWorld craftWorld = ((CraftWorld) location.getWorld());
        net.minecraft.server.v1_7_R3.World world = ((CraftWorld) location.getWorld()).getHandle();
        int x = location.getX(), y = location.getY(), z = location.getZ();
        for (me.paulbgd.blocks.api.block.Block block : blocks) {
            BlockPosition position = block.getPosition();
            BlockData data = block.getData();
            int j = x + position.getRelativeX(), k = y + position.getRelativeY(), l = z + position.getRelativeZ();
            Chunk chunk = craftWorld.getChunkAt(j >> 4, l >> 4);
            if (!chunk.isLoaded()) {
                chunk.load(true);
            }
            if (data.getId() == 0 && (!air || world.getType(j, k, l) == Blocks.AIR)) {
                continue; // no need to do air.. again
            }
            try {
                net.minecraft.server.v1_7_R3.Block id = CraftMagicNumbers.getBlock(data.getId());
                world.setTypeAndData(j, k, l, id, data.getBlockData(), 2); // 4 = no change
                if (data instanceof ComplexBlockData) {
                    NBTTagCompound nbtTagCompound = NBTUtils.newToOld(((ComplexBlockData) data).getNBT());
                    nbtTagCompound.setInt("x", j);
                    nbtTagCompound.setInt("y", k);
                    nbtTagCompound.setInt("z", l);
                    TileEntity tileEntity = world.getTileEntity(j, k, l);
                    tileEntity.a(nbtTagCompound);
                    world.setTileEntity(j, k, l, tileEntity);
                } else if (!(data instanceof SimpleBlockData)) {
                    throw new IllegalArgumentException(String.format(BlocksLanguage.INVALID_DATA_TYPE, data.getClass()));
                }
            } catch (NullPointerException e) {
                System.out.println("Invalid ID: " + data.getId());
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
