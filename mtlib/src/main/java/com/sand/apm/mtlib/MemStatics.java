package com.sand.apm.mtlib;

/**
 * @ProjectName: APM_MT
 * @Date: 2022/4/28
 * @Desc:
 */
public class MemStatics {

    public long javaHeap;//已经使用的java堆，不代表分配的总内存
    public long nativeheap;//已经使用的native堆，不带便分配的总内存
    public long totalAllocate;//已经分配的总内存（java+native）
    public int power;

    public MemStatics(long javaHeap, long nativeheap, long totalAllocate) {
        this.javaHeap = javaHeap;
        this.nativeheap = nativeheap;
        this.totalAllocate = totalAllocate;
        this.power= PowerHelper.getBatter(Statistics.autoInit());
    }
}