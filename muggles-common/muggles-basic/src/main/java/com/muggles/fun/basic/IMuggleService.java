package com.muggles.fun.basic;

import com.muggles.fun.basic.model.MuggleParam;

import java.io.Serializable;
import java.util.List;

/**
 * 基本CRUD查询service
 * @param <T> 针对泛型对象查询
 */
public interface IMuggleService<T> {

    /**
     * 根据id获取对象
     * @param id
     * @return
     */
    T getById(Serializable id);
    /**
     * 根据查询条件查询实体第一条记录
     * @param param 查询条件
     * @return  T
     * @param <C>   条件泛型
     */
    <C extends MuggleParam>T one(C param);
    /**
     * 根据查询条件查询实体集合
     * @param param 查询条件
     * @return  T
     * @param <C>   条件泛型
     */
    <C extends MuggleParam>List<T> list(C param);

    /**
     * 根据查询条件查询实体分页集合
     * @param param 查询条件
     * @return  T
     * @param <C>   条件泛型
     */
    <C extends MuggleParam>IMuggleService<T> page(C param);

    /**
     * 新增对象记录
     * @param t 对象实体
     * @return  Boolean
     */
    Boolean create(T t);

    /**
     * 根据id更新实体
     * @param t 对象实体
     * @return  Boolean
     */
    Boolean updateById(T t);

    /**
     * 将指定条件的记录更新诚实体非null字段
     * @param t     实体记录
     * @param param 更新条件
     * @return  Boolean
     * @param <C>   条件泛型
     */
    <C extends MuggleParam>Boolean updateById(T t,C param);
}
