package me.paulbgd.blocks.utils;

public class Utils {

    public static <T> boolean arrayContains(final T[] array, final T v) {
        for (final T e : array)
            if (e == v || v.equals(e))
                return true;
        return false;
    }

}
