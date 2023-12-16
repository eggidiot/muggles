package com.muggles.fun.core.context;

import com.muggles.fun.core.config.CoreConfigProperties;
import com.muggles.fun.core.config.JacksonConfig;
import com.muggles.fun.core.context.TaskProcessor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

/**
 * 应用启动时启动的方法事件
 */
@Slf4j
@Data
@Configuration
@AutoConfigureAfter(JacksonConfig.class)
public class AppStartedEventListener {

    /**
     * 核心配置参数
     */
    @Autowired
    CoreConfigProperties properties;

    /**
     * tomcat启动完成监听事件
     *
     * @param event
     */
    @EventListener
    public void listener(ApplicationStartedEvent event) {
        log.info(String.format("%s监听到事件源：%s.", ApplicationStartedEvent.class.getName(), event.getSource()));
        if (properties.isTasks()) {
            TaskProcessor.start();
        }
    }
}
