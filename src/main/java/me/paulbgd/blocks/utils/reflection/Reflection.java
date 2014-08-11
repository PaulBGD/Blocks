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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

public class Reflection {

    private static String version = StringUtils.split(Bukkit.getServer().getClass().getPackage().getName(), ".")[3];
    private static String nmsPackage = "net.minecraft.server." + version;
    private static String cbsPackage = "org.bukkit.craftbukkit." + version;

    public static ReflectionObject getObject(Object object) {
        return new ReflectionObject(object);
    }

    public static ReflectionField getField(int index, Class<?> type, Object object) {
        int i = 0;
        for (Field field : object.getClass().getDeclaredFields()) {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            if (field.getType().equals(type) && !Modifier.isStatic(field.getModifiers()) && i++ == index) {
                return new ReflectionField(field, object);
            }
        }
        return null;
    }

    public static ReflectionMethod getMethod(int index, Class<?> returnType, Object object) {
        if (returnType == null) {
            returnType = Void.TYPE;
        }
        int i = 0;
        System.out.println("Looking in " + object.getClass());
        for (Method method : object.getClass().getDeclaredMethods()) {
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            System.out.println("Looking for " + returnType + " == " + method.getReturnType() + " " + method.getName());
            if (method.getReturnType() == returnType && !Modifier.isStatic(method.getModifiers()) && i++ == index) {
                return new ReflectionMethod(method, object);
            }
        }
        System.out.println("Method index " + index + " returning " + returnType + " for " + object.getClass() + " does not exist");
        return null;
    }

