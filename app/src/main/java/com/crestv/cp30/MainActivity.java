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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.crestv.cp30.Enity.PlayRecordEnity;
import com.crestv.cp30.Enity.PrizeInformationEnity;
import com.crestv.cp30.Enity.Version;
import com.crestv.cp30.activity.MyReceiver;
import com.crestv.cp30.adapter.PrizeInformationAdapter;
import com.crestv.cp30.dao.Config;
import com.crestv.cp30.util.AppUtil;
import com.crestv.cp30.util.DatabaseUtil;
import com.crestv.cp30.util.DialogLoadingUtil;
import com.crestv.cp30.util.FileUtil;
import com.crestv.cp30.util.GetAppId;
import com.crestv.cp30.util.GetFilesUtil;
import com.crestv.cp30.util.L;
import com.crestv.cp30.util.TRequestQueue;
import com.crestv.cp30.util.UpdateAppUtil;
import com.crestv.cp30.view.CircleProgressBarView;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.download.DownloadListener;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.zhy.autolayout.AutoLayoutActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
 * MainActivity class that loads MainFragment
 */
public class MainActivity extends AutoLayoutActivity implements  View.OnClickListener{
    private Dialog dialog;
    private boolean isMainActvity;
    private CircleProgressBarView circleProgressBar;
    private TextView tv;
    private RelativeLayout rlDownLoad;
    private List<String> filePathList;
    private int num;
    private int errorCount;
    private RelativeLayout rlSurfaceView;

    private String filePath;
    private int w, h;
    private RelativeLayout rlMain;
    private TextView tvCurrentTime;

    private int newVerCode = 0;
    private String newVerName = "";
    private Gson gson=new Gson();
    private PrizeInformationEnity.DataBean dataBean;
    private List<PrizeInformationEnity.DataBean.ListHeroBean> listHeroBeanList;
    private PrizeInformationAdapter prizeInformationAdapter;

    private String startPlayTime;
    private String endPlayTime;
    private String uploadState;

    private List<PlayRecordEnity> playRecordEnityList;
    private int recordNum;

    private SharedPreferences mSharedPreferences;
    private AlertDialog.Builder mDialog;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    long sysTime = System.currentTimeMillis();
                    CharSequence sysTimeStr = DateFormat.format("hh:mm:ss", sysTime);
                    tvCurrentTime.setText(sysTimeStr);
                    break;

