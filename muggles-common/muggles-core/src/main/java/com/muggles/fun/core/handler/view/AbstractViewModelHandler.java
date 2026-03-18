package com.muggles.fun.core.handler.view;

import com.muggles.fun.basic.handler.IValueHandle;
import lombok.Getter;
import lombok.Setter;

import java.lang.annotation.Annotation;

/**
 * 视图处理器父类
 */
public abstract class AbstractViewModelHandler implements IValueHandle<Object, Object> {
    /**
     * 指定注解对象
     */
    @Getter
    @Setter
    Annotation anno;
    /**
     * 返回ModeView需要的注解
     * @return  Class
     */
    public abstract Class<? extends Annotation> getAnnotation();
}
