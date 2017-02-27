package com.crestv.cp30.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.util.List;

/**
 * Created by Administrator on 2017/2/27 0027.
 */

public class UpdateAppUtil {
    //app更新

    private static AlertDialog.Builder mDialog;

    /**
     * 强制更新
     *
     * @param appName
     * @param downUrl
     * @param updateinfo
     */
    public static   void forceUpdate(final Context context, final String appName, final String downUrl, final String updateinfo) {
        mDialog = new AlertDialog.Builder(context);
        mDialog.setTitle(appName + "又更新咯！");
        mDialog.setMessage(updateinfo);
        mDialog.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!canDownloadState(context)) {
                    showDownloadSetting(context);
                    return;
                }
                //   DownLoadApk.download(MainActivity.this,downUrl,updateinfo,appName);
                AppInnerDownLoder.downLoadApk(context, downUrl, appName);
            }
        }).setCancelable(false).create().show();
    }

    /**
     * 正常更新
     *
     * @param appName
     * @param downUrl
     * @param updateinfo
     */
    public static void normalUpdate(final Context context, final String appName, final String downUrl, final String updateinfo) {
        mDialog = new AlertDialog.Builder(context);
        mDialog.setTitle(appName + "又更新咯！");
        mDialog.setMessage(updateinfo);
        mDialog.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!canDownloadState(context)) {
                    showDownloadSetting(context);
                    return;
                }
                // AppInnerDownLoder.downLoadApk(MainActivity.this,downUrl,appName);
                AppInnerDownLoder.downLoadApk(context, downUrl, appName);
            }
        }).setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setCancelable(false).create().show();
    }

    /**
     * 无需跟新
     */
    public void noneUpdate(Context context) {
        mDialog = new AlertDialog.Builder(context);
        mDialog.setTitle("版本更新")
                .setMessage("当前已是最新版本无需更新")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false).create().show();
    }
    private static boolean canDownloadState(Context context) {
        try {
            int state = context.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");

            if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    private static void showDownloadSetting(Context context) {
        String packageName = "com.android.providers.downloads";
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + packageName));
        if (intentAvailable(intent,context)) {
            context.startActivity(intent);
        }
    }
    private static boolean intentAvailable(Intent intent,Context context) {
        PackageManager packageManager = context.getPackageManager();
        List list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
}