    public static ReflectionClass getClass(String name, PackageType packageType) {
        if (packageType == PackageType.NMS) {
            try {
                return new ReflectionClass(Class.forName(nmsPackage + "." + name));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            try {
                return new ReflectionClass(Class.forName(cbsPackage + "." + name));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Data
    public static class ReflectionObject {
        private final Object object;
        private final ReflectionClass reflectionClass;

        public ReflectionObject(Object object) {
            this.object = object;
            this.reflectionClass = new ReflectionClass(object.getClass());
        }

        public String getName() {
            return reflectionClass.getAClass().getSimpleName();
        }

        public ReflectionField getField(int index, Class<?> returnType) {
            return Reflection.getField(index, returnType, object);
        }

        public ReflectionField getField(String name) {
            return reflectionClass.getField(name, object);
        }

        public ReflectionMethod getMethod(int index, Class<?> returnType) {
            return Reflection.getMethod(index, returnType, object);
        }

        public ReflectionMethod getMethod(String name, Class<?> returnType) {
            return reflectionClass.getMethod(name, returnType, this.object);
        }

        public ReflectionMethod getMethod(String name, Class<?> returnType, int args) {
            return reflectionClass.getMethod(name, returnType, args, this.object);
        }

        public ReflectionField getStaticField(int index, Class<?> type) {
            return reflectionClass.getStaticField(index, type);
        }

        public ReflectionMethod getStaticMethod(int index, Class<?> returnType) {
            return reflectionClass.getStaticMethod(index, returnType);
        }

        @Override
        public String toString() {
            return "ReflectionObject{" + this.object.getClass() + "}";
        }

    }

    @Data
    public static class ReflectionClass {
        private final Class<?> aClass;

        public Object newInstance(Object... arguments) {

            try {
                return aClass.getConstructor(classesFromObject(arguments)).newInstance(arguments);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        }

        public ReflectionMethod getStaticMethod(int index, Class<?> returnType) {
            if (returnType == null) {
                returnType = Void.TYPE;
            }
            int i = 0;
            for (Method method : aClass.getDeclaredMethods()) {
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                if (method.getReturnType() == returnType && Modifier.isStatic(method.getModifiers()) && i++ == index) {
                    return new ReflectionMethod(method, null);
                }
            }
            return null;
        }

        public ReflectionMethod getStaticMethod(String name, Class<?> returnType) {
            if (returnType == null) {
                returnType = Void.TYPE;
            }
            for (Method method : aClass.getDeclaredMethods()) {
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                if (method.getReturnType() == returnType && Modifier.isStatic(method.getModifiers()) && method.getName().equals(name)) {
                    return new ReflectionMethod(method, null);
                }
            }
            return null;
        }

        public ReflectionField getField(String name, Object object) {
            for (Field method : aClass.getDeclaredFields()) {
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                if (!Modifier.isStatic(method.getModifiers()) && method.getName().equals(name)) {
                    return new ReflectionField(method, object);
                }
            }
            return null;
        }

        public ReflectionMethod getStaticMethod(String name, Class<?> returnType, Class<?>[] parameters) {
            if (returnType == null) {
                returnType = Void.TYPE;
            }
            methodLoop:
            for (Method method : aClass.getDeclaredMethods()) {
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                if (method.getReturnType() == returnType && Modifier.isStatic(method.getModifiers()) && method.getName().equals(name)) {
                    for (int i = 0, parametersLength = parameters.length; i < parametersLength; i++) {
                        if (!method.getParameterTypes()[i].equals(parameters[i])) {
                            continue methodLoop;
                        }
                    }
                    return new ReflectionMethod(method, null);
                }
            }
            return null;
        }

        public ReflectionField getStaticField(String name) {
            for (Field field : aClass.getDeclaredFields()) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                if (Modifier.isStatic(field.getModifiers()) && field.getName().equals(name)) {
                    return new ReflectionField(field, null);
                }
            }
            return null;
        }

        public ReflectionField getStaticField(int index, Class<?> type) {
            int i = 0;
            for (Field field : aClass.getDeclaredFields()) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                if (field.getType().equals(type) && Modifier.isStatic(field.getModifiers()) && i++ == index) {
                    return new ReflectionField(field, null);
                }
            }
            return null;
        }

        public ReflectionMethod getMethod(int index, Class<?> returnType, Object object) {
            if (!this.aClass.isAssignableFrom(object.getClass())) {
                throw new IllegalArgumentException("Cannot assign " + object.getClass() + " to " + this.aClass);
            }
            if (returnType == null) {
                returnType = Void.TYPE;
            }
            int i = 0;
            for (Method method : aClass.getDeclaredMethods()) {
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                if (method.getReturnType() == returnType && !Modifier.isStatic(method.getModifiers()) && i++ == index) {
                    return new ReflectionMethod(method, object);
                }
            }
            return null;
        }

        public ReflectionMethod getDirectMethod(int index, Class<?> returnType, Object object) {
            return getMethod(index, returnType, object);
        }

        public ReflectionMethod getMethod(String name, Class<?> returnType, Object object) {
            return this.getMethod(name, returnType, -1, object);
        }

        public ReflectionMethod getMethod(String name, Class<?> returnType, int argumentCount, Object object) {
            if (!this.aClass.isAssignableFrom(object.getClass())) {
                throw new IllegalArgumentException("Cannot assign " + object.getClass() + " to " + this.aClass);
            }
            if (returnType == null) {
                returnType = Void.TYPE;
            }
            for (Method method : aClass.getDeclaredMethods()) {
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                if (method.getReturnType() == returnType && (argumentCount == -1 || method.getParameterTypes().length == argumentCount) && !Modifier.isStatic(method.getModifiers()) && method.getName().equals(name)) {
                    return new ReflectionMethod(method, object);
                }
            }
            return null;
        }

        public ReflectionMethod getMethod(int index, Class<?>[] parameters, Object object) {
            int j = 0;
            if (!this.aClass.isAssignableFrom(object.getClass())) {
                throw new IllegalArgumentException("Cannot assign " + object.getClass() + " to " + this.aClass);
            }
            methodLoop:
            for (Method method : aClass.getDeclaredMethods()) {
                if (!Modifier.isStatic(method.getModifiers()) && method.getParameterTypes().length == parameters.length) {
                    for (int i = 0, parametersLength = parameters.length; i < parametersLength; i++) {
                        if (!method.getParameterTypes()[i].equals(parameters[i])) {
                            continue methodLoop;
                        }
                    }
                    if(j++ == index) {
                        return new ReflectionMethod(method, object);
                    }
                }
            }
            return null;
        }

        public ReflectionField getField(int index, Class<?> type, Object object) {
            int i = 0;
            for (Field field : aClass.getDeclaredFields()) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                if (field.getType().equals(type) && !Modifier.isStatic(field.getModifiers()) && i++ == index) {
                    return new ReflectionField(field, object);
                }
            }
            return null;
        }

        private static Class<?>[] classesFromObject(Object... objects) {
            Class<?>[] classes = new Class<?>[objects.length];
            for (int i = 0, objectsLength = objects.length; i < objectsLength; i++) {
                Class<?> clazz = objects[i].getClass();
                if (clazz.equals(Integer.class)) {
                    clazz = int.class;
                }
                classes[i] = clazz;
            }
            return classes;
        }

    }

    @Data
    public static class ReflectionMethod {
        private final Method method;
        private final Object object;

        public Object invoke(Object... args) {
            checkAccessible();
            Class<?>[] parameterTypes = method.getParameterTypes();

            for (int i = 0, parameterTypesLength = parameterTypes.length; i < parameterTypesLength; i++) {
                Class<?> checking = parameterTypes[i];
                if (checking.equals(int.class)) {
                    checking = Integer.class;
                }
                if (!checking.isAssignableFrom(args[i].getClass())) {
                    throw new IllegalArgumentException("Invalid argument '" + args[i] + "' (" + args[i].getClass() + ") for parameter '" + checking + "'!");
                }
            }
            try {
                return this.method.invoke(object, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return null;
        }

        private void checkAccessible() {
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
        }

    }

    @Data
    public static class ReflectionField {
        private final Field field;
        private final Object object;

        public Object getValue() {
            checkAccessible();
            try {
                return field.get(object);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }

        public void setValue(Object value) {
            checkAccessible();
            try {
                field.set(object, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        private void checkAccessible() {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
        }
    }

    public enum PackageType {
        NMS, CBS;
    }

}
