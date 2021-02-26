package com.ayg.tools;

/**
 * 测试实体类
 */
public class Entity {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void say(String say,double a,boolean c,Entity d,long f,int g){
        System.out.println("我是静态方法："+ say);
    }

    public void hi(String say,double a,boolean c,Entity d,long f,int g){
        System.out.println("我是实例方法："+say);
    }

    public String hi(){
        System.out.println("我没有参数");
        return "aaa";
    };

    public String inject(){
        System.out.println("inject");
        return "i'm inject return";
    };

    public static void say(String str){
        System.out.println(str);
    }

}
