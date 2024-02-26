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
import com.muggles.fun.basic.exception.MugglesBizException;
import com.muggles.fun.basic.model.MuggleParam;
import com.muggles.fun.repo.basic.convert.ParamsConverter;
import com.muggles.fun.repo.basic.criteria.QueryCriteria;
import com.muggles.fun.repo.basic.model.Muggle;
import com.muggles.fun.repo.mp.MugglePage;
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
        //3.设置join查询
        //TODO
        //4.设置查询字段
        List<String> columns = mapField2Colum(muggle.getFields(), muggle.getExcludes(), muggle.getSelector());
        if (CollUtil.isNotEmpty(columns)) {
            wrapper.select(columns);
        }
        //5.设置groupBy
        List<String> groupBys = columnLimitByEntity(muggle.getEntityClass(),muggle.getGroupBys());
        wrapper.groupBy(CollUtil.isNotEmpty(groupBys),
                groupBys.stream().map(f -> WrapperTranslator.column(muggle, f)).collect(Collectors.toList()));
        //6.设置orderBy
        List<MuggleParam.OrderBy> orderBys = orderByLimitByEntity(muggle.getEntityClass(),muggle.getOrderBys());
        orderBys.forEach(o -> wrapper.orderBy(o != null && StrUtil.isNotBlank(o.getField()), o.isAsc(),o.getField()));
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
        switch (muggle.getType()){
            case AND:
                wrapper.and(consumer-> criterias(muggle.getEntityClass(),consumer,muggle.getCriterias()));
                break;
            case OR:
                wrapper.or(consumer-> criterias(muggle.getEntityClass(),consumer,muggle.getCriterias()));
                break;
            case NESTED:
                wrapper.nested(consumer-> criterias(muggle.getEntityClass(),consumer,muggle.getCriterias()));
                break;
        }
        return wrapper;
    }

    /**
     * 将查询条件中的分页信息，转成分页对象
     * @param muggle    查询条件
     * @return          MugglePage<T>
     * @param <T>       泛型
     */
    public <T> MugglePage<T> toPage(Muggle<T> muggle){
        MugglePage<T> page = new MugglePage<>();
        page.setCurrent(muggle.getCurrent()).setSize(muggle.getSize());
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
                List<String> columns = table.getFieldList().stream().map(TableFieldInfo::getColumn).collect(Collectors.toList());
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
     * @return 字段属性名称
     */
    public List<String> columns(List<String> fields) {
        return columns((Class<?>) null, fields);
    }

    /**
     * 根据对象和属性名称获取对应的数据库属性名称
     *
     * @param entityClass 指定类型
     * @param field       指定属性
     * @return String
     */
    public String column(Class<?> entityClass, String field) {
        if (entityClass != null) {
            TableInfo table = TableInfoHelper.getTableInfo(entityClass);
            if (table != null) {
                List<TableFieldInfo> fields = table.getFieldList();
                for (TableFieldInfo f : fields) {
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
     * @return String
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
     * @return String
     */
    public String column(Muggle<?> muggle, String field) {
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
        //1.判断查询参数字典是否为空，如不为空则将查询参数转成查询条件集合
        if (MapUtil.isNotEmpty(muggle.getParams())) {
            List<QueryCriteria> cs = ParamsConverter.conertMap2Criterias(muggle.getParams());
            CollUtil.addAll(criterias, cs);
        }
        //2.返回拼装好后的查询条件
        return criterias(muggle.getEntityClass(), wrapper, criterias);
    }

    /**
     * 根据指定类型限定能够拼接的查询条件
     * @param entityClass   指定类型
     * @param wrapper       mp查询条件
     * @param criterias     业务查询条件集合
     * @return              QueryWrapper<T>
     * @param <T>           泛型
     */
    private <T>QueryWrapper<T> criterias(Class<T> entityClass, QueryWrapper<T> wrapper,List<QueryCriteria> criterias){
        //2.判断直接查询条件集合是否为空，如果不为空则直接使用查询条件查询
        List<QueryCriteria> result = criteriaLimitByEntity(entityClass, criterias);
        if (CollUtil.isNotEmpty(result)) {
            result.forEach(c -> MpCriteria.translate(wrapper, c));
        }
        return wrapper;
    }

    /**
     * 返回对象中可以查询的字段，
     * --重点：该方法返回的查询字段不做限定，若对应的实体没有返回指定的数据库字段，则默认返回下划线风格字段名称，因为真实的查询中，字段可能存在别名或者聚合函数等名称
     *
     * @param fields   查询字段集合
     * @param excludes 排除查询的字段集合
     * @param selector 查询模型对象
     * @param <T>      泛型类型
     * @return List<String>
     */
    <T> List<String> mapField2Colum(List<String> fields, List<String> excludes, T selector) {
        //0.设置返回值为空集合
        List<String> result = CollUtil.newArrayList();
        if (selector != null) {
            //0.1获取类型的属性列表
            Map<String, Object> selectorMap = MapUtil.newHashMap();
            //0.2过滤非NULL字段
            BeanUtil.beanToMap(selector, selectorMap,
                    CopyOptions.create().setIgnoreError(true).setIgnoreNullValue(true));
            if (CollUtil.isNotEmpty(selectorMap)) {
                CollUtil.addAll(fields, selectorMap.keySet());
            }
        }
        //1.若没有设置查询字段且没设置排除字段则采用默认查询
        if (CollUtil.isEmpty(fields) && CollUtil.isEmpty(excludes)) {
            return result;
        }
        //2.根据实体类型设置字段
        Class<?> entityClass = selector != null ? selector.getClass() : null;
        CollUtil.addAll(result, columns(entityClass, fields).removeAll(columns(entityClass, excludes)));
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
        Assert.notNull(entityClass,()->new MugglesBizException("指定实体类型不能为Null"));
        TableInfo table = TableInfoHelper.getTableInfo(entityClass);
        List<String> fieldList = table.getFieldList().stream().map(TableFieldInfo::getField).map(Field::getName).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(fieldList)){
            List<String> result = fields.stream().filter(fieldList::contains).collect(Collectors.toList());
            return result.stream().map(f->column(entityClass,f)).collect(Collectors.toList());
        }
        return fields;
    }

    /**
     * 根据指定的实体限定字段，默认策略丢弃不符合的字段
     *
     * @param entityClass 指定实体类型
     * @param fields      指定字段集合
     * @param <T>         泛型类型
     * @return List<String>
     */
    public <T> List<MuggleParam.OrderBy> orderByLimitByEntity(Class<T> entityClass, List<MuggleParam.OrderBy> fields) {
        Assert.notNull(entityClass,()->new MugglesBizException("指定实体类型不能为Null"));
        TableInfo table = TableInfoHelper.getTableInfo(entityClass);
        List<String> fieldList = table.getFieldList().stream().map(TableFieldInfo::getField).map(Field::getName).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(fieldList)){
            List<MuggleParam.OrderBy> result = fields.stream().filter(f->fieldList.contains(f.getField())).collect(Collectors.toList());
            return result.stream().map(f->f.setField(column(entityClass,f.getField()))).collect(Collectors.toList());
        }
        return fields;
    }

    /**
     * 根据指定的实体限定字段，默认策略丢弃不符合的字段
     *
     * @param entityClass 指定实体类型
     * @param fields      指定字段集合
     * @param <T>         泛型类型
     * @return List<String>
     */
    public <T> List<QueryCriteria> criteriaLimitByEntity(Class<T> entityClass, List<QueryCriteria> fields) {
        Assert.notNull(entityClass,()->new MugglesBizException("指定实体类型不能为Null"));
        TableInfo table = TableInfoHelper.getTableInfo(entityClass);
        List<String> fieldList = table.getFieldList().stream().map(TableFieldInfo::getField).map(Field::getName).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(fieldList)){
            List<QueryCriteria> result = fields.stream().filter(f->fieldList.contains(f.getAttribute())).collect(Collectors.toList());
            return result.stream().map(f->f.setAttribute(column(entityClass,f.getAttribute()))).collect(Collectors.toList());
        }
        return fields;
    }
}
