package com.muggles.fun.repo.basic.model;

import com.muggles.fun.basic.MFunction;
import com.muggles.fun.repo.basic.IFieldMapping;
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
    public <T> String fieldMappingColum(MFunction<T, ?> attribute) {
        Method method = attribute.getClass().getDeclaredMethod("writeReplace");
        method.setAccessible(true);
        SerializedLambda lambda = (SerializedLambda) method.invoke(attribute);
        String methodName = lambda.getImplMethodName(); // 比如 "getName"
        return methodNameToProperty(methodName);
    }

    /**
     * 获取属性名
     * @param methodName    方法名称
     * @return  String
     */
    private String methodNameToProperty(String methodName) {
        if (methodName.startsWith("is")) {
            return methodName.substring(2);
        } else if (methodName.startsWith("get") || methodName.startsWith("set")) {
            return methodName.substring(3);
        }
        return methodName;
    }
}
