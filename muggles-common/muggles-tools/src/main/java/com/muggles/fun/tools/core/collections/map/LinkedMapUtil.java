package com.muggles.fun.tools.core.collections.map;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 有序KV集合工具
 */
@UtilityClass
public class LinkedMapUtil {

    /**
     * 根据下表获取指定的KV
     * @param map       指定集合
     * @param index     下标
     * @return      <K, V> Map.Entry<K, V>
     * @param <K>   键类型
     * @param <V>   值类型
     */
    public static <K, V> Map.Entry<K, V> getEntryByIndex(LinkedHashMap<K, V> map, int index) {
        List<K> keys = new ArrayList<>(map.keySet());
        if (index < 0 || index >= keys.size()) {
            return null;
        }
        K key = keys.get(index);
        return Map.entry(key, map.get(key));
    }
}
