package com.muggles.fun.repo.basic.criteria;

import com.muggles.fun.basic.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import static com.muggles.fun.basic.Constants.RelationType;
/**
 * 连表查询时连表参数
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class OnCriteria {
    /**
     * 查询属性名称
     */
    protected String attr1;
    /**
     * 查询属性值
     */
    protected String attr2;
    /**
     * and连接
     */
    protected RelationType relation;
}
