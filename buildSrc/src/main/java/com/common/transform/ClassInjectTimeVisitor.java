package com.common.transform;


import com.common.plug.MTConfig;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


class ClassInjectTimeVisitor extends ClassVisitor {

    private String className;
    private MTConfig mtConfig;


    ClassInjectTimeVisitor(ClassVisitor cv, String fileName, MTConfig mtConfig) {
        super(Opcodes.ASM5, cv);
        this.className = fileName.substring(0, fileName.lastIndexOf("."));
        this.mtConfig=mtConfig;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
//        return mv;
        return new MethodAdapterVisitor(mv, access, name, desc, className,mtConfig);
    }

}