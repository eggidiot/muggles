package com.muggles.fun.core.config;

import cn.hutool.core.collection.CollUtil;

import com.muggles.fun.basic.anno.ViewModel;
import com.muggles.fun.core.handler.MuggleParamHandler;
import com.muggles.fun.core.handler.MuggleValueHandler;
import com.muggles.fun.core.handler.view.AutoUserResolverHandler;
import com.muggles.fun.core.handler.view.ViewModelReturnHandler;
import com.muggles.fun.core.handler.view.ViewModelhandler;
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
import tools.jackson.databind.json.JsonMapper;

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
@ConditionalOnProperty(prefix = "muggle.core.web", name = "config", havingValue = "auto", matchIfMissing = true)
public class WebMvcConfig implements InitializingBean {

	/**
	 * 请求响应处理器
	 */
	final RequestMappingHandlerAdapter requestMappingHandlerAdapter;

	/**
	 * jackson书写对象
	 */
	final JsonMapper jsonMapper;

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
		handlers.addFirst(new ViewModelReturnHandler().setJsonMapper(jsonMapper));
		requestMappingHandlerAdapter.setReturnValueHandlers(handlers);
		//2.设置参数处理器
		List<HandlerMethodArgumentResolver> resolvers = CollUtil.newArrayList(requestMappingHandlerAdapter.getArgumentResolvers());
		AutoUserResolverHandler resolver = new AutoUserResolverHandler();
		resolver.setJsonMapper(jsonMapper);
		resolvers.addFirst(resolver);
		requestMappingHandlerAdapter.setArgumentResolvers(resolvers);
		//3.此处设置模型视图转换处理器
		ViewModelReturnHandler.register(new ViewModelhandler());
	}
}
