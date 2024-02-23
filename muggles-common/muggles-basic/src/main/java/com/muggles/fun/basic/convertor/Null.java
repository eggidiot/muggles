package com.muggles.fun.basic.convertor;

/**
 * 空转换器
 *
 * @author tanghao
 * @date 2024/2/18 14:24
 */
public class Null implements ITypeConvertor<Object, Object> {
    /**
     * 把原始类型转换成目标类型
     *
     * @param source
     * @return
     */
    @Override
    public Object apply(Object source) {
        return source;
    }
}
