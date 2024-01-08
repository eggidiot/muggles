package com.muggles.fun.repo.mp.criteria;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.muggles.fun.repo.basic.convert.ParamsConverter;
import com.muggles.fun.repo.basic.criteria.QueryCriteria;
import com.muggles.fun.repo.basic.model.Muggle;
import lombok.experimental.UtilityClass;

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
     * @param muggle    通用查询条件
     * @return          Mp查询对象
     * @param <T>       泛型
     */
    public <T>QueryWrapper<T> translate(Muggle<T> muggle){
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        //1.判断查询参数字典是否为空，如不为空则将查询参数转成查询条件集合
        if (MapUtil.isNotEmpty(muggle.getParams())) {
            List<QueryCriteria> cs = ParamsConverter.conertMap2Criterias(muggle.getParams());
            CollUtil.addAll(muggle.getCriterias(), cs);
        }
        //2.判断直接查询条件集合是否为空，如果不为空则直接使用查询条件查询
        if (CollUtil.isNotEmpty(muggle.getCriterias())) {
            mapCriteria2Colum(muggle.getCriterias()).stream()
                    .filter(t -> CollUtil.isEmpty(this.colums) || CollUtil.contains(this.colums, t.getAttribute()))
                    .forEach(c -> c.translate(queryWrapper));
        }
        //3.处理子条件集合
        muggle.getRelations().forEach(r -> WrapperTranslator.translate(r));
        //4.设置groupby
        queryWrapper.groupBy(CollUtil.isNotEmpty(muggle.getGroupBys()),
                muggle.getGroupBys().stream().map(StrUtil::toUnderlineCase).collect(Collectors.toList()));
        //5.根据查询对象设置查询字段
        if (selectors != null) {
            //5.1获取类型的属性列表
            Map<String, Object> selectorMap = MapUtil.newHashMap();
            //5.2过滤非NULL字段
            BeanUtil.beanToMap(selectors, selectorMap,
                    CopyOptions.create().setIgnoreError(true).setIgnoreNullValue(true));
            //5.3找出查询字段集合
            List<String> names = selectorMap.keySet().stream().filter(key -> CollUtil.contains(this.colums, field2ColomMap.getOrDefault(key, key)))
                    .collect(Collectors.toList());
            //5.4设置selector作为查询字段则将不为null的属性值拼接到fields字段集合中去
            if (CollUtil.isNotEmpty(names)) {
                CollUtil.addAll(fields, names);
            }
            //5.5判断是否有排除字段的逻辑，有排除逻辑则默认获取所有的数据
            if (CollUtil.isEmpty(fields) && CollUtil.isNotEmpty(excludes)) {
                CollUtil.addAll(fields, this.colums);
            }
        }
        //6.根据查询字段值设置查询字段
        if (CollUtil.isNotEmpty(fields)) {
            fields = mapField2Colum(fields).stream().filter(f -> !mapField2Colum(excludes).contains(f))
                    .collect(Collectors.toList());
            if (CollUtil.isEmpty(fields)) {
                throw new FlineBizException("至少需要有一个查询字段");
            }
            queryWrapper.select(fields);
        }
        //7.添加排序字段
        if (CollUtil.isEmpty(orderBys) && defaultOrderBy) {
            orderBys.add(new OrderBy("id", RepoConstants.DESC));
        }
        orderBys.forEach(o -> queryWrapper.orderBy(o != null && StrUtil.isNotBlank(o.getField()), o.isAsc(),
                StrUtil.toUnderlineCase(o.getField())));
        return queryWrapper;
    }

    /**
     * 根据字段属性名称获取表字段名称
     * @param field 属性名称
     * @return
     */
    public String column(String field){
        return column(null, field);
    }

    /**
     * 根据对象和属性名称获取对应的数据库属性名称
     * @param entityClass
     * @param field
     * @return
     * @param <T>
     */
    public <T>String column(Class<T> entityClass,String field){
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
}
