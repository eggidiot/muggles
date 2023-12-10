package com.muggles.fun.basic.model;

import com.muggles.fun.basic.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义查询参数，支持简单的模糊查询和等值查询
 * 等值查询的时候传入condition使用对象的属性作为查询值
 * 需要附加检索时
 *
 * @param <T>
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public abstract class MuggleParam<T> {
    /**
     * 连接符
     */
    protected Constants.RelationType relationType = Constants.RelationType.AND;
    /**
     * 每页显示条数，默认 10
     */
    protected long size = 10;
    /**
     * 当前页
     */
    protected long current = 1;
    /**
     * 查询条件
     */
    protected Map<String, Object> params = new HashMap<>();
    /**
     * 查询字段，
     */
    protected List<String> fields = new ArrayList<>();
    /**
     * 排除字段
     */
    protected List<String> excludes = new ArrayList<>();
    /**
     * 排序字段
     */
    protected List<OrderBy> orderBys = new ArrayList<>();
    /**
     * groupBy字段
     */
    protected List<String> groupBys = new ArrayList<>();
    /**
     * 查询子条件集合
     */
    protected List<MuggleParam<T>> relations = new ArrayList<>();
    /**
     * 查询对象字段限定
     */
    protected T selectors;
    /**
     * 默认是否包含一个排序方式
     */
    protected boolean defaultOrderBy = true;

    /**
     * 排序字段
     */
    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderBy {
        /**
         * 排序字段名
         */
        String field;
        /**
         * 排序方式
         */
        Integer asc = Constants.ASC;

        /**
         * 判断当前条件是否正序
         *
         * @return boolean
         */
        public boolean isAsc() {
            return getAsc() > Constants.DESC;
        }
    }
}
