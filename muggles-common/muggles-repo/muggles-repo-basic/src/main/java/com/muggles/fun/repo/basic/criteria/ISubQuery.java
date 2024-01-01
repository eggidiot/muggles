package com.muggles.fun.repo.basic.criteria;


import cn.hutool.core.lang.Assert;
import com.muggles.fun.basic.exception.MugglesBizException;
import com.muggles.fun.repo.basic.model.Muggle;
import com.muggles.fun.tools.core.bean.LambdaFunction;

/**
 * 获取对应的子查询参数对象接口
 */
public interface ISubQuery {

	String DEFAULT_COLUMN = "id";

	/**
	 * 获取子查询参数
	 *
	 * @param clazz 子查询实体对象
	 * @param <V>   实体类型
	 * @return Muggle<V>
	 */
	default <V> Muggle<V> subQuery(Class<V> clazz) {
		return new Muggle<V>().setEntityClass(clazz).select(DEFAULT_COLUMN);
	}

	/**
	 * 获取子查询参数
	 *
	 * @param clazz       子查询实体对象
	 * @param selectField 查询字段
	 * @param <V>         实体类型
	 * @return Muggle<V>
	 */
	default <V> Muggle<V> subQuery(Class<V> clazz, String selectField) {
		return new Muggle<V>().setEntityClass(clazz).select(selectField);
	}

	/**
	 * 获取子查询参数
	 *
	 * @param clazz       子查询实体对象
	 * @param selectField 查询字段
	 * @param <V>         实体类型
	 * @return Muggle<V>
	 */
	default <V> Muggle<V> subQuery(Class<V> clazz, LambdaFunction<V, ?> selectField) {
		return new Muggle<V>().setEntityClass(clazz).select(selectField);
	}

	/**
	 * 获取子查询参数
	 *
	 * @param entity  		查询条件实体 所有非空属性为eq条件
	 * @param selectField 	子查询结果字段
	 * @param <V>         	实体类型
	 * @return Muggle<V>
	 */
	default <V> Muggle<V> subQuery(V entity, String selectField) {
		Assert.notNull(entity,()-> new MugglesBizException("子查询queryParam对象不能为空"));
		return new Muggle<V>().eqAll(entity).select(selectField);
	}

	/**
	 * 获取子查询参数
	 *
	 * @param entity  		查询条件实体 所有非空属性为eq条件
	 * @param selectField 	子查询结果字段
	 * @param <V>         	实体类型
	 * @return Muggle<V>
	 */
	default <V> Muggle<V> subQuery(V entity, LambdaFunction<V, ?> selectField) {
		Assert.notNull(entity,()-> new MugglesBizException("子查询queryParam对象不能为空"));
		return new Muggle<V>().eqAll(entity).select(selectField);
	}
}
