package com.muggles.fun.repo.basic.service;

import com.muggles.fun.basic.ICrudService;
import com.muggles.fun.repo.basic.model.Muggle;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 通用Service继承接口
 *
 * @param <T>
 */
public interface ICommonService<T> extends ICrudService<T> {
	/**
	 * 根据 entity 条件，查询一条记录，并锁定
	 *
	 * @param muggle 实体对象封装操作类（可以为 null）
	 * @return T
	 */
	T oneForUpdate(Muggle<T> muggle);
	/**
	 * 根据 entity 条件，查询全部记录，并锁定
	 *
	 * @param muggle 实体对象封装操作类（可以为 null）
	 * @return List<T>
	 */
	List<T> listForUpdate(Muggle<T> muggle);
	/**
	 * 根据ID更新字为null
	 *
	 * @param fields 置NULL字段集合
	 * @param id     实体主键
	 * @return	boolean
	 */
	boolean updateFieldNullById(List<String> fields, Serializable id);
	/**
	 * 根据ID更新字段自增
	 *
	 * @param entity 需要更新的自增实体，会忽略非数字类型字段值，对于自增负数会添加条件判断
	 * @return	boolean
	 */
	default boolean updateFieldSelfById(T entity) {
		return updateFieldSelfById(entity, true);
	}
	/**
	 * 根据ID更新字段自增，会忽略非数字类型字段值
	 *
	 * @param entity  实体
	 * @param postive 是否添加非负判断
	 * @return	boolean
	 */
	boolean updateFieldSelfById(T entity, boolean postive);
	/**
	 * 实体对象类型
	 * @return	Class<T>
	 */
	Class<T> getTClass();

	/**
	 * 获取实体对象对应的表名
	 *
	 * @return String
	 */
	String tableName();

	/**
	 * 物理删除
	 *
	 * @param id 主键
	 * @return	boolean
	 */
	boolean removePhyById(Serializable id);

	/**
	 * 根据主键恢复某个逻辑删的数据
	 * @param id	记录主键
	 * @return	boolean
	 */
	boolean recoverById(Serializable id);
}
