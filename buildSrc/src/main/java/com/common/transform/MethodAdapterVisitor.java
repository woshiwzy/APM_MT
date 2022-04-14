package com.common.transform;

import com.common.plug.MTConfig;
import com.common.util.MTLog;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;


class MethodAdapterVisitor extends AdviceAdapter {

    private String className;
    private String methodName;
    private int index;
    private int start, end;
    private MTConfig mtConfig;

    protected MethodAdapterVisitor(MethodVisitor mv, int access, String name, String desc, String className, MTConfig mtConfig) {
        super(Opcodes.ASM5, mv, access, name, desc);
        methodName = name;
        this.className = className;
        this.mtConfig = mtConfig;
    }


    @Override
    public void visitLineNumber(int line, Label start) {
        super.visitLineNumber(line, start);
    }

    @Override
    public void visitInsn(int opcode) {
        super.visitInsn(opcode);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return super.visitAnnotation(desc, visible);
    }


    @Override
    protected void onMethodEnter() {
        MTLog.redlog("方法进入:onMethodEnter " + methodName);
        //储备本地变量备用
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
        index = newLocal(Type.LONG_TYPE);
        start = index;
        mv.visitVarInsn(LSTORE, start);

    }


    @Override
    protected void onMethodExit(int opcode) {
        MTLog.redlog("方法进入:onMethodExit " + methodName);
        mv.visitVarInsn(LLOAD, start);
        String owner = this.mtConfig.mtCallBackPackage.replace(".", "/") + "/" + MTConfig.MTClassname;
        mv.visitMethodInsn(INVOKESTATIC, owner, MTConfig.MTMethod, "(J)V", false);

    }
}