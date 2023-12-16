package com.muggles.fun.repo.basic;

/**
 * 实体初始化接口
 */
public interface IEntityInitialize<T> {

    /**
     *
     * @return  实体对象
     */
    default T init(){
        return null;
    }
}
