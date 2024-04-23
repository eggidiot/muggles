package com.muggles.fun.dynamic;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * 属性配置
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 0.9.0
 */
@Data
@Accessors(chain = true)
public class AttrProp {

    /**
     * 属性名称
     */
    private String name;

    /**
     * 配置对
     */
    private Map<String, String> propPairs;

}
