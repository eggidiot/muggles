package com.muggles.fun.repo.mp.criteria;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muggles.fun.basic.exception.MugglesBizException;
import com.muggles.fun.basic.model.MuggleParam;
import com.muggles.fun.repo.basic.converter.ParamsConverter;
import com.muggles.fun.repo.basic.criteria.QueryCriteria;
import com.muggles.fun.repo.basic.model.Muggle;
import com.muggles.fun.repo.mp.criteria.gen.MpCriteria;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 将查询对象转换成对应的查询
 */
@UtilityClass
public class WrapperTranslator {

    /**
     * 获取实体类的Java属性名列表
     *
     * @param entityClass 实体类型
     * @return 属性名列表
     */
    private List<String> getEntityFieldNames(Class<?> entityClass) {
        TableInfo table = TableInfoHelper.getTableInfo(entityClass);
        if (table == null) {
            return CollUtil.newArrayList();
        }
        return table.getFieldList().stream()
                .map(TableFieldInfo::getField)
                .map(Field::getName)
                .collect(Collectors.toList());
    }

    /**
     * 将muggle通用查询条件翻译成Mybatis-plus里使用的QueryWrapper
     *
     * @param muggle 通用查询条件
     * @param <T>    泛型
     * @return Mp查询对象
     */
    public <T> QueryWrapper<T> translate(Muggle<T> muggle) {
        //1.处理查询条件成为mp查询条件
        QueryWrapper<T> wrapper = genCriterias(muggle);
        //2.处理子条件集合
        muggle.getRelations().forEach(r -> WrapperTranslator.translate(r.setEntityClass(muggle.getEntityClass()), wrapper));
        //3.设置查询字段
        List<String> columns = mapField2Colum(muggle.getFields(), muggle.getExcludes(), muggle.getSelector());
        if (CollUtil.isNotEmpty(columns)) {
            wrapper.select(columns);
        }
        //4.设置groupBy（columnLimitByEntity已完成字段名到列名的转换，无需二次映射）
        List<String> groupBys = columnLimitByEntity(muggle.getEntityClass(), muggle.getGroupBys());
        wrapper.groupBy(CollUtil.isNotEmpty(groupBys), groupBys);
        //5.设置orderBy
        List<MuggleParam.OrderBy> orderBys = orderByLimitByEntity(muggle.getEntityClass(), muggle.getOrderBys());
        orderBys.forEach(o -> {
            if (o != null && StrUtil.isNotBlank(o.getField())) {
                wrapper.orderBy(true, o.isAsc(), o.getField());
            }
        });
        return wrapper;
    }

    /**
     * 为已知条件添加条件
     *
     * @param muggle  查询条件
     * @param wrapper mp查询条件
     * @param <T>     泛型类型
     * @return QueryWrapper<T>
     */
    public <T> QueryWrapper<T> translate(Muggle<T> muggle, QueryWrapper<T> wrapper) {
        switch (muggle.getType()) {
            case AND:
                wrapper.and(consumer -> criterias(muggle.getEntityClass(), consumer, muggle.getCriterias()));
                break;
            case OR:
                wrapper.or(consumer -> criterias(muggle.getEntityClass(), consumer, muggle.getCriterias()));
                break;
            case NESTED:
                wrapper.nested(consumer -> criterias(muggle.getEntityClass(), consumer, muggle.getCriterias()));
                break;
        }
        return wrapper;
    }

    /**
     * 将查询条件中的分页信息，转成分页对象
     *
     * @param muggle 查询条件
     * @param <T>    泛型
     * @return Page<T>
     */
    public <T> Page<T> toPage(Muggle<T> muggle) {
        Page<T> page = new Page<>();
        page.setCurrent(muggle.getCurrent()).setSize(muggle.getSize());
        page.setSearchCount(muggle.getSearchCount());
        return page;
    }

