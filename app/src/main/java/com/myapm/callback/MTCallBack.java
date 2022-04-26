package com.myapm.callback;

import com.apm.tools.MTHelper;
import com.apm.tools.MemInfo;
import com.commontech.basemodule.utils.FileSizer;
import com.commontech.basemodule.utils.KLog;
import com.sand.apm.mt.App;


/*
 *这个类是MT插件自动生成的，done方法将被注入到方法类中

 *start参数是对应方法开始的时间，done将会在方法结束时刻调用

 *你可以在此处统计方法执行时间或者App运行时信息

 *注意:如果你删除这个类，将会自动创建
 */

public class MTCallBack {

    public static void mtDone(long start) {

        long end = System.currentTimeMillis();

        long cost = end - start;

        StackTraceElement[] sts = Thread.currentThread().getStackTrace();

        //sts[3] 就是mtDone被调用所在的方法，也可以循环向上查询更深的栈层级

        String currentMethodName = sts[3].getClassName() + "." + sts[3].getMethodName();

        String mtLog = currentMethodName + " 耗时:" + cost + " ms,Thread.name:" + Thread.currentThread().getName();

        System.out.println(mtLog);

        KLog.d(App.tag, mtLog);


        MemInfo runTimeMemInfo = MTHelper.memInfoByRunTime();
//        KLog.d(App.tag, runTimeMemInfo.stringInfo);
        MemInfo debugMemInfo = MTHelper.memInfoByDebug();

        KLog.d(App.tag, "Java 堆内存:" + FileSizer.formatFile(runTimeMemInfo.usedMem) + ",Native 堆内存:" + FileSizer.formatFile(debugMemInfo.usedMem) + ",APP内存占用合计(总Java堆+总Native堆):" + FileSizer.formatFile(MTHelper.getAppUseMemByKB() * 1024));

//        KLog.d(App.tag,"totalMem:"+ FileSizer.formatFile(MTHelper.getMemory()));
//        KLog.d(App.tag,"Debug mem:"+MTHelper.memInfoByDebug().stringInfo);

    }

}