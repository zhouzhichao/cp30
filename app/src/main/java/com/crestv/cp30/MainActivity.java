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

import android.content.ContentResolver;
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

import com.crestv.cp30.activity.BaseActivity;

import java.io.IOException;

/*
 * MainActivity class that loads MainFragment
 */
public class MainActivity extends BaseActivity implements SurfaceHolder.Callback, View.OnClickListener {

    private Button btn;
    private RelativeLayout rl;
    private SeekBar seekBar;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    private MediaPlayer mediaPlayer;//播放视频的对象

    private int w, h;

    public static boolean isForeground = false;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
            }
            MainActivity.super.getSingle();//调用方法
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//获取屏幕宽和高
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        h = displayMetrics.heightPixels;
        w = displayMetrics.widthPixels;
        Log.e("======","==="+h);
        initId();
        //registerMessageReceiver();
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){//用户手指拖到
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
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();//暂停
                } else if (mediaPlayer != null) {
                    mediaPlayer.start();//开始
                }

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
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
//初始化播放视频对象
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
//将视频展示到 SurfaceView上
        mediaPlayer.setDisplay(holder);

//开始播放视频
        playVideo(0);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


    //播放视频方法
    private void playVideo(int pos) {

//mediaPlayer 清空下
        mediaPlayer.reset();

//得到视频的URi地址
        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/" + R.raw.heart);

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


                while (true){
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
                    h*mediaPlayer.getVideoWidth()/mediaPlayer.getVideoHeight()
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
