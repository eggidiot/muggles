package com.muggles.fun.basic.anno;

import com.muggles.fun.basic.converter.IViewConverter;

/**
 * 视图层模型注解
 */
public @interface ViewModel {
    /**
     * 需要转换的VO类对象，需要实现Function接口
     * @return	Class
     */
    Class<? extends IViewConverter> converter() default IViewConverter.class;
    /**
     * 默认token生成键
     *
     * @return  String
     */
    String dataKey() default "#result?.data";
}
