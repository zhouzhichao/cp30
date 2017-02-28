package com.crestv.cp30.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;
import static android.text.TextUtils.isEmpty;

/**
 * Created by Administrator on 2017/2/28 0028.
 */

public class GetAppId {
    /**
     * deviceID的组成为：渠道标志+识别符来源标志+hash后的终端识别符
     *
     * 渠道标志为：
     * 1，andriod（a）
     *
     * 识别符来源标志：
     * 1， wifi mac地址（wifi）；
     * 2， IMEI（imei）；
     * 3， 序列号（sn）；
     * 4， id：随机码。若前面的都取不到时，则随机生成一个随机码，需要缓存。
     *
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {


        StringBuilder deviceId = new StringBuilder();
// 渠道标志
        deviceId.append("a");

        try {

//wifi mac地址
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            String wifiMac = info.getMacAddress();
            if(!isEmpty(wifiMac)){
                deviceId.append("wifi");
                deviceId.append(wifiMac);
                L.e("getDeviceId : ", deviceId.toString());
                return deviceId.toString();
            }

//IMEI（imei）
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();
            if(!isEmpty(imei)){
                deviceId.append("imei");
                deviceId.append(imei);
                L.e("getDeviceId : ", deviceId.toString());
                return deviceId.toString();
            }

//序列号（sn）
            String sn = tm.getSimSerialNumber();
            if(!isEmpty(sn)){
                deviceId.append("sn");
                deviceId.append(sn);
                L.e("getDeviceId : ", deviceId.toString());
                return deviceId.toString();
            }

//如果上面都没有， 则生成一个id：随机码
            String uuid = getUUID(context);
            if(!isEmpty(uuid)){
                deviceId.append("id");
                deviceId.append(uuid);
                L.e("getDeviceId : ", deviceId.toString());
                return deviceId.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            deviceId.append("id").append(getUUID(context));
        }

        L.e("getDeviceId : ", deviceId.toString());

        return deviceId.toString();
    }


    /**
     * 得到全局唯一UUID
     */
    public static String getUUID(Context context){
        String uuid=null;
        SharedPreferences mShare = context.getSharedPreferences("sysCacheMap",MODE_PRIVATE);
        if(mShare != null){
            uuid = mShare.getString("uuid", "");
        }

        if(isEmpty(uuid)){
            uuid = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = mShare.edit();
            editor.putString("uuid", uuid);
            //saveSysMap(context, "sysCacheMap", "uuid", uuid);
        }

        L.e("getUUID : " + uuid);
        return uuid;
    }
}
