package com.muggles.fun.dynamic;

import cn.hutool.core.convert.Convert;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.Clob;
import java.time.LocalDateTime;
import java.util.*;

import static com.muggles.fun.dynamic.Const.*;


/**
 * 实体数据
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 0.9.0
 */
public class EntityData extends LinkedHashMap<String, Object> implements Serializable {
    private static final long serialVersionUID = -1951012511464327448L;

    public EntityData setId(Long id) {
        this.put(ATTR_ID, id);
        return this;
    }

    public Long getId() {
        return this.getLong(ATTR_ID);
    }

    public EntityData setVersion(Integer version) {
        this.put(ATTR_VERSION, version);
        return this;
    }

    public Integer getVersion() {
        return this.getInt(ATTR_VERSION);
    }

    public EntityData setDeleteFlag(boolean isDelete) {
        this.put(ATTR_DELETE_FLAG, isDelete? 1 : 0);
        return this;
    }

    public Integer getDeleteFlag() {
        return this.getInt(ATTR_DELETE_FLAG);
    }

    public EntityData setCreateTime(LocalDateTime time) {
        this.put(ATTR_CREATE_TIME, time);
        return this;
    }

    public LocalDateTime getCreateTime() {
        return this.getLocalDateTime(ATTR_CREATE_TIME);
    }

    public EntityData setUpdateTime(LocalDateTime time) {
        this.put(ATTR_UPDATE_TIME, time);
        return this;
    }

    public LocalDateTime getUpdateTime() {
        return this.getLocalDateTime(ATTR_UPDATE_TIME);
    }

    /**
     * 获得特定类型值
     *
     * @param <T>          值类型
     * @param attr         字段名
     * @param defaultValue 默认值
     * @return 字段值
     */
    public <T> T get(String attr, T defaultValue) {
        final Object result = get(attr);
        return (T) (result != null ? result : defaultValue);
    }

    /**
     * 获取并转换
     *
     * @param attr       字段名
     * @param targetType 目标类型
     */
    public <T> T getConvert(String attr, Class<T> targetType) {
        if(isNull(get(attr))) {
            return null;
        }
        return Convert.convert(targetType, get(attr));
    }

    /**
     * @param attr 字段名
     * @return 字段值
     */
    public Integer getInt(String attr) {
        return getConvert(attr, Integer.class);
    }

    /**
     * @param attr 字段名
     * @return 字段值
     */
    public LocalDateTime getLocalDateTime(String attr) {
        return getConvert(attr, LocalDateTime.class);
    }

    /**
     * @param attr 字段名
     * @return 字段值
     */
    public Long getLong(String attr) {
        return getConvert(attr, Long.class);
    }

    /**
     * 获得Clob类型结果
     *
     * @param attr 参数
     * @return Clob
     */
    public Clob getClob(String attr) {
        return get(attr, null);
    }

    /**
     * 获得Blob类型结果
     *
     * @param attr 参数
     * @return Blob
     */
    public Blob getBlob(String attr) {
        return get(attr, null);
    }

    /**
     * 获得Blob类型结果
     *
     * @param attr 参数
     * @return Blob
     */
    public Boolean getBoolean(String attr) {
        return getConvert(attr, Boolean.class);
    }

    /**
     * @param attrName 字段名
     * @return 字段值
     */
    public <T> List<T> getList(String attrName) {
        return (List<T>) get(attrName);
    }

    public String getStrDefault(String attr, String defaultValue) {
        return Optional.ofNullable(getStr(attr)).orElse(defaultValue);
    }

    /**
     * 获得字符串值<br>
     * 支持Clob、Blob、RowId
     *
     * @param attr 字段名
     * @return 字段对应值
     */
    public String getStr(String attr) {
        final Object obj = get(attr);
        if (obj == null) {
            return null;
        }
        return Convert.convert(String.class, obj);
    }

    private boolean isNull(Object value) {
        if(value == null) {
            return true;
        }
        return value instanceof net.sf.json.JSONNull && Objects.equals(value, "null");
    }

}
