package com.muggles.fun.core.handler.view;

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

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * 转换VO对象的分装
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
@Accessors(chain = true)
public class ViewModelReturnHandler extends MuggleValueHandler {
    /**
     * 简易工厂方法模式
     */
    static Map<Class<? extends Annotation>, AbstractViewModelHandler> handlerMap = new LinkedHashMap<>();

    /**
     * Whether the given {@linkplain MethodParameter method return type} is
     * supported by this handler.
     *
     * @param returnType the method return type to check
     * @return {@code true} if this handler supports the supplied return type;
     * {@code false} otherwise
     */
    @Override
    public boolean supportsReturnType(@NotNull MethodParameter returnType) {
        for (Class<? extends Annotation> annoClazz : handlerMap.keySet()) {
            if (returnType.hasParameterAnnotation(annoClazz) || returnType.hasMethodAnnotation(annoClazz)) {
                return true;
            }
        }
        return false;
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
        //1.创建MV处理器责任链
        ViewModelHandlerChain chain = genHandlersDependOnMethodAnnos(returnType);
        response.getWriter().write(getJsonMapper().writeValueAsString(chain.process(returnValue)));
    }

    /**
     * 根据方法注解
     *
     * @param returnType 返回值参数
     * @return List<AbstractViewModelHandler>
     */
    ViewModelHandlerChain genHandlersDependOnMethodAnnos(MethodParameter returnType) {
        List<AbstractViewModelHandler> chain = new ArrayList<>();
        handlerMap.keySet().forEach(annoClazz -> {
            if (returnType.hasParameterAnnotation(annoClazz) || returnType.hasMethodAnnotation(annoClazz)) {
                Annotation anno =  returnType.getParameterAnnotation(annoClazz) == null ? returnType.getMethodAnnotation(annoClazz) : returnType.getParameterAnnotation(annoClazz) ;
                //1.将对应的注解对象设置到对应的处理器中去
                if (anno != null && handlerMap.get(annoClazz) == null) {
                    AbstractViewModelHandler handler = handlerMap.get(annoClazz);
                    handler.setAnno(anno);
                    chain.add(handler);
                }
            }
        });
        return new ViewModelHandlerChain().setChain(chain);
    }

    /**
     * 对指定的模型视图注册对应的视图处理器
     *
     * @param handler 模型视图处理器
     */
    public static void register(AbstractViewModelHandler handler, int index) {
        //1.插入位置大于集合尺寸，默认插入最后
        if (index >= handlerMap.size()) {
            handlerMap.put(handler.getAnnotation(), handler);
            return;
        }
        //2.插入位置小于0，默认插入最前
        Map<Class<? extends Annotation>, AbstractViewModelHandler> oldMap = handlerMap;
        handlerMap = new LinkedHashMap<>();
        if (index < 0) {
            handlerMap.put(handler.getAnnotation(), handler);
            handlerMap.putAll(oldMap);
        }
        //3.插入指定位置
        int i = 0;
        for (Map.Entry<Class<? extends Annotation>, AbstractViewModelHandler> entry : oldMap.entrySet()) {
            if (i++ == index) {
                handlerMap.put(handler.getAnnotation(), handler);
            }
            handlerMap.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 默认插入最后
     * @param handler 模型视图处理器
     */
    public static void register(AbstractViewModelHandler handler) {
        register(handler,handlerMap.size());
    }
}
