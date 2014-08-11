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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.paulbgd.blocks.utils.reflection.BlocksReflection;
import me.paulbgd.blocks.utils.reflection.Reflection;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
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
import org.jnbt.ShortTag;
import org.jnbt.StringTag;
import org.jnbt.Tag;

public class NBTUtils {

    public static Reflection.ReflectionObject newToOld(Tag compoundTag) throws IOException {
        return loadJSONFromOld(loadJSON(compoundTag));
    }

    public static Tag oldToNew(Object nbtTagCompound) throws IOException {
        Object loaded = load(new Reflection.ReflectionObject(nbtTagCompound));
        if (loaded instanceof String) {
            throw new IllegalArgumentException("Loaded string '" + loaded + "' instead of json!");
        }
        System.out.println("Loaded JSON: " + loaded.toString());
        Tag tag = jsonToNewNBT((JSONObject) loaded);
        System.out.println("Tag: " + tag.toString());
        return tag;
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
                if(entry.getKey().equals("nbtTagList")) {
                    System.out.println("It's a tag list!");
                    JSONArray jsonArray = (JSONArray) entry.getValue();
                    List<Tag> tagList = new ArrayList<>();
                    for (Object o : jsonArray) {
                        tagList.add(loadNBT("", o));
                    }
                    return new ListTag(name, Tag.class, tagList);
                } else if(entry.getKey().equals("nbtTagByteArray")) {
                    System.out.println("It's a byte list!");
                    JSONArray jsonArray = (JSONArray) entry.getValue();
                    byte[] bytes = new byte[jsonArray.size()];
                    for (int i = 0, jsonArraySize = jsonArray.size(); i < jsonArraySize; i++) {
                        Object o = jsonArray.get(i);
                        bytes[i] = (byte) o;
                    }
                    return new ByteArrayTag(name, bytes);
                } else if(entry.getKey().equals("nbtTagInArray")) {
                    System.out.println("It's a int list!");
                    JSONArray jsonArray = (JSONArray) entry.getValue();
                    int[] ints = new int[jsonArray.size()];
                    for (int i = 0, jsonArraySize = jsonArray.size(); i < jsonArraySize; i++) {
                        Object o = jsonArray.get(i);
                        ints[i] = (int) o;
                    }
                    return new IntArrayTag(name, ints);
                } else {
                    Tag put = loadNBT(entry.getKey(), entry.getValue());
                    System.out.println("Loaded tag of type " + put.getClass().getSimpleName());
                    tagHashMap.put(entry.getKey(), put);
                }
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

    private static Reflection.ReflectionObject loadJSONFromOld(Object json) {
        if (json instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) json;
            if (jsonObject.containsKey("nbtTagList")) {
                JSONArray jsonArray = (JSONArray) jsonObject.get("nbtTagList");
                Reflection.ReflectionObject reflectionObject = new Reflection.ReflectionObject(Reflection.getClass("NBTTagList", Reflection.PackageType.NMS).newInstance());
                for (Object object : jsonArray) {
                    reflectionObject.getMethod("add", null, 1).invoke(loadJSONFromOld(object).getObject());
                }
                return reflectionObject;
            } else if (jsonObject.containsKey("nbtTagIntArray")) {
                JSONArray jsonArray = (JSONArray) jsonObject.get("nbtTagIntArray");
                int[] ints = new int[jsonArray.size()];
                for (int i = 0; i < ints.length; i++) {
                    ints[i] = (Integer) jsonArray.get(i);
                }

                return new Reflection.ReflectionObject(Reflection.getClass("NBTTagIntArray", Reflection.PackageType.NMS).newInstance(new Object[]{ints}));
            } else if (jsonObject.containsKey("nbtTagByteArray")) {
                JSONArray jsonArray = (JSONArray) jsonObject.get("nbtTagByteArray");
                byte[] ints = new byte[jsonArray.size()];
                for (int i = 0; i < ints.length; i++) {
                    ints[i] = (Byte) jsonArray.get(i);
                }
                return new Reflection.ReflectionObject(Reflection.getClass("NBTTagByteArray", Reflection.PackageType.NMS).newInstance(new Object[]{ints}));
            } else {
                Reflection.ReflectionObject nbt = new Reflection.ReflectionObject(BlocksReflection.getNbtTagCompound().newInstance());
                for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                    nbt.getMethod("set", null, 2).invoke(entry.getKey(), loadJSONFromOld(entry.getValue()).getObject());
                }
                return nbt;
            }
        } else if (json instanceof String) {
            return new Reflection.ReflectionObject(Reflection.getClass("NBTTagString", Reflection.PackageType.NMS).newInstance(json));
        } else if (json instanceof Double) {
            return new Reflection.ReflectionObject(Reflection.getClass("NBTTagDouble", Reflection.PackageType.NMS).newInstance(json));
        } else if (json instanceof Float) {
            return new Reflection.ReflectionObject(Reflection.getClass("NBTTagFloat", Reflection.PackageType.NMS).newInstance(json));
        } else if (json instanceof Long) {
            return new Reflection.ReflectionObject(Reflection.getClass("NBTTagLong", Reflection.PackageType.NMS).newInstance(json));
        } else if (json instanceof Integer) {
            return new Reflection.ReflectionObject(Reflection.getClass("NBTTagInt", Reflection.PackageType.NMS).newInstance((int) json));
        } else if (json instanceof Short) {
            return new Reflection.ReflectionObject(Reflection.getClass("NBTTagShort", Reflection.PackageType.NMS).newInstance(json));
        } else if (json instanceof Byte) {
            return new Reflection.ReflectionObject(Reflection.getClass("NBTTagByte", Reflection.PackageType.NMS).newInstance(json));
        } else {
            return new Reflection.ReflectionObject(Reflection.getClass("NBTTagString", Reflection.PackageType.NMS).newInstance(json.toString()));
        }
    }

    private static Object load(Reflection.ReflectionObject nbt) {
        if (nbt.getName().equals("NBTTagList")) {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            List<?> baseList = (List<?>) nbt.getField(0, List.class).getValue();
            for (Object base : baseList) {
                jsonArray.add(load(new Reflection.ReflectionObject(base)));
            }
            jsonObject.put("nbtTagList", jsonArray);
            return jsonObject;
        } else if (nbt.getName().equals("NBTTagCompound")) {
            JSONObject jsonObject = new JSONObject();
            Set keys = (Set) nbt.getMethod("c", Set.class).invoke();
            for (Object key : keys) {
                jsonObject.put((String) key, load(new Reflection.ReflectionObject(nbt.getMethod("get", Reflection.getClass("NBTBase", Reflection.PackageType.NMS).getAClass()).invoke(key))));
            }
            return jsonObject;
        } else if (nbt.getName().equals("NBTTagIntArray")) {
            JSONArray jsonArray = new JSONArray();
            jsonArray.addAll(Arrays.asList((int[]) nbt.getMethod("c", int[].class).invoke()));
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("nbtTagIntArray", jsonArray);
            return jsonObject;
        } else if (nbt.getName().equals("NBTTagByteArray")) {
            JSONArray jsonArray = new JSONArray();
            jsonArray.addAll(Arrays.asList((byte[]) nbt.getMethod("c", byte[].class).invoke()));
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("nbtTagByteArray", jsonArray);
            return jsonObject;
        } else if (nbt.getName().equals("NBTTagString")) {
            return nbt.getMethod("a_", String.class).invoke();
        } else if (nbt.getName().equals("NBTTagDouble")) {
            return nbt.getMethod("g", double.class).invoke();
        } else if (nbt.getName().equals("NBTTagFloat")) {
            return nbt.getMethod("h", float.class).invoke();
        } else if (nbt.getName().equals("NBTTagLong")) {
            return nbt.getMethod("c", long.class).invoke();
        } else if (nbt.getName().equals("NBTTagInt")) {
            return nbt.getMethod("d", int.class).invoke();
        } else if (nbt.getName().equals("NBTTagShort")) {
            return nbt.getMethod("e", short.class).invoke();
        } else if (nbt.getName().equals("NBTTagByte")) {
            return nbt.getMethod("f", byte.class).invoke();
        } else {
            return nbt.getName();
        }
    }

}
