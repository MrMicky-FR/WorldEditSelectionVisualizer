/*
 * Decompiled with CFR 0_110.
 *
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 */

package com.darkblade12.particleeffect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;

public final class ReflectionUtils {
    private ReflectionUtils() {
    }

    public static /* varargs */ Constructor<?> getConstructor(final Class<?> clazz, final Class<?>... parameterTypes)
            throws NoSuchMethodException {
        final Class<?>[] primitiveTypes = DataType.getPrimitive(parameterTypes);
        for (final Constructor<?> constructor : clazz.getConstructors()) {
            if (!DataType.compare(DataType.getPrimitive(constructor.getParameterTypes()), primitiveTypes)) {
                continue;
            }
            return constructor;
        }
        throw new NoSuchMethodException(
                "There is no such constructor in this class with the specified parameter types");
    }

    public static /* varargs */ Constructor<?> getConstructor(final String className, final PackageType packageType,
            final Class<?>... parameterTypes) throws NoSuchMethodException, ClassNotFoundException {
        return ReflectionUtils.getConstructor(packageType.getClass(className), parameterTypes);
    }

    public static /* varargs */ Object instantiateObject(final Class<?> clazz, final Object... arguments)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException {
        return ReflectionUtils.getConstructor(clazz, DataType.getPrimitive(arguments)).newInstance(arguments);
    }

