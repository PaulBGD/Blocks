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

package me.paulbgd.blocks.utils.reflection;

import lombok.Getter;
import org.bukkit.World;

public class BlocksReflection {

    @Getter
    private static final Class<?> nmsWorld = Reflection.getClass("World", Reflection.PackageType.NMS).getAClass();
    private static final Class<?> nmsWorldServer = Reflection.getClass("WorldServer", Reflection.PackageType.NMS).getAClass();
    @Getter
    private static final Class<?> tileEntityClass = Reflection.getClass("TileEntity", Reflection.PackageType.NMS).getAClass();
    @Getter
    private static final Reflection.ReflectionClass craftMagicNumbers = Reflection.getClass("util.CraftMagicNumbers", Reflection.PackageType.CBS);
    @Getter
    private static final Class<?> nmsBlock = Reflection.getClass("Block", Reflection.PackageType.NMS).getAClass();
    @Getter
    private static final Reflection.ReflectionClass nbtTagCompound = Reflection.getClass("NBTTagCompound", Reflection.PackageType.NMS);
    private static final Reflection.ReflectionMethod getId = craftMagicNumbers.getStaticMethod(1, int.class);
    private static final Reflection.ReflectionClass craftWorld = Reflection.getClass("CraftWorld", Reflection.PackageType.CBS);

    public static Reflection.ReflectionObject getWorldHandle(World world) {
        Reflection.ReflectionMethod getHandle = craftWorld.getMethod(0, nmsWorldServer, world);
        return new Reflection.ReflectionObject(getHandle.invoke());
    }

    public static int getId(Object block) {
        return (int) getId.invoke(block);
    }

}
