package com.muggles.fun.repo.mp.config;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.github.yitter.idgen.YitIdHelper;

/**
 * 自定义ID生成器
 */
public class MpIdGenerator implements IdentifierGenerator {
    /**
     * 生成Id
     *
     * @param entity 实体
     * @return id
     */
    @Override
    public Number nextId(Object entity) {
        return YitIdHelper.nextId();
    }
}
