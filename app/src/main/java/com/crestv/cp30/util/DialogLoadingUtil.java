package com.crestv.cp30.util;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.crestv.cp30.R;


/**
 * Created by Chou on 2016/10/25.
 */

public class DialogLoadingUtil {
    public static Dialog beginLoading(Context context) {
            Dialog progressDialog = new Dialog(context, R.style.progress_dialog);
            progressDialog.setContentView(R.layout.dialog);
            // 设置ProgressDialog 是否可以按退回按键取消
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


            TextView msg = (TextView) progressDialog.findViewById(R.id.id_tv_loadingmsg);
            msg.setText("加载中...");
    return progressDialog;
    }

}