    /**
     * 获取查询条件对应的表字段
     *
     * @param muggle 查询条件
     * @param <T>    泛型类型
     * @return List<String>
     */
    public <T> List<String> columns(Muggle<T> muggle) {
        if (muggle != null && muggle.getEntityClass() != null) {
            return columns(muggle.getEntityClass());
        }
        return CollUtil.newArrayList();
    }

    /**
     * 获取查询条件对应的表字段
     *
     * @param entityClass 指定实体类型
     * @param <T>         泛型类型
     * @return List<String>
     */
    public <T> List<String> columns(Class<T> entityClass) {
        if (entityClass != null) {
            TableInfo table = TableInfoHelper.getTableInfo(entityClass);
            if (table != null) {
                List<String> columns = table.getFieldList().stream()
                        .map(TableFieldInfo::getColumn).collect(Collectors.toList());
                CollUtil.addAll(columns, table.getKeyColumn());
                return columns.stream().distinct().collect(Collectors.toList());
            }
        }
        return CollUtil.newArrayList();
    }

    /**
     * 根据字段属性名称获取表字段名称
     *
     * @param field 属性名称
     * @param <T>   泛型类型
     * @return 字段属性名称
     */
    public <T> String column(String field) {
        return column((Class<T>) null, field);
    }

    /**
     * 根据字段属性名称获取表字段名称
     *
     * @param fields 属性名称
     * @param <T>    泛型类型
     * @return 字段属性名称
     */
    public <T> List<String> columns(List<String> fields) {
        return columns((Class<T>) null, fields);
    }

    /**
     * 根据对象和属性名称获取对应的数据库属性名称
     *
     * @param entityClass 指定类型
     * @param field       指定属性
     * @param <T>         泛型类型
     * @return String
     */
    public <T> String column(Class<T> entityClass, String field) {
        if (entityClass != null) {
            TableInfo table = TableInfoHelper.getTableInfo(entityClass);
            if (table != null) {
                for (TableFieldInfo f : table.getFieldList()) {
                    if (f.getField().getName().equals(field)) {
                        return f.getColumn();
                    }
                }
            }
        }
        return StrUtil.toUnderlineCase(field);
    }

    /**
     * 根据对象和属性名称获取对应的数据库属性名称
     *
     * @param entityClass 指定类型
     * @param fields      指定属性集合
     * @param <T>         泛型类型
     * @return List<String>
     */
    public <T> List<String> columns(Class<T> entityClass, List<String> fields) {
        if (CollUtil.isEmpty(fields)) {
            return CollUtil.newArrayList();
        }
        return fields.stream().map(f -> column(entityClass, f)).collect(Collectors.toList());
    }

    /**
     * 查找指定属性对应的字段
     *
     * @param muggle 查询条件
     * @param field  指定属性
     * @param <T>    泛型类型
     * @return String
     */
    public <T> String column(Muggle<T> muggle, String field) {
        return column(muggle != null ? muggle.getEntityClass() : null, field);
    }

