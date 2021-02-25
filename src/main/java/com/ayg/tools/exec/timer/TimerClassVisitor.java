package com.ayg.tools.exec.timer;

import com.ayg.tools.exec.timer.cmds.ExecParam;
import com.ayg.tools.exec.timer.cmds.IExeCmd;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 *  @Description: 定义计数器扫描待修改类的visitor，本质就是访问者模式
 */
public class TimerClassVisitor extends ClassVisitor {
    private String className;
    private String annotationDesc;
    private IExeCmd exeCmd;

    public TimerClassVisitor(ClassVisitor cv, String className, IExeCmd exeCmd) {
        super(Opcodes.ASM5, cv);
        this.className = className;
        this.exeCmd = exeCmd;
    }


    /**
     * 访问注解：将访问一个个类的注解
     */
    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        this.annotationDesc = desc;
        return super.visitAnnotation(desc, visible);
    }



    /**
     * 访问方法：将访问一个个方法
     */
    @Override
    public MethodVisitor visitMethod(int access, final String name, final String desc, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if (mv == null || name.equals("<init>") ||name.equals("<clinit>")) {
            return mv;
        }

        if(className == null){
            return mv;
        }

        //判断无需执行
        if(!exeCmd.execTime(ExecParam.Vf(className.replace("/", "."), annotationDesc, name, ""))){
            return mv;
        }

        final String key = className + name + desc;
        return new AdviceAdapter(Opcodes.ASM5, mv, access, name, desc) {
            //方法进入时获取开始时间
            @Override public void onMethodEnter() {
                // 判断是否为静态方法，非静态方法中局部变量第一个值是this，静态方法是第一个入参参数
                boolean isStaticMethod = 0 != (access & ACC_STATIC);
                
                List<String> tag = getTag(desc);
                int parameterCount = tag.size();
                // 创建数组对象
                if (parameterCount >= 6) {
                    // 将单字节的常量值(-128~127)推送至栈顶
                    mv.visitVarInsn(BIPUSH, parameterCount);//初始化数组长度
                } else {
                    switch (parameterCount) {
                        case 1:
                            // 将int型1推送至栈顶
                            mv.visitInsn(ICONST_1);
                            break;
                        case 2:
                            mv.visitInsn(ICONST_2);
                            break;
                        case 3:
                            mv.visitInsn(ICONST_3);
                            break;
                        case 4:
                            mv.visitInsn(ICONST_4);
                            break;
                        case 5:
                            mv.visitInsn(ICONST_5);
                        default:
                            mv.visitInsn(ICONST_0);
                    }
                }
                // 创建一个引用型（如类，接口，数组）的数组，并将其引用值压入栈顶
                mv.visitTypeInsn(ANEWARRAY, Type.getDescriptor(Object.class));
                // 给数组填充值，非静态方法变量表第一个参数为包名，静态方法没这个变量，所以静态方法从0开始取，非静态方法从1开始取
                int cursor = isStaticMethod ? 0 : 1;
                for (int i = 0; i < parameterCount; i++) {
                    // 复制栈顶数值并将复制值压入栈顶
                    mv.visitInsn(DUP);
                    // 将单字节的常量值(-128~127)推送至栈顶
                    if (i > 5) {
                        mv.visitVarInsn(BIPUSH, i);
                    } else {
                        switch (i) {
                            case 0:
                                mv.visitInsn(ICONST_0);
                                break;
                            case 1:
                                mv.visitInsn(ICONST_1);
                                break;
                            case 2:
                                mv.visitInsn(ICONST_2);
                                break;
                            case 3:
                                mv.visitInsn(ICONST_3);
                                break;
                            case 4:
                                mv.visitInsn(ICONST_4);
                                break;
                            case 5:
                                mv.visitInsn(ICONST_5);
                                break;
                        }
                    }

                    String type = tag.get(i);
                    if ("Z".equals(type)) {
                        mv.visitVarInsn(ILOAD, cursor++);  //获取对应的参数
                        mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Boolean.class), "valueOf", "(Z)Ljava/lang/Boolean;", false);
                    } else if ("C".equals(type)) {
                        mv.visitVarInsn(ILOAD, cursor++);  //获取对应的参数
                        mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Character.class), "valueOf", "(C)Ljava/lang/Character;", false);
                    } else if ("B".equals(type)) {
                        mv.visitVarInsn(ILOAD, cursor++);  //获取对应的参数
                        mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Byte.class), "valueOf", "(B)Ljava/lang/Byte;", false);
                    } else if ("S".equals(type)) {
                        mv.visitVarInsn(ILOAD, cursor++);  //获取对应的参数
                        mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Short.class), "valueOf", "(S)Ljava/lang/Short;", false);
                    } else if ("I".equals(type)) {
                        mv.visitVarInsn(ILOAD, cursor++);  //获取对应的参数
                        mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Integer.class), "valueOf", "(I)Ljava/lang/Integer;", false);
                    } else if ("F".equals(type)) {
                        mv.visitVarInsn(FLOAD, cursor++);  //获取对应的参数
                        mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Float.class), "valueOf", "(F)Ljava/lang/Float;", false);
                    } else if ("J".equals(type)) {
                        mv.visitVarInsn(LLOAD, cursor++);  //获取对应的参数
                        mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Long.class), "valueOf", "(J)Ljava/lang/Long;", false);
                        cursor++;
                    } else if ("D".equals(type)) {
                        mv.visitVarInsn(DLOAD, cursor++);  //获取对应的参数
                        mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Double.class), "valueOf", "(D)Ljava/lang/Double;", false);
                        cursor++;
                    } else {
                        mv.visitVarInsn(ALOAD, cursor++);  //获取对应的参数
                    }
                    // 将栈顶引用型数值存入指定数组的指定索引位置
                    mv.visitInsn(AASTORE);
                }

                //int arrIndex = newLocal(Type.LONG_TYPE);
                //System.out.println(arrIndex);
                int arrIndex = parameterCount + 1;
                // 将栈顶引用型数值存入第几个本地变量
                mv.visitVarInsn(ASTORE, arrIndex);


                // 打印方法参数值
                this.visitVarInsn(ALOAD, arrIndex);
                this.visitMethodInsn(Opcodes.INVOKESTATIC, "com/ayg/tools/exec/timer/ExecTime", "printSql", "([Ljava/lang/Object;)V", false);

                // 读入this，执行本类的其他方法
                this.visitVarInsn(ALOAD,0);
                this.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/ayg/tools/Entity", "inject", "()Ljava/lang/String;", false);

                arrIndex = arrIndex + 1;
                this.visitVarInsn(ASTORE, arrIndex);
                this.visitVarInsn(ALOAD, arrIndex);
                this.visitMethodInsn(Opcodes.INVOKESTATIC, "com/ayg/tools/exec/timer/ExecTime", "printRet", "(Ljava/lang/String;)V", false);

                // 记录方法开始的时间戳
                this.visitLdcInsn(key);
                this.visitMethodInsn(Opcodes.INVOKESTATIC, "com/ayg/tools/exec/timer/ExecTime", "start", "(Ljava/lang/String;)V", false);
            }

            //方法退出时获取结束时间并计算执行时间
            @Override public void onMethodExit(int opcode) {
                this.visitLdcInsn(key);
                this.visitMethodInsn(Opcodes.INVOKESTATIC, "com/ayg/tools/exec/timer/ExecTime", "end", "(Ljava/lang/String;)V", false);
                //向栈中压入类名称
                this.visitLdcInsn(className);
                //向栈中压入方法名
                this.visitLdcInsn(name);
                //向栈中压入方法描述
                this.visitLdcInsn(desc);
                this.visitMethodInsn(Opcodes.INVOKESTATIC, "com/ayg/tools/exec/timer/ExecTime", "execTime", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
                        false);
            }
        };
    }

    /**
     * 将变量描述符处理为数组
     * @param desc
     * @return
     */
    public List<String> getTag(String desc) {
        List<String> retVal = new ArrayList<>();

        Matcher m = Pattern.compile("(L.*?;|\\[{0,2}L.*?;|[ZCBSIFJD]|\\[{0,2}[ZCBSIFJD]{1})").matcher(desc.substring(0, desc.lastIndexOf(')') + 1));

        while (m.find()) {
            String block = m.group(1);
            retVal.add(block);
        }

        return retVal;
    }

}
