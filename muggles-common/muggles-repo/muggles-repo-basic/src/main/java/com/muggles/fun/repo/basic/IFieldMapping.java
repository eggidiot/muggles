package com.muggles.fun.repo.basic;

import com.muggles.fun.basic.MFunction;

/**
 * 属性映射字段接口
 */
public interface IFieldMapping {
    /**
     * 属性映射字段
     * @param attribute 属性get方法
     * @return  String
     */
    <T>String fieldMappingColum(MFunction<T, ?> attribute);
}
