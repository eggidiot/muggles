package com.muggles.fun.dynamic;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * <p>
 * 字典接口
 * </p>
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 0.9.0
 */
public interface Dict<K, V> {

    /**
     * 字典的内存仓库
     */
    Map<String, Map<Object, Dict<?,?>>> dictInMemory = new HashMap<>();

    /**
     * 字典名称
     *
     * @return String
     */
    String getName();

    /**
     * 字典项关键字
     *
     * @return K
     */
    K getItemKey();

    /**
     * 字典项显示文本
     *
     * @return String
     */
    String getItemText();

    /**
     * 字典项对象
     *
     * @return String
     */
    V getItem();

    /**
     * 内部实体
     */
    @Data
    @NoArgsConstructor
    class DictObj implements Dict {
        private String name;
        private Object itemKey;
        private String itemText;
        private Object item;
        public DictObj(Dict dict) {
            this.name = dict.getName();
            this.itemKey = dict.getItemKey();
            this.itemText = dict.getItemText();
            this.item = dict.getItem();
        }
    }

    /**
     * 注册自己
     */
    default void register() {
        dictInMemory.computeIfAbsent(getName(), (k) -> new HashMap<>())
                .put(getItemKey(), new DictObj(this));
    }

    /**
     * 从内存仓库中根据名称获取对应字典
     */
    static Collection<Dict<?,?>> getDictList(String name) {
        if (StringUtils.isBlank(name)) {
            return Collections.emptyList();
        }
        return Optional.ofNullable(dictInMemory.get(name)).map(Map::values).orElse(Collections.emptyList());
    }
}
