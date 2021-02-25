package com.ayg.tools.exec.timer;


import com.ayg.tools.Entity;
import com.mysql.jdbc.PreparedStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * @Description： 执行时长记录
 */
public class ExecTime {

    private static final Logger LOG = LoggerFactory.getLogger(ExecTime.class);

    private static final ThreadLocal<Map<String, Long>> costTimeLocal = new ThreadLocal<Map<String, Long>>();
    private static final String ST_FIX = "ST_"; //开始时间
    private static final String ET_FIX = "ET_"; //结束时间

    private ExecTime() {
        //just for private
    }

    public static void start(String key) {
        try {
            Map<String, Long> map = costTimeLocal.get();
            if (map == null) {
                map = new HashMap<>();
                costTimeLocal.set(map);
            }
            map.put(ST_FIX + key, System.currentTimeMillis());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    public static void end(String key) {
        try {
            costTimeLocal.get().put(ET_FIX + key, System.currentTimeMillis());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * 打印参数
     * @param obj
     */
    public static void printVar(Object[] obj){
        System.out.println(Arrays.toString(obj));
    }


    public static void printRet(String ret){
        System.out.println(ret);
    }


    /**
     * 打印sql信息
     * @param obj
     */
    public static void printSql(Object[] obj){
        try {
            /*Object o = obj[0];
            Object target = AopTargetUtil.getTarget(o);
            System.out.println(target);
            com.mysql.jdbc.PreparedStatement statement = (PreparedStatement) target;


            System.out.println("------------------------------------------");
            System.out.println();
            System.out.println("      " + statement.asSql());*/
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
            System.out.println("------------------------------------------");

    }


    public static void execTime(String className, String methodName, String methodDesc) {
        try {
            String key = className + methodName + methodDesc;
            long costTime = costTimeLocal.get().get(ET_FIX + key) - costTimeLocal.get().get(ST_FIX + key);
            System.out.println(className.replace("/", ".") + "." + methodName + " cost:" + costTime + "ms");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
