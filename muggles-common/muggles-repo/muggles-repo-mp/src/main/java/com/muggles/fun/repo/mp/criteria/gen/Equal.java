package com.muggles.fun.repo.mp.criteria.gen;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fline.tp.tools.core.util.ObjectUtil;
import com.fline.tp.tools.core.util.StrUtil;

/**
 * 相等查询条件
 */
class Equal implements IGenCriteria {

	/**
	 * 根据查询条件和MP对象生成MP查询条件
	 *
	 * @param wrapper  MP查询条件
	 * @param criteria 查询条件参数
	 * @return QueryWrapper<?>
	 */
	@Override
	public QueryWrapper<?> translate(QueryWrapper<?> wrapper, MpCriteria criteria) {
		wrapper.eq(StrUtil.isNotBlank(criteria.getAttribute()) && ObjectUtil.isNotNull(criteria.getValue()), StrUtil.toUnderlineCase(criteria.getAttribute()), criteria.getValue());
		return wrapper;
	}
}