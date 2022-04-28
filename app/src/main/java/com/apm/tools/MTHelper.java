package com.apm.tools;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;
import android.text.format.Formatter;

import com.commontech.basemodule.utils.FileSizer;


/**
 * @ProjectName: APM_MT
 * @Date: 2022/4/11
 * @Desc: 工具类
 * <p>
 *  https://www.jianshu.com/p/8a0a039397a8
 *  先说结论：一般情况下有：VSS >= RSS >= PSS >= USS。
 * 1.VSS - Virtual Set Size 虚拟耗用内存（包含共享库占用的内存）
 * 2.RSS - Resident Set Size 常驻内存/实际使用物理内存（包含共享库占用的内存）
 * 3.PSS - Proportional Set Size 实际使用的物理内存（比例分配共享库占用的内存）
 * 4.USS - Unique Set Size 进程独自占用的物理内存（不包含共享库占用的内存）
 */
public class MTHelper {

    public static String tag = "mt";


    public static String getCurrentMethodName(StackTraceElement[] sts){
        String currentMethodName = sts[3].getClassName() + "." + sts[3].getMethodName();
        return currentMethodName;
    }


    public static String formatData(Context context, long fileData) {
        return Formatter.formatFileSize(context, fileData);
    }


    /**
     * 从Activity Manager 中获取内存信息
     *
     * @param context
     * @return
     */
    public static String memInfoByAM(Context context) {

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        String totalMem = "Sys ava total mem:" + formatData(context, memoryInfo.totalMem) + "\n";
        String currentLefetMem = "Sys left mem:" + formatData(context, memoryInfo.availMem) + "\n";
        String isLowMem = "Is Low mem status:" + memoryInfo.lowMemory + "\n";
        String threshold = "Mem threshold:" + memoryInfo.threshold + "\n";
        return totalMem + currentLefetMem + isLowMem + threshold;
    }

    /**
     * 从Debug类中获取类信息，最有用的是Native堆占用信息
     *
     * @return
     */
    public static MemInfo getNativeHeap() {
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


        Debug.MemoryInfo osMemoryInfo = new Debug.MemoryInfo();
        Debug.getMemoryInfo(osMemoryInfo);

        int dalvikPrivateDirty = osMemoryInfo.dalvikPrivateDirty;
        int dalvikPss = osMemoryInfo.dalvikPss;
        int dalvikSharedDirty = osMemoryInfo.dalvikSharedDirty;

//        //返回的是当前进程navtive堆中已使用的内存大小
//        Log.i(tag,"NativeHeapSizeTotal:"+(Debug.getNativeHeapSize()>>10)); //>>10转化为KB
//        //返回的是当前进程navtive堆中已经剩余的内存大小
//        Log.i(tag,"NativeAllocatedHeapSize:"+(Debug.getNativeHeapAllocatedSize()>>10));
////        返回的是当前进程navtive堆本身总的内存大小
//        Log.i(tag,"NativeAllocatedFree:"+(Debug.getNativeHeapFreeSize()>>10));

        String debugMemInfo = "Native堆内存大小:" + FileSizer.formatFile(Debug.getNativeHeapSize()) + " Native堆内存已占用:" + FileSizer.formatFile(Debug.getNativeHeapAllocatedSize()) + " Navtive堆内存剩余:" + FileSizer.formatFile(Debug.getNativeHeapFreeSize());
        return new MemInfo(debugMemInfo, Debug.getNativeHeapAllocatedSize());
    }


    public static Long getNativeHeap2() {

        return Debug.getNativeHeapAllocatedSize();
    }

    /**
     * 建议用这个方法获取内存信息和profiler看到的内存信息一致
     *
     * @return
     */
    public static MemInfo getJavaHeap() {

        long maxMem = Runtime.getRuntime().maxMemory();//app可用的最大内存
        long used = Runtime.getRuntime().totalMemory();//app已占用的内存
        long alocateNotUse = Runtime.getRuntime().freeMemory();//app已经占用，但实际并未使用的内存
        long totalUsed = used - alocateNotUse;//获取已经分配的内存

        String memInfo = "App可用最大内存:" + FileSizer.formatFile(maxMem) + " App 已经占用内存:" + FileSizer.formatFile(used) + " 占用但未用:" + FileSizer.formatFile(alocateNotUse) + " 正在使用:" + FileSizer.formatFile(totalUsed);

        return new MemInfo(memInfo, totalUsed);
    }


    public static long getJavaHeap2() {

        long maxMem = Runtime.getRuntime().maxMemory();//app可用的最大内存
        long used = Runtime.getRuntime().totalMemory();//app已占用的内存
        long alocateNotUse = Runtime.getRuntime().freeMemory();//app已经占用，但实际并未使用的内存
        long totalUsed = used - alocateNotUse;//获取已经分配的内存

//        String memInfo = "App可用最大内存:" + FileSizer.formatFile(maxMem) + " App 已经占用内存:" + FileSizer.formatFile(used) + " 占用但未用:" + FileSizer.formatFile(alocateNotUse) + " 正在使用:" + FileSizer.formatFile(totalUsed);
//        return new MemInfo("", totalUsed);
        return  totalUsed;
    }

    public static int getAppUseMem() {

        //PSS:PSS - Proportional Set Size 实际使用的物理内存

        Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();

        Debug.getMemoryInfo(memoryInfo);


// dalvikPrivateClean + nativePrivateClean + otherPrivateClean;

        int totalPrivateClean = memoryInfo.getTotalPrivateClean();

// dalvikPrivateDirty + nativePrivateDirty + otherPrivateDirty;

        int totalPrivateDirty = memoryInfo.getTotalPrivateDirty();

// dalvikPss + nativePss + otherPss;

        int totalPss = memoryInfo.getTotalPss();

// dalvikSharedClean + nativeSharedClean + otherSharedClean;

        int totalSharedClean = memoryInfo.getTotalSharedClean();

// dalvikSharedDirty + nativeSharedDirty + otherSharedDirty;

        int totalSharedDirty = memoryInfo.getTotalSharedDirty();

// dalvikSwappablePss + nativeSwappablePss + otherSwappablePss;

        int totalSwappablePss = memoryInfo.getTotalSwappablePss();

        int total = totalPrivateClean + totalPrivateDirty + totalPss + totalSharedClean + totalSharedDirty + totalSwappablePss;

        return total*1024;//默认以KB计算，*1024以MB计算

    }


}