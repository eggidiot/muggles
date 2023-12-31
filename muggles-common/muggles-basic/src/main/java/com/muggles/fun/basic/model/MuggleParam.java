package com.muggles.fun.basic.model;

import com.muggles.fun.basic.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
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
@NoArgsConstructor
public abstract class MuggleParam<T,C extends MuggleParam<T,C>> {
    /**
     * 连接符
     */
    @Getter
    protected Constants.RelationType relationType = Constants.RelationType.AND;
    /**
     * 每页显示条数，默认 10
     */
    @Getter
    protected long size = 10;
    /**
     * 当前页
     */
    @Getter
    protected long current = 1;
    /**
     * 查询条件
     */
    @Getter
    protected Map<String, Object> params = new HashMap<>();
    /**
     * 查询字段，
     */
    @Getter
    protected List<String> fields = new ArrayList<>();
    /**
     * 排除字段
     */
    @Getter
    protected List<String> excludes = new ArrayList<>();
    /**
     * 排序字段
     */
    @Getter
    protected List<OrderBy> orderBys = new ArrayList<>();
    /**
     * groupBy字段
     */
    @Getter
    protected List<String> groupBys = new ArrayList<>();
    /**
     * 查询子条件集合
     */
    @Getter
    protected List<C> relations = new ArrayList<>();
    /**
     * 查询对象字段限定
     */
    @Getter
    protected T selectors;

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
    /**
     * 设置连接符
     * @param relationType
     */
    public C setRelationType(Constants.RelationType relationType) {
        this.relationType = relationType;
        return (C) this;
    }
    /**
     * 设置每页显示条数，默认 10
     */
    public C setSize(long size) {
        this.size = size;
        return (C) this;
    }
    /**
     * 设置当前页
     */
    public C setCurrent(long current) {
        this.current = current;
        return (C) this;
    }
    /**
     * 查询条件
     */
    public C setParams(Map<String, Object> params) {
        this.params = params;
        return (C) this;
    }
    /**
     * 查询字段，
     */
    public C setFields(List<String> fields) {
        this.fields = fields;
        return (C) this;
    }
    /**
     * 排除字段
     */
    public C setExcludes(List<String> excludes) {
        this.excludes = excludes;
        return (C) this;
    }
    /**
     * 排序字段
     */
    public C setOrderBys(List<OrderBy> orderBys) {
        this.orderBys = orderBys;
        return (C) this;
    }
    /**
     * groupBy字段
     */
    public C setGroupBys(List<String> groupBys) {
        this.groupBys = groupBys;
        return (C) this;
    }
    /**
     * 查询子条件集合
     */
    public C setRelations(List<C> relations) {
        this.relations = relations;
        return (C) this;
    }
    /**
     * 查询对象字段限定
     */
    public C setSelectors(T selectors) {
        this.selectors = selectors;
        return (C) this;
    }
}
