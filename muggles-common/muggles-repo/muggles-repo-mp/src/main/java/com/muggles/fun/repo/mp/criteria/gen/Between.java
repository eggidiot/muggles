package com.muggles.fun.repo.mp.criteria.gen;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/**
 * 区间查询条件
 */
class Between implements IGenCriteria {
	/**
	 * 根据查询条件和MP对象生成MP查询条件
	 *
	 * @param wrapper  MP查询条件
	 * @param criteria 查询条件参数
	 * @return QueryWrapper<?>
	 */
	@Override
	public QueryWrapper<?> translate(QueryWrapper<?> wrapper, MpCriteria criteria) {
		if (ObjectUtil.isNotNull(criteria.getValue())) {
			BetweenParam betweenParam = JSON.parseObject(JSONObject.toJSONString(criteria.getValue()), BetweenParam.class);
			wrapper.between(StrUtil.isNotBlank(criteria.getAttribute()) && ObjectUtil.isNotNull(betweenParam.getLoValue()) && ObjectUtil.isNotNull(betweenParam.getHiValue()), StrUtil.toUnderlineCase(criteria.getAttribute()), betweenParam.getLoValue(), betweenParam.getHiValue());
		}
		return wrapper;
	}
}
