package com.common.util;

/**
 * @ProjectName: frame_work_common
 * @Date: 2022/4/14
 * @Desc: 为控制输出添加颜色
 */
public class ColorLog {

    public static int RED_COLOR=31;
    public static int YELLOW_COLOR=32;
    public static int ORANGE_COLOR=33;
    public static int BLUE_COLOR=34;
    public static int PURPLE_COLOR=35;
    public static int GREEN_COLOR=36;


    /**
     * @param colour  颜色代号：背景颜色代号(41-46)；前景色代号(31-36)
     * @param type    样式代号：0无；1加粗；3斜体；4下划线
     * @param content 要打印的内容
     */
    private static String getFormatLogString(String content, int colour, int type) {
        boolean hasType = type != 1 && type != 3 && type != 4;
        if (hasType) {
            return String.format("\033[%dm%s\033[0m", colour, content);
        } else {
            return String.format("\033[%d;%dm%s\033[0m", colour, type, content);
        }
    }

    public static String red(String log){
        return getFormatLogString(log, 31, 0);
    }
    public static String yellow(String log){
        return getFormatLogString(log, 32, 1);
    }
    public static String orange(String log){
        return getFormatLogString(log, 33, 0);
    }
    public static String blue(String log){
        return getFormatLogString(log, 34, 0);
    }
    public static String purple(String log){
        return getFormatLogString(log, 35, 0);
    }

    public static String green(String log){
        return getFormatLogString(log, 36, 0);
    }





//    public static void main(String[] args) {
//        System.out.println("控制台颜色测试：");
//        System.out.println(getFormatLogString("[ 红色 ]", 31, 0));
//        System.out.println(getFormatLogString("[ 黄色 ]", 32, 0));
//        System.out.println(getFormatLogString("[ 橙色 ]", 33, 0));
//        System.out.println(getFormatLogString("[ 蓝色 ]", 34, 0));
//        System.out.println(getFormatLogString("[ 紫色 ]", 35, 0));
//        System.out.println(getFormatLogString("[ 绿色 ]", 36, 0));
//    }
}
