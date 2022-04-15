package com.common.util;

import org.apache.commons.io.FileUtils;
import org.gradle.api.Project;

import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * @ProjectName: APM_MT
 * @Date: 2022/4/15
 * @Desc: Help config mt parameter setting
 */
public class MTPlugHelper {

    public final static String MT_FLAG_LABEL ="mt.work";//gradle.properties
    public final static String DEFAULT_MT_OPEN ="true";//mt default status

    /**
     * config gradle.properties file
     * @param project
     */
    public static boolean config(Project project){

//        Map<Project, Set<Task>> tasks = project.getAllTasks(false);
//        Task asssebuDebugTask = project.getTasks().findByName("assembleDebug");
//        Task asssebuReleaseTask = project.getTasks().findByName("assembleRelease");
//        MTLog.redlog("asssebuDebugTask：=====> " + (asssebuDebugTask==null));
//        MTLog.redlog("asssebuReleaseTask：=====> " + (asssebuReleaseTask==null));
        //新版本gradle的API
//        AndroidComponentsExtension androidComponents = project.getExtensions().getByType(AndroidComponentsExtension.class);
        //gradle 自带的api，只能读取，不能写入，会报错
//        Project rootProject=project.getRootProject();
//        Map<String, ?> props = rootProject.getProperties();

        //获取项目的根路径，以下两种方法都可以
        String projectDir=project.getRootProject().getProjectDir().getAbsolutePath();
//        String rootDir=project.getRootDir().getAbsolutePath();
        //gralde.properties 文件路径
        String gradlePropertiesFile=projectDir+ File.separator+"gradle.properties";
        boolean needAddConfig=true;

        try {

            List<String> oldlines = FileUtils.readLines(new File(gradlePropertiesFile), "utf-8");

            Optional<String> mtFlagLine = oldlines.stream().filter(s -> { if (null != s && s.trim().startsWith(MT_FLAG_LABEL)) {
                    return true;
                }
                return false;
            }).findFirst();

            needAddConfig=(mtFlagLine.isEmpty());
            if(needAddConfig){
                oldlines.add("#"+ MT_FLAG_LABEL +" =true open mtplug else close mtplug  ");
                oldlines.add(MT_FLAG_LABEL +"="+ DEFAULT_MT_OPEN);
                FileUtils.writeLines(new File(gradlePropertiesFile),oldlines);
            }



        }catch (Exception e){
            e.printStackTrace();
        }
        MTLog.yellowLog("gradle.properties file check compeleted!");
        return needAddConfig;
    }
}