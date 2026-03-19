package com.muggles.fun.repo.basic.model;

import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.util.StrUtil;
import com.muggles.fun.basic.MFunction;
import com.muggles.fun.repo.basic.IFieldMapping;
import com.muggles.fun.tools.core.bean.LambdaFunction;
import lombok.SneakyThrows;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

/**
 * 默认属性映射字段
 */
public class DefaultMapping implements IFieldMapping {
    /**
     * 属性映射字段，默认直接返回属性名称
     *
     * @param attribute 属性get方法
     * @return String
     */
    @SneakyThrows
    @Override
    public <T> String fieldMappingColum(LambdaFunction<T, ?> attribute) {
        return StrUtil.toUnderlineCase(LambdaUtil.getFieldName(attribute));
    }
}
