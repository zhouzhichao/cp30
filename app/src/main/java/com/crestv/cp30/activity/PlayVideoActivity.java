package com.crestv.cp30.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.crestv.cp30.MainActivity;
import com.crestv.cp30.R;
import com.crestv.cp30.util.TRequestQueue;

import java.io.IOException;



public class PlayVideoActivity extends Activity implements SurfaceHolder.Callback, View.OnClickListener{
    private Button btn;
    private RelativeLayout rl;
    private SeekBar seekBar;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    private MediaPlayer mediaPlayer;//播放视频的对象
    private String filePath;
    private int w, h;
    private Thread threadSeekbar;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
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
        Intent intent=getIntent();
        filePath=intent.getStringExtra("filePath");


    }

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

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                ActivityOptionsCompat compat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(PlayVideoActivity.this,
                                surfaceView, getResources().getString(R.string.transition_movie_img));
                Intent intent=new Intent(PlayVideoActivity.this, MainActivity.class);
                intent.putExtra("versionCode",1);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                ActivityCompat.startActivity(PlayVideoActivity.this, intent, compat.toBundle());

            }
        });
    }

    private void initId() {
        btn = ((Button) findViewById(R.id.btn));
        btn.setOnClickListener(this);
        rl = ((RelativeLayout) findViewById(R.id.rlSurfaceView));
        seekBar = ((SeekBar) findViewById(R.id.seekBar));

        surfaceView = ((SurfaceView) findViewById(R.id.surfaceView));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TRequestQueue.cancelAllQueue();
        mediaPlayer.release();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //初始化播放视频对象
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        //将视频展示到 SurfaceView上
        mediaPlayer.setDisplay(holder);
        initListener();
        playVideo(0,filePath);
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
            mediaPlayer.setDataSource(PlayVideoActivity.this, uri);
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
        /*threadSeekbar=new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(0);
                }
            }
        });
        threadSeekbar.start();*/

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
}
