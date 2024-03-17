package com.muggles.fun.dynamic;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 实体数据接口
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 0.2.1
 */
public interface EntityData extends Serializable, Map<String, Object> {

    /**
     * 表名
     */
    String getTableName();

    /**
     * 主键
     */
    Long getId();

    /**
     * 获取创建时间
     */
    LocalDateTime getCreateTime();

    /**
     * 获取更新时间
     */
    LocalDateTime getUpdateTime();

    /**
     * 获取版本号
     *
     * @return null: 不存在这个字段
     */
    Integer getVersion();

    /**
     * 获取是否删除标志
     *
     * @return null: 不存在这个字段；false: 被逻辑删除；true：未被逻辑删除
     */
    Boolean getDeleteFlag();

}
