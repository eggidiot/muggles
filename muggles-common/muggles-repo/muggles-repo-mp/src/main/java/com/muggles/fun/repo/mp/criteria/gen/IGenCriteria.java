package com.muggles.fun.repo.mp.criteria.gen;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/**
 * 生成查询条件,同一包名下可见
 */
interface IGenCriteria {

	/**
	 * 根据查询条件和MP对象生成MP查询条件
	 *
	 * @param wrapper  MP查询条件
	 * @param criteria 查询条件参数
	 * @return QueryWrapper<?>
	 */
	QueryWrapper<?> translate(QueryWrapper<?> wrapper, MpCriteria criteria);
}
