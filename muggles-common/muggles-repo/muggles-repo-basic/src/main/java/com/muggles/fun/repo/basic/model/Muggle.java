package com.muggles.fun.repo.basic.model;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.muggles.fun.basic.Constants;
import com.muggles.fun.basic.exception.MugglesBizException;
import com.muggles.fun.basic.model.MuggleParam;
import com.muggles.fun.repo.basic.criteria.CriteriaType;
import com.muggles.fun.repo.basic.criteria.QueryCriteria;
import com.muggles.fun.repo.basic.criteria.between.BetweenParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 核心查询数据结构，用于默认通用查询，和特定查询方案
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class Muggle<T> extends MuggleParam<T, Muggle<T>> {
    /**
     * 取别名
     */
    protected String alias = "t";
    /**
     * 子查询时外部默认方式
     */
    protected String subQueryMethod = "in";
    /**
     * 查询条件，数组形式，父类属性中的params最终也将翻译到该属性
     */
    protected List<QueryCriteria> criterias = new ArrayList<>();
    /**
     * 适用于关联查询的集合
     */
    protected List<Muggle<?>> joins = new ArrayList<>();

    /**
     * 指定查询字段
     *
     * @param fields 字段名边长数组
     * @return FlineParam<T>
     */
    public Muggle<T> select(LambdaFunction<T, ?>... fields) {
        List<LambdaFunction<T, ?>> list = Arrays.asList(fields);
        this.fields.addAll(list.stream().map(this::columnsToString).collect(Collectors.toList()));
        return this;
    }

    /**
     * 添加排序字段
     *
     * @param fields 排序字段
     * @return FlineParam<T>
     */
    public Muggle<T> orderBy(LambdaFunction<T, ?>... fields) {
        Arrays.asList(fields).forEach(f -> orderBy(f, Constants.ASC));
        return this;
    }

    /**
     * 排除查询字段
     *
     * @param fields 字段边长数组
     * @return FlineParam<T>
     */
    public Muggle<T> excludes(LambdaFunction<T, ?>... fields) {
        List<LambdaFunction<T, ?>> list = Arrays.asList(fields);
        this.excludes.addAll(list.stream().map(this::columnsToString).collect(Collectors.toList()));
        return this;
    }

    /**
     * 添加排序字段
     *
     * @param groups 分组字段
     * @return FlineParam<T>
     */
    public Muggle<T> groupBy(LambdaFunction<T, ?>... groups) {
        Assert.notNull(groups, () -> new MugglesBizException("分组字段不能传null值"));
        List<LambdaFunction<T, ?>> list = Arrays.asList(groups);
        CollUtil.addAll(groupBys, list.stream().map(this::columnsToString).collect(Collectors.toList()));
        return this;
    }

    /**
     * 添加排序字段
     *
     * @param field 字段
     * @param asc   排序方式
     * @return FlineParam<T>
     */
    public Muggle<T> orderBy(LambdaFunction<T, ?> field, int asc) {
        return orderBy(columnsToString(field),asc);
    }

    /**
     * 添加相等查询参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> eq(String attribute, Object value) {
        criterias.add(new QueryCriteria(attribute, value, CriteriaType.Equal, type()));
        return this;
    }

    /**
     * 添加不等参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> notEq(String attribute, Object value) {
        criterias.add(new QueryCriteria(attribute, value, CriteriaType.NotEqual, type()));
        return this;
    }

    /**
     * 添加模糊参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> like(String attribute, Object value) {
        criterias.add(new QueryCriteria(attribute, value, CriteriaType.Like, type()));
        return this;
    }

    /**
     * 添加模糊取反参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> notLike(String attribute, Object value) {
        criterias.add(new QueryCriteria(attribute, value, CriteriaType.NotLike, type()));
        return this;
    }

    /**
     * 添加左模糊参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> likeLeft(String attribute, Object value) {
        criterias.add(new QueryCriteria(attribute, value, CriteriaType.LikeLeft, type()));
        return this;
    }

    /**
     * 添加左模糊取反参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> notLikeLeft(String attribute, Object value) {
        criterias.add(new QueryCriteria(attribute, value, CriteriaType.NotLikeLeft, type()));
        return this;
    }

    /**
     * 添加右模糊参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> likeRight(String attribute, Object value) {
        criterias.add(new QueryCriteria(attribute, value, CriteriaType.LikeRight, type()));
        return this;
    }

    /**
     * 添加右模糊取反参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> notLikeRight(String attribute, Object value) {
        criterias.add(new QueryCriteria(attribute, value, CriteriaType.NotLikeRight, type()));
        return this;
    }

    /**
     * 添加大于参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> greaterthan(String attribute, Object value) {
        criterias.add(new QueryCriteria(attribute, value, CriteriaType.Greaterthan, type()));
        return this;
    }

    /**
     * 添加小于参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> lessthan(String attribute, Object value) {
        criterias.add(new QueryCriteria(attribute, value, CriteriaType.Lessthan, type()));
        return this;
    }

    /**
     * 添加小于等于参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> lessthanOrEqual(String attribute, Object value) {
        criterias.add(new QueryCriteria(attribute, value, CriteriaType.LessthanOrEqual, type()));
        return this;
    }

    /**
     * 添加大于等于参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> greaterthanOrEqual(String attribute, Object value) {
        criterias.add(new QueryCriteria(attribute, value, CriteriaType.GreaterthanOrEqual, type()));
        return this;
    }

    /**
     * 添加范围参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> in(String attribute, Collection<?> value) {
        criterias.add(new QueryCriteria(attribute, value, CriteriaType.In, type()));
        return this;
    }

    /**
     * 添加范围参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public <V> Muggle<T> in(String attribute, Muggle<V> value) {
        criterias.add(new QueryCriteria(attribute, value, CriteriaType.SubQuery, type()));
        return this;
    }

    /**
     * 添加null参数
     *
     * @param attribute 参数名
     * @return Muggle<T>
     */
    public Muggle<T> isNull(String attribute) {
        criterias.add(new QueryCriteria(attribute, null, CriteriaType.IsNull, type()));
        return this;
    }

    /**
     * 添加非null参数
     *
     * @param attribute 参数名
     * @return Muggle<T>
     */
    public Muggle<T> isNotNull(String attribute) {
        criterias.add(new QueryCriteria(attribute, null, CriteriaType.IsNotNull, type()));
        return this;
    }

    /**
     * 添加范围参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> between(String attribute, BetweenParam value) {
        criterias.add(new QueryCriteria(attribute, value, CriteriaType.Between, type()));
        return this;
    }

    /**
     * 添加范围反向参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> notIn(String attribute, Collection<?> value) {
        criterias.add(new QueryCriteria(attribute, value, CriteriaType.NotIn, type()));
        return this;
    }

    /**
     * 获取字段名称
     *
     * @param attribute 字段属性名称
     * @return String
     */
    protected String columnsToString(LambdaFunction<T, ?> attribute) {
        return null;
    }
}
