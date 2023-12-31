package com.muggles.fun.repo.mp.criteria.gen;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

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
	 * @return
	 */
	@Override
	public QueryWrapper<?> translate(QueryWrapper<?> wrapper, MpCriteria criteria) {
		Object param = criteria.getValue();
		Map<String, Object> objectMap = BeanUtil.beanToMap(param, true, true);
		objectMap.forEach(wrapper::eq);
		return wrapper;
	}
}
