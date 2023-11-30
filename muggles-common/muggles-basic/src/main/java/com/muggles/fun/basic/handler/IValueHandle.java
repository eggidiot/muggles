package com.muggles.fun.basic.handler;

import java.util.function.Function;

/**
 * 值处理链
 */
public interface IValueHandle extends Function {
    /**
     * 是否支持处理值
     * @return
     */
    default boolean support(Object value){
        return true;
    }
    /**
     * 处理返回值
     * @param value 方法返回值
     * @param chain 多个处理器形成的责任链
     * @return  新返回值
     */
    default Object handle(Object value, IValueHandleChain chain){
        if (support(value)) {
            value = apply(value);
        }
        return chain.process(value);
    }
}
