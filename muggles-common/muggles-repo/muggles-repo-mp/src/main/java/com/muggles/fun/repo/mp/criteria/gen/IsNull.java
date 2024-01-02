package com.muggles.fun.repo.mp.criteria.gen;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/**
 * is null 条件
 */
class IsNull implements IGenCriteria {
	/**
	 * 是否添加not
	 */
	private boolean not = false;
	/**
	 * 是否添加not
	 * @return
	 */
	public IsNull not(){
		this.not = true;
		return this;
	}
	/**
	 * 根据查询条件和MP对象生成MP查询条件
	 *
	 * @param wrapper  MP查询条件
	 * @param criteria 查询条件参数
	 * @return QueryWrapper<?>
	 */
	@Override
	public QueryWrapper<?> translate(QueryWrapper<?> wrapper, MpCriteria criteria) {
		if (not) {
			wrapper.isNotNull(StrUtil.isNotBlank(criteria.getAttribute()), StrUtil.toUnderlineCase(criteria.getAttribute()));
		} else {
			wrapper.isNull(StrUtil.isNotBlank(criteria.getAttribute()), StrUtil.toUnderlineCase(criteria.getAttribute()));
		}
		return wrapper;
	}
}
