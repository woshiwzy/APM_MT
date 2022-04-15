package com.common.transform;


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
        return "_______MTransForm______Running____";
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        MTLog.orangeLog("********************Transform Start********************");
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
                MTLog.redLog("Injectinging :\n" + directoryInput.getFile() + "\n done,will copy to:\n" + dest+"\n");
                processInjectDirFiles(directoryInput.getFile(), dest, directoryInput.getFile(), mtConfig);
            }

        }
        MTLog.orangeLog("********************Transform Done********************");
    }



    /**
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
     * @param processingFile
     * @param dest
     * @param mtConfig
     */
    private void processInject(String inputDir, File processingFile, File dest, MTConfig mtConfig) {
        try {
            if (processingFile.isDirectory()) {
                return;
            }
            boolean needPlug = mtConfig.needPlug(processingFile.getAbsolutePath());
            MTLog.blueLog("Injecting class:" + processingFile.getAbsolutePath() + " file exist：" + (processingFile.exists()) + " need inject：" + needPlug);
            File outFile =getOutPutFile(processingFile, inputDir,dest);
            if (needPlug) {
                FileInputStream fis = new FileInputStream(processingFile);
                //class reader
                ClassReader cr = new ClassReader(fis);
                //class writer
                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
                //combine reader wirter visitor
                cr.accept(new ClassInjectTimeVisitor(cw, processingFile.getName(), mtConfig), ClassReader.EXPAND_FRAMES);
                byte[] newClassBytes = cw.toByteArray();
//             //mkdirs
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
     * Inject class only
     *
     * @return
     */
    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;//
    }


    /**
     * Inject this project only
     * @return
     */
    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.PROJECT_ONLY;
    }


    /**
     * @return
     */
    @Override
    public boolean isIncremental() {
        return false;
    }
}
