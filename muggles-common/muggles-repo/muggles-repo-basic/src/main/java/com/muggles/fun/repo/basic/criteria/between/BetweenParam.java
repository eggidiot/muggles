package com.muggles.fun.repo.basic.criteria.between;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 区间查询模型
 */
@Data
@Accessors(chain = true)
public class BetweenParam {
	/**
	 * 区间下限
	 */
	private Object loValue;
	/**
	 * 区间上限
	 */
	private Object hiValue;
}
