package com.sand.apm.mt;

public class Test {

    public static void method1()  {

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("method1");

    }

    public static void method2(){

        System.out.println("method2");

    }

}
