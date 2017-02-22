package com.crestv.cp30.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.crestv.cp30.Manifest;
import com.crestv.cp30.R;
import com.crestv.cp30.util.L;
import com.crestv.cp30.util.TRequestQueue;
import com.crestv.cp30.view.CircleProgressBarView;
import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.download.DownloadListener;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import org.json.JSONObject;

import java.util.List;

public class DownLoadActivity extends Activity {

    private CircleProgressBarView circleProgressBar;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_load);
        initId();
        initDownLoad();
    }

    private void initDownLoad() {
        // 先判断是否有权限。
        if (AndPermission.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // 有权限，直接do anything.
            TRequestQueue.getDownLoadInstance().noHttpDownLoadFile(0x103, "http://file.86580.cn:9001/syptapp/cers/LionHeart.mp4", new JSONObject(), downloadListener);
        } else {
            // 申请权限。
            AndPermission.with(this)
                    .requestCode(100)
                    .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                    .send();

        }
    }

    private void initId() {
        circleProgressBar = ((CircleProgressBarView) findViewById(R.id.circleProgressBar));
        tv = ((TextView) findViewById(R.id.tv));
    }

    //下载视频监听
    private DownloadListener downloadListener = new DownloadListener() {
        @Override
        public void onDownloadError(int what, Exception exception) {
            L.e("==exception==", "==" + exception);
        }

        @Override
        public void onStart(int what, boolean isResume, long rangeSize, Headers responseHeaders, long allCount) {
            L.e("==rangeSize==", "==" + rangeSize);
            L.e("==allCount==", "==" + allCount);
            circleProgressBar.setVisibility(View.VISIBLE);
            circleProgressBar.setMax(100);
        }

        @Override
        public void onProgress(int what, int progress, long fileCount, long speed) {
            circleProgressBar.setProgress(progress);
        }

        @Override
        public void onFinish(int what, String filePath) {
            L.e("==filePath==", "==" + filePath);
            circleProgressBar.setVisibility(View.GONE);
            //开始播放视频
            Intent intent =new Intent(DownLoadActivity.this,PlayVideoActivity.class);
            intent.putExtra("filePath",filePath);
            startActivity(intent);
            finish();
        }

        @Override
        public void onCancel(int what) {
            circleProgressBar.setVisibility(View.GONE);
        }
    };
    //申请权限监听
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 只需要调用这一句，其它的交给AndPermission吧，最后一个参数是PermissionListener。
        AndPermission.onRequestPermissionsResult(requestCode, permissions, grantResults, permissionListener);
    }

    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantPermissions) {
            //权限申请成功，执行下载
            // 权限申请成功回调。
            if (requestCode == 100) {
                // TODO 相应代码。
                TRequestQueue.getDownLoadInstance().noHttpDownLoadFile(0x103, "http://file.86580.cn:9001/syptapp/cers/LionHeart.mp4", new JSONObject(), downloadListener);

            } else if (requestCode == 101) {
                // TODO 相应代码。
            }
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // 权限申请失败回调。

            // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
            if (AndPermission.hasAlwaysDeniedPermission(DownLoadActivity.this, deniedPermissions)) {
                // 第一种：用默认的提示语。
                AndPermission.defaultSettingDialog(DownLoadActivity.this, requestCode).show();

                // 第二种：用自定义的提示语。
                // AndPermission.defaultSettingDialog(this, REQUEST_CODE_SETTING)
                // .setTitle("权限申请失败")
                // .setMessage("我们需要的一些权限被您拒绝或者系统发生错误申请失败，请您到设置页面手动授权，否则功能无法正常使用！")
                // .setPositiveButton("好，去设置")
                // .show();

                // 第三种：自定义dialog样式。
                // SettingService settingService =
                //    AndPermission.defineSettingDialog(this, REQUEST_CODE_SETTING);
                // 你的dialog点击了确定调用：
                // settingService.execute();
                // 你的dialog点击了取消调用：
                // settingService.cancel();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TRequestQueue.cancelAllQueue();
    }
}
