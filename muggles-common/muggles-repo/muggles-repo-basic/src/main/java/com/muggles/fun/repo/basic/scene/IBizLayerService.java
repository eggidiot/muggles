package com.muggles.fun.repo.basic.scene;

import com.muggles.fun.basic.model.IMugglePage;
import com.muggles.fun.repo.basic.model.Muggle;
import com.muggles.fun.repo.basic.service.IMuggleService;

import java.util.List;

/**
 * 自定义业务查询接口
 */
public interface IBizLayerService {
    /**
     * 保存实体对象
     *
     * @param entity 对象实体
     * @return  Boolean
     */
    <T>Boolean saveDo(T entity);

    /**
     * 更新对象
     *
     * @param entity 对象实体
     * @return  Boolean
     */
    <T>Boolean updateDo(T entity);

    /**
     * 根据对象类型和主键获取对象
     *
     * @param id     主键
     * @param tClass 对象类型
     * @param <T>   泛型类型
     * @return  T
     */
    <T> T getDo(Long id, Class<T> tClass);

    /**
     * 根据对象类型和主键删除对象
     *
     * @param id     主键
     * @param tClass 对象类型
     * @param <T>   泛型类型
     * @return  Boolean
     */
    <T> Boolean removeDo(Long id, Class<T> tClass);

    /**
     * 根据类型获取对象集合
     *
     * @param tClass 查询类型
     * @param <T>   泛型类型
     * @return  List<T>
     */
    <T> List<T> listDos(Class<T> tClass);

    /**
     * 根据类型和查询条件获取分页查询结果
     *
     * @param param  查询参数
     * @param tClass 查询类型
     * @param <T>   泛型类型
     * @return  List<T>
     */
    <T> IMugglePage<T> pageDos(Muggle<T> param, Class<T> tClass);

    /**
     * 根据类型和查询条件获取集合查询结果
     *
     * @param param  查询参数
     * @param tClass 查询类型
     * @param <T>   泛型类型
     * @return  List<T>
     */
    <T> List<T> listDos(Muggle<T> param, Class<T> tClass);

    /**
     * 根据查询条件获取单条查询结果
     *
     * @param param  查询参数
     * @param tClass 对象类型
     * @param <T>   泛型类型
     * @return  T
     */
    <T> T oneDo(Muggle<T> param, Class<T> tClass);

    /**
     * 根据对象类型获取相应的对象业务service
     *
     * @param tClass 对象类型
     * @param <T>   泛型类型
     * @return  IMuggleService<T>
     */
    <T> IMuggleService<T> getService(Class<T> tClass);

    /**
     * 根据类型获取相应的查询条件生成方法
     *
     * @param tClass 数据类型
     * @param <T>   泛型类型
     * @return  Muggle<T>
     */
    <T> Muggle<T> where(Class<T> tClass);
}
