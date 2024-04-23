package com.muggles.fun.dynamic;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.function.Supplier;

/**
 * 属性元
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 0.9.0
 */
@Data
@Accessors(chain = true)
public class AttrMeta implements Attr {
    private static final long serialVersionUID = 1L;

    /**
     * 属性名，驼峰命名。
     */
    private String name;

    /**
     * 属性类型
     */
    private String attrType;

    /**
     * 属性名展示
     */
    private String label;

    /**
     * 描述
     */
    private String description;

    /**
     * 引用 <br/>
     */
    private String reference;

    /**
     * 是否非空 <br/>
     */
    private boolean notNull;

    /**
     * 是否唯一 <br/>
     */
    private boolean unique;

    /**
     * 是否不可变 <br/>
     */
    private boolean immutable;

    /**
     * 是否内置 <br/>
     */
    private boolean builtIn;

    /**
     * 是否隐藏 <br/>
     */
    private boolean hide;

    /**
     * 读取排除 <br/>
     */
    private boolean readExclude;

    /**
     * 顺序 <br/>
     */
    private int ordinal;

    /**
     * 数据是否下划线存储形式
     */
    private boolean underLineDb;

    /**
     * 默认值
     */
    private Object defaultValue;

    public AttrMeta(String name, String attrType, String label, boolean notNull, boolean unique, boolean immutable,
                    Integer ordinal, Object defaultValue) {
        this.name = name;
        this.attrType = attrType;
        this.label = label;
        this.description = "";
        this.notNull = notNull;
        this.unique = unique;
        this.immutable = immutable;
        this.builtIn = false;
        this.ordinal = ordinal;
        this.underLineDb = true;
        this.defaultValue = defaultValue;
    }

    public AttrMeta(String name, String attrType, String label, String description, boolean notNull, boolean unique, boolean immutable,
                    Integer ordinal, Object defaultValue) {
        this.name = name;
        this.attrType = attrType;
        this.label = label;
        this.description = description;
        this.notNull = notNull;
        this.unique = unique;
        this.immutable = immutable;
        this.builtIn = false;
        this.ordinal = ordinal;
        this.underLineDb = true;
        this.defaultValue = defaultValue;
    }

    public AttrMeta(String name, String attrType, String label, String description, String reference,
                    boolean notNull, boolean unique, boolean immutable, boolean builtIn,
                    Integer ordinal, Object defaultValue) {
        this.name = name;
        this.attrType = attrType;
        this.label = label;
        this.description = description;
        this.reference = reference;
        this.notNull = notNull;
        this.unique = unique;
        this.immutable = immutable;
        this.builtIn = builtIn;
        this.ordinal = ordinal;
        this.underLineDb = true;
        this.defaultValue = defaultValue;
    }

    public AttrMeta(String name, String attrType, String label, String description, boolean notNull, boolean unique, boolean immutable,
                    Integer ordinal, Supplier<Object> defaultValueSetter) {
        this.name = name;
        this.attrType = attrType;
        this.label = label;
        this.description = description;
        this.notNull = notNull;
        this.unique = unique;
        this.immutable = immutable;
        this.builtIn = false;
        this.ordinal = ordinal;
        this.underLineDb = true;
        this.defaultValue = defaultValueSetter;
    }

    public AttrMeta(String name, String attrType, String label, String description, String reference, boolean notNull, boolean unique, boolean immutable,
                    Integer ordinal, Object defaultValue) {
        this.name = name;
        this.attrType = attrType;
        this.label = label;
        this.description = description;
        this.reference = reference;
        this.notNull = notNull;
        this.unique = unique;
        this.immutable = immutable;
        this.builtIn = false;
        this.ordinal = ordinal;
        this.underLineDb = true;
        this.defaultValue = defaultValue;
    }

    /**
     * 属性字段名
     */
    @Override
    public String getFieldName() {
        return name;
    }

    /**
     * 数据库列名
     */
    @Override
    public String getColumnName() {
        if (underLineDb) {
            return StrUtil.toUnderlineCase(name);
        } else {
            return name;
        }
    }

    @Override
    public Object getDefaultValue() {
        if (defaultValue instanceof Supplier<?>) {
            return ((Supplier) defaultValue).get();
        } else {
            return defaultValue;
        }
    }

    /**
     * 常用的字段属性（可为空、非唯一、可变、非内置）
     * @return AttrMeta
     */
    public static AttrMeta ofNormal(String name, String attrType, String label, String description, Integer ordinal, Object defaultValue) {
        return new AttrMeta(name, attrType, label, description, null, false, false, false, false, ordinal, defaultValue);
    }

    /**
     * 不可变字段属性（不可为空、唯一、不可变、非内置）
     * @return AttrMeta
     */
    public static AttrMeta ofImmutable(String name, String attrType, String label, String description, Integer ordinal, Object defaultValue) {
        return new AttrMeta(name, attrType, label, description, null, true, true, true, false, ordinal, defaultValue);
    }

    /**
     * 内置字段属性（不可为空、唯一、不可变、内置）
     * @return AttrMeta
     */
    public static AttrMeta ofImmutableBuildIn(String name, String attrType, String label, String description,
                                              Integer ordinal, Object defaultValue) {
        return new AttrMeta(name, attrType, label, description, null,
                true, true, true, true, ordinal, defaultValue);
    }

    /**
     * 内置字段属性（不可为空、唯一、不可变、内置）
     * @return AttrMeta
     */
    public static AttrMeta ofImmutableBuildIn(String name, String attrType, String label, String description,
                                              Integer ordinal, Supplier<Object> defaultValueSupplier) {
        return new AttrMeta(name, attrType, label, description, null,
                true, true, true, true, ordinal, defaultValueSupplier);
    }

    /**
     * 内置字段属性（不可为空、唯一、可变、内置）
     * @return AttrMeta
     */
    public static AttrMeta ofBuildIn(String name, String attrType, String label, String description,
                                              Integer ordinal, Object defaultValue) {
        return new AttrMeta(name, attrType, label, description, null,
                true, true, false, true, ordinal, defaultValue);
    }

    /**
     * 内置字段属性（不可为空、唯一、可变、内置）
     * @return AttrMeta
     */
    public static AttrMeta ofBuildIn(String name, String attrType, String label, String description,
                                              Integer ordinal, Supplier<Object> defaultValueSupplier) {
        return new AttrMeta(name, attrType, label, description, null,
                true, true, false, true, ordinal, defaultValueSupplier);
    }

    /**
     * 常用的应用字段属性（可为空、非唯一、可变、非内置）
     * @return AttrMeta
     */
    public static AttrMeta ofNormalReference(String name, String attrType, String label, String description, Integer ordinal,
                                             String reference, Object defaultValue) {
        return new AttrMeta(name, attrType, label, description, reference, false, false, false, false, ordinal, defaultValue);
    }
}
