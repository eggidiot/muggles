package com.muggles.fun.repo.mp.criteria.gen;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fline.tp.tools.core.collection.CollectionUtil;
import com.fline.tp.tools.core.util.ObjectUtil;
import com.fline.tp.tools.core.util.StrUtil;

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
			JSONArray array = JSON.parseArray(JSONArray.toJSONString(criteria.getValue()));
			wrapper.in(StrUtil.isNotBlank(criteria.getAttribute()) && CollectionUtil.isNotEmpty(array), StrUtil.toUnderlineCase(criteria.getAttribute()), array);
		}
		return wrapper;
	}
}
