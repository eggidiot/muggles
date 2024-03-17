package com.muggles.fun.dynamic;


import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * 属性接口
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 0.9.0
 */
public interface Attr extends Serializable {

    /**
     * 属性名
     */
    String getName();

    /**
     * 属性字段名
     */
    String getFieldName();

    /**
     * 数据库列名
     */
    String getColumnName();

    /**
     * 属性名展示
     */
    String getLabel();

    /**
     * 属性类型
     */
    String getAttrType();

    /**
     * 描述
     *
     * @return String
     */
    String getDescription();

    @JsonIgnore
    default AttrType findAttrType() {
        return AttrType.getAttrType(getAttrType());
    }

    /**
     * Java类型
     */
    @JsonIgnore
    default Class<?> getJavaType() {
        return AttrType.getJavaType(getAttrType());
    }

    /**
     * 引用 <br/>
     * <ul>
     *     <li>1. 字典类型 {@link Dict}：引用字典名称</li>
     * </ul>
     */
    String getReference();

    /**
     * 默认值
     *
     * @return String
     */
    Object getDefaultValue();

    /**
     * 是否非空
     */
    default boolean isNotNull() {
        return false;
    }

    /**
     * 是否唯一
     */
    default boolean isUnique() {
        return false;
    }

    /**
     * 是否不可变
     */
    default boolean isImmutable() {
        return false;
    }

    /**
     * 是否内置
     */
    default boolean isBuiltIn() {
        return false;
    }

    /**
     * 是否隐藏
     */
    default boolean isHide() {
        return false;
    }

    /**
     * 顺序
     */
    int getOrdinal();

}
