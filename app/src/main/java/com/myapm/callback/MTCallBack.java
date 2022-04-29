package com.myapm.callback;

import android.util.Log;

import com.apm.tools.Action;
import com.apm.tools.MTHelper;
import com.apm.tools.Statistics;


/*
 *这个类是MT插件自动生成的，done方法将被注入到方法类中

 *start参数是对应方法开始的时间，done将会在方法结束时刻调用

 *你可以在此处统计方法执行时间或者App运行时信息

 *注意:如果你删除这个类，将会自动创建
 */

public class MTCallBack {

    public static void mtStart(long start) {

        String currentMethodName = MTHelper.getCurrentMethodName(Thread.currentThread().getStackTrace());
        Action action=Action.createFromStart(currentMethodName,start);
        Statistics.start(action.getKey(),action);

        Log.d("mt", "mtstart====>:" + currentMethodName);
        System.out.println("mtstart====>" + currentMethodName);

    }

    public static void mtDone(long start) {
        long end = System.currentTimeMillis();
        long cost = end - start;
        String currentMethodName = MTHelper.getCurrentMethodName(Thread.currentThread().getStackTrace());
        Log.d("mt", "mtEnd====>:" + currentMethodName + " 耗时: " + cost + " 毫秒");

        Statistics.close(Action.createKey(currentMethodName,start));

//        String mtLog = currentMethodName + " 耗时:" + cost + " ms,Thread.name:" + Thread.currentThread().getName();
//        System.out.println(mtLog);
//        Log.d("mt", "<======"+mtLog);



    }

}