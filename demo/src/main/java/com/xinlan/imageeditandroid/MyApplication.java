package com.xinlan.imageeditandroid;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

/**
 * 作者：我的孩子叫好帅 on 2019-12-17 17:33
 * Q Q：779594494
 * 邮箱：18733215730@163.com
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }
    }
}
