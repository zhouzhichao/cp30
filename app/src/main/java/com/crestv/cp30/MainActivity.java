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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.crestv.cp30.activity.DownLoadActivity;
import com.crestv.cp30.util.L;

/*
 * MainActivity class that loads MainFragment
 */
public class MainActivity extends Activity  {
    private int versionCode=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent=getIntent();
        versionCode=intent.getIntExtra("versionCode",0);
        if (versionCode==0){
            Intent intent1=new Intent(this, DownLoadActivity.class);
            startActivity(intent1);
            L.e("==","==");
            finish();
        }else {

        }
    }

    //private MessageReceiver mMessageReceiver;
   /* public static final String MESSAGE_RECEIVED_ACTION = "com.crestv.cp30.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + messge + "\n");

                setCostomMsg(showMsg.toString());
            }
        }
    }
    private void setCostomMsg(String msg){

    }*/


}
