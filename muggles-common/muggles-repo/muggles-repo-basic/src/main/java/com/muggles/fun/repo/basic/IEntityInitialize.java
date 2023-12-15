package com.muggles.fun.repo.basic;

/**
 * 实体初始化接口
 */
public interface IEntityInitialize<T> {

    /**
     *
     * @param t
     * @return
     */
    default T init(T t){
        return t;
    }
}
