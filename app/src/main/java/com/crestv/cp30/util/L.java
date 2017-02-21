package com.crestv.cp30.util;

import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * log日志打印/保存
 * Created by txj on 16/7/23.
 */
public class L {
    private static final SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat hms = new SimpleDateFormat("hh-mm-ss");

    private L() {
    }

    public static final String TAG = L.class.getName();

    public static void e(String str) {
        e(TAG, str);
    }

    public static void e(String tag, String str) {
        Log.e(tag, str);
    }

    public static void e(Throwable e) {
        e(TAG, e);
    }

    public static void e(String tag, Throwable e) {
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            Log.e(tag, stackTraceElement.toString());
        }
    }

    public static void i(String str) {
        i(TAG, str);
    }

    public static void i(String tag, String str) {
        Log.i(tag, str);
    }

    public static void w(String str) {
        w(TAG, str);
    }

    public static void w(String tag, String str) {
        Log.w(tag, str);
    }

    public static void d(String str) {
        d(TAG, str);
    }

    public static void d(String tag, String str) {
        Log.d(tag, str);
    }

    public static void v(String str) {
        v(TAG, str);
    }

    public static void v(String tag, String str) {
        Log.v(tag, str);
    }

    public static String getYmd() {
        return ymd.format(Calendar.getInstance().getTime());
    }

    public static String getHms() {
        return hms.format(Calendar.getInstance().getTime());
    }

    public static String getYmdHms() {
        return getYmd() + " " + getHms();
    }

    public static void saveToLog(Throwable e) {
        saveToLog(new File(FileUtil.SDCARD_APP_DIR, getYmd() + ".log"), e);
    }

    public static void saveToLog(CharSequence str) {
        saveToLog(new File(FileUtil.SDCARD_APP_DIR, getYmd() + ".log"), str);
    }

    public static void saveToLog(File logFile, CharSequence str) {
        try {
            IO.io(new StringBuffer().append('\n').append(getHms()).append("\t:\n").append(str).toString(), new FileWriter(logFile, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveToLog(File logFile, Throwable ex) {
        try {
            IO.io(new StringBuffer().append('\n').append(getHms()).append(":\n").append(getExceptionInfo(ex)).toString(), new FileWriter(logFile, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static CharSequence getExceptionInfo(Throwable e) {
        if (e == null) {
            return "";
        }
        StringBuffer buf = new StringBuffer();
        buf.append('\n').append(e);
        for (StackTraceElement element : e.getStackTrace()) {
            buf.append("\tat ").append(element.toString()).append('\n');
        }
        buf.append("Caused by: " + e.getCause())
                .append(getExceptionInfo(e.getCause()))
                .append('\n');
        return buf;
    }
}
