package com.muggles.fun.core.encrypt;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import lombok.Data;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 加密配置
 */
@EnableEncryptableProperties
@Configuration
@Data
public class JasyptConfig {

    /**
     * 密码加盐
     */
    @Value("${jasypt.encryptor.password:#{muggle-salt}}")
    public String password;

    /**
     * 加密工具类
     */
    @Bean(name = "encryptor")
    StringEncryptor encryptor() {
        DefaultEncryptor encryptor = new DefaultEncryptor();
        encryptor.getStandardPBEStringEncryptor().setPassword(getPassword());
        return encryptor;
    }
}

