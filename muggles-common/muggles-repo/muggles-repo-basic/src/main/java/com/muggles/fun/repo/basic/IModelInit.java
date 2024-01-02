package com.muggles.fun.repo.basic;

/**
 * 实体初始化业务属性接口
 */
public interface IModelInit<T> {

    /**
     *
     * @return  实体对象
     */
    default T init(){
        return null;
    }
}
