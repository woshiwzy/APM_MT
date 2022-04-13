package com.common.plug;

import com.android.build.api.variant.AndroidComponentsExtension;
import com.android.build.gradle.AppExtension;
import com.android.build.gradle.AppPlugin;
import com.android.build.gradle.LibraryPlugin;
import com.android.build.gradle.api.ApplicationVariant;
import com.android.build.gradle.api.BaseVariant;
import com.android.build.gradle.internal.TaskManager;
import com.android.build.gradle.internal.dsl.BuildType;
import com.common.task.CreateCallBackTask;
import com.common.task.MyTask;
import com.common.transform.MTransForm;
import com.common.util.MTLog;

import org.gradle.api.DomainObjectSet;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.internal.DefaultDomainObjectCollection;
import org.gradle.api.tasks.TaskProvider;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * 自定义插件
 */
public class MTPlug implements Plugin<Project> {


    @Override
    public void apply(Project project) {

//        Map<Project, Set<Task>> tasks = project.getAllTasks(false);
//        Task asssebuDebugTask = project.getTasks().findByName("assembleDebug");
//        Task asssebuReleaseTask = project.getTasks().findByName("assembleRelease");
//
//        MTLog.redlog("asssebuDebugTask：=====> " + (asssebuDebugTask==null));
//        MTLog.redlog("asssebuReleaseTask：=====> " + (asssebuReleaseTask==null));


        //新版本的API
//        AndroidComponentsExtension androidComponents = project.getExtensions().getByType(AndroidComponentsExtension.class);

        //添加自定义扩展
        MTConfig mtConfig = project.getExtensions().create("MTConfig", MTConfig.class);
        AppExtension appExtension = (AppExtension) project.getProperties().get("android");

        if (mtConfig.work) {

//            DefaultDomainObjectCollection<BaseVariant> variants
//            if (project.plugins.hasPlugin(AppPlugin.class)) {
//                variants = project.android.applicationVariants
//            } else if (project.plugins.hasPlugin(LibraryPlugin.class)) {
//                variants = project.android.libraryVariants
//            } else {
//                return
//            }
//            variants.all { variant ->
//                    def task = project.tasks.create("create${variant.name.capitalize()}JavaTask",
//                    CreateJavaTask.class)
//                //注册生成java类的task，指定生成地址，需要和task中写入java的地址一致
////                    variant.registerJavaGeneratingTask(task,new File(project.buildDir, "generated/source/container"))
//                variant.registerJavaGeneratingTask(task, new File(project.projectDir, "src/main/java"))
//            }

            MTLog.redlog("MT 已经插件开启");
            appExtension.registerTransform(new MTransForm(mtConfig), Collections.EMPTY_LIST);
            //配置完成后才能读取到mtconfig信息
            project.afterEvaluate(project1 -> {

                MTLog.redlog("这个包下面的所有类都将被注入：=====> " + mtConfig.pkgs.toString());
                MTLog.redlog("白名单里面的都要被注入：=====> " + mtConfig.whiteList.toString());
                MTLog.redlog("获取自定义扩展配置_黑名单：=====> " + mtConfig.blackList.toString());
                MTLog.redlog("绝对不注入这些方法：=====> " + mtConfig.excludeMethods.toString());

                DomainObjectSet<ApplicationVariant> variants = appExtension.getApplicationVariants();
                CreateCallBackTask myTask = project.getTasks().create("CreateCallBackTask", CreateCallBackTask.class);

                for(ApplicationVariant variant:variants){
                    MTLog.redlog("variants====>:"+variant.getName()+"-"+variant.getFlavorName());
                    variant.registerJavaGeneratingTask(myTask,new File(project.getProjectDir(), "src/main/java"));
                }

            });

//            //生成自定义代码
//            CreateCallBackTask myTask = project.getTasks().create("CreateCallBackTask", CreateCallBackTask.class);
//            myTask.dependsOn(project.getTasks().findByName("assembleDebug"));

        } else {
            MTLog.redlog("MT 已经插件已经关闭");
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
