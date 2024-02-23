package com.muggles.fun.basic.convertor;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * 转换器spi缓存工厂类
 *
 * @author tanghao
 * @date 2024/2/18 14:09
 */
public class ConvertHandlerFactory {

    private ConvertHandlerFactory() {
    }

    /**
     * 缓存容器
     */
    private static final Map<String, ITypeConvertor<?, ?>> handlerMap = new HashMap<>();

    // 静态初始化块，用于加载并缓存所有 ConvertHandler 实现
    static {
        loadHandlers();
    }

    /**
     * 加载并缓存 ConvertHandler 实现
     */
    private static void loadHandlers() {
        ServiceLoader<ITypeConvertor> loader = ServiceLoader.load(ITypeConvertor.class);
        for (ITypeConvertor<?, ?> handler : loader) {
            handlerMap.put(handler.getClass().getName(), handler);
        }
    }

    /**
     * 通过转换器类型获取实例
     * @param className
     * @return
     */
    public static ITypeConvertor getHandler(String className) {
        return handlerMap.get(className);
    }

}
