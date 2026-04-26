package com.tradesys.util;

public class DataScopeContext {

    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();

    private DataScopeContext() {
    }

    public static void set(String condition) {
        CONTEXT.set(condition);
    }

    public static String get() {
        return CONTEXT.get();
    }

    public static void remove() {
        CONTEXT.remove();
    }
}
