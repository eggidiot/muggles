package com.muggles.fun.core.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.experimental.UtilityClass;

/**
 * 当前操作用户上下文，禁止在业务层使用该对象传参
 */
@UtilityClass
public class UserContext {

    /**
     * 保存线程变量
     */
    public final ThreadLocal<Object> local = new TransmittableThreadLocal<>();

    /**
     * 获取当前用户
     *
     * @param <T>	返回值类型
     * @return	T
     */
    public <T> T getUser() {
        Object t = local.get();
        return t != null ? (T) t : null;
    }
    /**
     * 设置当前用户
     *
     * @param user	设置用户信息
     * @param <T>泛型类型
     */
    public <T> void putUser(T user) {
        local.set(user);
    }

    /**
     * 清除当前用户
     */
    public void clear(){
        local.remove();
    }
}
