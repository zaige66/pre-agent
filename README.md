## Java Agent实现无代码侵入方法执行时长、方法返回值、方法参数、方法执行sqk打印方案

## 写在前面
感谢 [YorkHwang](https://github.com/YorkHwang)在哔哩哔哩上传的视频，该项目是参考其项目[exec-timer](https://github.com/YorkHwang/exec-timer)，想法都是基于此项目

### 一、使用方法

- 获取agent包
      方式一：直接下载jar：[exec-timer.jar](https://github.com/YorkHwang/exec-timer/blob/master/jar/exec-timer.jar)
	
	方式二：自己打包: mvn clean package
	在target目录下将生成对应的exec-timer.jar
- 功能说明
```
多个指令与多个方法间用「,」分隔
```
|功能名|指令说明|
|---|---|
|sql|打印sql语句，该功能无需配置类名与方法名，详细调用方面见例子|
|execTime|打印方法执行时间|
|args|打印方法请求参数|
|return|打印方法返回值|

VM加上如下参数
-javaagent:exec-timer.jar全路径=包名或者类名$方法名，方法名，也可以一个都不填$功能名，功能名||包名或者类名$方法名，方法名，也可以一个都不填$功能名，功能名

- 调用案例
    - com.a.b.c 包下所有类的所有方法 执行的sql 与 方法执行时间
    ```
    -javaagent:target/exec-timer.jar=com.a.b.c$$sql,execTime
    ```
    - com.a.b.c.D.java 类的所有方法 执行的sql 与 方法执行时间
    ```
    -javaagent:target/exec-timer.jar=com.a.b.c.D$$sql,execTime
    ```
    - com.a.b.c.D.java 类的f方法 执行打印方法请求参数
    ```
    -javaagent:target/exec-timer.jar=com.a.b.c.D$f$sql,execTime
    ```

 ## 注意事项
 - 当出现如下报错时
    ```java
    java.lang.IllegalArgumentException
        at org.objectweb.asm.ClassReader.<init>(Unknown Source)
        at org.objectweb.asm.ClassReader.<init>(Unknown Source)
        at com.ayg.tools.exec.timer.ExecTimerTransformer.transform(ExecTimerTransformer.java:57)
        at sun.instrument.TransformerManager.transform(TransformerManager.java:188)
        at sun.instrument.InstrumentationImpl.transform(InstrumentationImpl.java:428)
        at java.lang.ClassLoader.defineClass1(Native Method)
        at java.lang.ClassLoader.defineClass(ClassLoader.java:763)
        at java.security.SecureClassLoader.defineClass(SecureClassLoader.java:142)
        at java.net.URLClassLoader.defineClass(URLClassLoader.java:468)
        at java.net.URLClassLoader.access$100(URLClassLoader.java:74)
        at java.net.URLClassLoader$1.run(URLClassLoader.java:369)
        at java.net.URLClassLoader$1.run(URLClassLoader.java:363)
        at java.security.AccessController.doPrivileged(Native Method)
        at java.net.URLClassLoader.findClass(URLClassLoader.java:362)
        at java.lang.ClassLoader.loadClass(ClassLoader.java:424)
        at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:349)
        at java.lang.ClassLoader.loadClass(ClassLoader.java:357)
        at java.lang.Class.forName0(Native Method)
    ```
   需要在项目中引入如下依赖
   ```pom
           <dependency>
               <groupId>org.ow2.asm</groupId>
               <artifactId>asm-all</artifactId>
               <version>5.0.4</version>
           </dependency>
   ```
   spring中cglib 引入的依赖是如下包，与本项目需要的包不一致，所以需要单独引入
   ```
        <dependency>
          <groupId>org.ow2.asm</groupId>
          <artifactId>asm</artifactId>
          <version>4.2</version>
        </dependency>
   ```
    

