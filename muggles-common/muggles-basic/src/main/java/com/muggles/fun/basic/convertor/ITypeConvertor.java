package com.muggles.fun.basic.convertor;

import java.util.function.Function;

/**
 * 自定义转换处理器接口
 *
 * @author tanghao
 * @date 2024/2/18 14:09
 */
public interface ITypeConvertor<S,T> extends Function<S,T> {

}
