package com.muggles.fun.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muggles.fun.basic.Constants;
import com.muggles.fun.core.config.time.DateTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jackson.JsonMixinModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;


/**
 * jackson配置时间序列对象
 *
 * @author Lenovo
 */
@Configuration
@ConditionalOnClass(ObjectMapper.class)
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
     * @param mixinModule
     * @return
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer(JsonMixinModule mixinModule) {
        return builder -> {
            builder.locale(Locale.CHINA);
            builder.timeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
            builder.simpleDateFormat(Constants.DATE_FORMAT);
            builder.modules(new DateTimeModule(configProperties.getFormat(), configProperties.isTimestamp()), mixinModule);
        };
    }
}

