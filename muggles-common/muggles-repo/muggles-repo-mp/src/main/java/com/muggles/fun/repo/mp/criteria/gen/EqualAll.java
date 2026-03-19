package com.muggles.fun.repo.mp.criteria.gen;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.muggles.fun.repo.basic.criteria.QueryCriteria;

import java.util.Map;

/**
 * 批量eq查询条件
 *
 * @author tanghao
 * @date 2023/6/20 18:03
 */
class EqualAll implements IGenCriteria {
	/**
	 * 批量eq查询条件
	 *
	 * @param wrapper  MP查询条件
	 * @param criteria 查询条件参数
	 * @return	QueryWrapper<?>
	 */
	@Override
	public QueryWrapper<?> translate(QueryWrapper<?> wrapper, QueryCriteria criteria) {
		Object param = criteria.getValue();
		Map<String, Object> objectMap = BeanUtil.beanToMap(param, true, true);
		wrapper.allEq(objectMap);
		return wrapper;
	}
}
