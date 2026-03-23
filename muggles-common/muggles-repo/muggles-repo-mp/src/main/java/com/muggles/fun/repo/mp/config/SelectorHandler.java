package com.muggles.fun.repo.mp.config;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import com.muggles.fun.repo.basic.model.Muggle;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * 全局通知参数处理器
 * 当使用Muggle对象作为参数时，默认设置selector对象
 */
@Slf4j
@RestControllerAdvice
public class SelectorHandler extends RequestBodyAdviceAdapter {

	/**
	 * FlineParam需填充类型属性名称
	 */
	private static final String SELECTOR_NAME = "selector";
	/**
	 * 填充所用的空对象
	 */
	private static final Map<String, Object> EMPTY_OBJECT = MapUtil.newHashMap();
	/**
	 * json书写器
	 */
	@Autowired
	private JsonMapper jsonMapper;

	/**
	 * 判断是否支持当前参数类型
	 *
	 * @param methodParameter the method parameter
	 * @param targetType      the target type, not necessarily the same as the method
	 *                        parameter type, e.g. for {@code HttpEntity<String>}.
	 * @param converterType   the selected converter type
	 * @return boolean
	 */
	@Override
	public boolean supports(MethodParameter methodParameter, @NotNull Type targetType, @NotNull Class<? extends HttpMessageConverter<?>> converterType) {
		Class<?> clazz = methodParameter.getParameterType();
		return Muggle.class.isAssignableFrom(clazz);
	}

	/**
	 * 将重新拼接的参数写回body主体
	 *
	 * @param inputMessage  the request
	 * @param parameter     the target method parameter
	 * @param targetType    the target type, not necessarily the same as the method
	 *                      parameter type, e.g. for {@code HttpEntity<String>}.
	 * @param converterType the converter used to deserialize the body
	 * @return HttpInputMessage
     */
	@NotNull
	@Override
	public HttpInputMessage beforeBodyRead(@NotNull HttpInputMessage inputMessage, @NotNull MethodParameter parameter, @NotNull Type targetType, @NotNull Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
		// 读取入参信息
		byte[] paramBytes = readParam(inputMessage);
		// 扩展入参字段
		byte[] extRequestDataByte = putSelectors(paramBytes);
		// 重新构建HttpInputMessage对象
		return buildHttpInputMessage(extRequestDataByte, inputMessage);
	}

	/**
	 * 读取入参信息
	 *
	 * @param inputMessage http消息
	 * @return byte[]
     */
	private byte[] readParam(HttpInputMessage inputMessage) throws IOException {
		return IoUtil.readBytes(inputMessage.getBody());
	}

	/**
	 * 扩展入参字段
	 *
	 * @param requestDataByte 请求字节数组
	 * @return byte[]
     */
	private byte[] putSelectors(byte[] requestDataByte) throws IOException {
		byte[] requestDataByteNew;
		Map<String, Object> map = jsonMapper.readValue(requestDataByte, new TypeReference<Map<String, Object>>() {
		});
		if (map.get(SELECTOR_NAME) != null) {
			return requestDataByte;
		}
		map.put(SELECTOR_NAME, EMPTY_OBJECT);
		requestDataByteNew = jsonMapper.writeValueAsBytes(map);
		return requestDataByteNew;
	}

	/**
	 * 重新构建HttpInputMessage对象
	 *
	 * @param bytes        请求字节数组
	 * @param inputMessage 请求消息
	 * @return HttpInputMessage
	 */
	private HttpInputMessage buildHttpInputMessage(byte[] bytes, HttpInputMessage inputMessage) {
		InputStream rawInputStream = new ByteArrayInputStream(bytes);
		return new HttpInputMessage() {
			@NotNull
			@Override
			public HttpHeaders getHeaders() {
				return inputMessage.getHeaders();
			}

			@NotNull
			@Override
			public InputStream getBody() throws IOException {
				return rawInputStream;
			}
		};
	}
}
