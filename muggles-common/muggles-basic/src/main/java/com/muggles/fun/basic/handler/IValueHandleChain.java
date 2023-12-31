package com.muggles.fun.basic.handler;

import java.util.List;

/**
 * 值处理器链对象
 */
public interface IValueHandleChain {

    /**
     * 本责任链是否支持对象处理
     * @param value 呗处理对象
     * @return
     */
    default boolean support(Object value){
        boolean res = false;
        List<IValueHandle> chain = getChain();
        for (IValueHandle handle: chain) {
            res = handle.support(value);
            if (res) {
                break;
            }
        }
        return res;
    }

    /**
     * 获取链中所有元素
     * @return
     */
    List<IValueHandle> getChain();
    /**
     * 注册值处理器
     * @param handle
     * @param index
     * @return
     */
    default List<IValueHandle> register(IValueHandle handle, Integer index){
        List<IValueHandle> chain = getChain();
        //1.插入位置大于或者等于目前链中元素个数，则插入在链末端
        if (index >= chain.size()) {
            chain.add(handle);
        //2.插入位置小于0则在头部插入
        } else if (index < 0) {
            chain.add(0, handle);
        //3.正常插入
        } else {
            chain.add(index, handle);
        }
        return chain;
    }
    /**
     * 注册
     * @param handle
     * @return
     */
    default List<IValueHandle> register(IValueHandle handle){
        int size = getChain().size();
        return register(handle, size);
    }

    /**
     * 注册
     * @param handle
     * @return
     */
    default List<IValueHandle> registerBefore(IValueHandle handle, Class<? extends IValueHandle> beforeType){
        List<IValueHandle> chain = getChain();
        int index = 0;
        for (IValueHandle vh:chain) {
            if (vh.getClass().equals(beforeType)) {
                break;
            }
            index++;
        }
        return register(handle, index);
    }

    /**
     * 注册处理器在指定类型之后
     * @param handle
     * @return
     */
    default List<IValueHandle> registerAfter(IValueHandle handle, Class<? extends IValueHandle> beforeType){
        List<IValueHandle> chain = getChain();
        int index = 0;
        for (IValueHandle vh:chain) {
            if (vh.getClass().equals(beforeType)) {
                break;
            }
            index++;
        }
        return register(handle, index + 1);
    }
    /**
     * 值处理器核心方法
     * @param value
     * @return
     */
    default Object process(Object value){
        return value;
    }
}
