package com.muggles.fun.muggles.cache.basic;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 手动调用缓存接口
 */
public interface ICacheService extends ICache {

    /**
     * 获取对象不存在则根据mapping函数计算值
     * @param key				键
     * @param time 				缓存时间
     * @param unit 				时间单位
     * @param mappingFunction	计算函数
     * @return	T
     * @param <T>	类型
     */
    <T> T computeIfAbsent(Serializable key, Long time, TimeUnit unit, Function<Serializable, ? extends T> mappingFunction);
    /**
     * 清空当前缓存
     *
     * @return List<String>
     */
    default boolean clear() {
        List<CacheEntry> list = list();
        list.forEach(e -> remove(e.key));
        return true;
    }
}
