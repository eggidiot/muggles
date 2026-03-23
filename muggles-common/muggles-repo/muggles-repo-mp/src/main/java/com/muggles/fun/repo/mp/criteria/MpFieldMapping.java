package com.muggles.fun.repo.mp.criteria;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.muggles.fun.repo.basic.IFieldMapping;
import com.muggles.fun.tools.core.bean.LambdaFunction;

import java.util.function.Function;

/**
 *
 * @param <T>
 */
public class MpFieldMapping<T> extends LambdaQueryWrapper<T> implements IFieldMapping {

    /**
     * 属性映射字段
     *
     * @param attribute 属性get方法
     * @return String
     */
    @Override
    public <T> String fieldMappingColum(LambdaFunction<T, ?> attribute) {
        SFunction func = new SFunction(){

            /**
             * Applies this function to the given argument.
             *
             * @param o the function argument
             * @return the function result
             */
            @Override
            public Object apply(Object o) {
                try {
                    return attribute.call((T) o);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        return super.columnsToString(func);
    }
}
