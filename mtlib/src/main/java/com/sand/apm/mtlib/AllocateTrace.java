package com.sand.apm.mtlib;

/**
 * @ProjectName: APM_MT
 * @Date: 2022/4/29
 * @Desc:
 */
public class AllocateTrace {

    public String claz;
    public int size;
    public int count;

    public AllocateTrace(String claz, int size) {
        this.claz = claz;
        this.size = size;
    }

    public void increate() {
        count++;
    }


}

