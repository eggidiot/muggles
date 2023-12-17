package com.muggles.fun.repo.basic.model;

import com.muggles.fun.basic.model.MuggleParam;
import lombok.Getter;

/**
 * 核心查询数据结构，用于默认通用查询，和特定查询方案
 */
@Getter
public class Muggle<T> extends MuggleParam<T,Muggle<T>> {
    /**
     * 实体别名
     */
    protected String alias;
}
