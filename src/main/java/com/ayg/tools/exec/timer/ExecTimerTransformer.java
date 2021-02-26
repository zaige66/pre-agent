package com.ayg.tools.exec.timer;

import com.ayg.tools.exec.timer.cmds.Commond;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Description:方法执行时长字节码转换器
 * @Author: York.Hwang
 * @Date: 2020/2/15 23:49
 */
public class ExecTimerTransformer implements ClassFileTransformer {


    private List<Commond> commondList;
    private Map<String,Commond> targetClassListMap = new HashMap<>();

    public ExecTimerTransformer(List<Commond> commondList ) {
        super();
        this.commondList = commondList;
        for (Commond commond : commondList) {
            targetClassListMap.put(commond.getClassName(),commond);
        }
    }


    /**
     * @Description:覆写转换方法
     * 参数说明
     * loader: 定义要转换的类加载器，如果是引导加载器，则为null
     * className:完全限定类内部形式的类名称和中定义的接口名称，例如"java.lang.instrument.ClassFileTransformer"
     * classBeingRedefined:如果是被重定义或重转换触发，则为重定义或重转换的类；如果是类加载，则为 null
     * protectionDomain:要定义或重定义的类的保护域
     * classfileBuffer:类文件格式的输入字节缓冲区（不得修改，一个格式良好的类文件缓冲区（转换的结果），如果未执行转换,则返回 null。
     */
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {

        // 如果不是需要修改的类
        Commond commond = findNeedInject(className);
        if (commond == null){
            return classfileBuffer;
        }

        try {
            //第一步：读取类的字节码流
            ClassReader reader = new ClassReader(classfileBuffer);
            //第二步：创建操作字节流值对象，ClassWriter.COMPUTE_MAXS:表示自动计算栈大小
            ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);
            //第三步：接受一个ClassVisitor子类进行字节码修改
            reader.accept(new TimerClassVisitor(writer, className, commond), ClassReader.EXPAND_FRAMES);
            //第四步：返回修改后的字节码流
            byte[] bytes = writer.toByteArray();

            // 将修改后的字节码临时输出到文件。仅调试用
            try {
                if (className.contains("Entity")) {
                    FileOutputStream fileOutputStream = new FileOutputStream("/Users/kangxuan/self_workspace/exec-timer/target/test.class");
                    fileOutputStream.write(bytes);
                    fileOutputStream.flush();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return bytes;
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }

    private Commond findNeedInject(String className) {
        // 代理类不再进行增强，只增强原始类
        if (className.contains("$")){
         return null;
        }

        Set<String> strings = targetClassListMap.keySet();
        for (String string : strings) {
            if (className.startsWith(string)){
                return targetClassListMap.get(string);
            }
        }
        return null;
    }


}  
