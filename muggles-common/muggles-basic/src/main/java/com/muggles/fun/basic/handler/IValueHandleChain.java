package com.muggles.fun.basic.handler;

import java.util.List;

/**
 * 值处理器链对象
 */
public interface IValueHandleChain {
    /**
     * 获取链中所有元素
     * @return  List<IValueHandle<T,R>>
     */
    <T,R>List<IValueHandle<T,R>> getChain();
    /**
     * 注册值处理器
     * @param handle    处理器
     * @param index     处理器下标
     * @return  List<IValueHandle<T,R>>
     */
    default <T,R>IValueHandleChain register(IValueHandle<T,R> handle, Integer index){
        List<IValueHandle<T,R>> chain = getChain();
        //1.插入位置大于或者等于目前链中元素个数，则插入在链末端
        if (index >= chain.size()) {
            chain.add(handle);
        //2.插入位置小于0则在头部插入
        } else if (index < 0) {
            chain.addFirst(handle);
        //3.正常插入
        } else {
            chain.add(index, handle);
        }
        return this;
    }

    /**
     * 获取当前处理器处理任务
     * @return  IValueHandle<T,R>
     * @param <T>   入参
     * @param <R>   返回值
     */
    <T,R>IValueHandle<T,R> current();
    /**
     * 注册
     * @param handle    注册处理器
     * @return  List<IValueHandle<T,R>>
     */
    default <T,R>IValueHandleChain register(IValueHandle<T,R> handle){
        int size = getChain().size();
        return register(handle, size);
    }

    /**
     * 注册
     * @param handle    注册处理器
     * @return  List<IValueHandle<T,R>>
     */
    default <T,R>IValueHandleChain registerBefore(IValueHandle<T,R> handle, Class<? extends IValueHandle<T,R>> beforeType){
        List<IValueHandle<T,R>> chain = getChain();
        int index = 0;
        for (IValueHandle<?,?> vh:chain) {
            if (vh.getClass().equals(beforeType)) {
                break;
            }
            index++;
        }
        return register(handle, index);
    }

    /**
     * 注册处理器在指定类型之后
     * @param handle    注册处理器
     * @return  List<IValueHandle<T,R>>
     */
    default <T,R>IValueHandleChain registerAfter(IValueHandle<T,R> handle, Class<? extends IValueHandle<T,R>> beforeType){
        List<IValueHandle<T,R>> chain = getChain();
        int index = 0;
        for (IValueHandle<T,R> vh:chain) {
            if (vh.getClass().equals(beforeType)) {
                break;
            }
            index++;
        }
        return register(handle, index + 1);
    }
    /**
     * 值处理器核心方法
     * @param value 处理对象
     * @return  R
     */
    default <T,R>R process(T value){
        IValueHandle<T,R> handle = current();
        return handle.handle(value,this);
    }
}
