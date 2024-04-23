package com.muggles.fun.dynamic.service;

import com.muggles.fun.basic.model.IMugglePage;
import com.muggles.fun.dynamic.EntityData;
import com.muggles.fun.dynamic.EntitySchema;
import com.muggles.fun.repo.basic.model.Muggle;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 实体数据服务接口
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 0.9.0
 */
public interface EntityDataService {

    /**
     * 实体新增数据
     *
     * @param schema 实体结构
     * @param data   实体数据
     */
    boolean add(EntitySchema schema, EntityData data);

    /**
     * 更新一条记录
     *
     * @param id   数据id
     * @param data 动态表单数据
     */
    default boolean update(EntitySchema schema, Serializable id, EntityData data) {
        return update(schema, id, data, true);
    }

    /**
     * 更新一条记录
     *
     * @param id   数据id
     * @param data 动态表单数据
     */
    boolean update(EntitySchema schema, Serializable id, EntityData data, boolean updateValidate);

    /**
     * 更新一条记录
     *
     * @param oldData 旧数据
     * @param newData 新数据
     */
    default boolean update(EntitySchema schema, EntityData oldData, EntityData newData) {
        return update(schema, oldData, newData, true);
    }

    /**
     * 更新一条记录
     *
     * @param oldData        旧数据
     * @param newData        新数据
     * @param updateValidate 是否需要校验
     */
    boolean update(EntitySchema schema, EntityData oldData, EntityData newData, boolean updateValidate);

    /**
     * 批量更新记录
     *
     * @param data 动态表单数据
     */
    boolean updateBatch(EntitySchema schema, Collection<EntityData> data);

    /**
     * 删除一条记录，根据数据推断出是否需要逻辑删除
     *
     * @param dataId 动态表单数据的Id
     */
    boolean delById(EntitySchema schema, Serializable dataId);

    /**
     * 逻辑删除一条记录
     *
     * @param param 删除条件
     * @return boolean
     */
    boolean del(EntitySchema schema, Muggle<EntityData> param);

    /**
     * 物理删除一条记录
     *
     * @param schema 实体结构
     * @param dataId 动态表单数据的Id
     */
    boolean delPhysicsById(EntitySchema schema, Serializable dataId);

    /**
     * 删除所有的数据，物理删除
     *
     * @param schema 实体结构
     */
    boolean truncate(EntitySchema schema);

    /**
     * 根据查询条件返回查询分页结果
     *
     * @param param  查询条件
     * @return PageRec
     */
    IMugglePage<EntityData> page(EntitySchema schema, Muggle<EntityData> param);

    /**
     * 根据查询条件返回查询集合结果
     *
     * @param param 查询条件
     * @return List
     */
    List<EntityData> list(EntitySchema schema, Muggle<EntityData> param);

    /**
     * 树列表，返回值对象
     */
    List<EntityData> treeList(EntitySchema schema, Muggle<EntityData> param);

    /**
     * 结果的条目数
     *
     * @param param 查询条件
     * @return T
     */
    int count(EntitySchema schema, Muggle<EntityData> param);


    /**
     * 根据查询条件返回查询单条记录
     *
     * @param param 查询条件
     * @return T
     */
    EntityData one(EntitySchema schema, Muggle<EntityData> param);

    /**
     * 根据查询条件返回查询单条记录
     *
     * @param id id
     */
    EntityData getById(EntitySchema schema, Serializable id);

}
