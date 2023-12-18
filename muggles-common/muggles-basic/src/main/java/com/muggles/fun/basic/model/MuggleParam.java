package com.muggles.fun.basic.model;

import com.muggles.fun.basic.Constants;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.*;
import java.util.function.Consumer;

/**
 * 自定义查询参数，支持简单的模糊查询和等值查询
 * 等值查询的时候传入condition使用对象的属性作为查询值
 * 需要附加检索时
 *
 * @param <T>
 */
@Getter
@NoArgsConstructor
public abstract class MuggleParam<T, C extends MuggleParam<T, C>> {
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
     * 查询不同条件关联关系的集合
     */
    protected List<C> relations = new ArrayList<>();
    /**
     * 查询不同条件关联关系的集合
     */
    protected List<C> subqueries = new ArrayList<>();
    /**
     * 查询对象字段限定
     */
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
     *
     * @param relationType 链接符枚举
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
     * 查询不同条件关联关系的集合
     */
    public C setRelations(List<C> relations) {
        this.relations = relations;
        return (C) this;
    }

    /**
     * 设置子查询集合
     *
     * @param subqueries 子查询集合
     * @return C
     */
    public C setSubqueries(List<C> subqueries) {
        this.subqueries = subqueries;
        return (C) this;
    }

    /**
     * 查询对象字段限定
     */
    public C setSelectors(T selectors) {
        this.selectors = selectors;
        return (C) this;
    }

    /**
     * 指定查询字段
     *
     * @return C
     */
    public C select(String... fields) {
        this.fields.addAll(Arrays.asList(fields));
        return (C) this;
    }

    /**
     * 排除查询字段
     *
     * @param fields 字段名边长数组
     * @return C
     */
    public C excludes(String... fields) {
        this.excludes.addAll(Arrays.asList(fields));
        return (C) this;
    }

    /**
     * 修改or连接符
     *
     * @return C
     */
    public C or() {
        return setRelationType(Constants.RelationType.OR);
    }

    /**
     * 修改and连接符
     *
     * @return C
     */
    public C and() {
        return setRelationType(Constants.RelationType.AND);
    }

    /**
     * 修改or连接符
     *
     * @return C
     */
    @SneakyThrows
    public C relation(Consumer<C> consumer, Constants.RelationType type) {
        C c = (C) this.getClass().newInstance();
        consumer.accept(c.setRelationType(type));
        relations.add(c);
        return (C) this;
    }

    /**
     * or方式连接內联一组条件
     *
     * @param consumer 执行函数
     * @return C
     */
    public C or(Consumer<C> consumer) {
        return relation(consumer, Constants.RelationType.OR);
    }

    /**
     * and方式连接內联一组条件
     *
     * @param consumer 执行函数
     * @return C
     */
    public C and(Consumer<C> consumer) {
        return relation(consumer, Constants.RelationType.AND);
    }

    /**
     * 添加排序字段
     *
     * @param orders 排序字段
     * @return C
     */
    public C orderBy(OrderBy... orders) {
        List<OrderBy> obs = Arrays.asList(orders);
        for (OrderBy ob:obs) {
            if (ob != null) {
                orderBys.add(ob);
            }
        }
        return (C) this;
    }

    /**
     * 添加排序字段
     *
     * @param fields 排序字段
     * @return C
     */
    public C orderBy(String... fields) {
        Arrays.asList(fields).forEach(f -> orderBy(f, Constants.ASC));
        return (C) this;
    }

    /**
     * 添加排序字段
     *
     * @param fields 排序字段
     * @return C
     */
    public C orderByDesc(String... fields) {
        Arrays.asList(fields).forEach(f -> orderBy(f, Constants.DESC));
        return (C) this;
    }

    /**
     * 添加排序字段
     *
     * @param field 字段
     * @param asc   排序方式
     * @return C
     */
    public C orderBy(String field, int asc) {
        OrderBy ob = new OrderBy(field, asc);
        orderBys.add(ob);
        return (C) this;
    }

    /**
     * 添加排序字段
     *
     * @param groups 分组字段
     * @return C
     */
    public C groupBy(String... groups) {
        List<String> gs = Arrays.asList(groups);
        for (String s:gs) {
            if (s != null && !s.trim().isEmpty()) {
                groupBys.add(s);
            }
        }
        return (C) this;
    }

}
