package com.muggles.fun.repo.basic.model;

import java.io.Serializable;
import java.util.function.Function;

/**
 * 可以序列化的lambda表达式
 * @param <T>
 * @param <R>
 */
@FunctionalInterface
public interface LambdaFunction<T, R> extends Function<T, R>, Serializable {
}
