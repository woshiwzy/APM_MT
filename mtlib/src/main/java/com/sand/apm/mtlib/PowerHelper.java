package com.sand.apm.mtlib;

import static android.content.Context.BATTERY_SERVICE;

import android.app.Application;
import android.os.BatteryManager;

/**
 * @ProjectName: APM_MT
 * @Date: 2022/5/7
 * @Desc:
 */
public class PowerHelper {


    public static int getBatter(Application application) {
        BatteryManager batteryManager = (BatteryManager) application.getSystemService(BATTERY_SERVICE);
        int battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        return battery;
    }
}