package com.muggles.fun.repo.basic.criteria;

import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;

/**
 * 查询条件类型
 *
 * @author kay
 */
@Getter
public enum CriteriaType {
	/**
	 * 忽略该条件
	 */
	Ignore,
	/**
	 * 该条件必须为正数
	 */
	Positive,
	/**
	 * 该条件为相等判定
	 */
	Equal,
	/**
	 * 该条件为不等判定
	 */
	NotEqual,
	/**
	 * 该条件为模糊判定
	 */
	Like,
	/**
	 * 模糊取反判定
	 */
	NotLike,
	/**
	 * 该条件为大于判定
	 */
	Greaterthan,
	/**
	 * 该条件为小于判定
	 */
	Lessthan,
	/**
	 * 该条件为小于等于判定
	 */
	LessthanOrEqual,
	/**
	 * 该条件为大于等于判定
	 */
	GreaterthanOrEqual,
	/**
	 * 该条件为范围判定
	 */
	In,
	/**
	 * 该条件null判定
	 */
	IsNull,
	/**
	 * 该条件非null判定
	 */
	IsNotNull,
	/**
	 * 该条件为范围判定，左小右大
	 */
	Between,
	/**
	 * 该条件为范围反向判定
	 */
	NotIn,
	/**
	 * 该条件表示所有条件用equal查询
	 */
	EqualAll,
	/**
	 * 左模糊判定
	 */
	LikeLeft,
	/**
	 * 右模糊判定
	 */
	LikeRight,
	/**
	 * 左模糊取反判定
	 */
	NotLikeLeft,
	/**
	 * 右模糊取反判定
	 */
	NotLikeRight,
	/**
	 * 子查询定义
	 */
	SubQuery,
	/**
	 * 连表查询
	 */
	JoinQuery;

	/**
	 * 根据枚举名称获取枚举对象
	 *
	 * @param name 名称值
	 * @return CriteriaType
	 */
	public static CriteriaType get(String name) {
		if (EnumUtil.contains(CriteriaType.class, name)) {
			return EnumUtil.fromString(CriteriaType.class, name);
		}
		//找不到匹配的值则忽略条件
		return Ignore;
	}

	/**
	 * 根据枚举名称获取枚举对象
	 *
	 * @param name 名称值
	 * @return CriteriaType
	 */
	public static CriteriaType getNameIgnoreCase(String name) {
		for (CriteriaType type : CriteriaType.values()) {
			if (StrUtil.equalsIgnoreCase(type.name(), name))
				return type;
		}
		//找不到匹配的值则忽略条件
		return Ignore;
	}
}
