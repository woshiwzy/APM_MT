package com.common.task;

import com.common.util.MTLog;
import com.common.util.PathUtils;

import org.apache.commons.io.FileUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;

/**
 * @ProjectName: APM_MT
 * @Date: 2022/4/13
 * @Desc: 生成MT回调类
 */
public class CreateCallBackTask extends DefaultTask {



    @TaskAction
    void doTask() {

        try {
            File file = new File(getProject().getProjectDir(), "src/main/java/MTCallBack.java");
//            if (!file.exists()) {
//                file.mkdirs();
//            }


            String projectPath=PathUtils.getCurrentProjectPath();

            MTLog.redlog("projectPath----->>"+projectPath);
            MTLog.redlog("生成代码类运行结束----->>"+file.getAbsolutePath());

            String javaClass = "public class MTCallBack{" +
                    "\n"+
                    "public static void done(long start){" +
                    "\n"+
                    "}" +
                    "}";

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
}