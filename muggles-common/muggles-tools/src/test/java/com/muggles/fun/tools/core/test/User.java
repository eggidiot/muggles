package com.muggles.fun.tools.core.test;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 测试用户类
 */
@Data
@Accessors(chain = true)
public class User {
    /**
     * 主键
     */
    private Long id;
    /**
     * 名称
     */
    private String name;
}
