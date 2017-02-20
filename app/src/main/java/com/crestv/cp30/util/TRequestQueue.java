package com.crestv.cp30.util;


import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.RequestQueue;

/**
 *
 * 使用方法:TRequestQueue.getInstance().addStrResp(what,"mainpage/init",params,resp);
 */
public class TRequestQueue {
    public static final int QUEUENUM = 8;
    private RequestQueue mRequestQueue;

    private TRequestQueue() {
    }

    private static final TRequestQueue single = new TRequestQueue();

    public  static  void cancelQueue (Object sign)
    {
        single.mRequestQueue.cancelBySign(sign);
    }
    public  static  void cancelAllQueue ()
    {
        single.mRequestQueue.cancelAll();
    }

    //静态工厂方法
    public static TRequestQueue getInstance() {
        if (single.mRequestQueue==null)
            single.mRequestQueue = NoHttp.newRequestQueue(QUEUENUM);
        return single;
    }




}
