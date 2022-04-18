package com.apm.tools;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;
import android.text.format.Formatter;
import android.util.Log;


/**
 * @ProjectName: APM_MT
 * @Date: 2022/4/11
 * @Desc: 工具类
 */
public class MTHelper {

    public static String tag="mt";

    public static String formatData(Context context,long fileData){
        return Formatter.formatFileSize(context,fileData);
    }

    public static String memInfoByAM(Context context){

        ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo=new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        String totalMem="Sys ava total mem:"+formatData(context,memoryInfo.totalMem)+"\n";
        String currentLefetMem="Sys left mem:"+formatData(context,memoryInfo.availMem)+"\n";
        String isLowMem="Is Low mem status:"+memoryInfo.lowMemory+"\n";
        String threshold="Mem threshold:"+memoryInfo.threshold+"\n";
        return totalMem+currentLefetMem+isLowMem+threshold;
    }

    public static String memInfoByDebug(){
//        Debug的getMemoryInfo(Debug.MemoryInfo memoryInfo)或ActivityManager的MemoryInfo[] getProcessMemoryInfo(int[] pids)。比较详细.数据的单位是KＢ.
//                MemoryInfo的Field如下
//        dalvikPrivateDirty： The private dirty pages used by dalvik。
//        dalvikPss ：The proportional set size for dalvik.
//                dalvikSharedDirty ：The shared dirty pages used by dalvik.
//                nativePrivateDirty ：The private dirty pages used by the native heap.
//                nativePss ：The proportional set size for the native heap.
//                nativeSharedDirty ：The shared dirty pages used by the native heap.
//                otherPrivateDirty ：The private dirty pages used by everything else.
//        otherPss ：The proportional set size for everything else.
//        otherSharedDirty ：The shared dirty pages used by everything else.
//
//        Android和Linux一样有大量内存在进程之间进程共享。某个进程准确的使用好多内存实际上是很难统计的。
//        因为有paging out to disk（换页），所以如果你把所有映射到进程的内存相加，它可能大于你的内存的实际物理大小。
//        dalvik：是指dalvik所使用的内存。
//        native：是被native堆使用的内存。应该指使用C\C++在堆上分配的内存。
//        other:是指除dalvik和native使用的内存。但是具体是指什么呢？至少包括在C\C++分配的非堆内存，比如分配在栈上的内存。puzlle!
//        private:是指私有的。非共享的。
//        share:是指共享的内存。
//        PSS：实际使用的物理内存（比例分配共享库占用的内存）
//        Pss：它是把共享内存根据一定比例分摊到共享它的各个进程来计算所得到进程使用内存。网上又说是比例分配共享库占用的内存，那么至于这里的共享是否只是库的共享，还是不清楚。
//
//        PrivateDirty：它是指非共享的，又不能换页出去（can not be paged to disk ）的内存的大小。比如Linux为了提高分配内存速度而缓冲的小对象，即使你的进程结束，该内存也不会释放掉，它只是又重新回到缓冲中而已。
//        SharedDirty:参照PrivateDirty我认为它应该是指共享的，又不能换页出去（can not be paged to disk ）的内存的大小。比如Linux为了提高分配内存速度而缓冲的小对象，即使所有共享它的进程结束，该内存也不会释放掉，它只是又重新回到缓冲中而已。
//
//        注意１：MemoryInfo所描述的内存使用情况都可以通过命令adb shell "dumpsys meminfo %curProcessName%" 得到。
//
//        注意２：如果想在代码中同时得到多个进程的内存使用或非本进程的内存使用情况请使用ActivityManager的MemoryInfo[] getProcessMemoryInfo(int[] pids)，
//
//        否则Debug的getMemoryInfo(Debug.MemoryInfo memoryInfo)就可以了。
//
//        注意３：可以通过ActivityManager的List<ActivityManager.RunningAppProcessInfo> getRunningAppProcesses()得到当前所有运行的进程信息。ActivityManager.RunningAppProcessInfo中就有进程的id，名字以及该进程包括的所有apk包名列表等。
//
//        注意4：数据的单位是KB.
        //https://www.jianshu.com/p/a283409c3d1c


        Debug.MemoryInfo osMemoryInfo=new Debug.MemoryInfo();
        Debug.getMemoryInfo(osMemoryInfo);

        int dalvikPrivateDirty = osMemoryInfo.dalvikPrivateDirty;
        int dalvikPss = osMemoryInfo.dalvikPss;
        int dalvikSharedDirty = osMemoryInfo.dalvikSharedDirty;


        //返回的是当前进程navtive堆中已使用的内存大小
        Log.i(tag,"NativeHeapSizeTotal:"+(Debug.getNativeHeapSize()>>10)); //>>10转化为KB
        //返回的是当前进程navtive堆中已经剩余的内存大小
        Log.i(tag,"NativeAllocatedHeapSize:"+(Debug.getNativeHeapAllocatedSize()>>10));
//        返回的是当前进程navtive堆本身总的内存大小
        Log.i(tag,"NativeAllocatedFree:"+(Debug.getNativeHeapFreeSize()>>10));

        return "";
        
    }


    public static String memInfoByRunTime(){
        long maxMem = Runtime.getRuntime().maxMemory();//app可用的最大内存
        long used=Runtime.getRuntime().totalMemory();//app已占用的内存
        long alocateNotUse=Runtime.getRuntime().freeMemory();//app已经占用，但实际并未使用的内存
        long totalUsed=used-alocateNotUse;//获取已经分配的内存
        return "";
    }


}