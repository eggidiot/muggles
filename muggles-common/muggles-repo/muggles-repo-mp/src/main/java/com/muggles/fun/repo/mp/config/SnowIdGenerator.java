package com.muggles.fun.repo.mp.config;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.github.yitter.idgen.YitIdHelper;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 自定义ID生成策略
 */
@Data
@Accessors(chain = true)
public class SnowIdGenerator implements IdentifierGenerator {

    /**
     * 生成Id
     *
     * @param entity 实体
     * @return id
     */
    @Override
    public Long nextId(Object entity) {
        return YitIdHelper.nextId();
    }

}
