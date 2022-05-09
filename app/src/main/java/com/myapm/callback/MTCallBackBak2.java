package com.myapm.callback;

import com.apm.tools.Action;
import com.apm.tools.MTHelper;
import com.apm.tools.Statistics;

/*
 *这个类是MT插件自动生成的，done方法将被注入到方法类中

 *start参数是对应方法开始的时间，done将会在方法结束时刻调用

 *你可以在此处统计方法执行时间或者App运行时信息

 *注意:如果你删除这个类，将会自动创建
 */

public class MTCallBackBak2 {

    public static void mtStart(long start) {
        String currentMethodName = MTHelper.getCurrentMethodName(Thread.currentThread().getStackTrace());
        Statistics.start(currentMethodName, start);
    }

    public static void mtDone(long start) {
        String currentMethodName = MTHelper.getCurrentMethodName(Thread.currentThread().getStackTrace());
        Statistics.finish(Action.createKey(currentMethodName, start));
    }

}