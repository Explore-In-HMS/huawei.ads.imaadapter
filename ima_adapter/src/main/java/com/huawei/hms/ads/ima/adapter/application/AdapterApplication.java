package com.huawei.hms.ads.ima.adapter.application;

import android.app.Application;

import com.huawei.hms.ads.vast.player.VastApplication;

public class AdapterApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        VastApplication.init(AdapterApplication.this, true);
    }
}