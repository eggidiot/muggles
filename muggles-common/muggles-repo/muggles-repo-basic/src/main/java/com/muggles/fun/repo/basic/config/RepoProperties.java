package com.muggles.fun.repo.basic.config;

import com.muggles.fun.repo.basic.RepoConstants;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 持久层属性列表
 */
@Data
@Accessors(chain = true)
public class RepoProperties {
    /**
     * 查询条件字段不存在策略
     */
    RepoConstants.FieldStrategy fieldStrategy;
    /**
     * 集合查询时集合为空的策略
     */
    RepoConstants.InCondiStrategy inCondiStrategy;
}
