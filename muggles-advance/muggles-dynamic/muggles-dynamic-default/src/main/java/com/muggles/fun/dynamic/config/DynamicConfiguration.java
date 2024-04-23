package com.muggles.fun.dynamic.config;

import cn.hutool.db.Session;
import cn.hutool.db.ds.DSFactory;
import cn.hutool.db.ds.GlobalDSFactory;
import cn.hutool.setting.Setting;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * 配置
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 0.9.0
 */
@Configuration
@ConditionalOnClass(DataSource.class)
@EnableConfigurationProperties({DynamicDsProperties.class})
@AutoConfigureAfter({DataSourceAutoConfiguration.class})
@ComponentScan("com.muggles.fun.dynamic")
public class DynamicConfiguration {

    /**
     * 数据库资源
     */
    private final DataSource dataSource;
    private final Session session;

    private final DynamicDsProperties dynamicDsProperties;

    public DynamicConfiguration(DataSource dataSource, DynamicDsProperties dbProperties) {
        this.dynamicDsProperties = dbProperties;
        this.dataSource = resolveDataSource(dataSource);
        this.session = new Session(this.dataSource);
    }


    /**
     * 如果{@link DynamicDsProperties#isSeparateDs()}成立，则创建独立的数据库连接资源，但是要保证配置合法：<br/>
     * {@link DynamicDsProperties#getDsUrl()} <br/>
     * {@link DynamicDsProperties#getDsDriverClassName()} <br/>
     * {@link DynamicDsProperties#getDsUsername()} <br/>
     * {@link DynamicDsProperties#getDsDriverClassName()} <br/>
     * 如果{@link DynamicDsProperties#isSeparateDs()}不成立，则使用服务本身的连接资源
     *
     * @return {@link DataSource}
     * @see DSFactory
     */
    private DataSource resolveDataSource(DataSource dataSource) {
        DataSource actualDataSource;
        if (!dynamicDsProperties.isSeparateDs()) {
            actualDataSource = dataSource;
        } else {
            Objects.requireNonNull(dynamicDsProperties.getDsUrl());
            Objects.requireNonNull(dynamicDsProperties.getDsDriverClassName());
            Objects.requireNonNull(dynamicDsProperties.getDsUsername());
            Objects.requireNonNull(dynamicDsProperties.getDsPassword());

            Setting setting = new Setting();
            setting.put(DSFactory.KEY_ALIAS_URL[0], dynamicDsProperties.getDsUrl());
            setting.put(DSFactory.KEY_ALIAS_DRIVER[0], dynamicDsProperties.getDsDriverClassName());
            setting.put(DSFactory.KEY_ALIAS_USER[0], dynamicDsProperties.getDsUsername());
            setting.put(DSFactory.KEY_ALIAS_PASSWORD[0], dynamicDsProperties.getDsPassword());

            DSFactory dsFactory = DSFactory.create(setting);
            GlobalDSFactory.set(dsFactory);
            actualDataSource = dsFactory.getDataSource();
        }
        return actualDataSource;
    }

}
