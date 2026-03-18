package com.muggles.fun.core.handler.view;

import cn.hutool.core.collection.CollUtil;
import com.muggles.fun.basic.handler.IValueHandle;
import com.muggles.fun.basic.handler.IValueHandleChain;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 默认值对象处理器链
 */
@Data
@Accessors(chain = true)
public class ValueHandlerChain implements IValueHandleChain {
    /**
     * 值处理器集合
     */
    List<IValueHandle> chain = CollUtil.newArrayList();
    /**
     * 当前处理器下标
     */
    int currentIndex = 0;
    /**
     * 获取当前处理器处理任务
     * @return  IValueHandle<T,R>
     * @param <T>   入参
     * @param <R>   返回值
     */
    @Override
    public <T, R> IValueHandle<T, R> current() {
        if (currentIndex < chain.size()) {
            return chain.get(currentIndex++);
        }
        return null;
    }
}
