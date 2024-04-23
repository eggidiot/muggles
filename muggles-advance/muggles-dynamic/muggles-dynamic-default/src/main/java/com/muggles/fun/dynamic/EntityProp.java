package com.muggles.fun.dynamic;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 实体配置
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangjun</a>
 * @since 0.9.0
 */
@Data
@Accessors(chain = true)
public class EntityProp {

    /**
     * 配置唯一key
     *
     */
    private String key;

    /**
     * 属性配置列表
     */
    private List<AttrProp> attrProps;

    @JsonIgnore
    private String[] keys;

    public void setKey(String key) {
        this.key = key;
        this.keys = StrUtil.split(key, ":").toArray(new String[0]);
    }

    @JsonIgnore
    public boolean hasBehavior() {
        return keys.length > 1;
    }

    @JsonIgnore
    public String getScene() {
        return keys[0];
    }

    @JsonIgnore
    public String getBehavior() {
        if(hasBehavior()) {
            return keys[1];
        } else {
            return null;
        }
    }

}
