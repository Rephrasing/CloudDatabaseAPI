package com.github.rephrasing.cloud.bson;

import java.util.ArrayList;
import java.util.List;

public class IdentifierClassChecker {

    private static final List<Class<?>> wrapperClasses = new ArrayList<>();
    private static final List<Class<?>> primitiveClasses = new ArrayList<>();

    static {
        primitiveClasses.add(char.class);
        primitiveClasses.add(int.class);
        primitiveClasses.add(double.class);
        primitiveClasses.add(short.class);
        primitiveClasses.add(float.class);
        primitiveClasses.add(long.class);

        wrapperClasses.add(String.class);
        wrapperClasses.add(Character.class);
        wrapperClasses.add(Integer.class);
        wrapperClasses.add(Double.class);
        wrapperClasses.add(Short.class);
        wrapperClasses.add(Long.class);
        wrapperClasses.add(Float.class);
    }

    public static boolean isWrapperOrPrimitive(Object object) {
        return wrapperClasses.contains(object.getClass()) || primitiveClasses.contains(object.getClass());
    }

}
