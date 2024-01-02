package com.muggles.fun.repo.mp.criteria.gen;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/**
 * 模糊查询条件
 */
class Like implements IGenCriteria {
	/**
	 * 是否添加not
	 */
	private boolean not = false;
	/**
	 * 采用哪个方式like
	 */
	LIKEMODE mode = LIKEMODE.NONE;
	/**
	 * like方式
	 */
	public static enum LIKEMODE{
		NONE,
		LEFT,
		RIGHT
	}
	/**
	 * 是否添加not
	 * @return
	 */
	public Like not(){
		this.not = true;
		return this;
	}

	/**
	 * 普通like模式
	 * @return
	 */
	public Like none(){
		this.mode = LIKEMODE.NONE;
		return this;
	}

	/**
	 * likeleft模式
	 * @return
	 */
	public Like left(){
		this.mode = LIKEMODE.LEFT;
		return this;
	}
	/**
	 * likeright模式
	 * @return
	 */
	public Like right(){
		this.mode = LIKEMODE.RIGHT;
		return this;
	}
	/**
	 * 根据查询条件和MP对象生成MP查询条件
	 *
	 * @param wrapper  mp查询条件对象
	 * @param criteria fline定义查询条件对象
	 * @return QueryWrapper
	 */
	@Override
	public QueryWrapper translate(QueryWrapper wrapper, MpCriteria criteria) {
		if (not) {
			switch (mode) {
				case NONE:
					wrapper.notLike(StrUtil.isNotBlank(criteria.getAttribute()) && ObjectUtil.isNotNull(criteria.getValue()), StrUtil.toUnderlineCase(criteria.getAttribute()), criteria.getValue());
					break;
				case LEFT:
					wrapper.notLikeLeft(StrUtil.isNotBlank(criteria.getAttribute()) && ObjectUtil.isNotNull(criteria.getValue()), StrUtil.toUnderlineCase(criteria.getAttribute()), criteria.getValue());
					break;
				case RIGHT:
					wrapper.notLikeRight(StrUtil.isNotBlank(criteria.getAttribute()) && ObjectUtil.isNotNull(criteria.getValue()), StrUtil.toUnderlineCase(criteria.getAttribute()), criteria.getValue());
					break;
			}
		} else {
			switch (mode) {
				case NONE:
					wrapper.like(StrUtil.isNotBlank(criteria.getAttribute()) && ObjectUtil.isNotNull(criteria.getValue()), StrUtil.toUnderlineCase(criteria.getAttribute()), criteria.getValue());
					break;
				case LEFT:
					wrapper.likeLeft(StrUtil.isNotBlank(criteria.getAttribute()) && ObjectUtil.isNotNull(criteria.getValue()), StrUtil.toUnderlineCase(criteria.getAttribute()), criteria.getValue());
					break;
				case RIGHT:
					wrapper.likeRight(StrUtil.isNotBlank(criteria.getAttribute()) && ObjectUtil.isNotNull(criteria.getValue()), StrUtil.toUnderlineCase(criteria.getAttribute()), criteria.getValue());
					break;
			}
		}
		return wrapper;
	}
}
