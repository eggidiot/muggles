package com.muggles.fun.repo.mp.criteria.gen;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/**
 * 模糊查询条件
 */
class Like implements IGenCriteria {
	/**
	 * 根据查询条件和MP对象生成MP查询条件
	 *
	 * @param wrapper  mp查询条件对象
	 * @param criteria fline定义查询条件对象
	 * @return QueryWrapper
	 */
	@Override
	public QueryWrapper translate(QueryWrapper wrapper, MpCriteria criteria) {
		wrapper.like(StrUtil.isNotBlank(criteria.getAttribute()) && ObjectUtil.isNotNull(criteria.getValue()), StrUtil.toUnderlineCase(criteria.getAttribute()), criteria.getValue());
		return wrapper;
	}
}
