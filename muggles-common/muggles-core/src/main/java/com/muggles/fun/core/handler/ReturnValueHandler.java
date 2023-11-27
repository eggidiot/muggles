package com.muggles.fun.core.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 转换VO对象的分装
 */
@Slf4j
@Data
@Accessors(chain = true)
public abstract class ReturnValueHandler implements HandlerMethodReturnValueHandler {
	/**
	 * JACKSON书写JSON对象
	 */
	private ObjectMapper objectMapper;

	/**
	 * Handle the given return value by adding attributes to the model and
	 * setting a view or setting the
	 * {@link ModelAndViewContainer#setRequestHandled} flag to {@code true}
	 * to indicate the response has been handled directly.
	 *
	 * @param returnValue  the value returned from the handler method
	 * @param returnType   the type of the return value. This type must have
	 *                     previously been passed to {@link #supportsReturnType} which must
	 *                     have returned {@code true}.
	 * @param mavContainer the ModelAndViewContainer for the current request
	 * @param webRequest   the current request
	 * @throws Exception if the return value handling results in an error
	 */
	@Override
	public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {

	}
}
