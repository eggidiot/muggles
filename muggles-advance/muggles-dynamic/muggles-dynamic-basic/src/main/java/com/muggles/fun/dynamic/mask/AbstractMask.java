package com.muggles.fun.dynamic.mask;

import java.util.HashMap;
import java.util.Map;

/**
 * 面具抽象
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangjun</a>
 * @since 0.9.0
 */
public abstract class AbstractMask implements Mask {

    private static Map<String, Class<? extends AbstractMask>> repository = new HashMap<>();

    protected String key;

    public void setKey(String key) {
        this.key = key;
    }

    protected abstract String getKey();

   protected AbstractMask() {
       repository.put(getKey(), this.getClass());
    }

    public static Map<String, Class<? extends AbstractMask>> getRepository() {
       return repository;
    }


}
