package com.muggles.fun.basic;

import com.muggles.fun.basic.model.IMugglePage;
import com.muggles.fun.basic.model.MuggleParam;

import java.io.Serializable;
import java.util.List;

/**
 * 基本CRUD查询service
 * @param <T> 针对泛型对象查询
 */
public interface ICrudService<T,C extends MuggleParam<T, C>> {
    /**
     * 查询全表记录
     * @return	List<T>
     */
    List<T> list();
    /**
     * 根据id获取对象
     * @param id    主键
     * @return  T
     */
    T getById(Serializable id);
    /**
     * 根据查询条件查询实体第一条记录
     * @param param 查询条件
     * @return  T
     */
    T one(C param);
    /**
     * 根据查询条件查询实体集合
     * @param param 查询条件
     * @return  T
     */
    List<T> list(C param);

    /**
     * 根据查询条件查询实体分页集合
     * @param param 查询条件
     * @return  T
     */
    IMugglePage<T> page(C param);

    /**
     * 新增对象记录
     * @param t 对象实体
     * @return  Boolean
     */
    boolean save(T t);

    /**
     * 根据id更新实体
     * @param t 对象实体
     * @return  Boolean
     */
    boolean updateById(T t);

    /**
     * 根据记录的id值是否存在进行更新或者保存的操作
     *
     * @param entity 实体对象
     * @return	boolean
     */
    boolean saveOrUpdate(T entity);

    /**
     * 将指定条件的记录更新诚实体非null字段
     * @param t     实体记录
     * @param param 更新条件
     * @return  Boolean
     */
    Boolean update(T t,C param);

    /**
     * 根据id删除记录
     * @param id    实体记录id
     * @return  Boolean
     */
    boolean removeById(Serializable id);
    /**
     * 批量保存实体
     * @param list  实体记录
     * @return  Boolean
     */
    boolean saveBatch(List<T> list);
    /**
     * 根据实体id批量更新实体
     * @param list  实体记录
     * @return  Boolean
     */
    boolean updateBatchById(List<T> list);

    /**
     * 根据实体id批量插入或者更新实体
     * @param list  实体记录
     * @return  Boolean
     */
    boolean saveOrUpdateBatchById(List<T> list);

    /**
     * 根据id集合批量删除记录
     * @param ids   实体id集合
     * @return  Boolean
     */
    boolean removeBatchById(List<Serializable> ids);

    /**
     * 根据条件删除实体记录
     * @param param 通用查询参数
     * @return  C
     */
    Boolean remove(C param);
}
