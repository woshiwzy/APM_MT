package com.common.plug;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.api.ApplicationVariant;
import com.common.task.CreateCallBackTask;
import com.common.transform.MTransForm;
import com.common.util.ColorLog;
import com.common.util.MTLog;
import com.common.util.PropTool;

import org.gradle.api.DomainObjectSet;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义插件
 */
public class MTPlug implements Plugin<Project> {

    @Override
    public void apply(Project project) {

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

        String gradlePropertiesFile=projectDir+File.separator+"gradle.properties";
        File propertiesFile=new File(gradlePropertiesFile);

        boolean graPropFileExist=propertiesFile.exists();
        MTLog.yellowLog("项目根路径："+projectDir);
        MTLog.orangeLog("gradle.properties文件存在否:"+graPropFileExist);
        if(graPropFileExist){

            try {
                HashMap<String,String> propSets = PropTool.readProperties(propertiesFile);

                for(Map.Entry<String,String> proEntry:propSets.entrySet()){
                    MTLog.redLog("prooName:"+proEntry.getKey()+" value:"+proEntry.getValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            MTLog.redLog("gradle.properties 文件不存在!!!!!");
        }


        //读取配置文件
        Object mtWork=project.getRootProject().getProperties().get("mt.work");
        boolean work=  Boolean.valueOf((mtWork==null|| Boolean.valueOf(mtWork.toString())==false)?false:true);
        MTLog.yellowLog("是否开启MT插件："+work);

        //添加自定义扩展
        MTConfig mtConfig = project.getExtensions().create("MTConfig", MTConfig.class);
        AppExtension appExtension = (AppExtension) project.getProperties().get("android");
        if (work) {
            MTLog.redLog("已经插件开启");
            appExtension.registerTransform(new MTransForm(mtConfig), Collections.EMPTY_LIST);
            //配置完成后才能读取到mtconfig信息
            project.afterEvaluate(project1 -> {

                MTLog.redLog("这个包下面的所有类都将被注入：=====> " + mtConfig.pkgs.toString());
                MTLog.redLog("白名单里面的都要被注入：=====> " + mtConfig.whiteList.toString());
                MTLog.redLog("获取自定义扩展配置_黑名单：=====> " + mtConfig.blackList.toString());
                MTLog.redLog("绝对不注入这些方法：=====> " + mtConfig.excludeMethods.toString());

                //添加生成代码的Task
                DomainObjectSet<ApplicationVariant> variants = appExtension.getApplicationVariants();
                CreateCallBackTask createCodeTask = project.getTasks().create("CreateCallBackTask", CreateCallBackTask.class);
                createCodeTask.setMtConfig(mtConfig);
                for (ApplicationVariant variant : variants) {
                    MTLog.redLog("为:" + variant.getName() + " 添加代码生成任务");
                    variant.registerJavaGeneratingTask(createCodeTask, new File(project.getProjectDir(), "src/main/java"));
                }

            });

//            //生成自定义代码
//            CreateCallBackTask createCodeTask = project.getTasks().create("CreateCallBackTask", CreateCallBackTask.class);
//            createCodeTask.dependsOn(project.getTasks().findByName("assembleDebug"));

        } else {
            MTLog.redLog("MT:已经关闭插件");
        }

//        DomainObjectSet<ApplicationVariant> variants = appExtension.getApplicationVariants();
//        for(ApplicationVariant variant:variants){
//            MTLog.redlog("variants====>:"+variant.getName()+"-"+variant.getFlavorName());
//        }
//
//        NamedDomainObjectContainer<BuildType> types = appExtension.getBuildTypes();
//        for(BuildType buildType:types){
//            MTLog.redlog("types====>:"+buildType.getName()+"-"+buildType.isDebuggable()+"-"+buildType.getIsDefault());
//        }

    }
}
