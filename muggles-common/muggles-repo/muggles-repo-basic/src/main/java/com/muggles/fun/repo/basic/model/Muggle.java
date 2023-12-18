package com.muggles.fun.repo.basic.model;

import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.func.LambdaUtil;
import com.muggles.fun.basic.model.MuggleParam;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 核心查询数据结构，用于默认通用查询，和特定查询方案
 */
@Getter
public class Muggle<T> extends MuggleParam<T,Muggle<T>> {
    /**
     * 实体别名
     */
    protected String alias;
    /**
     * 适用于关联查询的集合
     */
    protected List<Muggle<?>> joins = new ArrayList<>();

    /**
     * 获取字段名称
     *
     * @param attribute 字段属性名称
     * @return String
     */
    protected String columnsToString(Func1<T, ?> attribute) {
        LambdaUtil.resolve(attribute);
        return null;
    }
}
