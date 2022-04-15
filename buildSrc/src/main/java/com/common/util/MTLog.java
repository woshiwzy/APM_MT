package com.common.util;

import java.math.BigInteger;
import java.security.MessageDigest;

public class MTLog {


    public static void log(String content) {
        System.out.println("MT:" + content);
    }

    public static void redLog(String content) {
        System.err.println(ColorLog.red("MT:" + content));
    }

    public static void purpLog(String content) {
        System.out.println(ColorLog.purple("MT:" + content));
    }

    public static void yellowLog(String content) {
        System.out.println(ColorLog.yellow("MT:" + content));
    }

    public static void orangeLog(String content) {
        System.out.println(ColorLog.orange("MT:" + content));
    }

    public static void blueLog(String content) {
        System.out.println(ColorLog.blue("MT:" + content));
    }

    public static void greenLog(String content) {
        System.out.println(ColorLog.green("MT:" + content));
    }


    /**
     * @param input 参数字符串
     * @return 生成的hash值
     * @author Bean_bag
     * @description 进行Hash运算
     */
    public static String generateHash(String input) {
        try {
            //参数校验
            if (null == input) {
                return null;
            }
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(input.getBytes());
            byte[] digest = md.digest();
            BigInteger bi = new BigInteger(1, digest);
            String hashText = bi.toString(16);
            while (hashText.length() < 32) {
                hashText = "0" + hashText;
            }
            return hashText;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
