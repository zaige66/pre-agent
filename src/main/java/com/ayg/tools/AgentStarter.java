package com.ayg.tools;


import com.ayg.tools.exec.timer.ExecTimerTransformer;
import com.ayg.tools.exec.timer.cmds.Commond;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;

import java.util.Arrays;
import java.util.List;


/**
 *  @Description:代理程序入口类
 */
public class AgentStarter {

    private static final Logger LOG = LoggerFactory.getLogger(AgentStarter.class);

    public static void premain(String args, Instrumentation instrumentation) {
        LOG.info("执行时长计数器开启,参数{}", args);
        try {
            List<Commond> commondList = Commond.buildCommonds(args);
            //添加字节码转换器
            if (commondList.isEmpty()){
                LOG.info("未解析到增强指令，不进行增强处理");
                return;
            }
            instrumentation.addTransformer(new ExecTimerTransformer(commondList));
        } catch (Exception e) {
            LOG.warn("执行时长计数器代理程序执行启动失败错误信息如下，但不影响程序正常:");
            e.printStackTrace();
        }
    }
}
