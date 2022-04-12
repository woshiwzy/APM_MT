package com.common.util;

import java.math.BigInteger;
import java.security.MessageDigest;

public class Util {


    public static void redlog(String content) {
        System.err.println(content);
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
