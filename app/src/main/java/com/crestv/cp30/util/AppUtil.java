package com.crestv.cp30.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 应用程序工具包
 * Created by txj on 16/7/23.
 */
public class AppUtil {

    /**
     * 判断程序是否处于后台
     *
     * @return true表示程序当前处于后台，false表示程序当前处于前台
     */
    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.processName.equals(context.getPackageName())) {
                return appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND;
            }
        }
        return false;
    }

    public static String getMid(Context context) {
        String imei = "", AndroidID = "", serialNo = "";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            imei = tm.getDeviceId();
            AndroidID = android.provider.Settings.System.getString(context.getContentResolver(), "android_id");
            serialNo = getDeviceSerialForMid2();
        } catch (Exception e) {
        }
//        String m2 = getMD5Str(imei + AndroidID + serialNo);
        return imei + AndroidID + serialNo;
    }

    private static String getDeviceSerialForMid2() {
        String serial = "";
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception ignored) {
        }
        return serial;
    }

    //版本名
    public static String getVersionName(Context context) {
        try {
            return getPackageInfo(context).versionName;
        } catch (Exception e) {
            return "";
        }
    }

    //版本号
    public static int getVersionCode(Context context) {
        try {
            return getPackageInfo(context).versionCode;
        } catch (Exception e) {
            return 0;
        }
    }

    private static PackageInfo pi;

    private static PackageInfo getPackageInfo(Context context) {
        if (pi != null) {
            return pi;
        }
        try {
            pi = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            return pi;
        } catch (Exception e) {
        }
        return pi;
    }

    /**
     * 命令行
     *
     * @param exec
     */
    public static int exec(String exec) {
        int status = Integer.MIN_VALUE;
        try {
            Process p = Runtime.getRuntime().exec(exec);
            status = p.exitValue();
            p.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return status;
    }

    /**
     * 是否连接网络
     */
    public static boolean isConnNet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable();
    }

    /**
     * 是否连接Wifi
     */
    public static boolean isConnWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiNetworkInfo != null && wifiNetworkInfo.isConnected();
    }

    /**
     * 获取屏幕尺寸与密度.
     * DisplayMetrics{density=1.5, width=480, height=854, scaledDensity=1.5, xdpi=160.421, ydpi=159.497}
     * DisplayMetrics{density=2.0, width=720, height=1280, scaledDensity=2.0, xdpi=160.42105, ydpi=160.15764}
     *
     * @return mDisplayMetrics
     */
    public static DisplayMetrics getDisplayMetrics() {
        return Resources.getSystem().getDisplayMetrics();
    }
}
