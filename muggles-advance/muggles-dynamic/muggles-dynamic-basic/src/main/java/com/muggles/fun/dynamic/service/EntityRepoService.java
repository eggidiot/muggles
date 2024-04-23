package com.muggles.fun.dynamic.service;

import com.muggles.fun.basic.model.IMugglePage;
import com.muggles.fun.dynamic.Attr;
import com.muggles.fun.dynamic.EntityData;
import com.muggles.fun.dynamic.EntitySchema;
import com.muggles.fun.dynamic.ViewParam;
import com.muggles.fun.repo.basic.model.Muggle;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 实体仓库服务
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 0.9.0
 */
public interface EntityRepoService {

    /**
     * 分页实体结构数据
     */
    IMugglePage<EntityData> pageSchema(Muggle<EntityData> param);

    /**
     * 将实体结构数据存储到仓库中
     *
     * @param entitySchema 实体定义
     * @return 成功返回<code>true</code>, 否则<code>false</code>
     */
    boolean addSchema(EntitySchema entitySchema);

    /**
     * 将实体结构数据存储到仓库中
     *
     * @param entitySchema 实体定义
     * @return 成功返回<code>true</code>, 否则<code>false</code>
     */
    boolean copySchema(String name, EntitySchema entitySchema);

    /**
     * 修改实体结构基本信息，不包含属性
     *
     * @param entitySchema 实体定义
     * @return 成功返回<code>true</code>, 否则<code>false</code>
     */
    boolean updateSchema(String name, EntitySchema entitySchema, Predicate<Attr>... attrFilters);

    /**
     * 仓库中是否存在实体结构
     *
     * @param name 实体名称
     */
    boolean existSchema(String name);

    /**
     * 仓库中是否存在实体结构
     *
     * @param entitySchema 实体结构
     */
    boolean existSchema(EntitySchema entitySchema);

    /**
     * 断言存在实体则执行函数，否则抛出异常
     *
     * @param name 实体名称
     * @param then 函数
     */
    <T> T getEntitySchemaThen(String name, Function<EntitySchema, T> then);

    /**
     * 获取实体结构
     *
     * @param name 实体名称
     * @return EntitySchema
     */
    EntitySchema getSchema(String name);

    /**
     * 获取实体结构明细，详细的列出实体的结构，前端接受可以根据该结构进行渲染属性配置页面
     */
    EntityData getSchemaData(String name, ViewParam view);

    /**
     * 删除一个实体结构
     *
     * @param id 实体Id
     * @return 成功返回 {@code true}, 否则 {@code false}
     */
    boolean delSchema(Long id);

    /**
     * 删除一个实体结构
     *
     * @param id        实体Id
     * @param isPhysics 是否物理删除
     * @return 成功返回 {@code true}, 否则 {@code false}
     */
    boolean delSchema(Long id, boolean isPhysics);

    /**
     * 逻辑实体结构属性列表
     */
    List<EntityData> listSchemaAttr(String name, ViewParam view);

    /**
     * 实体属性 - 修改
     */
    boolean updateSchemaAttr(String name, Attr attr);

    /**
     * 实体属性 - 新增
     */
    boolean addSchemaAttr(String name, Attr attr);

    /**
     * 实体属性 - 新增
     */
    boolean addSchemaAttrs(String name, Collection<Attr> attrs);

    /**
     * 实体属性 - 删除
     */
    boolean delSchemaAttr(String name, String attrName);
}
