package com.muggles.fun.repo.mp.service;

import com.muggles.fun.basic.model.IMugglePage;
import com.muggles.fun.repo.basic.model.Muggle;
import com.muggles.fun.repo.basic.service.IMuggleService;
import com.muggles.fun.repo.mp.mapper.CommonMapper;

import java.util.List;

public class MpServiceImpl<M extends CommonMapper<T>, T> extends CommonServiceImpl<M,T,Muggle<T>> implements IMuggleService<T> {
    /**
     * 根据查询条件查询实体第一条记录
     *
     * @param param 查询条件
     * @return T
     */
    @Override
    public T one(Muggle<T> param) {
        return null;
    }

    /**
     * 根据查询条件查询实体集合
     *
     * @param param 查询条件
     * @return T
     */
    @Override
    public List<T> list(Muggle<T> param) {
        return null;
    }

    /**
     * 根据查询条件查询实体分页集合
     *
     * @param param 查询条件
     * @return T
     */
    @Override
    public IMugglePage<T> page(Muggle<T> param) {
        return null;
    }

    /**
     * 将指定条件的记录更新诚实体非null字段
     *
     * @param t     实体记录
     * @param param 更新条件
     * @return Boolean
     */
    @Override
    public Boolean update(T t, Muggle<T> param) {
        return null;
    }

    /**
     * 将指定条件的记录批量更新诚实体非null字段
     *
     * @param list  实体记录
     * @param param 更新条件
     * @return Boolean
     */
    @Override
    public Boolean updateBatch(List<T> list, Muggle<T> param) {
        return null;
    }

    /**
     * 根据条件删除实体记录
     *
     * @param param 通用查询参数
     * @return C
     */
    @Override
    public Boolean remove(Muggle<T> param) {
        return null;
    }

    /**
     * 根据 entity 条件，查询一条记录，并锁定
     *
     * @param muggle 实体对象封装操作类（可以为 null）
     * @return T
     */
    @Override
    public T oneForUpdate(Muggle<T> muggle) {
        return null;
    }

    /**
     * 根据 entity 条件，查询全部记录，并锁定
     *
     * @param muggle 实体对象封装操作类（可以为 null）
     * @return List<T>
     */
    @Override
    public List<T> listForUpdate(Muggle<T> muggle) {
        return null;
    }
}
