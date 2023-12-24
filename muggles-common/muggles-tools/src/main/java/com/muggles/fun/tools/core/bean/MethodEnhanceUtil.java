package com.muggles.fun.tools.core.bean;

import cn.hutool.core.lang.Assert;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * 方法增强工具
 */
@Slf4j
@UtilityClass
public class MethodEnhanceUtil {

    /**
     * 返回增强对象，默认方式不指定方法与增强方式，会给类声明的所有方法添加默认前后置处理器
     *
     * @param t   目标对象
     * @param <T> 泛型
     * @return 增强对象
     */
    public <T> T enhancer(T t) {
        CglibProxyFactory factory = new CglibProxyFactory(t);
        Method[] methods = t.getClass().getDeclaredMethods();
        for (Method m : methods) {
            factory.enhance(m, (obj, method, args) -> {
                        log.info("前置切面对象:{},方法:{},参数:{}", t, method.getName(), args);
                        return true;
                    }, (obj, method, args, value) -> {
                        log.info("前置切面对象:{},方法:{},参数:{},返回值:{}", t, method.getName(), args, value);
                        return value;
                    }
            );
        }
        return factory.get();
    }

    /**
     * 返回增强对象
     *
     * @param t   目标对象
     * @param <T> 泛型
     * @return 增强对象
     */
    public <T> T enhancer(T t, String method, IEnhanceBefore before, IEnhanceAfter after) {
        Method m = findMethod(t, method);
        return enhancer(t, m, before, after);
    }

    /**
     * 返回增强对象
     *
     * @param t   目标对象
     * @param <T> 泛型
     * @return 增强对象
     */
    public <T> T enhancer(T t, Method method, IEnhanceBefore before, IEnhanceAfter after) {
        Assert.notNull(t, () -> new IllegalStateException("指定对象不能为Null"));
        Assert.notNull(t, () -> new IllegalStateException("被增强的指定方法不能为Null"));
        CglibProxyFactory factory = new CglibProxyFactory(t);
        factory.enhance(method, before, after);
        return factory.get();
    }

    /**
     * 设置某个方法前置拦截，插入业务逻辑
     *
     * @param t      增强对象
     * @param method 增强方法
     * @param before 前置处理器
     * @param <T>    泛型
     * @return 增强对象
     */
    public <T> T enhancerBefore(T t, String method, IEnhanceBefore before) {
        return enhancer(t, method, before, null);
    }

    /**
     * 设置某个方法前置拦截，插入业务逻辑
     *
     * @param t      增强对象
     * @param method 增强方法
     * @param before 前置处理器
     * @param <T>    泛型
     * @return 增强对象
     */
    public <T> T enhancerBefore(T t, Method method, IEnhanceBefore before) {
        return enhancer(t, method, before, null);
    }

    /**
     * 设置某个方法后置拦截，插入业务逻辑
     *
     * @param t      增强对象
     * @param method 增强方法
     * @param after  后置处理器
     * @param <T>    泛型
     * @return 增强对象
     */
    public <T> T enhancerAfter(T t, String method, IEnhanceAfter after) {
        return enhancer(t, method, null, after);
    }

    /**
     * 设置某个方法后置拦截，插入业务逻辑
     *
     * @param t      增强对象
     * @param method 增强方法
     * @param after  后置处理器
     * @param <T>    泛型
     * @return 增强对象
     */
    public <T> T enhancerAfter(T t, Method method, IEnhanceAfter after) {
        return enhancer(t, method, null, after);
    }

    /**
     * 返回与方法名匹配的第一个方法，要正确使用，要求被查找的方法名没有被重写
     *
     * @param t      对象
     * @param method 方法
     * @param <T>    对象泛型
     * @return 目标方法
     */
    public <T> Method findMethod(T t, String method) {
        Assert.notNull(t, () -> new IllegalStateException("对象不能为Null"));
        return findMethod(t.getClass(), method);
    }

    /**
     * 返回与方法名匹配的第一个方法，要正确使用，要求被查找的方法名没有被重写
     *
     * @param tClass 指定类型
     * @param method 方法
     * @param <T>    对象泛型
     * @return 目标方法
     */
    public <T> Method findMethod(Class<T> tClass, String method) {
        Assert.notNull(tClass, () -> new IllegalStateException("指定class不能为null"));
        Method[] methods = tClass.getMethods();
        for (Method m : methods) {
            if (m.getName().equals(method)) {
                return m;
            }
        }
        return null;
    }

    /**
     * 根据方法名找对象方法
     *
     * @param t          对象
     * @param method     方法
     * @param paramTypes 参数类型，可变数组
     * @param <T>        对象泛型
     * @return 目标方法
     */
    public <T> Method findMethod(T t, String method, Class<?>... paramTypes) {
        Assert.notNull(t, () -> new IllegalStateException("对象不能为Null"));
        return findMethod(t.getClass(), method, paramTypes);
    }

    /**
     * 根据方法名找对象方法
     *
     * @param tClass     指定类型
     * @param method     方法
     * @param paramTypes 参数类型，可变数组
     * @param <T>        对象泛型
     * @return 目标方法
     */
    public <T> Method findMethod(Class<T> tClass, String method, Class<?>... paramTypes) {
        Assert.notNull(tClass, () -> new IllegalStateException("指定class不能为null"));
        try {
            return tClass.getMethod(method, paramTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
