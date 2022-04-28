package com.apm.tools;

/**
 * @ProjectName: APM_MT
 * @Date: 2022/4/26
 * @Desc: memInfo
 */
public class MemInfo {

    public String stringInfo;
    public long usedMem;

    public MemInfo(String stringInfo, long usedMem) {
        this.stringInfo = stringInfo;
        this.usedMem = usedMem;
    }

    public String getStringInfo() {
        return stringInfo;
    }

    public void setStringInfo(String stringInfo) {
        this.stringInfo = stringInfo;
    }

    public long getUsedMem() {
        return usedMem;
    }

    public void setUsedMem(long usedMem) {
        this.usedMem = usedMem;
    }
}