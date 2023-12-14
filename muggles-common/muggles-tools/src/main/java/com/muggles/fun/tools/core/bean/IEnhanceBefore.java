package com.muggles.fun.tools.core.bean;

import java.lang.reflect.Method;

/**
 * 前置增强接口
 */
@FunctionalInterface
public interface IEnhanceBefore {
    /**
     * 执行拦截逻辑
     * @param obj           当前this对象
     * @param method        执行方法
     * @param args          执行参数
     * @return              方法返回值,建议返回原值
     */
    boolean before(Object obj,Method method, Object[] args);
}
