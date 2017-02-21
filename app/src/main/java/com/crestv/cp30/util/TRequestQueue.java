package com.crestv.cp30.util;


import android.util.Log;

import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.download.DownloadListener;
import com.yanzhenjie.nohttp.download.DownloadQueue;
import com.yanzhenjie.nohttp.download.DownloadRequest;
import com.yanzhenjie.nohttp.rest.RequestQueue;

import org.json.JSONObject;

/**
 *
 * 使用方法:TRequestQueue.getInstance().addStrResp(what,"mainpage/init",params,resp);
 */
public class TRequestQueue {
    public static final int QUEUENUM = 8;
    private RequestQueue mRequestQueue;
    private DownloadQueue downloadQueue;

    private TRequestQueue() {
    }

    private static final TRequestQueue single = new TRequestQueue();

    public  static  void cancelQueue (Object sign)
    {
        single.mRequestQueue.cancelBySign(sign);
    }
    public  static  void cancelAllQueue ()
    {
        if (single.mRequestQueue!=null){
            single.mRequestQueue.cancelAll();
        }else if (single.downloadQueue!=null){
            single.downloadQueue.cancelAll();
        }
    }

    //静态工厂方法
    public static TRequestQueue getInstance() {
        if (single.mRequestQueue==null)
            single.mRequestQueue = NoHttp.newRequestQueue(QUEUENUM);
        return single;
    }
    public static TRequestQueue getDownLoadInstance() {
        if (single.downloadQueue==null)
            single.downloadQueue = NoHttp.newDownloadQueue(3);
        return single;
    }

    //Nohttp文件下载
    public <T> void noHttpDownLoadFile(int what, final String url, JSONObject object, DownloadListener downloadListener){
        /**
         * 创建请求对象
         */
        String fileName=url.substring(url.lastIndexOf("/")+1);
        Log.e("fileName==","fileName=="+fileName);
        String filePath= FileUtil.getSdcardFileDir("JiuSheng/Game").getAbsolutePath();
        DownloadRequest downLoadRequest = NoHttp.createDownloadRequest(url,filePath,fileName,true, false);
        /*object.put("ts", ts);
        object.put("pk", pk);*/
        downloadQueue.add(what, downLoadRequest, downloadListener);
    }




}
