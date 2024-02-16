package com.muggles.fun.repo.mp.criteria.gen;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.muggles.fun.repo.basic.criteria.QueryCriteria;
import com.muggles.fun.repo.basic.criteria.between.BetweenParam;

/**
 * 区间查询条件
 */
class Between implements IGenCriteria {
	/**
	 * 是否添加not
	 */
	private boolean not = false;
	/**
	 * 是否添加not
	 * @return
	 */
	public Between not(){
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
		if (ObjectUtil.isNotNull(criteria.getValue())) {
			BetweenParam betweenParam = (BetweenParam) criteria.getValue();
			if (not) {
				wrapper.notBetween(StrUtil.isNotBlank(criteria.getAttribute()) && ObjectUtil.isNotNull(betweenParam.getLoValue()) && ObjectUtil.isNotNull(betweenParam.getHiValue()), criteria.getAttribute(), betweenParam.getLoValue(), betweenParam.getHiValue());
			} else {
				wrapper.between(StrUtil.isNotBlank(criteria.getAttribute()) && ObjectUtil.isNotNull(betweenParam.getLoValue()) && ObjectUtil.isNotNull(betweenParam.getHiValue()), criteria.getAttribute(), betweenParam.getLoValue(), betweenParam.getHiValue());
			}
		}
		return wrapper;
	}
}
