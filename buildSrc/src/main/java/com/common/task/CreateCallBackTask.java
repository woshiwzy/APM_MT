package com.common.task;

import com.common.plug.MTConfig;
import com.common.util.ColorLog;
import com.common.util.MTLog;

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

            String callStaticMethodStart=MTConfig.MTMethodStart;
            String callStaticMethodDone = MTConfig.MTMethodDone;//注入的方法

            String fileChildPath = "src/main/java/" + mtPackage.replace(".", "/");
            MTLog.yellowLog("------------------------------------------");
            MTLog.purpLog("MT will create a callback class here:" + ColorLog.yellow(fileChildPath));
            MTLog.yellowLog("------------------------------------------");

            String javaFilePath = fileChildPath + "/MTCallBack.java";
            File file = new File(getProject().getProjectDir(), javaFilePath);

            if (file.exists()) {
                return;
            } else {
                File parentFile = file.getParentFile();

                parentFile.mkdirs();//创建包路径
            }

//            String projectPath = PathUtils.getCurrentProjectPath();

            MTLog.redLog("生成代码类运行结束----->>" + file.getAbsolutePath());

            String javaClass =
                    "package " + mtConfig.mtCallBackPackage + ";\n" +
                    "import android.util.Log;\n" +
                    "import com.sand.apm.mtlib.Action;\n"+
                    "import com.sand.apm.mtlib.KLog;\n"+
                    "import com.sand.apm.mtlib.MTHelper;\n"+
                    "import com.sand.apm.mtlib.Statistics;\n"+

                    "\n\n" +
                    "/*\n" +
                    "*这个类是MT插件自动生成的，done方法将被注入到方法类中" + "\n\n" +
                    "*start参数是对应方法开始的时间，done将会在方法结束时刻调用" + "\n\n" +
                    "*你可以在此处统计方法执行时间或者App运行时信息\n\n" +
                    "*注意:如果你删除这个类，将会自动创建" +
                    "\n*/\n\n" +
                    "public class MTCallBack{" +
                    "\n\n" +
                      "   public static void "+callStaticMethodStart+"(long start){" +"\n\n" +
                            //start 方法开始======================================
                            "   String currentMethodName = MTHelper.getCurrentMethodName(Thread.currentThread().getStackTrace());"+ "\n\n" +
                            "   Action action = Statistics.start(currentMethodName, start);"+ "\n\n" +
                            "   KLog.d(\"mt\", \"mt_call_back_start==>\" + action.toLineString());"+ "\n\n" +
                            //start 方法结束======================================
                            "\n\n" +
                            "   }"+
                            "\n\n" +
                    "   public static void " + callStaticMethodDone + "(long start){" +

                            //end 方法开始======================================
                    "\n\n" +
                            "   String currentMethodName = MTHelper.getCurrentMethodName(Thread.currentThread().getStackTrace());"+"\n\n" +
                            "   Action action = Statistics.finish(Action.createKey(currentMethodName, start));"+"\n\n" +
                            "   if (null != action) {"+"\n\n" +
                            "   KLog.d(\"mt\", \"mt_call_back_done==>\" + action.toLineString());"+"\n\n" +

                    "   }   \n   }\n\n" +
                    //end 方法结束======================================
                    "   }";
            MTLog.redLog("最终将注入：" + javaFilePath + " 的"+MTConfig.MTMethodDone +"方法");
            FileUtils.writeByteArrayToFile(file, javaClass.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            MTLog.redLog("生成代码出现问题");
        }

        MTLog.greenLog("生成代码类运行结束-----");

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