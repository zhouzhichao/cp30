package com.crestv.cp30.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.crestv.cp30.util.TRequestQueue;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;

/**
 * Created by Administrator on 2017/2/20 0020.
 */

public class BaseActivity extends Activity implements View.OnClickListener{

    /**
     * 请求队列
     */
    //private RequestQueue mRequestQueue;
    private TRequestQueue single;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 创建请求队列, 默认并发3个请求, 传入数字改变并发数量: NoHttp.newRequestQueue(5);
        //mRequestQueue = NoHttp.newRequestQueue();
        single=TRequestQueue.getInstance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

       /* mRequestQueue.cancelAll(); // 退出页面时时取消所有请求。
        mRequestQueue.stop(); // 退出时销毁队列，回收资源。*/
        single.cancelAllQueue();
    }
    protected TRequestQueue getSingle(){
        return single;
    }

    /**
     * 发起一个请求。
     *
     * @param what     what.
     * @param request  请求对象。
     * @param listener 结果监听。
     * @param <T>      要请求到的数据类型。
     */
    public <T> void request(int what, Request<T> request, OnResponseListener<T> listener) {
        //mRequestQueue.add(what, request, listener);
    }

    @Override
    public void onClick(View v) {

    }


}
