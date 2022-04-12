package com.common.plug;

import com.android.build.gradle.AppExtension;
import com.common.transform.MyTransForm;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.util.Collections;

class MTPlug implements Plugin<Project> {


    @Override
    public void apply(Project project) {
        //添加自定义扩展
        MTConfig mtConfig = project.getExtensions().create("MTConfig", MTConfig.class);
//        project.getExtensions().getByType(AppExtension.class).registerTransform(new MyTransForm(mtConfig));

        AppExtension appExtension = (AppExtension)project.getProperties().get("android");

        appExtension.registerTransform(new MyTransForm(mtConfig), Collections.EMPTY_LIST);

        //配置完成后才能读取到mtconfig信息
        project.afterEvaluate(project1 -> {

            System.err.println("这个包下面的所有类都将被注入：=====> " + mtConfig.pkgs.toString() );
            System.err.println("白名单里面的都要被注入：=====> " + mtConfig.whiteList.toString() );
            System.err.println("获取自定义扩展配置_黑名单：=====> " + mtConfig.blackList.toString() );
            System.err.println("绝对不注入这些方法：=====> " + mtConfig.excludeMethods.toString() );

            //插入自己的Transform用来对字节码进行插装
//            project.getExtensions().getByType(AppExtension.class).registerTransform(new MyTransForm(mtConfig));
        });

    }
}
