package com.muggles.fun.dynamic;

import cn.hutool.core.util.StrUtil;

import java.io.Serializable;

/**
 * 实体结构接口
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 0.2.1
 */
public interface EntitySchema extends Serializable {
    /**
     * 实体名称，一般也是表名，一般满足下划线的形式。
     */
    String getName();

    /**
     * db存储是否是下划线的形式，默认是 true
     */
    default boolean isDbUnderLineStyle() {
        return true;
    }

    /**
     * 实体表名
     */
    default String getTableName() {
        if (isDbUnderLineStyle()) {
            return StrUtil.toUnderlineCase(getName());
        } else {
            return getName();
        }
    }

    /**
     * 属性
     */
    Attr[] getAttrs();

    /**
     * 描述
     */
    String getDescription();

    /**
     * 是否逻辑删
     */
    default boolean isDeleteLogical() {
        return true;
    }

    /**
     * 是否支持版本
     */
    default boolean isVersionSupport() {
        return true;
    }

}
