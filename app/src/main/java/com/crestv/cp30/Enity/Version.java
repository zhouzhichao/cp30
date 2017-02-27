package com.crestv.cp30.Enity;

import java.util.List;

/**
 * Created by txj on 16/9/2.
 */
public class Version {

    /**
     * msg : 操作成功!
     * status : 1
     * data : [{"appname":"站果","apkname":"zhanguo.apk","verName":"1.0.53","verCode":73}]
     */

    private String msg;
    private int status;
    private List<DataBean> data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * appname : 站果
         * apkname : zhanguo.apk
         * verName : 1.0.53
         * verCode : 73
         */

        private String appname;
        private String apkname;
        private String verName;
        private int verCode;

        public String getAppname() {
            return appname;
        }

        public void setAppname(String appname) {
            this.appname = appname;
        }

        public String getApkname() {
            return apkname;
        }

        public void setApkname(String apkname) {
            this.apkname = apkname;
        }

        public String getVerName() {
            return verName;
        }

        public void setVerName(String verName) {
            this.verName = verName;
        }

        public int getVerCode() {
            return verCode;
        }

        public void setVerCode(int verCode) {
            this.verCode = verCode;
        }
    }

}