                default:
                    break;
            }
        }
    };
    private TextView tvCountTimer;
    private ListView listViewPrize;

    private  DatabaseUtil dbUtil;
    private VideoView videoView;

    @Override
    protected void onStart() {
        super.onStart();
        // 先判断是否有权限。
        if (AndPermission.hasPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

        } else {
            // 申请权限。
            AndPermission.with(this)
                    .requestCode(100)
                    .permission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    .send();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide the status bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //hide the title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initId();
        initListener();
        checkAppVersion();
        initPrizeInformation();
        //插入
        dbUtil = new DatabaseUtil(this);
        dbUtil.open();

    }

    private void initPrizeInformation() {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("page",1);
            jsonObject.put("perpage", 10);
            jsonObject.put("pk", "string");
            jsonObject.put("token","5Wv3iYGKDshXrKNHlrCZEg==");
            jsonObject.put("ts", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TRequestQueue.getInstance().addStrResp(0x104, "shopcertificate/HeroRankingList",jsonObject, mOnResponseListener);
    }

    private void checkAppVersion() {
        TRequestQueue.getInstance().addStrRespGet(0x101, Config.UPDATE_VERJSON, mOnResponseListener);
    }
    OnResponseListener<String> mOnResponseListener=new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            if (what == 0x104) {
                dialog = DialogLoadingUtil.beginLoading(MainActivity.this);
                dialog.setCancelable(false);
                dialog.show();
            }
        }
        @Override
        public void onSucceed(int what, Response<String> response) {
            if (what==0x100){
                try {
                    String result = response.get();
                    result=result.substring(0,result.lastIndexOf("}")+1);
                    L.e(result);
                    Gson gson = new Gson();
                    Version model = gson.fromJson(result, Version.class);
                    int status = model.getStatus();
                    if (status == 1) {
                        newVerCode = model.getData().get(0).getVerCode();
                        newVerName = model.getData().get(0).getVerName();
                        filePathList=new ArrayList<>();
                        filePathList.add("http://file.86580.cn:9001/syptapp/cers/LionHeart02.mp4");
                        filePathList.add("http://file.86580.cn:9001/syptapp/cers/LionHeart04.mp4");
                        if (newVerCode > AppUtil.getVersionCode(getApplicationContext())) {
                            downLoadVideo(filePathList);//下载更新视频
                        }
                    } else {
                        newVerCode = -1;
                        newVerName = "";
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            } else
            if (what==0x101){
                try {
                    String result = response.get();
                    result=result.substring(0,result.lastIndexOf("}")+1);
                    L.e(result);
                    Gson gson = new Gson();
                    Version model = gson.fromJson(result, Version.class);
                    int status = model.getStatus();
                    if (status == 1) {
                        newVerCode = model.getData().get(0).getVerCode();
                        newVerName = model.getData().get(0).getVerName();

                        if (newVerCode > AppUtil.getVersionCode(getApplicationContext())) {
                            doNewVersionUpdate();
                        }else {
                            checkVersion();
                        }
                    } else {
                        newVerCode = -1;
                        newVerName = "";
                        checkVersion();
                    }
                } catch (Exception ex) {
                    newVerCode = -1;
                    newVerName = "";
                    ex.printStackTrace();
                    checkVersion();
                }
            }else
            if (what==0x104){
                String result=response.get();
                L.e("==reslt","=="+result);
                try {
                    PrizeInformationEnity prizeInformationEnity=gson.fromJson(result,PrizeInformationEnity.class);
                    listHeroBeanList.clear();
                    listHeroBeanList.addAll(prizeInformationEnity.getData().getListHero());
                    prizeInformationAdapter.notifyDataSetChanged();
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }

            }else
                if (what==0x105){
                    //上传播放记录，上传成功后删除数据库中的记录
                    if (recordNum<playRecordEnityList.size()){
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("filePath", playRecordEnityList.get(recordNum).getFileName());
                            jsonObject.put("startPlayTime", playRecordEnityList.get(recordNum).getStartPlayTime());
                            jsonObject.put("endPlayTime", playRecordEnityList.get(recordNum).getEndPlayTime());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String _id=playRecordEnityList.get(recordNum-1).getId();
                        dbUtil.deleteSingleRecord(Long.valueOf(_id));
                        recordNum++;
                        TRequestQueue.getInstance().addStrResp(0x105, "baidu", jsonObject, mOnResponseListener);
                    }
                }
        }

        @Override
        public void onFailed(int what, Response<String> response) {
            if (what==0x105){
                //上传播放记录失败，不删除记录
                if (recordNum<playRecordEnityList.size()){
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("filePath", playRecordEnityList.get(recordNum).getFileName());
                        jsonObject.put("startPlayTime", playRecordEnityList.get(recordNum).getStartPlayTime());
                        jsonObject.put("endPlayTime", playRecordEnityList.get(recordNum).getEndPlayTime());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    L.e("==fi","=="+playRecordEnityList.get(recordNum).getFileName());
                    L.e("==endPlayTime","=="+playRecordEnityList.get(recordNum).getEndPlayTime());
                    recordNum++;
                    TRequestQueue.getInstance().addStrResp(0x105, "baidu", jsonObject, mOnResponseListener);
                }

            }
            L.e("==","=="+what);
        }

        @Override
        public void onFinish(int what) {
            if (Integer.valueOf(what) != null) {
                if (what == 0x104) {
                    dialog.dismiss();
                }
            }
        }
    };
    //当前版本检测
    private void doNewVersionUpdate() {
        int verCode = AppUtil.getVersionCode(this);
        String verName = AppUtil.getVersionName(this);


        StringBuffer sb = new StringBuffer();
        sb.append("当前版本:");
        sb.append(verName);
        sb.append(" Code:");
        sb.append(verCode);
        sb.append(", 发现新版本:");
        sb.append(newVerName);
        sb.append(" Code:");
        sb.append(newVerCode);
        sb.append(", 是否更新?");
        UpdateAppUtil.normalUpdate(mDialog,this,"九盛中彩游戏App", Config.APK_DOWNLOAD_URL, sb.toString());

    }
    //检测视频版本
    private void checkVersion() {
        int gameVersion=mSharedPreferences.getInt("gameVersion",0);
        int adVersion=mSharedPreferences.getInt("adVersion",0);
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("gameVerCode",gameVersion);
            jsonObject.put("adVerCode",adVersion);
            jsonObject.put("mac", GetAppId.getDeviceId(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TRequestQueue.getInstance().addStrRespGet(0x100, Config.UPDATE_VERJSON, mOnResponseListener);

    }
    //下载开奖结果视频
    private void downLoadVideo(String filePath) {
        //0:下载的游戏介绍视频;1:开奖视频;2:广告视频;
        TRequestQueue.getDownLoadInstance().noHttpDownLoadFile(0x106,filePath , new JSONObject(), downloadListener,1);
    }
    //下载的更新视频
    private void downLoadVideo(List<String> filePathList) {
         /*Intent intent1=new Intent(this, DownLoadActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);*/
        rlMain.setVisibility(View.GONE);
        rlDownLoad.setVisibility(View.VISIBLE);
        circleProgressBar.setProgress(0);
        isMainActvity=false;
        num=0;
        errorCount=0;
        // 先判断是否有权限。
        if (AndPermission.hasPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // 有权限，直接do anything.
            TRequestQueue.getDownLoadInstance().noHttpDownLoadFile(0x103,filePathList.get(num) , new JSONObject(), downloadListener,0);
        } else {
            // 申请权限。
            AndPermission.with(this)
                    .requestCode(100)
                    .permission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    .send();

        }
    }


    private void initId() {
        videoView = ((VideoView) findViewById(R.id.videoView));
        mDialog = new AlertDialog.Builder(this);
        listViewPrize = ((ListView) findViewById(R.id.listViewPrize));
        mSharedPreferences = getSharedPreferences("videoVersionInfo", MODE_PRIVATE);
        tvCurrentTime = ((TextView) findViewById(R.id.tvCurrentTime));
        tvCountTimer = ((TextView) findViewById(R.id.tvCountTimer));
        rlDownLoad = ((RelativeLayout) findViewById(R.id.activity_down_load));
        rlMain = ((RelativeLayout) findViewById(R.id.rlMain));
        circleProgressBar = ((CircleProgressBarView) findViewById(R.id.circleProgressBar));
        tv = ((TextView) findViewById(R.id.tv));
        rlSurfaceView = ((RelativeLayout) findViewById(R.id.rlSurfaceView));
        //获取屏幕宽和高
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        h = displayMetrics.heightPixels;
        w = displayMetrics.widthPixels;
        Log.e("======", "===" + h);
        //registerMessageReceiver();

        new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    try {
                        Thread.sleep(1000);
                        Message msg = new Message();
                        msg.what = 0;
                        mHandler.sendMessage(msg);
                        }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                        }
                    } while(true);
            }
        }).start();

        listHeroBeanList=new ArrayList<>();
        prizeInformationAdapter=new PrizeInformationAdapter(this,listHeroBeanList,R.layout.prize_information_item);
        listViewPrize.setAdapter(prizeInformationAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isMainActvity=true;
    }

    private void initListener() {
        //接口回调，获得极光推送自定义推送内容
        MyReceiver.setNotifyListener(new MyReceiver.NotifyListener() {
            @Override
            public void notifyListener(String filePath) {
                if (isMainActvity==true) {
                   /* Intent intent = new Intent(MainActivity.this, PlayVideoActivity.class);
                    intent.putExtra("filePath", filePath);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);*/
                    final String firstChar=filePath.substring(0,1);
                    final String otherString=filePath.substring(1,filePath.length());
                    L.e("==firstChar","=="+firstChar);
                    L.e("==otherString","=="+otherString);
                    if (firstChar.equals("1")){
                        new CountDownTimer(30*1000,1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                tvCountTimer.setVisibility(View.VISIBLE);
                                tvCountTimer.setText(String.valueOf(millisUntilFinished/1000));
                                isMainActvity=false;
                                downLoadVideo(otherString);
                            }

                            @Override
                            public void onFinish() {
                                tvCountTimer.setVisibility(View.GONE);
                                isMainActvity=true;
                                playVideo(0,otherString.substring(otherString.lastIndexOf("/")+1),Integer.valueOf(firstChar));
                            }
                        }.start();
                    }else if (firstChar.equals("4")){
                        List<String> listFiles=new ArrayList<String>();
                        listFiles.add("LionHeart02.mp4");
                        listFiles.add("LionHeart04.mp4");
                        File []filenames= GetFilesUtil.getFiles(listFiles);
                        for (int i = 0; i < filenames.length; i++) {//删除
                            //File file = new File (filenames[i]);
                            if (filenames[i].exists()) {
                                filenames[i].delete();
                            }
                        }
                    }else if (firstChar.equals("0")){
                        checkVersion();
                    }else {
                        String secondChar=filePath.substring(1,2);
                        String lastString=filePath.substring(2,filePath.length());
                        //rlSurfaceView.setVisibility(View.VISIBLE);
                        try {
                            int key=Integer.valueOf(secondChar);
                            playVideo(0, lastString,Integer.valueOf(key));

                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        });

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                checkVersion();//视频更新
            }
        });

        //视频准备播放时的监听
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                startPlayTime=String.valueOf(System.currentTimeMillis());
                //what为3时表示准备就绪，这时再将原来的VideoView隐藏掉就不会出现黑屏
                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what==3){
                            rlMain.setVisibility(View.GONE);
                            isMainActvity = false;
                        }
                        return false;
                    }
                });
            }
        });
        //视频播放完成的监听
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                rlMain.setVisibility(View.VISIBLE);
                isMainActvity=true;

                endPlayTime=String.valueOf(System.currentTimeMillis());
                dbUtil.createRecord(filePath, startPlayTime,endPlayTime,0);
                playRecordEnityList=new ArrayList<PlayRecordEnity>();
                //查询
                Cursor cursor = dbUtil.fetchAllRecord();
                recordNum=0;
                if(cursor != null){
                    while(cursor.moveToNext()){
                        Log.e("Student==", "==Name: " + cursor.getString(1) +
                                " start== " + cursor.getString(2)+"id=="+cursor.getString(0));
                        PlayRecordEnity playRecordEnity=new PlayRecordEnity();
                        playRecordEnity.setId(cursor.getString(0));
                        playRecordEnity.setFileName(cursor.getString(1));
                        playRecordEnity.setStartPlayTime(cursor.getString(2));
                        playRecordEnity.setEndPlayTime(cursor.getString(3));
                        playRecordEnityList.add(playRecordEnity);
                    }
                }
                if (playRecordEnityList.size()>0) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("filePath", playRecordEnityList.get(recordNum).getFileName());
                        jsonObject.put("startPlayTime", playRecordEnityList.get(recordNum).getStartPlayTime());
                        jsonObject.put("endPlayTime", playRecordEnityList.get(recordNum).getEndPlayTime());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    recordNum++;
                    TRequestQueue.getInstance().addStrResp(0x105, "baidu", jsonObject, mOnResponseListener);
                }
            }
        });
    }

    //下载视频监听 0x103:游戏介绍视频；0x106:开奖结果视频
    private DownloadListener downloadListener = new DownloadListener() {
        @Override
        public void onDownloadError(int what, Exception exception) {
            L.e("==exception==", "==" + exception);
            if (what==0x103) {
                if (exception.toString().contains("The network is not available")) {

                } else {
                    errorCount++;
                    if (errorCount > 60) {

                    } else {
                        TRequestQueue.getDownLoadInstance().noHttpDownLoadFile(0x103, filePathList.get(num), new JSONObject(), downloadListener, 0);
                    }
                }
            }else if (what==0x106){

            }
        }

        @Override
        public void onStart(int what, boolean isResume, long rangeSize, Headers responseHeaders, long allCount) {
            L.e("==rangeSize==", "==" + rangeSize);
            L.e("==allCount==", "==" + allCount);
            if (what==0x103) {
                tv.setText("正在更新第" + (num + 1) + "个视频，共" + filePathList.size() + "个");
                circleProgressBar.setVisibility(View.VISIBLE);
                circleProgressBar.setMax(100);
            }else if (what==0x106){

            }
        }

        @Override
        public void onProgress(int what, int progress, long fileCount, long speed) {
            if (what==0x103) {
                circleProgressBar.setProgress(progress);
            }else if (what==0x106){

            }
        }

        @Override
        public void onFinish(int what, String filePath) {
            L.e("==filePath==", "==" + filePath);
            //circleProgressBar.setVisibility(View.GONE);
            if (what==0x103) {
                num++;
                if (num < filePathList.size()) {
                    TRequestQueue.getDownLoadInstance().noHttpDownLoadFile(0x103, filePathList.get(num), new JSONObject(), downloadListener, 0);
                } else {
                    rlDownLoad.setVisibility(View.GONE);
                    rlMain.setVisibility(View.VISIBLE);
                    isMainActvity = true;
                    initPrizeInformation();
                }
            }else if (what==0x106){

            }

        }

        @Override
        public void onCancel(int what) {

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
                //TRequestQueue.getDownLoadInstance().noHttpDownLoadFile(0x103, filePathList.get(num), new JSONObject(), downloadListener);

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
                try {
                    Thread.sleep(3000);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else {
                finish();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TRequestQueue.cancelAllQueue();
        isMainActvity=false;
        dbUtil.close();
    }

    //播放视频方法
    private void playVideo(int pos,String url,int key) {
        String filePathName;
        if (key==0){
            filePathName="JiuSheng/Game";
        }else if (key==1){
            filePathName="JiuSheng/Prize";
        }else if (key==2){
            filePathName="JiuSheng/Ad";
        }else {
            filePathName="JiuSheng/Down";
        }
        String path= FileUtil.getSdcardFileDir(filePathName).getAbsolutePath();
        filePath=url;
        url=path+"/"+url;
        L.e("==path",url);
        Uri uri = Uri.parse(url);
        //设置视频播放地址
        videoView.setVideoURI(uri);
        videoView.start();
    }
    //控制全屏播放
    //android:configChanges="orientation|screenSize"
    @Override
    public void onClick(View v) {

    }

}
