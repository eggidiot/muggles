package com.muggles.fun.repo.mp.criteria.gen;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fline.tp.tools.core.util.StrUtil;

/**
 * is not null 条件
 */
class IsNotNull implements IGenCriteria {
	/**
	 * 根据查询条件和MP对象生成MP查询条件
	 *
	 * @param wrapper  MP查询条件
	 * @param criteria 查询条件参数
	 * @return QueryWrapper<?>
	 */
	@Override
	public QueryWrapper<?> translate(QueryWrapper<?> wrapper, MpCriteria criteria) {
		wrapper.isNotNull(StrUtil.isNotBlank(criteria.getAttribute()), StrUtil.toUnderlineCase(criteria.getAttribute()));
		return wrapper;
	}
}
