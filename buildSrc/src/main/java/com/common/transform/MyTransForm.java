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
import com.common.Util;
import com.common.plug.MTConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import groovyjarjarasm.asm.ClassReader;
import groovyjarjarasm.asm.ClassWriter;

import static com.common.Util.redlog;



public class MyTransForm extends Transform {


    private MTConfig mtConfig;

    public MyTransForm(MTConfig mtConfig) {
        this.mtConfig=mtConfig;
    }

    @Override
    public String getName() {
        return "_MTTransForm_";
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);

        redlog("自定义transform 正在运行");

        //当前是否是增量编译
        boolean isIncremental = transformInvocation.isIncremental();
        //消费型输入，可以从中获取jar包和class文件夹路径。需要输出给下一个任务
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        //引用型输入，无需输出。
        Collection<TransformInput> referencedInputs = transformInvocation.getReferencedInputs();
        //OutputProvider管理输出路径，如果消费型输入为空，你会发现OutputProvider == null
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
        for(TransformInput input : inputs) {

            for(JarInput jarInput : input.getJarInputs()) {
                File dest = outputProvider.getContentLocation(jarInput.getFile().getAbsolutePath(), jarInput.getContentTypes(), jarInput.getScopes(), Format.JAR);

                Util.redlog("jarInput===>:"+jarInput.getFile());
                //将修改过的字节码copy到dest，就可以实现编译期间干预字节码的目的了
//                FileUtils.copyFile(jarInput.getFile(), dest);
            }


            for(DirectoryInput directoryInput : input.getDirectoryInputs()) {
                File dest = outputProvider.getContentLocation(directoryInput.getName(), directoryInput.getContentTypes(), directoryInput.getScopes(), Format.DIRECTORY);
                //将修改过的字节码copy到dest，就可以实现编译期间干预字节码的目的了
                Util.redlog("directoryInput===>:"+directoryInput.getFile()+" ===> "+dest);
//                FileUtils.copyDirectory(directoryInput.getFile(), dest);
                File[] allfiles = directoryInput.getFile().listFiles();
                if(null!=allfiles ){
                    for(File f:allfiles){
                        Util.redlog("文件夹下的文件:"+f.getAbsolutePath());
                    }
                }
            }


        }



    }

    /**
     * 注入代码
     * @param src
     * @param dest
     * @param mtConfig
     */
    private void processInject(File src, File dest, MTConfig mtConfig) {
        try {

            String dir = src.getAbsolutePath();
            File[] allFiles = src.listFiles();
            for (File file : allFiles) {

                if(file.isDirectory()){
                    return;
                }

                System.err.println("类文件路径:"+file.getAbsolutePath()+" 文件是否存在："+(file.exists()));

                FileInputStream fis = new FileInputStream(file);
                //插桩
                ClassReader cr = new ClassReader(fis);
                // 写出器
                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

                //分析，处理结果写入cw
                cr.accept(new ClassInjectTimeVisitor(cw, file.getName(),mtConfig), ClassReader.EXPAND_FRAMES);

                byte[] newClassBytes = cw.toByteArray();
                //class文件绝对地址
                String absolutePath = file.getAbsolutePath();
                //class文件绝对地址去掉目录，得到全类名
                String fullClassPath = absolutePath.replace(dir, "");
                File outFile = new File(dest, fullClassPath);

                mkdirs(outFile.getParentFile());

                FileOutputStream fos = new FileOutputStream(outFile);
                fos.write(newClassBytes);
                fos.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


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
     *
     * @return
     */
    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.PROJECT_ONLY;
    }


    /**
     * 是否增量更新
     *
     * @return
     */
    @Override
    public boolean isIncremental() {
        return false;
    }
}
