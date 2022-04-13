package com.common.transform;

import static com.common.util.MTLog.redlog;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.common.plug.MTConfig;
import com.common.util.MTLog;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;


public class MTransForm extends Transform {

    private MTConfig mtConfig;

    public MTransForm(MTConfig mtConfig) {
        this.mtConfig = mtConfig;
    }

    @Override
    public String getName() {
        //"\33[42;1m"+"文字"+"\n\33[0m"
        return "_______MTransForm______Running____";
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        redlog("MTransForm 正在运行");

        //当前是否是增量编译
        boolean isIncremental = transformInvocation.isIncremental();
        //消费型输入，可以从中获取jar包和class文件夹路径。需要输出给下一个任务
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        //引用型输入，无需输出。
        Collection<TransformInput> referencedInputs = transformInvocation.getReferencedInputs();
        //OutputProvider管理输出路径，如果消费型输入为空，你会发现OutputProvider == null
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
        for (TransformInput input : inputs) {

            for (JarInput jarInput : input.getJarInputs()) {
                File dest = outputProvider.getContentLocation(jarInput.getFile().getAbsolutePath(), jarInput.getContentTypes(), jarInput.getScopes(), Format.JAR);
                //如果需要在此处处理字节码
                FileUtils.copyFile(jarInput.getFile(), dest);//这里展示不处理jar直接拷贝过去(处理jar的话，先解开包在处理再重新打包比较麻烦)
            }

            for (DirectoryInput directoryInput : input.getDirectoryInputs()) {
                File dest = outputProvider.getContentLocation(directoryInput.getName(), directoryInput.getContentTypes(), directoryInput.getScopes(), Format.DIRECTORY);
                ////如果需要在此处处理字节码，就可以实现编译期间干预字节码的目的了
                MTLog.redlog("directoryInput===>:" + directoryInput.getFile() + " ===> " + dest);
                processInjectDirFiles(directoryInput.getFile(), dest, directoryInput.getFile(), mtConfig);
            }

        }
    }

    /**
     * 递归处理各种class 文件
     *
     * @param rootDic
     * @param rootDest
     * @param processingFile
     * @param mtConfig
     */
    private void processInjectDirFiles(File rootDic, File rootDest, File processingFile, MTConfig mtConfig) {
        if (null != processingFile && processingFile.isFile()) {
            processInject(rootDic.getAbsolutePath(), processingFile, rootDest, mtConfig);
        } else if (null != processingFile && processingFile.isDirectory()) {
            File[] subFiels = processingFile.listFiles();
            for (File tempFile : subFiels) {
                processInjectDirFiles(rootDic, rootDest, tempFile, mtConfig);
            }
        }
    }

    /**
     * 注入代码
     *
     * @param processingFile 正在transform文件
     * @param dest transform的下一个输入地址
     * @param mtConfig 配置信息
     */
    private void processInject(String inputDir, File processingFile, File dest, MTConfig mtConfig) {
        try {
            if (processingFile.isDirectory()) {
                return;
            }
            //判断该类是否需要插装
            boolean needPlug = mtConfig.needPlug(processingFile.getAbsolutePath());
            MTLog.redlog("类文件路径:" + processingFile.getAbsolutePath() + " 文件是否存在：" + (processingFile.exists()) + " 是否需要插桩：" + needPlug);
            File outFile =getOutPutFile(processingFile, inputDir,dest);
            if (needPlug) {
                FileInputStream fis = new FileInputStream(processingFile);
                //类读取器
                ClassReader cr = new ClassReader(fis);
                // 写出器
                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
                //分析，处理结果写入cw
                cr.accept(new ClassInjectTimeVisitor(cw, processingFile.getName(), mtConfig), ClassReader.EXPAND_FRAMES);
                byte[] newClassBytes = cw.toByteArray();
//             //class文件绝对地址去掉目录，得到全类名
                mkdirs(outFile.getParentFile());
                FileUtils.writeByteArrayToFile(outFile,newClassBytes);
            }else {
                FileUtils.copyFile(processingFile,outFile);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 获取下一个tranform的输入路径
     * @param processingFile
     * @param rootDicDir
     * @param dest
     * @return
     */
    private static File getOutPutFile(File processingFile,String rootDicDir,File dest){
        String absolutePath = processingFile.getAbsolutePath();
        String fullClassPath = absolutePath.replace(rootDicDir, "");
        File outFile = new File(dest, fullClassPath);
        return  outFile;
    }



    public static File mkdirs(File folder) {
        if (!folder.mkdirs() && !folder.isDirectory()) {
            throw new RuntimeException("Cannot create directory " + folder);
        }
        return folder;
    }


    /**
     * 处理class文件
     *
     * @return
     */
    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;//只处理class文件
    }


    /**
     * 应用transform范围
     * 只处理本项目
     * @return
     */
    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.PROJECT_ONLY;
    }


    /**
     * 是否增量更新
     * @return
     */
    @Override
    public boolean isIncremental() {
        return false;
    }
}
