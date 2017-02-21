/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.crestv.cp30;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.crestv.cp30.util.L;
import com.crestv.cp30.util.TRequestQueue;
import com.crestv.cp30.view.CircleProgressBarView;
import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.download.DownloadListener;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/*
 * MainActivity class that loads MainFragment
 */
public class MainActivity extends Activity implements SurfaceHolder.Callback, View.OnClickListener {

    private Button btn;
    private RelativeLayout rl;
    private SeekBar seekBar;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    private MediaPlayer mediaPlayer;//播放视频的对象

    private int w, h;

    public static boolean isForeground = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
            }
        }
    };
    private CircleProgressBarView circleProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取屏幕宽和高
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        h = displayMetrics.heightPixels;
        w = displayMetrics.widthPixels;
        Log.e("======", "===" + h);
        initId();
        //registerMessageReceiver();
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        initListener();
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
            if (AndPermission.hasAlwaysDeniedPermission(MainActivity.this, deniedPermissions)) {
                // 第一种：用默认的提示语。
                AndPermission.defaultSettingDialog(MainActivity.this, requestCode).show();

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
            playVideo(0,filePath);
        }

        @Override
        public void onCancel(int what) {
            circleProgressBar.setVisibility(View.GONE);
        }
    };

    private void initListener() {
        //seekBar监听
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {//用户手指拖到
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mediaPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.start();
            }
        });


        //控制视频  开始暂停
        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
               /* if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();//暂停
                } else if (mediaPlayer != null) {
                    mediaPlayer.start();//开始
                }*/
                return false;
            }
        });
    }

    private void initId() {
        btn = ((Button) findViewById(R.id.btn));
        btn.setOnClickListener(this);
        rl = ((RelativeLayout) findViewById(R.id.rl));
        seekBar = ((SeekBar) findViewById(R.id.seekBar));

        surfaceView = ((SurfaceView) findViewById(R.id.surfaceView));
        circleProgressBar = ((CircleProgressBarView) findViewById(R.id.circleProgressBar));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TRequestQueue.cancelAllQueue();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //初始化播放视频对象
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        //将视频展示到 SurfaceView上
        mediaPlayer.setDisplay(holder);


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


    //播放视频方法
    private void playVideo(int pos,String url) {
        //mediaPlayer 清空下
        mediaPlayer.reset();
        //得到视频的URi地址
        //Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/" + R.raw.heart);
        Uri uri = Uri.parse(url);
        try {
            //设置视频播放地址
            mediaPlayer.setDataSource(MainActivity.this, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            //准备播放
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //开始播放
        mediaPlayer.start();
        //从哪个位置开始播放
        mediaPlayer.seekTo(pos);
        //获取视频总时长  给seekBar设置最大值
        seekBar.setMax(mediaPlayer.getDuration());
        //让seekBar随着视频动
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(0);
                }
            }
        }).start();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.e("AAA", "===onConfigurationChanged==");
    }
    //控制全屏播放
    //android:configChanges="orientation|screenSize"
    @Override
    public void onClick(View v) {
    //横竖屏切换
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {//横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            surfaceView.setLayoutParams(new RelativeLayout.LayoutParams(
                    h,
                    h * mediaPlayer.getVideoWidth() / mediaPlayer.getVideoHeight()
            ));
            rl.setLayoutParams(new RelativeLayout.LayoutParams(
                    h,
                    h * mediaPlayer.getVideoWidth() / mediaPlayer.getVideoHeight()
            ));

        } else if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {//竖屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            surfaceView.setLayoutParams(new RelativeLayout.LayoutParams(
                    w,
                    w * mediaPlayer.getVideoHeight() / mediaPlayer.getVideoWidth()
            ));
            rl.setLayoutParams(new RelativeLayout.LayoutParams(
                    w,
                    w * mediaPlayer.getVideoHeight() / mediaPlayer.getVideoWidth()
            ));
        }
    }
    //private MessageReceiver mMessageReceiver;
   /* public static final String MESSAGE_RECEIVED_ACTION = "com.crestv.cp30.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + messge + "\n");

                setCostomMsg(showMsg.toString());
            }
        }
    }
    private void setCostomMsg(String msg){

    }*/


}
