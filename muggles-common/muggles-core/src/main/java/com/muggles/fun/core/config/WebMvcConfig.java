package com.muggles.fun.core.config;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.muggles.fun.core.handler.MuggleParamHandler;
import com.muggles.fun.core.handler.MuggleValueHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.List;

/**
 * 扩展webmvc配置
 */
@Slf4j
@ConfigurationPropertiesScan
@EnableAsync
@EnableScheduling
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "fline.core.web", name = "config", havingValue = "auto", matchIfMissing = true)
public class WebMvcConfig implements InitializingBean {

	/**
	 * 请求响应处理器
	 */
	final RequestMappingHandlerAdapter requestMappingHandlerAdapter;

	/**
	 * jackson书写对象
	 */
	final ObjectMapper objectMapper;

	/**
	 * Invoked by the containing {@code BeanFactory} after it has set all bean properties
	 * and satisfied {@link BeanFactoryAware}, {@code ApplicationContextAware} etc.
	 * <p>This method allows the bean instance to perform validation of its overall
	 * configuration and final initialization when all bean properties have been set.
	 */
	@Override
	public void afterPropertiesSet() {
		//1.设置返回值处理器
		List<HandlerMethodReturnValueHandler> handlers = CollUtil.newArrayList(requestMappingHandlerAdapter.getReturnValueHandlers());
		handlers.add(0, new MuggleValueHandler().setObjectMapper(objectMapper));
		requestMappingHandlerAdapter.setReturnValueHandlers(handlers);
		//2.设置参数处理器
		List<HandlerMethodArgumentResolver> resolvers = CollUtil.newArrayList(requestMappingHandlerAdapter.getArgumentResolvers());
		MuggleParamHandler resolver = new MuggleParamHandler();
		resolver.setObjectMapper(objectMapper);
		resolvers.add(0, resolver);
		requestMappingHandlerAdapter.setArgumentResolvers(resolvers);
	}
}
