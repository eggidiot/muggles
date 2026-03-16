package com.muggles.fun.core.config;

import com.muggles.fun.core.config.time.DateModule;
import com.muggles.fun.core.config.time.DateTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

/**
 * jackson配置时间序列对象
 *
 * @author Lenovo
 */
@Configuration
@ConditionalOnClass(JsonMapper.class)
@AutoConfigureBefore(JacksonAutoConfiguration.class)
@EnableConfigurationProperties(CoreConfigProperties.class)
public class JacksonConfig {

    /**
     * 核心配置参数
     */
    @Autowired
    CoreConfigProperties configProperties;

    /**
     * jackson配置
     *
     * @return JsonMapperBuilderCustomizer
     */
    @Bean
    public JsonMapperBuilderCustomizer customizer() {
        return builder -> {
            builder.addModule(new DateModule());
            builder.addModule(new DateTimeModule(configProperties.getFormat(), configProperties.isTimestamp()));
        };
    }
}

