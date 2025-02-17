package com.darewro.riderApp.view.utils;

import java.util.Arrays;

public class Logger {

    /**
     * Priority constant for the println method; use Log.v.
     */
    public static final int VERBOSE = 2;

    /**
     * Priority constant for the println method; use Log.d.
     */
    public static final int DEBUG = 3;

    /**
     * Priority constant for the println method; use Log.i.
     */
    public static final int INFO = 4;

    /**
     * Priority constant for the println method; use Log.w.
     */
    public static final int WARN = 5;

    /**
     * Priority constant for the println method; use Log.e.
     */
    public static final int ERROR = 6;

    /**
     * Priority constant for the println method.
     */
    public static final int ASSERT = 7;
    /**
     * @see <a href="http://stackoverflow.com/a/8899735" />
     */
    private static final int ENTRY_MAX_LEN = 4000;
    private static String LOG_TAG = "";

    /**
     * @param args If the last argument is an exception than it prints out the stack trace, and there should be no {}
     *             or %s placeholder for it.
     */
    public static void d(String tag, String message, Object... args) {
        LOG_TAG = tag;
        log(Logger.DEBUG, false, message, args);
    }

    public static void LogFullString(int priority, String tag, String message, Object... args) {
        LOG_TAG = tag;
        log(priority, true, message, args);
    }

    public static void i(String tag, String message, Object... args) {
        LOG_TAG = tag;
        log(Logger.INFO, false, message, args);
    }

    public static void w(String tag, String message, Object... args) {
        LOG_TAG = tag;
        log(Logger.WARN, false, message, args);
    }

    public static void e(String tag, String message, Object... args) {
        LOG_TAG = tag;
        log(Logger.ERROR, false, message, args);
    }

    private static void log(int priority, boolean ignoreLimit, String message, Object... args) {
        String print;
        if (args != null && args.length > 0 && args[args.length - 1] instanceof Throwable) {
            Object[] truncated = Arrays.copyOf(args, args.length - 1);
            Throwable ex = (Throwable) args[args.length - 1];
            print = formatMessage(message, truncated) + '\n' + android.util.Log.getStackTraceString(ex);
        } else {
            print = formatMessage(message, args);
        }
        if (ignoreLimit) {
            while (!print.isEmpty()) {
                int lastNewLine = print.lastIndexOf('\n', ENTRY_MAX_LEN);
                int nextEnd = lastNewLine != -1 ? lastNewLine : Math.min(ENTRY_MAX_LEN, print.length());
                String next = print.substring(0, nextEnd /*exclusive*/);
                android.util.Log.println(priority, LOG_TAG, next);
                if (lastNewLine != -1) {
                    // Don't print out the \n twice.
                    print = print.substring(nextEnd + 1);
                } else {
                    print = print.substring(nextEnd);
                }
            }
        } else {
            android.util.Log.println(priority, LOG_TAG, print);
        }
    }

    private static String formatMessage(String message, Object... args) {
        String formatted;
        try {
            /*
             * {} is used by SLF4J so keep it compatible with that as it's easy to forget to use %s when you are
             * switching back and forth between server and client code.
             */
            formatted = String.format(message.replaceAll("\\{\\}", "%s"), args);
        } catch (Exception ex) {
            formatted = message + Arrays.toString(args);
        }
        return formatted;
    }
}