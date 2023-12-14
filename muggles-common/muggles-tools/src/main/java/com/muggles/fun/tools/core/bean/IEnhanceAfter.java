package com.muggles.fun.tools.core.bean;

import java.lang.reflect.Method;

/**
 * 函数返回值拦截
 */
@FunctionalInterface
public interface IEnhanceAfter {
    /**
     * 返回值拦截
     * @param obj           当前this对象
     * @param method        执行方法
     * @param args          执行参数
     * @param returnValue   返回值
     * @return              方法返回值,建议返回原值
     */
    Object after(Object obj,Method method,  Object[] args, Object returnValue);
}
