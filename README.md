 **一个简单的APM Gradle插件，对指定的路径下的方法进行插装，统计方法执行时间,为APM 提供数据参考.**
 ** 效果图如下
 
 
 ![效果图](./mpt1.png)
 
 
 **该项目分为两部分** 

 **Android部分** ：主要通过插装的方法收集对应方法的执行时间，执行线程，可用内存等信息，然后生成一个文件 

 **Python 部分** ：利用生成的文件生成友好的可视图，方便分析对比数据 (这部分还未开始开发)

APM(意思是Android 性能优化)，MT（Method Trace）

# =================================================================================

# 快速使用
### 1.在根项目build.gradle文件中添加我的maven仓库地址，并设置插件依赖

```
// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        //1.我的maven 私有库地址
        maven {
            allowInsecureProtocol = true
            url 'http://161.117.195.45:6677/repository/sand_repo/'
        }
        google()
        mavenCentral()
        maven { url 'https://dl.google.com/dl/android/maven2/' }
        maven { url 'https://www.jitpack.io' }
        jcenter()
        maven { url 'https://jcenter.bintray.com' }

    }
    dependencies {
        classpath "com.android.tools.build:gradle:7.0.4"
        classpath "com.sand.group:mt:1.0.2" //2.依赖插件
    }
}
```
### 2.在app的build.gradle 中应用插件，并配置插件参数
```
plugins {
    id 'com.android.application'
    id 'com.sand.mt'//应用插件
}



//MTConfig 配置的参数，供插件调用
MTConfig {
    //配置自动生成MTCallBack类锁在的包路径
    mtCallBackPackage="com.mt.autogen.callback"
    //配置哪些包下面的类需要被插桩（这些包下面的方法都会被插桩）
    //这样的配置意味着com.sand.apm.mtdemo包下面的类，除去黑名单,excludeClasses,excludeMethods
    //的方法都会被插桩，如果某个类插桩后，运行时出现class not found
    //请把它的类名添加到excludeClasses中
    pkgs = [
            "com.sand.apm.mtdemo"
    ]
    whiteList = []//白名单这里的方法都插桩
    blackList = []//黑名单这里的方法都不插桩
    excludeMethods = [//这些方法都不插桩
                      "<init>", //构造方法不插桩
                      "<clinit>" //静态域的构造器不插桩
    ]
    excludeClasses = [//这里的类都不需要插桩,直接写类文件名
            "BuildConfig.class",
            "Tool.class"
    ]
}

```

**如果某个类插桩后，运行时出现class not found 请把它的类名添加到excludeClasses中**

**如果某个类插桩后，运行时出现class not found 请把它的类名添加到excludeClasses中**

**如果某个类插桩后，运行时出现class not found 请把它的类名添加到excludeClasses中**

配置好这些参数后执行同步,make,不出意外的话，你将会在src/main/java 下面看到自动生成的代码，
（如果一次不成功多试验几次，实在不行欢迎留言）


> **make 之后插件会在gradle.properties 文件中生成一个mt.work变量，如果你想关闭插件，配置其为false即可**
