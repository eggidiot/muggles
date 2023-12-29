package com.muggles.fun.tools.core.bean;

import cn.hutool.core.lang.func.Func1;

/**
 * 可以序列化的lambda表达式
 *
 * @param <T>
 * @param <R>
 */
@FunctionalInterface
public interface LambdaFunction<T, R> extends Func1<T, R> {
}
