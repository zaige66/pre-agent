package com.ayg.tools.exec.timer.cmds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 指令类
 */
public class Commond {
    private static final String COMMOND_SP = "\\|\\|";
    private static final String COMMONDDETAIL_SP = "\\$";
    private static final String DDETAIL_SP = ",";

    /**
     * 指令集
     */
    public static String PRINT_SQL = "sql"; // 打印sql语句
    public static String PRINT_EXECTIME = "execTime";// 打印执行时间
    public static String PRINT_ARGS = "args"; // 打印方法请求参数
    public static String PRINT_RET = "return"; // 打印方法返回值

    /**
     * 类名
     */
    private String className;
    /**
     * 命令名
     */
    private List<String> name = new ArrayList<>();

    private List<String> method = new ArrayList<>();

    public Commond() {
    }

    public Commond(String className, List<String> method,List<String> name) {
        this.className = className;
        this.method = method;
        this.name = name;
    }

    public List<String> getMethod() {
        return method;
    }

    public void setMethod(List<String> method) {
        this.method = method;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<String> getName() {
        return name;
    }

    public void setName(List<String> name) {
        this.name = name;
    }

    public static List<Commond> buildCommonds(String args){
        List<Commond> retList = new ArrayList<>();

        if (null == args || "".equals(args)){
            return retList;
        }
        String[] sps = args.split(COMMOND_SP);

        for (String sp : sps) {
            if (sp.equals(PRINT_SQL)){
                retList.add(new Commond("com/mysql/jdbc/PreparedStatement",Arrays.asList(new String[]{"execute"}), Arrays.asList(new String[]{"sql"})));
                continue;
            }

            Commond commond = new Commond();
            String[] split = sp.split(COMMONDDETAIL_SP);
            commond.setClassName(split[0].replace(".","/"));
            commond.setMethod(new ArrayList<>());
            if (!"".equals(split[1])){
                commond.setMethod(Arrays.asList(split[1].split(DDETAIL_SP)));
            }
            commond.setName(Arrays.asList(split[2].split(DDETAIL_SP)));
            retList.add(commond);
        }

        return retList;
    }

}
