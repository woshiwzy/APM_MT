package com.common.plug;



import java.util.Set;

public class MTConfig {

    public Set<String> pkgs;//以这个包名开始的话，会被插桩
    public Set<String> excludeMethods;//这个集合里面的方法不插桩
    public Set<String> excludeClasses;//这个集合里面的class 不插桩
    public Set<String> whiteList;//白名单里面的
    public Set<String> blackList;//黑名单中不需要插桩

    public boolean needPlug(String fullPathClass){
        if(null==fullPathClass){
            return false;
        }

        //在排除集合中一定不要插桩
        if(isClassInSet(excludeClasses,fullPathClass)){
            return false;
        }

        //黑名单中一定不会插桩
        if(isClassInSet(blackList,fullPathClass)){
            return false;
        }

        //在白名单中一定会插桩
        if(isClassInSet(whiteList,fullPathClass)){
            return true;
        }

        //如果以指定的报名开始,会被插桩
//        Util.redlog("full path repalce1:"+fullPathClass);
        String prefex1="intermediates\\javac\\debug\\classes\\";//debug class路径前缀
        String classPackPatch=fullPathClass.substring(fullPathClass.lastIndexOf(prefex1)+prefex1.length()).replace("\\",".");
//        Util.redlog("classPackPatch:"+classPackPatch);

        for(String pkg:pkgs){
            if(classPackPatch.startsWith(pkg)){
                return true;
            }
        }

        return false;
    }



    private boolean isClassInSet(Set<String> sets,String fullClassName){
        if(null==sets || null==fullClassName){
            return false;
        }
        return sets.contains(getClassName(fullClassName));
    }


    private String getClassName(String fullPathClass){
        if(null==fullPathClass){
            return "";
        }
        String className=fullPathClass.substring(fullPathClass.lastIndexOf("\\")+1);
        return className;
    }

}
