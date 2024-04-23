package com.muggles.fun.dynamic.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 配置信息
 */
@Data
@ConfigurationProperties(prefix = DynamicDsProperties.PROPERTIES_PREFIX)
public class DynamicDsProperties {

    public static final String PROPERTIES_PREFIX = "muggles.dynamic";

    // 数据源设置
    /**
     * 是否单独开启数据库配置，默认为否
     */
    private boolean separateDs = false;

    private String dsUrl;

    private String dsDriverClassName;

    private String dsUsername;

    private String dsPassword;
}
