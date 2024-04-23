package com.muggles.fun.dynamic;


import com.muggles.fun.dynamic.mask.MaskParam;

/**
 * 可以视图化的参数接口
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 0.9.0
 */
public interface ViewParam {

    /**
     * 场景
     */
    String getScene();
    /**
     * 动作
     */
    String getBehavior();

    MaskParam getMasks();

}
