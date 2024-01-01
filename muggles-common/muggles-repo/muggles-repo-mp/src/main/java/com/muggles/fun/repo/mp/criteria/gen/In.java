package com.muggles.fun.repo.mp.criteria.gen;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/**
 * 集合查询条件
 */
class In implements IGenCriteria {
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
			JSONArray array = JSONUtil.parseArray(JSONUtil.toJsonStr(criteria.getValue()));
			wrapper.in(StrUtil.isNotBlank(criteria.getAttribute()) && CollUtil.isNotEmpty(array), StrUtil.toUnderlineCase(criteria.getAttribute()), array);
		}
		return wrapper;
	}
}
