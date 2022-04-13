package com.myapm.callback;



/*
*这个类是MT插件自动生成的，done方法将被注入到方法类中

*start参数是对应方法开始的时间，done将会在方法结束时刻调用

*你可以在此处统计方法执行时间或者App运行时信息

*注意:如果你删除这个类，将会自动创建
*/

import com.orhanobut.logger.Logger;

public class MTCallBack{

   public static void done(long start){
      long end=System.currentTimeMillis();
      long cost=end-start;

      Logger.e("耗时:"+cost);
   }

}