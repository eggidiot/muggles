package com.muggles.fun.repo.mp.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.github.yitter.contract.IdGeneratorOptions;
import com.github.yitter.idgen.YitIdHelper;
import com.muggles.fun.basic.Constants;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.sql.DataSource;

/**
 * mp默认配置
 * @author y
 * @date 2019/10/29
 */
@Configuration
@ConditionalOnBean(DataSource.class)
public class MybatisPlusConfig implements InitializingBean {

    /**
     * 构造雪花算法所需要的当前设备服务号
     */
    @Value("${worker-id:#{1}}")
    private Short workerId;

    /**
     * 设置selectors参数处理器
     *
     * @return SelectorHandler
     */
    @Bean
    @Order(Constants.DEFAULT_ORDER)
    @ConditionalOnProperty(name = "muggle.fill-selector.enabled", havingValue = "true", matchIfMissing = true)
    public SelectorHandler selectorsHandler() {
        return new SelectorHandler();
    }

    /**
     * 默认自定义ID生成器
     * @return  IdentifierGenerator
     */
    @Bean
    @ConditionalOnMissingBean
    public IdentifierGenerator idGenerator() {
        return new MpIdGenerator();
    }

    /**
     * 设置字段默认提交字段插入
     *
     * @return MetaObjectHandler
     */
    @Bean
    @ConditionalOnMissingBean
    public MetaObjectHandler metaObjectHandler() {
        return new MpMetaObjHandler();
    }

    /**
     * 分页插件
     *
     * @return PaginationInterceptor
     */
    @Bean
    @ConditionalOnMissingBean(MybatisPlusInterceptor.class)
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        interceptor.addInnerInterceptor(optimisticLockerInterceptor());
        return interceptor;
    }

    /**
     * 乐观锁插件
     *
     * @return  乐观锁 插件
     */
    @Bean
    @ConditionalOnMissingBean
    public OptimisticLockerInnerInterceptor optimisticLockerInterceptor() {
        return new OptimisticLockerInnerInterceptor();
    }


    /**
     * 自定义配置选项
     *
     * @return 自定义配置选项
     */
    @Bean
    @ConditionalOnMissingBean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> {

        };
    }

    /**
     * 属性设置完成以后回调设置雪花ID
     */
    @Override
    public void afterPropertiesSet() {
        IdGeneratorOptions options = new IdGeneratorOptions(workerId);
        YitIdHelper.setIdGenerator(options);
    }
}

