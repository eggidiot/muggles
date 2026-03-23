package com.muggles.fun.repo.mp.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muggles.fun.basic.model.IMugglePage;
import com.muggles.fun.repo.basic.model.Muggle;
import com.muggles.fun.repo.basic.model.MugglePage;
import com.muggles.fun.repo.basic.service.IMuggleService;
import com.muggles.fun.repo.mp.criteria.WrapperTranslator;
import com.muggles.fun.repo.mp.join.JoinSqlParser;
import com.muggles.fun.repo.mp.mapper.CommonMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MpServiceImpl<M extends CommonMapper<T>, T> extends CommonServiceImpl<M,T,Muggle<T>> implements IMuggleService<T> {
    /**
     * 根据查询条件查询实体第一条记录
     *
     * @param param 查询条件
     * @return T
     */
    @Override
    public T one(Muggle<T> param) {
        if (CollUtil.isNotEmpty(param.getJoins())) {
            JoinSqlParser.buildJoinParam(param);
            List<T> results = mapper().queryJoinList(param);
            return CollUtil.isNotEmpty(results) ? results.getFirst() : null;
        }
        return getOne(WrapperTranslator.translate(param));
    }

    /**
     * 根据查询条件查询实体集合
     *
     * @param param 查询条件
     * @return List<T>
     */
    @Override
    public List<T> list(Muggle<T> param) {
        if (CollUtil.isNotEmpty(param.getJoins())) {
            JoinSqlParser.buildJoinParam(param);
            return mapper().queryJoinList(param);
        }
        return list(WrapperTranslator.translate(param));
    }

    /**
     * 根据查询条件查询实体分页集合
     *
     * @param param 查询条件
     * @return IMugglePage<T>
     */
    @Override
    public IMugglePage<T> page(Muggle<T> param) {
        Page<T> result;
        if (CollUtil.isNotEmpty(param.getJoins())) {
            JoinSqlParser.buildJoinParam(param);
            result = mapper().queryJoinPage(WrapperTranslator.toPage(param), param);
        } else {
            result = page(WrapperTranslator.toPage(param), WrapperTranslator.translate(param));
        }
        MugglePage<T> page = new MugglePage<>();
        page.setCurrent(result.getCurrent())
                .setSize(result.getSize()).setTotal(result.getTotal()).setPages(result.getPages()).setRecords(result.getRecords());
        return page;
    }

    /**
     * 联表查询返回复合对象列表
     * 支持返回非原表实体类型（如DTO、VO等复合对象）
     *
     * @param param      查询条件
     * @param resultType 返回对象类型
     * @param <R>        返回类型
     * @return List<R>
     */
    public <R> List<R> listJoin(Muggle<?> param, Class<R> resultType) {
        JoinSqlParser.buildJoinParam(param);
        List<Map<String, Object>> maps = mapper().queryJoinMaps(param);
        return maps.stream()
                .map(m -> BeanUtil.toBean(m, resultType))
                .collect(Collectors.toList());
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
        return update(t, WrapperTranslator.translate(param));
    }

    /**
     * 根据条件删除实体记录
     *
     * @param param 通用查询参数
     * @return C
     */
    @Override
    public Boolean remove(Muggle<T> param) {
        return remove(WrapperTranslator.translate(param));
    }

    /**
     * 根据 entity 条件，查询一条记录，并锁定
     *
     * @param muggle 实体对象封装操作类（可以为 null）
     * @return T
     */
    @Override
    public T oneForUpdate(Muggle<T> muggle) {
        return getOne(WrapperTranslator.translate(muggle).last("for update"));
    }

    /**
     * 根据 entity 条件，查询全部记录，并锁定
     *
     * @param muggle 实体对象封装操作类（可以为 null）
     * @return List<T>
     */
    @Override
    public List<T> listForUpdate(Muggle<T> muggle) {
        return list(WrapperTranslator.translate(muggle).last("for update"));
    }
}
