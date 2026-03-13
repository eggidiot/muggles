package com.muggles.fun.tools.core.bean;

import java.io.Serializable;
import java.util.function.Function;

/**
 * 可序列化的Function接口，支持通过 类::方法名 传递方法引用并提取方法名。
 * 用法示例：SFunction&lt;AccountService, ?&gt; func = AccountService::getAmount;
 *
 * @param <T> 入参类型
 * @param <R> 返回类型
 */
@FunctionalInterface
public interface SFunction<T, R> extends Function<T, R>, Serializable {
}
