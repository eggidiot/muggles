package com.muggles.fun.repo.basic.criteria;

import java.io.Serializable;

/**
 * 查询条件接口参数
 *
 * @author liwei
 */
public interface ICriteria<T> extends Serializable {

	/**
	 * 获取查询条件类型
	 *
	 * @return CriteriaType
	 */
	CriteriaType getType();

	/**
	 * 获取查询条件名称
	 *
	 * @return String
	 */
	String getAttribute();

	/**
	 * 获取查询条件值
	 *
	 * @return T
	 */
	T getValue();
}
