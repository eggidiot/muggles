package com.muggles.fun.basic.converter;

import com.muggles.fun.basic.MFunction;
import com.muggles.fun.basic.model.IMugglePage;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 视图对象转换器
 *
 * @param <T>
 * @param <R>
 */
public interface IViewConverter<T,R> extends MFunction<T,R> {
    /**
     * 转化集合对象
     *
     * @param list 数组对象
     * @return List<R>
     */
    default List<R> applyList(List<T> list) {
        return list.stream().map(this).collect(Collectors.toList());
    }

    /**
     * 转化分页对象
     *
     * @param page 分页对象
     * @return IFlinePage<R>
     */
    IMugglePage<R> applyPage(IMugglePage<T> page);
}
