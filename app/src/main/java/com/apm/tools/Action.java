package com.apm.tools;

/**
 * @ProjectName: APM_MT
 * @Date: 2022/4/28
 * @Desc: define the mem action
 */
public class Action {

    private String methodName;
    private long start;
    private long cost;

    private MemStatics startMem;//开始时内存情况
    private MemStatics endMem;//结束时内存情况


    /**
     * call on method start
     * @param methodName
     * @param start
     * @return
     */
    public static Action createFromStart(String methodName,long start){
        Action action=new Action();
        action.setMethodName(methodName);
        action.setStart(start);

        action.setStartMem(new MemStatics(MTHelper.getJavaHeap2(),MTHelper.getNativeHeap2(),MTHelper.getAppUseMem()));

        return action;
    }

    /**
     * call on method done
     * @return
     */
    public Action close(){
        this.setEndMem(new MemStatics(MTHelper.getJavaHeap2(),MTHelper.getNativeHeap2(),MTHelper.getAppUseMem()));
        this.cost=System.currentTimeMillis()-start;
        return this;
    }


    @Deprecated
    public static Action create(int actionType,String methodName,long start){

        Action action=new Action();
        action.setMethodName(methodName);
        action.setStart(start);
        action.setCost(System.currentTimeMillis()-start);

        return action;

    }



    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public String getKey(){
        return  methodName+"_"+start;
    }

    public static String createKey(String methodName,long start){
        return methodName+"_"+start;
    }

    public MemStatics getStartMem() {
        return startMem;
    }

    public void setStartMem(MemStatics startMem) {
        this.startMem = startMem;
    }

    public MemStatics getEndMem() {
        return endMem;
    }

    public void setEndMem(MemStatics endMem) {
        this.endMem = endMem;
    }

    @Override
    public String toString() {
        return "Action{" + " methodName='" + methodName + '\'' + ", start=" + start + ", cost=" + cost + '}';
    }
}