    /**
     * 根据查询条件生成mp查询条件
     *
     * @param muggle 查询条件
     * @param <T>    泛型类型
     * @return QueryWrapper<T> mp查询条件
     */
    <T> QueryWrapper<T> genCriterias(Muggle<T> muggle) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        List<QueryCriteria> criterias = CollUtil.newArrayList(muggle.getCriterias());
        if (MapUtil.isNotEmpty(muggle.getParams())) {
            CollUtil.addAll(criterias, ParamsConverter.conertMap2Criterias(muggle.getParams()));
        }
        return criterias(muggle.getEntityClass(), wrapper, criterias);
    }

    /**
     * 根据指定类型限定能够拼接的查询条件
     *
     * @param entityClass 指定类型
     * @param wrapper     mp查询条件
     * @param criterias   业务查询条件集合
     * @param <T>         泛型
     * @return QueryWrapper<T>
     */
    private <T> QueryWrapper<T> criterias(Class<T> entityClass, QueryWrapper<T> wrapper, List<QueryCriteria> criterias) {
        List<QueryCriteria> result = criteriaLimitByEntity(entityClass, criterias);
        if (CollUtil.isNotEmpty(result)) {
            result.forEach(c -> MpCriteria.translate(wrapper, c));
        }
        return wrapper;
    }

    /**
     * 返回对象中可以查询的字段
     * --重点：该方法返回的查询字段不做限定，若对应的实体没有返回指定的数据库字段，则默认返回下划线风格字段名称
     *
     * @param fields   查询字段集合
     * @param excludes 排除查询的字段集合
     * @param selector 查询模型对象
     * @param <T>      泛型类型
     * @return List<String>
     */
    <T> List<String> mapField2Colum(List<String> fields, List<String> excludes, T selector) {
        List<String> result = CollUtil.newArrayList();
        if (selector != null) {
            Map<String, Object> selectorMap = MapUtil.newHashMap();
            BeanUtil.beanToMap(selector, selectorMap,
                    CopyOptions.create().setIgnoreError(true).setIgnoreNullValue(true));
            if (CollUtil.isNotEmpty(selectorMap)) {
                CollUtil.addAll(fields, selectorMap.keySet());
            }
        }
        if (CollUtil.isEmpty(fields) && CollUtil.isEmpty(excludes)) {
            return result;
        }
        Class<?> entityClass = selector != null ? selector.getClass() : null;
        // 修复：removeAll返回boolean而非List，需先获取列表再移除
        List<String> selected = columns(entityClass, fields);
        selected.removeAll(columns(entityClass, excludes));
        CollUtil.addAll(result, selected);
        return result;
    }

    /**
     * 根据指定的实体限定字段，默认策略丢弃不符合的字段
     *
     * @param entityClass 指定实体类型
     * @param fields      指定字段集合
     * @param <T>         泛型类型
     * @return List<String>
     */
    public <T> List<String> columnLimitByEntity(Class<T> entityClass, List<String> fields) {
        Assert.notNull(entityClass, () -> new MugglesBizException("指定实体类型不能为Null"));
        List<String> fieldNames = getEntityFieldNames(entityClass);
        if (CollUtil.isNotEmpty(fieldNames)) {
            return fields.stream()
                    .filter(fieldNames::contains)
                    .map(f -> column(entityClass, f))
                    .collect(Collectors.toList());
        }
        return fields;
    }

    /**
     * 根据指定的实体限定排序字段，默认策略丢弃不符合的字段
     *
     * @param entityClass 指定实体类型
     * @param fields      指定字段集合
     * @param <T>         泛型类型
     * @return List<MuggleParam.OrderBy>
     */
    public <T> List<MuggleParam.OrderBy> orderByLimitByEntity(Class<T> entityClass, List<MuggleParam.OrderBy> fields) {
        Assert.notNull(entityClass, () -> new MugglesBizException("指定实体类型不能为Null"));
        List<String> fieldNames = getEntityFieldNames(entityClass);
        if (CollUtil.isNotEmpty(fieldNames)) {
            return fields.stream()
                    .filter(f -> fieldNames.contains(f.getField()))
                    .map(f -> f.setField(column(entityClass, f.getField())))
                    .collect(Collectors.toList());
        }
        return fields;
    }

    /**
     * 根据指定的实体限定查询条件，默认策略丢弃不符合的字段
     *
     * @param entityClass 指定实体类型
     * @param fields      指定字段集合
     * @param <T>         泛型类型
     * @return List<QueryCriteria>
     */
    public <T> List<QueryCriteria> criteriaLimitByEntity(Class<T> entityClass, List<QueryCriteria> fields) {
        Assert.notNull(entityClass, () -> new MugglesBizException("指定实体类型不能为Null"));
        List<String> fieldNames = getEntityFieldNames(entityClass);
        if (CollUtil.isNotEmpty(fieldNames)) {
            return fields.stream()
                    .filter(f -> fieldNames.contains(f.getAttribute()))
                    .map(f -> f.setAttribute(column(entityClass, f.getAttribute())))
                    .collect(Collectors.toList());
        }
        return fields;
    }
}
