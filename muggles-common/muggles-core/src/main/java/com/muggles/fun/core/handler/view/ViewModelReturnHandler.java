package com.muggles.fun.core.handler.view;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReflectUtil;
import com.muggles.fun.basic.anno.ViewModel;
import com.muggles.fun.basic.converter.IViewConverter;
import com.muggles.fun.basic.exception.MugglesBizException;
import com.muggles.fun.basic.model.IMugglePage;
import com.muggles.fun.tools.core.spel.SpelUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

/**
 * 转换VO对象的分装
 */
@Slf4j
@Data
@Accessors(chain = true)
public class ViewModelReturnHandler implements HandlerMethodReturnValueHandler {
	/**
	 * JACKSON书写JSON对象
	 */
	private JsonMapper jsonMapper;


	/**
	 * Whether the given {@linkplain MethodParameter method return type} is
	 * supported by this handler.
	 *
	 * @param returnType the method return type to check
	 * @return {@code true} if this handler supports the supplied return type;
	 * {@code false} otherwise
	 */
	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		return returnType.hasParameterAnnotation(ViewModel.class) || returnType.hasMethodAnnotation(ViewModel.class);
	}

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
	public void handleReturnValue(Object returnValue, @NotNull MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
		mavContainer.setRequestHandled(true);
		HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
		assert response != null;
		response.setContentType("application/json;charset=UTF-8");
		if (returnValue == null) {
			return;
		}
		//1.获取转换目标参数
		ViewModel toVo = returnType.getMethodAnnotation(ViewModel.class);
		assert toVo != null;
		Class<?> converterClass = toVo.converter();
		Assert.notEquals(converterClass, IViewConverter.class,()->new MugglesBizException("没有设置正确的视图转换器"));
		//2.看看外层包裹的是否是ResponseResult
		Object data = SpelUtil.dataInfo(toVo.dataKey(), returnValue);
		if (data == null) {
			response.getWriter().write(jsonMapper.writeValueAsString(returnValue));
			return;
		}
		//3.通过转换器转换视图对象
		IViewConverter<?,?> func = (IViewConverter<?,?>) ReflectUtil.newInstance(converterClass);
		response.getWriter().write(jsonMapper.writeValueAsString(convert(data, toVo.dataKey(), func)));
	}

	/**
	 * 返回视图转换以后的对象
	 * @param data		原始对象
	 * @param spel		spel表达式
	 * @param converter	转换器
	 * @return	Object
	 */
	Object convert(Object data,String spel, IViewConverter converter) {
		if (List.class.isAssignableFrom(data.getClass())) {
			return SpelUtil.wrapperInfo(spel, data, converter.applyList((List) data));
		} else if (IMugglePage.class.isAssignableFrom(data.getClass())) {
			return SpelUtil.wrapperInfo(spel, data, converter.applyPage((IMugglePage) data));
		} else {
			return SpelUtil.wrapperInfo(spel, data, converter.apply(data));
		}
	}

}
