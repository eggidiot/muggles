package com.muggles.fun.basic.handler;

import java.util.function.Function;

/**
 * 值处理链
 */
public interface IValueHandle<T, R> extends Function<T, R> {
    /**
     * 是否支持处理值
     * @return  boolean
     */
    default boolean support(T value){
        return true;
    }
    /**
     * 处理返回值
     * @param value 方法返回值
     * @param chain 多个处理器形成的责任链
     * @return  新返回值
     */
    default R handle(T value, IValueHandleChain chain){
        if (support(value)) {
            return apply(value);
        }
        return chain.process(value);
    }
}
