package com.itheima.utils;

/**
 * 本地线程存储登陆者id
 */
public class ThreadLocalUtils {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    //存储
    public static void set(Long id){
        threadLocal.set(id);
    }
    //获取
    public static Long get(){
        return threadLocal.get();
    }
    //清除
    public static void remove(){
        threadLocal.remove();
    }

}
