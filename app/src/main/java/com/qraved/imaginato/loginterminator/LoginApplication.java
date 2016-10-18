package com.qraved.imaginato.loginterminator;

import android.app.Application;
import android.util.Log;

import com.facebook.FacebookSdk;

/**
 *  init kit
 * Created by Administrator on 2016/10/18.
 */

public class LoginApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("robin","QravedApplication-->onCreate");

        TwitterHelper.initFabric(getApplicationContext());
        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}
