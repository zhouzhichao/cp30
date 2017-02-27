package com.crestv.cp30.util;


import android.util.Log;

import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.download.DownloadListener;
import com.yanzhenjie.nohttp.download.DownloadQueue;
import com.yanzhenjie.nohttp.download.DownloadRequest;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;

import org.json.JSONObject;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

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

    public <T> void addStrResp(int what, String url, JSONObject object, OnResponseListener<String> responseListener) {
        Request<String> request = NoHttp.createStringRequest(UrlR.API_URL + url, RequestMethod.POST);

        if (UrlR.API_URL.contains("https")) {
            SSLContext sslContext = SSLContextUtil.getDefaultSLLContext();// 方法见上文
            if (sslContext != null) {
                SSLSocketFactory socketFactory = sslContext.getSocketFactory();
                request.setSSLSocketFactory(socketFactory);
                request.setHostnameVerifier(SSLContextUtil.HOSTNAME_VERIFIER);// 见上文
            }
        }

        long ts = System.currentTimeMillis();
        //String pk = AES.Encrypt(String.valueOf(ts));
        try {
            object.put("ts", ts);
            //object.put("pk", pk);
            //object.put("token", MyApplication.getToken());
            L.e(object.toString() + "  " + url);
            request.setDefineRequestBodyForJson(object);
            mRequestQueue.add(what, request, responseListener);
        } catch (Exception ex) {
            L.e(ex);
        }
    }

    public <T> void addStrRespWithOutToken(int what, String url, JSONObject object, OnResponseListener<String> responseListener) {
        Request<String> request = NoHttp.createStringRequest(UrlR.API_URL + url, RequestMethod.POST);
        if (UrlR.API_URL.contains("https")) {
            SSLContext sslContext = SSLContextUtil.getDefaultSLLContext();// 方法见上文
            if (sslContext != null) {
                SSLSocketFactory socketFactory = sslContext.getSocketFactory();
                request.setSSLSocketFactory(socketFactory);
                request.setHostnameVerifier(SSLContextUtil.HOSTNAME_VERIFIER);// 见上文
            }
        }
        long ts = System.currentTimeMillis();
        //String pk = AES.Encrypt(String.valueOf(ts));
        try {
            object.put("ts", ts);
            //object.put("pk", pk);
            L.e(object.toString() + "  " + url);
            request.setDefineRequestBodyForJson(object);
            mRequestQueue.add(what, request, responseListener);
        } catch (Exception ex) {
            L.e(ex);
        }
    }

    public <T> void addStrResp(int what, String url, OnResponseListener<String> responseListener) {
        Request<String> request = NoHttp.createStringRequest(UrlR.API_URL + url, RequestMethod.POST);
        if (UrlR.API_URL.contains("https")) {
            SSLContext sslContext = SSLContextUtil.getDefaultSLLContext();// 方法见上文
            if (sslContext != null) {
                SSLSocketFactory socketFactory = sslContext.getSocketFactory();
                request.setSSLSocketFactory(socketFactory);
                request.setHostnameVerifier(SSLContextUtil.HOSTNAME_VERIFIER);// 见上文
            }
        }
        L.e(url);
        try {

//            request.setDefineRequestBodyForJson(object);
            mRequestQueue.add(what, request, responseListener);
        } catch (Exception ex) {
            L.e(ex);
        }
    }

    public <T> void addStrRespGet(int what, String url, OnResponseListener<String> responseListener) {
        Request<String> request = NoHttp.createStringRequest(UrlR.API_URL + url + "?" + System.currentTimeMillis(), RequestMethod.GET);
        if (UrlR.API_URL.contains("https")) {
            SSLContext sslContext = SSLContextUtil.getDefaultSLLContext();// 方法见上文
            if (sslContext != null) {
                SSLSocketFactory socketFactory = sslContext.getSocketFactory();
                request.setSSLSocketFactory(socketFactory);
                request.setHostnameVerifier(SSLContextUtil.HOSTNAME_VERIFIER);// 见上文
            }
        }
        L.e(url);
        try {

//            request.setDefineRequestBodyForJson(object);
            mRequestQueue.add(what, request, responseListener);
        } catch (Exception ex) {
            L.e(ex);
        }
    }


}
