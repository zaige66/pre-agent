package com.ayg.tools.exec.timer;


import com.alibaba.fastjson.JSON;
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
    public static void printArgs(String className,String methodName,Object[] obj){
        System.out.println("方法参数：" + className + "#" + methodName + " args：" + JSON.toJSONString(obj));
    }


    /**
     * 打印返回值
     * @param ret
     */
    public static void printRet(Object ret){
        System.out.println("方法返回值：" + JSON.toJSON(ret));
    }

    /**
     * 打印sql语句
     * @param sql
     */
    public static void printJdbcSql(String sql){
        System.out.println("---------------------------");
        System.out.println("    " + sql.replace("\r","").replace("\n","").replace("\r\n",""));
        System.out.println("---------------------------");
    }


    /**
     * 打印执行时间
     * @param className
     * @param methodName
     * @param methodDesc
     */
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
