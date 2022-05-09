package com.sand.apm.mt;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * @ProjectName: APM_MT
 * @Date: 2022/4/13
 * @Desc:
 */
public class App extends Application {

    public static String tag="mt";

    @Override
    public void onCreate() {
        super.onCreate();
    }
}