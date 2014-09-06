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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.server.v1_7_R4.*;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jnbt.ByteArrayTag;
import org.jnbt.ByteTag;
import org.jnbt.CompoundTag;
import org.jnbt.DoubleTag;
import org.jnbt.EndTag;
import org.jnbt.FloatTag;
import org.jnbt.IntArrayTag;
import org.jnbt.IntTag;
import org.jnbt.ListTag;
import org.jnbt.LongTag;
import org.jnbt.NBTInputStream;
import org.jnbt.NBTOutputStream;
import org.jnbt.ShortTag;
import org.jnbt.StringTag;
import org.jnbt.Tag;

public class NBTUtils {

    private final static Field tagListField;

    static {
        Field tagListField1;
        try {
            tagListField1 = NBTTagList.class.getDeclaredField("list");
            if (!tagListField1.isAccessible()) {
                tagListField1.setAccessible(true);
            }
        } catch (NoSuchFieldException e) {
            tagListField1 = null;
            e.printStackTrace();
        }
        tagListField = tagListField1;
    }

    // inventory specific
    public static JSONObject inventoryToJSON(Inventory inventory) {
        JSONObject json = new JSONObject();
        JSONObject items = new JSONObject();

        ItemStack[] contents = inventory.getContents();
        for (int i = 0, contentsLength = contents.length; i < contentsLength; i++) {
            ItemStack item = contents[i];
            if (item == null) {
                continue;
            }
            items.put(Integer.toString(i), itemToJSON(item)); // let's convert to JSON!
        }
        json.put("items", items);
        return json;
    }

    public static void jsonToInventory(Inventory inventory, JSONObject json) {
        if (!json.containsKey("items")) {
            return;
        }
        JSONObject items = (JSONObject) json.get("items");
        ItemStack[] itemStacks = new ItemStack[inventory.getSize()];
        for (Map.Entry<String, Object> entry : items.entrySet()) {
            int i = Integer.valueOf(entry.getKey());
            JSONObject jsonObject = (JSONObject) entry.getValue();
            itemStacks[i] = jsonToItem(jsonObject);
        }
        inventory.setContents(itemStacks); // instead of setting one at a time, this is faster
    }

    // block specific
    public static JSONObject blockToJSON(Block block) {
        World world = ((CraftWorld) block.getWorld()).getHandle();
        TileEntity tileEntity = world.getTileEntity(block.getX(), block.getY(), block.getZ());
        JSONObject jsonObject = new JSONObject();
        if (tileEntity == null) {
            return jsonObject;
        }
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        tileEntity.b(nbtTagCompound);
        return nbtToJSON(nbtTagCompound);
    }

    public static void jsonToBlock(Block block, JSONObject jsonObject) {
        NBTTagCompound nbtTagCompound = jsonToNBT(jsonObject);
        nbtTagCompound.setInt("x", block.getX());
        nbtTagCompound.setInt("y", block.getY());
        nbtTagCompound.setInt("z", block.getZ());
        World world = ((CraftWorld) block.getWorld()).getHandle();
        TileEntity tileEntity = world.getTileEntity(block.getX(), block.getY(), block.getZ());
        tileEntity.a(nbtTagCompound);
        world.setTileEntity(block.getX(), block.getY(), block.getZ(), tileEntity);
    }

    // item specific
    public static JSONObject itemToJSON(ItemStack item) {
        net.minecraft.server.v1_7_R4.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound nbt = nmsItem.save(new NBTTagCompound());
        return nbtToJSON(nbt);
    }

    public static ItemStack jsonToItem(JSONObject jsonObject) {
        NBTTagCompound nbt = jsonToNBT(jsonObject);
        net.minecraft.server.v1_7_R4.ItemStack itemStack = net.minecraft.server.v1_7_R4.ItemStack.createStack(nbt);

        return CraftItemStack.asBukkitCopy(itemStack);
    }

