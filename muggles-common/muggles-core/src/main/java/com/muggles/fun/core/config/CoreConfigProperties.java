package com.muggles.fun.core.config;

import com.fline.tp.core.Constants;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 核心配置
 */
@Data
@ConfigurationProperties(prefix = "fline.core.config")
public class CoreConfigProperties {

    /**
     * 日期格式
     */
    @Value("${spring.jackson.date-format:#{'yyyy-MM-dd HH:mm:ss'}}")
    private String format = Constants.DATE_FORMAT;

    /**
     * 是否用时间戳
     */
    @Value("${spring.jackson.serialization.write-dates-as-timestamps:#{false}}")
    private boolean timestamp = false;

    /**
     * 定时任务是否启动
     */
    private boolean tasks = true;
}
