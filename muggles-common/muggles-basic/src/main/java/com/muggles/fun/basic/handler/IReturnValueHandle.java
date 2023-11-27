package com.muggles.fun.basic.handler;

import java.util.List;
import java.util.function.Function;

/**
 * 返回值处理链
 */
public interface IReturnValueHandle extends Function {
    /**
     * 处理返回值
     * @param value 方法返回值
     * @param chain 多个处理器形成的责任链
     * @return  新返回值
     */
    default Object handle(Object value, IValueChain chain){
        return apply(value);
    }
}
