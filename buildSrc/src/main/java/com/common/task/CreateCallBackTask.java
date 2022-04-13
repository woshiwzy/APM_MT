package com.common.task;

import com.common.plug.MTConfig;
import com.common.util.MTLog;
import com.common.util.PathUtils;

import org.apache.commons.io.FileUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;

/**
 * @ProjectName: APM_MT
 * @Date: 2022/4/13
 * @Desc: 生成MT回调类
 */
public class CreateCallBackTask extends DefaultTask {

    @Input
    private MTConfig mtConfig;


    @TaskAction
    void doTask() {

        try {

            String mtPackage = mtConfig.mtCallBackPackage;//自动生成的包
            String callStaticMethod = "done";//注入的方法

            String fileChildPath = "src/main/java/" + mtPackage.replace(".", "/");
            MTLog.redlog("自动生成的类路径----->>" + fileChildPath);

            String javaFilePath = fileChildPath + "/MTCallBack.java";
            File file = new File(getProject().getProjectDir(), javaFilePath);

            if (file.exists()) {
                return;
            } else {
                File parentFile = file.getParentFile();
                parentFile.mkdirs();
            }

//            String projectPath = PathUtils.getCurrentProjectPath();

            MTLog.redlog("生成代码类运行结束----->>" + file.getAbsolutePath());

            String javaClass = "package " + mtConfig.mtCallBackPackage + ";\n\n" +
                    "\n\n" +
                    "/*\n" +
                    "*这个类是MT插件自动生成的，done方法将被注入到方法类中" + "\n\n" +
                    "*start参数是对应方法开始的时间，done将会在方法结束时刻调用" + "\n\n" +
                    "*你可以在此处统计方法执行时间或者App运行时信息\n\n" +
                    "*注意:如果你删除这个类，将会自动创建" +
                    "\n*/\n\n" +
                    "public class MTCallBack{" +
                    "\n\n" +
                    "   public static void " + callStaticMethod + "(long start){" +
                    "\n\n" +
                    "   }" +
                    "\n\n" +
                    "}";


            MTLog.redlog("最终将注入：" + javaFilePath + " 的done方法");

            FileUtils.writeByteArrayToFile(file, javaClass.getBytes());


        } catch (IOException e) {
            e.printStackTrace();
            MTLog.redlog("生成代码出现问题");
        }

        MTLog.redlog("生成代码类运行结束-----");

//        //生成java类
//        TypeSpec.Builder builder = TypeSpec.classBuilder("TestJava")
//                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
//        JavaFile javaFile = JavaFile.builder("com.test.sdk", builder.build()).build()
//        //将java写入到文件夹下
////        File file = new File(project.buildDir, "generated/source/container")
//        File file = new File(project.projectDir, "src/main/java")
//        if (!file.exists()) {
//            file.mkdirs()
//        }
//        javaFile.writeTo(file)
//        println "[write to]: ${file.absolutePath}"
//

    }


    public MTConfig getMtConfig() {
        return mtConfig;
    }

    public void setMtConfig(MTConfig mtConfig) {
        this.mtConfig = mtConfig;
    }
}