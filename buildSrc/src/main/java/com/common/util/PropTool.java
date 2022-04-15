package com.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

/**
 * @ProjectName: APM_MT
 * @Date: 2022/4/15
 * @Desc:
 */
public class PropTool {

    /**
     * 读取proproties文件配置的属性
     * @param propertiesFile
     * @return
     */
    public static HashMap<String,String> readProperties(File propertiesFile){
        Properties pro = new Properties();
        FileInputStream in = null;
        HashMap<String,String> propMap=new HashMap<>(10);
        try {
            in = new FileInputStream(propertiesFile);
            pro.load(in);
            Set<String> propSets = pro.stringPropertyNames();
            for(String propName :propSets){
                propMap.put(propName,pro.getProperty(propName));
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return propMap;
    }

    public static boolean writeProper2File(HashMap<String,String> promap,File proFile){


        return false;
    }


}