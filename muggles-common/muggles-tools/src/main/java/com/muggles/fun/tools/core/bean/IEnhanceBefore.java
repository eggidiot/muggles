package com.muggles.fun.tools.core.bean;

import java.lang.reflect.Method;

/**
 * 前置增强接口
 */
@FunctionalInterface
public interface IEnhanceBefore {
    /**
     * 执行拦截逻辑
     * @param args  方法调用参数
     * @return
     */
    boolean before(Method method, Object[] args);
}
