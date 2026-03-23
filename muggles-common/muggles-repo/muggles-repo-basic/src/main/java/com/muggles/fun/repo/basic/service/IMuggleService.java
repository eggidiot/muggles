package com.muggles.fun.repo.basic.service;

import cn.hutool.core.util.ReflectUtil;
import com.muggles.fun.basic.model.IMugglePage;
import com.muggles.fun.repo.basic.criteria.ISubQuery;
import com.muggles.fun.repo.basic.model.Muggle;

import java.util.List;

/**
 * 默认的muggle需要提供业务能力的service
 *
 * @param <T>
 */
public interface IMuggleService<T> extends ICommonService<T, Muggle<T>>, ISubQuery {

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

    /**
     * 根据查询条件查询实体第一条记录,返回指定类型
     * @param param 查询条件
     * @param clazz 指定类型
     * @return  R
     * @param <R>  指定类型泛型
     */
    <R>R one(Muggle<T> param,Class<R> clazz);
    /**
     * 根据查询条件查询实体集合,返回指定类型
     * @param param 查询条件
     * @param clazz 指定类型
     * @return  R
     * @param <R>  指定类型泛型
     */
    <R>List<R> list(Muggle<T> param,Class<R> clazz);

    /**
     * 根据查询条件查询实体分页集合,返回指定类型
     * @param param 查询条件
     * @param clazz 指定类型
     * @return  IMugglePage<R>
     * @param <R>  指定类型泛型
     */
    <R>IMugglePage<R> page(Muggle<T> param,Class<R> clazz);
}
