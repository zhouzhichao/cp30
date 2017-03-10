package com.crestv.cp30.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Chou on 2016/11/5.
 */

public class ShowToastUtil {
    private Toast toast=null;
    private static final ShowToastUtil single = new ShowToastUtil();
    public static ShowToastUtil getInstance(Context context, String msg) {
        if (single.toast==null)
            single.toast = Toast.makeText(context,msg, Toast.LENGTH_SHORT);
        return single;
    }
    public static ShowToastUtil getInstance(Context context) {
        if (single.toast==null)
            single.toast = Toast.makeText(context,"", Toast.LENGTH_SHORT);
        return single;
    }
    public void showToast(){
        toast.show();
    }
    public void showToast(String msg){
        toast.setText(msg);
        toast.show();
    }
}
