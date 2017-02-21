package com.crestv.cp30.application;

import android.app.Application;

import com.yanzhenjie.nohttp.NoHttp;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2017/2/20 0020.
 */

public class MyApplication extends Application {
    private static MyApplication _instance;

    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        //以下是nohttp配置
        NoHttp.initialize(this);
    }

    public static MyApplication getInstance() {
        return _instance;
    }
}
