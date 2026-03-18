package com.muggles.fun.core.handler.view;

import cn.hutool.core.collection.CollUtil;
import com.muggles.fun.basic.handler.IValueHandle;
import com.muggles.fun.basic.handler.IValueHandleChain;
import com.muggles.fun.tools.core.collections.map.LinkedMapUtil;
import lombok.Data;
import lombok.experimental.Accessors;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * 默认值对象处理器链
 */
@Data
@Accessors(chain = true)
public class ViewModelHandlerChain implements IValueHandleChain {
    /**
     * 值处理器集合
     */
    LinkedHashMap<? extends Annotation,AbstractViewModelHandler> chain = new LinkedHashMap<>();
    /**
     * 当前处理器下标
     */
    int currentIndex = 0;

    @Override
    public AbstractViewModelHandler current() {
        if (currentIndex < chain.size()) {
            return Objects.requireNonNull(LinkedMapUtil.getEntryByIndex(chain, currentIndex)).getValue();
        }
        return null;
    }


}
