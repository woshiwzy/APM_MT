 **一个简单的APM框架，对指定的路径下的方法进行插装，统计方法执行时间,为APM 提供数据参考.**

 
 **该项目分为两部分** 

 **Android部分** ：主要通过插装的方法收集对应方法的执行时间，执行线程，可用内存等信息，然后生成一个文件 

 **Python 部分** ：利用生成的文件生成友好的可视图，方便分析对比数据 (这部分还未开始开发)

APM(意思是Android 性能优化)，MT（Method Trace）
