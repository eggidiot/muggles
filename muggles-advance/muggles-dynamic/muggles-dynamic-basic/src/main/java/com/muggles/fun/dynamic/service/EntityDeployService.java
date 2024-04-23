package com.muggles.fun.dynamic.service;



import com.muggles.fun.dynamic.Attr;
import com.muggles.fun.dynamic.EntitySchema;

import java.util.Collection;
import java.util.List;


/**
 * 实体部署服务
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 0.9.0
 */
public interface EntityDeployService {

    /**
     * 部署一个实体结构，利用实体结构产生一个可以承载数据的载体（如：数据库创建表）
     *
     * @param name 实体定义名称
     * @return 成功返回<code>true</code>, 否则<code>false</code>
     */
    boolean deploySchema(String name);

    /**
     * 部署一个实体结构，利用实体结构产生一个可以承载数据的载体（如：数据库创建表）
     *
     * @param entitySchema 实体定义
     * @return 成功返回<code>true</code>, 否则<code>false</code>
     */
    boolean deploySchema(EntitySchema entitySchema);

    /**
     * 是否部署过该实体结构
     *
     * @param name 实体定义名称
     */
    boolean isDeployedSchema(String name);

    /**
     * 是否部署过该实体结构
     *
     * @param entitySchema 实体结构
     */
    boolean isDeployedSchema(EntitySchema entitySchema);

    /**
     * 查询已经部署的实体结构的字段列表
     */
    List<String> listDeployedColumns(String name);

    /**
     * 查询已经部署的实体结构的字段列表
     */
    List<String> listDeployedColumns(EntitySchema entitySchema);

    /**
     * 返回内置实体当前的属性相对已部署增加的属性信息列表
     */
    List<Attr> listDiffAddAttrs(String name);

    /**
     * 增量部署，对比已部署的实体结构与当前实体结构，找出新增的字段并进行增量部署
     * 为了防止MDL锁，在10秒未完成的时候抛出异常，上层可以进行重试
     *
     * @param name 实体定义名称
     * @return 成功返回<code>true</code>, 否则<code>false</code>
     */
    boolean diffAddSchemaAttrs(String name);

    /**
     * 对实体属性增加一个属性 <br/>
     * 为了防止MDL锁，在10秒未完成的时候抛出异常，上层可以进行重试
     *
     * @param name 实体定义名称
     * @return 成功返回<code>true</code>, 否则<code>false</code>
     */
    boolean addSchemaAttrs(String name, Attr... attrs);

    /**
     * 对实体属性增加一个属性 <br/>
     * 为了防止MDL锁，在10秒未完成的时候抛出异常，上层可以进行重试
     *
     * @param name 实体定义名称
     * @return 成功返回<code>true</code>, 否则<code>false</code>
     */
    boolean addSchemaAttrs(String name, Collection<Attr> attrs);

    /**
     * 对实体属性删除一个属性 <br/>
     * 为了防止MDL锁，在10秒未完成的时候抛出异常，上层可以进行重试
     *
     * @param name 实体定义名称
     * @return 成功返回<code>true</code>, 否则<code>false</code>
     */
    boolean delSchemaAttrs(String name, Collection<Attr> attrs);

    /**
     * 对实体属性删除一个属性
     *
     * @param name 实体定义名称
     * @return 成功返回<code>true</code>, 否则<code>false</code>
     */
    boolean delSchemaAttrs(String name, Attr... attrs);

    /**
     * 对实体属性修改一个属性 <br/>
     * 为了防止MDL锁，在10秒未完成的时候抛出异常，上层可以进行重试
     *
     * @param name 实体定义名称
     * @return 成功返回<code>true</code>, 否则<code>false</code>
     */
    boolean modifySchemaAttrs(String name, Collection<Attr> attrs);

    /**
     * 对实体属性修改一个属性
     *
     * @param name 实体定义名称
     * @return 成功返回<code>true</code>, 否则<code>false</code>
     */
    boolean modifySchemaAttrs(String name, Attr... attrs);

    /**
     * 删除一个实体结构
     *
     * @param name 实体定义名称
     * @return 成功返回 {@code true}, 否则 {@code false}
     */
    boolean dropSchema(String name);

}
