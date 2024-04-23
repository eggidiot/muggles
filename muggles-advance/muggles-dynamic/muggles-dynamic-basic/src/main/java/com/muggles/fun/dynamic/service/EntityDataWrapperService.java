package com.muggles.fun.dynamic.service;

import com.muggles.fun.basic.model.IMugglePage;
import com.muggles.fun.dynamic.EntityData;
import com.muggles.fun.dynamic.ViewParam;
import com.muggles.fun.repo.basic.model.Muggle;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * EntityDataWrapperService
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 0.9.0
 */
public interface EntityDataWrapperService {


    /**
     * 实体新增数据
     *
     * @param name 实体名称
     * @param data 实体数据
     */
    boolean add(String name, EntityData data);

    /**
     * 更新一条记录
     *
     * @param data 动态表单数据
     */
    boolean update(String name, Serializable dataId, EntityData data);

    /**
     * 批量更新记录
     *
     * @param data 动态表单数据
     */
    boolean updateBatch(String name, Collection<EntityData> data);

    /**
     * 删除一条记录，根据数据推断出是否需要逻辑删除
     *
     * @param dataId 动态表单数据的Id
     */
    boolean delById(String name, Serializable dataId);

    /**
     * 逻辑删除一条记录
     *
     * @param param 删除条件
     * @return boolean
     */
    boolean del(String name, Muggle<EntityData> param);

    /**
     * 物理删除一条记录
     *
     * @param name   实体名称
     * @param dataId 动态表单数据的Id
     */
    boolean delPhysicsById(String name, Serializable dataId);

    /**
     * 删除所有的数据，物理删除
     *
     * @param name 实体名称
     */
    boolean truncate(String name);

    /**
     * 根据查询条件返回查询分页结果
     *
     * @param param 查询条件
     * @return PageRec
     */
    IMugglePage<EntityData> page(String name, Muggle<EntityData> param);

    /**
     * 根据查询条件返回查询分页结果
     *
     * @param param 查询条件
     * @return PageRec
     */
    IMugglePage<EntityData> page(String name, Muggle<EntityData> param, ViewParam view);

    /**
     * 根据查询条件返回查询集合结果
     *
     * @param param 查询条件
     * @return List
     */
    List<EntityData> list(String name, Muggle<EntityData> param);

    /**
     * 树列表，返回值对象
     */
    List<EntityData> treeList(String name, Muggle<EntityData> param);

    /**
     * 结果的条目数
     *
     * @param param 查询条件
     * @return T
     */
    int count(String name, Muggle<EntityData> param);


    /**
     * 根据查询条件返回查询单条记录
     *
     * @param param 查询条件
     * @return T
     */
    EntityData one(String name, Muggle<EntityData> param);

    /**
     * 根据查询条件返回查询单条记录
     *
     * @param id id
     */
    EntityData getById(String name, Serializable id);

}
