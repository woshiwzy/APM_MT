package com.sand.apm.mtlib;

import android.app.Application;
import android.os.Environment;
import android.util.Log;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @ProjectName: APM_MT
 * @Date: 2022/4/28
 * @Desc: Statistics tool
 */
public class Statistics {

    public static SimpleDateFormat FMT = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
    public static String tag = "mt";
    private static final String MT_PREFIX = "mt_";
    private static final String MT_DIR = "mtfiles";

    private static ConcurrentHashMap<String, Action> statisMap = new ConcurrentHashMap<>();

    private static ScheduledExecutorService extors = null;
    private static Application app;

    /**
     * 给日志工具赋值一个全局的Applicatoion 才能写文件到磁盘
     *
     * @param globalApp
     */
    public static void init(Application globalApp) {
        app = globalApp;
    }

    public static Application autoInit() {
        if (null == app) {
            try {

                //从ActivityThread中获取Application实例
                //https://juejin.cn/post/6887980244389593096

                //1.调用ActivityThread#currentActivityThread方法获取ActivityThread实例
                Class clazz = Class.forName("android.app.ActivityThread");
                Method currentThreadMethod = clazz.getMethod("currentActivityThread");
                currentThreadMethod.setAccessible(true);

                //2.activityThread 调用ActivityThread#getApplication 方法获取Application实例
                Method appMethod = clazz.getMethod("getApplication");
                appMethod.setAccessible(true);

                Object activityThread = currentThreadMethod.invoke(null);
                Application application = (Application) appMethod.invoke(activityThread);

                app = application;
                KLog.d(tag, "反射获取Applicaion成功");

            } catch (Exception e) {
                e.printStackTrace();
                KLog.d(tag, "获取Application失败");
            }

        }

        return app;
    }


    static {
        extors = Executors.newSingleThreadScheduledExecutor();
        extors.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                dumps();
            }
        }, 10, 10, TimeUnit.SECONDS);
    }


    public static Action start(String currentMethodName,long start) {

        Action action=Action.createFromStart(currentMethodName,start);
        statisMap.put(action.getKey(), action);
        Log.d("mt", "mtstart====>:" + action.getMethodName());

        return action;
    }

    /**
     * 闭合事件
     *
     * @param key
     */
    public static Action finish(String key) {
        Action action = statisMap.get(key);
        if (null != action) {
            action.close();
            Log.d(tag, action.toString());
        } else {
            //error
        }
        return action;
    }


    /**
     * 打印log到文件
     */
    public static void dumps() {
        if (null == autoInit()) {
            statisMap.clear();//不清除可能导致OOM
            KLog.e(tag, "*************没有调用Statistics.init进行初始化，日志系统无法工作********");
            return;
        }

        Set<Map.Entry<String, Action>> sets = statisMap.entrySet();
        String logFileName = MT_PREFIX + FMT.format(new Date()) + ".txt";
        File extDir = app.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        String absLogPath = extDir.getAbsolutePath() + File.separator + MT_DIR + File.separator + logFileName;

//        KLog.d(App.tag, "log 输出目录:" + absLogPath);

        int dumpsCount = 0;
        try {
            File outFile = new File(absLogPath);
            File parentFile = outFile.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }

            BufferedWriter bw = null;
            for (Map.Entry<String, Action> entry : sets) {

                if (!outFile.exists()) {
                    outFile.createNewFile();
                    bw = new BufferedWriter(new FileWriter(outFile));
                }
                Action acton = entry.getValue();
                boolean isDone = acton.isDone();
                String logLine = "MT_" + (isDone ? "D," : "N,") + acton.toLineString();
                if (isDone) {
                    statisMap.remove(acton.getKey());
                }
                bw.write(logLine + "\n");
                dumpsCount++;

            }

            if (null != bw) {
                bw.flush();
                bw.close();
            }
            KLog.i(tag, " 本次 dumps:" + dumpsCount + " 条");
        } catch (Exception e) {
            e.printStackTrace();
            KLog.e(tag, " 写log异常:" + e.getLocalizedMessage());
        } finally {

        }

    }


}