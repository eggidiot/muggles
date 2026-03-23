package com.muggles.fun.repo.basic.service;

import cn.hutool.core.util.ReflectUtil;
import com.muggles.fun.repo.basic.model.Muggle;

/**
 * 默认的muggle需要提供业务能力的service
 *
 * @param <T>
 */
public interface IMuggleService<T> extends ICommonService<T, Muggle<T>>{

    /**
     * 返回泛型有参数实例
     *
     * @return FlineParam<T>
     */
    default Muggle<T> where(T entity) {
        return new Muggle<T>().setSelector(entity);
    }

    /**
     * 返回泛型有参数实例
     *
     * @return FlineParam<T>
     */
    default Muggle<T> where() {
        return where(ReflectUtil.newInstance(getTClass()));
    }
}
