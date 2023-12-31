package com.muggles.fun.repo.mp.criteria.gen;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fline.tp.tools.core.util.ObjectUtil;
import com.fline.tp.tools.core.util.StrUtil;

/**
 * 右模糊查询条件
 */
public class LikeRight implements IGenCriteria {
	/**
	 * 根据查询条件和MP对象生成MP查询条件
	 *
	 * @param wrapper  mp查询条件对象
	 * @param criteria fline定义查询条件对象
	 * @return QueryWrapper
	 */
	@Override
	public QueryWrapper<?> translate(QueryWrapper<?> wrapper, MpCriteria criteria) {
		wrapper.likeRight(StrUtil.isNotBlank(criteria.getAttribute()) && ObjectUtil.isNotNull(criteria.getValue()), StrUtil.toUnderlineCase(criteria.getAttribute()), criteria.getValue());
		return wrapper;
	}
}
