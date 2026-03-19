package com.muggles.fun.repo.basic.model;

import com.muggles.fun.basic.MFunction;
import com.muggles.fun.basic.model.MuggleParam;
import com.muggles.fun.repo.basic.IFieldMapping;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 核心查询数据结构，用于默认通用查询，和特定查询方案
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class Muggle<T> extends MuggleParam<T,Muggle<T>> {

    /**
     * 是否执行count标记
     */
    protected Boolean searchCount = true;
    /**
     * 属性映射字段工具
     */
    private IFieldMapping mapping;
    /**
     * 内置属性映射字段工具
     */
    @Setter
    static Class<? extends IFieldMapping> mappingClazz = DefaultMapping.class;

    /**
     * 无参构造方法
     */
    @SneakyThrows
    public Muggle() {
        // 获取无参构造器
        Constructor<? extends IFieldMapping> constructor = mappingClazz.getDeclaredConstructor();
        // 创建实例
        mapping = constructor.newInstance();
    }

    /**
     * 生成MP的分页组件，用于查询，只传递页数和单页大小
     *
     * @return FlinePage<T>
     */
    public MugglePage<T> page() {
        return new MugglePage<T>().setCurrent(getCurrent()).setSize(getSize());
    }

    /**
     * 指定查询字段
     *
     * @param fields 字段边长数组
     * @return FlineParam<T>
     */
    public Muggle<T> fields(String... fields) {
        this.fields.addAll(Arrays.asList(fields));
        return this;
    }

    /**
     * 生成MP的分页组件，用于查询，只传递页数和单页大小
     *
     * @return FlineParam<T>
     */
    public Muggle<T> fields(MFunction<T, ?>... fields) {
        List<MFunction<T, ?>> list = Arrays.asList(fields);
        this.fields.addAll(list.stream().map(this::columnsToString).toList());
        return this;
    }

    /**
     * 指定查询字段
     *
     * @return FlineParam<T>
     */
    public Muggle<T> select(String... fields) {
        return fields(fields);
    }

    /**
     * 指定查询字段
     *
     * @param fields 字段名边长数组
     * @return FlineParam<T>
     */
    public Muggle<T> select(MFunction<T, ?>... fields) {
        return fields(fields);
    }

    /**
     * 排除查询字段
     *
     * @param fields 字段名边长数组
     * @return FlineParam<T>
     */
    public Muggle<T> excludes(String... fields) {
        this.excludes.addAll(Arrays.asList(fields));
        return this;
    }

    /**
     * 获取字段名称
     *
     * @param attribute 字段属性名称
     * @return String
     */
    protected String columnsToString(MFunction<T, ?> attribute) {
        return mapping.fieldMappingColum(attribute);
    }


}
