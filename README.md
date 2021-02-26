## Java Agent实现无代码侵入方法执行时长打印方案

## 写在前面
感谢 [YorkHwang](https://github.com/YorkHwang)在哔哩哔哩上传的视频，该项目是参考其项目[exec-timer](https://github.com/YorkHwang/exec-timer)，想法都是基于此项目

### 一、使用方法

- 获取agent包
      方式一：直接下载jar：[exec-timer.jar](https://github.com/YorkHwang/exec-timer/blob/master/jar/exec-timer.jar)
	
	方式二：自己打包: mvn clean package
	在target目录下将生成对应的exec-timer.jar
- 测试用例

VM加上如下参数

-javaagent:target/exec-timer.jar=@M-com.ayg.tools.test.AppTest$testApp||@C-com.ayg.tools.test.AppTest

执行测试用例 com.ayg.tools.test.AppTest.testApp()

- Jar启动方式

javar -javaaget:[exec-timer.jar全路径]=@M|C|P-包全名|类全名$方法1,方法2...方法N -jar [可执行Jar的全路径]

示例：
java -javaagent:/code/open/exec-timer/target/exec-timer.jar=@P-com.ayg.contract.service -jar contract-web.jar

- 命令说明

a.指定类方法打印执行时长： __@M-类全名$方法1,方法2__ 
示例：@M-com.ayg.contract.service.ContractService$addContract,updateContract

b.指定类所有方法打印执行时长： __@C-类全名__ 
示例：@C-com.ayg.contract.service.ContractService

c.指定包下所有类所有方法打印执行时长： __@P-包名__ 
示例：@M-com.ayg.contract.service

 __多个命令用双竖线||间隔__ 
 
 
 
 
 # 打印方法参数
 
 ## 注意事项
 当int取值 -1~5 时，JVM采用iconst指令将常量压入栈中。
 当int取值 -128~127 时，JVM采用 bipush 指令将常量压入栈中。
 
这两个不可乱用
 




