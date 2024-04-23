package com.muggles.fun.dynamic;


import cn.hutool.core.util.StrUtil;

import java.util.Collection;
import java.util.Collections;

/**
 * 内置抽象的实体结构
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangjun</a>
 * @since 0.9.0
 */
public abstract class AbstractInternalEntitySchema implements EntitySchema {


    /**
     * 实体名称，一般也是表名，一般满足下划线的形式。
     */
    @Override
    public final String getName() {
        String entityName = StrUtil.lowerFirst(this.getClass().getSimpleName());
        if (entityName.endsWith("Schema")) {
            return entityName.substring(0, entityName.length() - 6);
        }
        return entityName;
    }

    /**
     * 描述
     */
    @Override
    public String getDescription() {
        return "";
    }

    /**
     * 属性
     */
    @Override
    public final Attr[] getAttrs() {
        return getAttrMetas();
    }

    /**
     * 属性
     */
    protected abstract AttrMeta[] getAttrMetas();

    /**
     * 实体配置
     */
    public Collection<EntityProp> getEntityProp() {
        return Collections.emptyList();
    }

}
