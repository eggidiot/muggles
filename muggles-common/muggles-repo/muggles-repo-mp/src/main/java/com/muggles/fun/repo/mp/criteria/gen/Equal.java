package com.muggles.fun.repo.mp.criteria.gen;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.muggles.fun.repo.basic.criteria.QueryCriteria;

/**
 * 相等查询条件
 */
class Equal implements IGenCriteria {
	/**
	 * 是否添加not
	 */
	private boolean not = false;
	/**
	 * 是否添加not
	 * @return
	 */
	public Equal not(){
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
	public QueryWrapper<?> translate(QueryWrapper<?> wrapper, QueryCriteria criteria) {
		if (not) {
			wrapper.ne(StrUtil.isNotBlank(criteria.getAttribute()) && ObjectUtil.isNotNull(criteria.getValue()), criteria.getAttribute(), criteria.getValue());
		} else {
			wrapper.eq(StrUtil.isNotBlank(criteria.getAttribute()) && ObjectUtil.isNotNull(criteria.getValue()), criteria.getAttribute(), criteria.getValue());
		}
		return wrapper;
	}
}
