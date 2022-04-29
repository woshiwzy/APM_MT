package com.apm.tools;

import java.util.HashMap;

/**
 * @ProjectName: APM_MT
 * @Date: 2022/4/29
 * @Desc:
 */
public class AllocateMap {


    public static HashMap<String, AllocateTrace> allocateTraceHashMap = new HashMap<>();

    public static void count(String claz, int size) {
        AllocateTrace allocateTrace = allocateTraceHashMap.get(claz);
        if (null == allocateTrace) {
            allocateTrace = new AllocateTrace(claz, size);
            allocateTraceHashMap.put(claz, allocateTrace);
        } else {
            allocateTrace.increate();
        }

    }

    public static void dump() {


    }
}