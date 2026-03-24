package com.muggles.fun.repo.mp.scene;

import com.muggles.fun.repo.basic.scene.AbsBizService;
import com.muggles.fun.repo.basic.service.IMuggleService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * 基于MP方案设计的业务层父类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class MpBizServiceImpl extends AbsBizService {


    /**
     * 包含所有实体的单体操作组件service
     */
    @Autowired
    protected Map<String, IMuggleService<?>> serviceMap;
    /**
     * 根据实体类型获取单实体service组件，若有自定义
     *
     * @param tClass 实体类型
     * @param <T>    实体类型
     * @return IFlineService<T>
     */
    @Override
    public <T> IMuggleService<T> getService(Class<T> tClass) {
        return service(this.serviceMap, tClass);
    }
}