    public static NBTTagCompound newToOld(Tag compoundTag) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        NBTOutputStream nbtOutputStream = new NBTOutputStream(byteArrayOutputStream);
        nbtOutputStream.writeTag(compoundTag);
        nbtOutputStream.close();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        NBTTagCompound nbtTagCompound = NBTCompressedStreamTools.a(byteArrayInputStream);
        byteArrayInputStream.close();
        return nbtTagCompound;
    }

    public static Tag oldToNew(NBTTagCompound nbtTagCompound) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(NBTCompressedStreamTools.a(nbtTagCompound));
        NBTInputStream nbtInputStream = new NBTInputStream(byteArrayInputStream);
        return nbtInputStream.readTag();
    }

    public static NBTTagCompound jsonToNBT(JSONObject jsonObject) {
        return (NBTTagCompound) loadJSON(jsonObject);
    }

    public static CompoundTag jsonToNewNBT(JSONObject jsonObject) {
        return (CompoundTag) loadNBT("", jsonObject);
    }

    private static Tag loadNBT(String name, Object object) {
        if (object instanceof Byte) {
            return new ByteTag(name, (byte) object);
        } else if (object instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) object;
            HashMap<String, Tag> tagHashMap = new HashMap<>();
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                tagHashMap.put(name, loadNBT(entry.getKey(), entry.getValue()));
            }
            return new CompoundTag(name, tagHashMap);
        } else if (object instanceof Double) {
            return new DoubleTag(name, ((double) object));
        } else if (object instanceof String) {
            String string = (String) object;
            if (string.equals("EndTag")) {
                return new EndTag();
            } else {
                return new StringTag(name, string);
            }
        } else if (object instanceof Float) {
            return new FloatTag(name, (Float) object);
        } else if (object instanceof Integer) {
            return new IntTag(name, (int) object);
        } else if (object instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) object;
            Object value1 = jsonArray.isEmpty() ? null : jsonArray.get(0);
            if (value1 instanceof Byte) {
                byte[] bytes = new byte[jsonArray.size()];
                for (int i = 0, jsonArraySize = jsonArray.size(); i < jsonArraySize; i++) {
                    Object o = jsonArray.get(i);
                    bytes[i] = (byte) o;
                }
                return new ByteArrayTag(name, bytes);
            } else if (value1 instanceof Integer) {
                int[] ints = new int[jsonArray.size()];
                for (int i = 0, jsonArraySize = jsonArray.size(); i < jsonArraySize; i++) {
                    Object o = jsonArray.get(i);
                    ints[i] = (int) o;
                }
                return new IntArrayTag(name, ints);
            } else {
                List<Tag> tagList = new ArrayList<>();
                for (Object o : jsonArray) {
                    tagList.add(loadNBT("", o));
                }
                return new ListTag(name, Tag.class, tagList);
            }
        } else if (object instanceof Long) {
            return new LongTag(name, (long) object);
        } else if (object instanceof Short) {
            return new ShortTag(name, (short) object);
        } else {
            // no idea
            return new StringTag(name, object.toString());
        }
    }

    public static JSONObject nbtToJSON(CompoundTag compoundTag) {
        return (JSONObject) loadJSON(compoundTag);
    }

    private static Object loadJSON(Tag tag) {
        if (tag instanceof ByteArrayTag) {
            JSONArray jsonArray = new JSONArray();
            jsonArray.addAll(Arrays.asList(((ByteArrayTag) tag).getValue()));
            return jsonArray;
        } else if (tag instanceof CompoundTag) {
            CompoundTag compoundTag = (CompoundTag) tag;
            JSONObject jsonObject = new JSONObject();
            for (Map.Entry<String, Tag> entry : compoundTag.getValue().entrySet()) {
                jsonObject.put(entry.getKey(), loadJSON(entry.getValue()));
            }
            return jsonObject;
        } else if (tag instanceof EndTag) {
            return "EndTag";// not much we can do, it's not a valid tag
        } else if (tag instanceof IntArrayTag) {
            JSONArray jsonArray = new JSONArray();
            jsonArray.addAll(Arrays.asList(((IntArrayTag) tag).getValue()));
            return jsonArray;
        } else if (tag instanceof ListTag) {
            JSONArray jsonArray = new JSONArray();
            for (Tag tag1 : ((ListTag) tag).getValue()) {
                jsonArray.add(loadJSON(tag1));
            }
            return jsonArray;
        } else {
            return tag.getValue();
        }
    }

    public static JSONObject nbtToJSON(NBTTagCompound nbt) {
        return (JSONObject) load(nbt);
    }

    private static NBTBase loadJSON(Object json) {
        if (json instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) json;
            if (jsonObject.containsKey("nbtTagList")) {
                // load array
                JSONArray jsonArray = (JSONArray) jsonObject.get("nbtTagList");
                NBTTagList nbtTagList = new NBTTagList();
                for (Object object : jsonArray) {
                    nbtTagList.add(loadJSON(object));
                }
                return nbtTagList;
            } else if (jsonObject.containsKey("nbtTagIntArray")) {
                JSONArray jsonArray = (JSONArray) jsonObject.get("nbtTagIntArray");
                int[] ints = new int[jsonArray.size()];
                for (int i = 0; i < ints.length; i++) {
                    ints[i] = (Integer) jsonArray.get(i);
                }
                return new NBTTagIntArray(ints);
            } else if (jsonObject.containsKey("nbtTagByteArray")) {
                JSONArray jsonArray = (JSONArray) jsonObject.get("nbtTagByteArray");
                byte[] ints = new byte[jsonArray.size()];
                for (int i = 0; i < ints.length; i++) {
                    ints[i] = (Byte) jsonArray.get(i);
                }
                return new NBTTagByteArray(ints);
            } else {
                // well, it's not an array/list
                NBTTagCompound nbt = new NBTTagCompound();
                for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                    nbt.set(entry.getKey(), loadJSON(entry.getValue()));
                }
                return nbt;
            }
        } else if (json instanceof String) {
            return new NBTTagString((String) json);
        } else if (json instanceof Double) {
            return new NBTTagDouble((Double) json);
        } else if (json instanceof Float) {
            return new NBTTagFloat((Float) json);
        } else if (json instanceof Long) {
            return new NBTTagLong((Long) json);
        } else if (json instanceof Integer) {
            return new NBTTagInt((Integer) json);
        } else if (json instanceof Short) {
            return new NBTTagShort((Short) json);
        } else if (json instanceof Byte) {
            return new NBTTagByte((Byte) json);
        } else {
            return new NBTTagString(json.toString());
        }
    }

    private static Object load(NBTBase nbt) {
        if (nbt instanceof NBTTagList) {
            NBTTagList nbtList = (NBTTagList) nbt;
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            if (tagListField != null) {
                try {
                    List<?> baseList = (List<?>) tagListField.get(nbtList);
                    for (Object base : baseList) {
                        jsonArray.add(load((NBTBase) base));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            jsonObject.put("nbtTagList", jsonArray);
            return jsonObject;
        } else if (nbt instanceof NBTTagCompound) {
            NBTTagCompound nbtTagCompound = (NBTTagCompound) nbt;
            JSONObject jsonObject = new JSONObject();
            Set keys = nbtTagCompound.c();
            for (Object key : keys) {
                jsonObject.put((String) key, load(nbtTagCompound.get((String) key)));
            }
            return jsonObject;
        } else if (nbt instanceof NBTTagIntArray) {
            JSONArray jsonArray = new JSONArray();
            NBTTagIntArray nbtTagIntArray = (NBTTagIntArray) nbt;
            jsonArray.addAll(Arrays.asList(nbtTagIntArray.c()));

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("nbtTagIntArray", jsonArray);
            return jsonObject;
        } else if (nbt instanceof NBTTagByteArray) {
            JSONArray jsonArray = new JSONArray();
            NBTTagByteArray nbtTagByteArray = (NBTTagByteArray) nbt;
            jsonArray.addAll(Arrays.asList(nbtTagByteArray.c()));

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("nbtTagByteArray", jsonArray);
            return jsonObject;
        } else if (nbt instanceof NBTTagString) {
            NBTTagString nbtTagString = (NBTTagString) nbt;
            return nbtTagString.a_();
        } else if (nbt instanceof NBTTagDouble) {
            NBTTagDouble nbtTagDouble = (NBTTagDouble) nbt;
            return nbtTagDouble.g();
        } else if (nbt instanceof NBTTagFloat) {
            NBTTagFloat nbtTagFloat = (NBTTagFloat) nbt;
            return nbtTagFloat.h();
        } else if (nbt instanceof NBTTagLong) {
            NBTTagLong nbtTagLong = (NBTTagLong) nbt;
            return nbtTagLong.c();
        } else if (nbt instanceof NBTTagInt) {
            NBTTagInt nbtTagInt = (NBTTagInt) nbt;
            return nbtTagInt.d();
        } else if (nbt instanceof NBTTagShort) {
            NBTTagShort nbtTagShort = (NBTTagShort) nbt;
            return nbtTagShort.e();
        } else if (nbt instanceof NBTTagByte) {
            NBTTagByte nbtTagByte = (NBTTagByte) nbt;
            return nbtTagByte.f();
        } else {
            // wat
            return nbt.toString();
        }
    }

}
