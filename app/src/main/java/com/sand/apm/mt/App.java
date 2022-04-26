package com.sand.apm.mt;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.commontech.basemodule.utils.KLog;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.DiskLogStrategy;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.LogcatLogStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.sand.apm.mt.BuildConfig;

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

        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(2)         // (Optional) How many method line to show. Default 2
                .methodOffset(5)        // (Optional) Hides internal method calls up to offset. Default 5
                .logStrategy(new LogcatLogStrategy()) // (Optional) Changes the log strategy to print out. Default LogCat
                .tag(tag)   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();

        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy){
            @Override
            public boolean isLoggable(int priority, @Nullable String tag) {
                return BuildConfig.DEBUG;
            }
        });

        registerComponentCallbacks(new ComponentCallbacks() {
            @Override
            public void onConfigurationChanged(@NonNull Configuration newConfig) {

            }

            @Override
            public void onLowMemory() {

            }
        });

        KLog.init(true);

    }
}