package com.apm.tools;

import java.util.HashMap;

/**
 * @ProjectName: APM_MT
 * @Date: 2022/4/28
 * @Desc: Statistics tool
 */
public class Statistics {

    public static HashMap<String, Action> statisMap = new HashMap<>();

    public static void start(String key, Action action) {
        statisMap.put(key, action);
    }


    public static void close(String key) {
        Action action = statisMap.get(key);
        if (null != action) {
            action.close();

        } else {
            //error

        }

    }

    public static void dumps(){


    }

}