    public static /* varargs */ Object instantiateObject(final String className, final PackageType packageType,
            final Object... arguments) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
        return ReflectionUtils.instantiateObject(packageType.getClass(className), arguments);
    }

    public static /* varargs */ Method getMethod(final Class<?> clazz, final String methodName,
            final Class<?>... parameterTypes) throws NoSuchMethodException {
        final Class<?>[] primitiveTypes = DataType.getPrimitive(parameterTypes);
        for (final Method method : clazz.getMethods()) {
            if (!method.getName().equals(methodName)
                    || !DataType.compare(DataType.getPrimitive(method.getParameterTypes()), primitiveTypes)) {
                continue;
            }
            return method;
        }
        throw new NoSuchMethodException(
                "There is no such method in this class with the specified name and parameter types");
    }

    public static /* varargs */ Method getMethod(final String className, final PackageType packageType,
            final String methodName, final Class<?>... parameterTypes)
            throws NoSuchMethodException, ClassNotFoundException {
        return ReflectionUtils.getMethod(packageType.getClass(className), methodName, parameterTypes);
    }

    public static /* varargs */ Object invokeMethod(final Object instance, final String methodName,
            final Object... arguments)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
        return ReflectionUtils.getMethod(instance.getClass(), methodName, DataType.getPrimitive(arguments))
                .invoke(instance, arguments);
    }

    public static /* varargs */ Object invokeMethod(final Object instance, final Class<?> clazz,
            final String methodName, final Object... arguments)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
        return ReflectionUtils.getMethod(clazz, methodName, DataType.getPrimitive(arguments)).invoke(instance,
                arguments);
    }

    public static /* varargs */ Object invokeMethod(final Object instance, final String className,
            final PackageType packageType, final String methodName, final Object... arguments)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
            ClassNotFoundException {
        return ReflectionUtils.invokeMethod(instance, packageType.getClass(className), methodName, arguments);
    }

    public static Field getField(final Class<?> clazz, final boolean declared, final String fieldName)
            throws NoSuchFieldException, SecurityException {
        final Field field = declared ? clazz.getDeclaredField(fieldName) : clazz.getField(fieldName);
        field.setAccessible(true);
        return field;
    }

    public static Field getField(final String className, final PackageType packageType, final boolean declared,
            final String fieldName) throws NoSuchFieldException, SecurityException, ClassNotFoundException {
        return ReflectionUtils.getField(packageType.getClass(className), declared, fieldName);
    }

    public static Object getValue(final Object instance, final Class<?> clazz, final boolean declared,
            final String fieldName)
            throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        return ReflectionUtils.getField(clazz, declared, fieldName).get(instance);
    }

    public static Object getValue(final Object instance, final String className, final PackageType packageType,
            final boolean declared, final String fieldName) throws IllegalArgumentException, IllegalAccessException,
            NoSuchFieldException, SecurityException, ClassNotFoundException {
        return ReflectionUtils.getValue(instance, packageType.getClass(className), declared, fieldName);
    }

    public static Object getValue(final Object instance, final boolean declared, final String fieldName)
            throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        return ReflectionUtils.getValue(instance, instance.getClass(), declared, fieldName);
    }

    public static void setValue(final Object instance, final Class<?> clazz, final boolean declared,
            final String fieldName, final Object value)
            throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        ReflectionUtils.getField(clazz, declared, fieldName).set(instance, value);
    }

    public static void setValue(final Object instance, final String className, final PackageType packageType,
            final boolean declared, final String fieldName, final Object value) throws IllegalArgumentException,
            IllegalAccessException, NoSuchFieldException, SecurityException, ClassNotFoundException {
        ReflectionUtils.setValue(instance, packageType.getClass(className), declared, fieldName, value);
    }

    public static void setValue(final Object instance, final boolean declared, final String fieldName,
            final Object value)
            throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        ReflectionUtils.setValue(instance, instance.getClass(), declared, fieldName, value);
    }

    public static enum DataType {
        BYTE(Byte.TYPE, Byte.class), SHORT(Short.TYPE, Short.class), INTEGER(Integer.TYPE, Integer.class), LONG(
                Long.TYPE, Long.class), CHARACTER(Character.TYPE, Character.class), FLOAT(Float.TYPE,
                        Float.class), DOUBLE(Double.TYPE, Double.class), BOOLEAN(Boolean.TYPE, Boolean.class);

        private static final Map<Class<?>, DataType> CLASS_MAP;
        private final Class<?>                       primitive;
        private final Class<?>                       reference;

        private DataType(final Class<?> primitive, final Class<?> reference) {
            this.primitive = primitive;
            this.reference = reference;
        }

        public Class<?> getPrimitive() {
            return this.primitive;
        }

        public Class<?> getReference() {
            return this.reference;
        }

        public static DataType fromClass(final Class<?> clazz) {
            return CLASS_MAP.get(clazz);
        }

        public static Class<?> getPrimitive(final Class<?> clazz) {
            final DataType type = DataType.fromClass(clazz);
            return type == null ? clazz : type.getPrimitive();
        }

        public static Class<?> getReference(final Class<?> clazz) {
            final DataType type = DataType.fromClass(clazz);
            return type == null ? clazz : type.getReference();
        }

        public static Class<?>[] getPrimitive(final Class<?>[] classes) {
            final int length = classes == null ? 0 : classes.length;
            final Class<?>[] types = new Class[length];
            for (int index = 0; index < length; ++index) {
                types[index] = DataType.getPrimitive(classes[index]);
            }
            return types;
        }

        public static Class<?>[] getReference(final Class<?>[] classes) {
            final int length = classes == null ? 0 : classes.length;
            final Class<?>[] types = new Class[length];
            for (int index = 0; index < length; ++index) {
                types[index] = DataType.getReference(classes[index]);
            }
            return types;
        }

        public static Class<?>[] getPrimitive(final Object[] objects) {
            final int length = objects == null ? 0 : objects.length;
            final Class<?>[] types = new Class[length];
            for (int index = 0; index < length; ++index) {
                types[index] = DataType.getPrimitive(objects[index].getClass());
            }
            return types;
        }

        public static Class<?>[] getReference(final Object[] objects) {
            final int length = objects == null ? 0 : objects.length;
            final Class<?>[] types = new Class[length];
            for (int index = 0; index < length; ++index) {
                types[index] = DataType.getReference(objects[index].getClass());
            }
            return types;
        }

        public static boolean compare(final Class<?>[] primary, final Class<?>[] secondary) {
            if (primary == null || secondary == null || primary.length != secondary.length) {
                return false;
            }
            for (int index = 0; index < primary.length; ++index) {
                final Class<?> primaryClass = primary[index];
                final Class<?> secondaryClass = secondary[index];
                if (primaryClass.equals(secondaryClass) || primaryClass.isAssignableFrom(secondaryClass)) {
                    continue;
                }
                return false;
            }
            return true;
        }

        static {
            CLASS_MAP = new HashMap<Class<?>, DataType>();
            for (final DataType type : DataType.values()) {
                CLASS_MAP.put(type.primitive, type);
                CLASS_MAP.put(type.reference, type);
            }
        }
    }

    public static enum PackageType {
        MINECRAFT_SERVER("net.minecraft.server." + PackageType.getServerVersion()), CRAFTBUKKIT(
                "org.bukkit.craftbukkit." + PackageType.getServerVersion()), CRAFTBUKKIT_BLOCK(CRAFTBUKKIT,
                        "block"), CRAFTBUKKIT_CHUNKIO(CRAFTBUKKIT, "chunkio"), CRAFTBUKKIT_COMMAND(CRAFTBUKKIT,
                                "command"), CRAFTBUKKIT_CONVERSATIONS(CRAFTBUKKIT,
                                        "conversations"), CRAFTBUKKIT_ENCHANTMENS(CRAFTBUKKIT,
                                                "enchantments"), CRAFTBUKKIT_ENTITY(CRAFTBUKKIT,
                                                        "entity"), CRAFTBUKKIT_EVENT(CRAFTBUKKIT,
                                                                "event"), CRAFTBUKKIT_GENERATOR(CRAFTBUKKIT,
                                                                        "generator"), CRAFTBUKKIT_HELP(CRAFTBUKKIT,
                                                                                "help"), CRAFTBUKKIT_INVENTORY(
                                                                                        CRAFTBUKKIT,
                                                                                        "inventory"), CRAFTBUKKIT_MAP(
                                                                                                CRAFTBUKKIT,
                                                                                                "map"), CRAFTBUKKIT_METADATA(
                                                                                                        CRAFTBUKKIT,
                                                                                                        "metadata"), CRAFTBUKKIT_POTION(
                                                                                                                CRAFTBUKKIT,
                                                                                                                "potion"), CRAFTBUKKIT_PROJECTILES(
                                                                                                                        CRAFTBUKKIT,
                                                                                                                        "projectiles"), CRAFTBUKKIT_SCHEDULER(
                                                                                                                                CRAFTBUKKIT,
                                                                                                                                "scheduler"), CRAFTBUKKIT_SCOREBOARD(
                                                                                                                                        CRAFTBUKKIT,
                                                                                                                                        "scoreboard"), CRAFTBUKKIT_UPDATER(
                                                                                                                                                CRAFTBUKKIT,
                                                                                                                                                "updater"), CRAFTBUKKIT_UTIL(
                                                                                                                                                        CRAFTBUKKIT,
                                                                                                                                                        "util");

        private final String path;

        private PackageType(final String path) {
            this.path = path;
        }

        private PackageType(final PackageType parent, final String path) {
            this((parent) + "." + path);
        }

        public String getPath() {
            return this.path;
        }

        public Class<?> getClass(final String className) throws ClassNotFoundException {
            return Class.forName((this) + "." + className);
        }

        @Override
        public String toString() {
            return this.path;
        }

        public static String getServerVersion() {
            return Bukkit.getServer().getClass().getPackage().getName().substring(23);
        }
    }

}
