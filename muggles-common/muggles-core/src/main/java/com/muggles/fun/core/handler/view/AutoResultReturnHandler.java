package com.muggles.fun.core.handler.view;

import com.muggles.fun.basic.anno.AutoResult;
import com.muggles.fun.basic.model.R;
import com.muggles.fun.core.handler.MuggleValueHandler;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import tools.jackson.databind.json.JsonMapper;

/**
 * 对正常返回结果的封装
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
@Accessors(chain = true)
public class AutoResultReturnHandler extends MuggleValueHandler {

    /**
     * JACKSON书写JSON对象
     */
    private JsonMapper objectMapper;

    @Override
    public boolean supportsReturnType(@NotNull MethodParameter returnType) {
        return returnType.hasParameterAnnotation(AutoResult.class);
    }

    @Override
    public void handleReturnValue(Object returnValue, @NotNull MethodParameter returnType, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest) throws Exception {
        mavContainer.setRequestHandled(true);
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        assert response != null;
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(R.ok(returnValue)));
    }

}
