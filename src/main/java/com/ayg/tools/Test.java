package com.ayg.tools;

/**
 * 测试类
 */
public class Test {


    public static void main(String[] args) {
        Entity entity = new Entity();
        entity.setName("aa");
        Entity.say("静态方法",3,false,entity,6,7);

        new Entity().hi("实例方法",3,false,entity,6,7);
    }
}
