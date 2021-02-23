package com.ayg.tools.exec.timer.cmds;


public interface IExeCmd {
    String COM = ",";
    String DOLLAR = "\\$";

    /**
     *  @Description:是否执行计算执行时长
     */
     boolean execTime(ExecParam execParam);

     void printInit();

}
