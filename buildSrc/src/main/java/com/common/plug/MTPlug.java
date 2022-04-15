package com.common.plug;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.api.ApplicationVariant;
import com.common.task.CreateCallBackTask;
import com.common.transform.MTransForm;
import com.common.util.MTLog;
import com.common.util.MTPlugHelper;

import org.gradle.api.DomainObjectSet;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.io.File;
import java.util.Collections;

/**
 * 自定义插件
 */
public class MTPlug implements Plugin<Project> {


    private boolean isOpenMt(Project project){
        //读取配置文件
        Object mtWork=project.getRootProject().getProperties().get(MTPlugHelper.MT_FLAG_LABEL);
        boolean work=  Boolean.valueOf((mtWork==null|| Boolean.valueOf(mtWork.toString())==false)?false:true);
        return work;
    }

    @Override
    public void apply(Project project) {
        //为项目配置基础插件开关参数
        MTPlugHelper.config(project);
        boolean work=isOpenMt(project);
        MTLog.yellowLog("Open MTPlug："+work);

        MTLog.purpLog("****************************************************************");
        MTLog.redLog("If you want close MTPlug, config gradle.properties file made mt.work=false");
        MTLog.purpLog("****************************************************************");

        //添加自定义扩展
        MTConfig mtConfig = project.getExtensions().create("MTConfig", MTConfig.class);
        AppExtension appExtension = (AppExtension) project.getProperties().get("android");
        if (work) {
            MTLog.redLog("*************MTPlug start************");
            appExtension.registerTransform(new MTransForm(mtConfig), Collections.EMPTY_LIST);
            //配置完成后才能读取到mtconfig信息
            project.afterEvaluate(project1 -> {

//                MTLog.redLog("这个包下面的所有类都将被注入：=====> " + mtConfig.pkgs.toString());
//                MTLog.redLog("白名单里面的都要被注入：=====> " + mtConfig.whiteList.toString());
//                MTLog.redLog("获取自定义扩展配置_黑名单：=====> " + mtConfig.blackList.toString());
//                MTLog.redLog("绝对不注入这些方法：=====> " + mtConfig.excludeMethods.toString());

                //添加生成代码的Task
                DomainObjectSet<ApplicationVariant> variants = appExtension.getApplicationVariants();
                CreateCallBackTask createCodeTask = project.getTasks().create("CreateCallBackTask", CreateCallBackTask.class);
                createCodeTask.setMtConfig(mtConfig);
                for (ApplicationVariant variant : variants) {
                    MTLog.redLog("Add generate task for " + variant.getName());
                    variant.registerJavaGeneratingTask(createCodeTask, new File(project.getProjectDir(), "src/main/java"));
                }

            });

//            //生成自定义代码
//            CreateCallBackTask createCodeTask = project.getTasks().create("CreateCallBackTask", CreateCallBackTask.class);
//            createCodeTask.dependsOn(project.getTasks().findByName("assembleDebug"));

        } else {
            MTLog.redLog("*************MTPlug end************");
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
