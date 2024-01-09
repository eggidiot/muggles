package com.muggles.fun.repo.mp.criteria.gen;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.muggles.fun.repo.basic.criteria.CriteriaType;
import com.muggles.fun.repo.basic.criteria.ICriteria;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 查询条件生成mybtis-plus方式
 */
public abstract class MpCriteria implements ICriteria<Object> {

	/**
	 * 查询条件生成容器
	 */
	static Map<String, Supplier<IGenCriteria>> criteriaMap = new HashMap<>();

	/**
	 * 初始化成员变量
	 */
	static {
		//1.添加相等条件
		putCriteria(CriteriaType.Equal.name(), ()->new Equal());
		//2.添加不等条件
		putCriteria(CriteriaType.NotEqual.name(), ()->new Equal().not());
		//3.添加模糊查询条件
		putCriteria(CriteriaType.Like.name(), ()->new Like());
		//4.添加模糊查询条件
		putCriteria(CriteriaType.NotLike.name(), ()->new Like().not());
		//5.添加大于查询条件
		putCriteria(CriteriaType.Greaterthan.name(), ()->new Greater());
		//6.添加小于查询条件
		putCriteria(CriteriaType.Lessthan.name(), ()->new Less());
		//7.添加小于等于查询条件
		putCriteria(CriteriaType.LessthanOrEqual.name(), ()->new Less().orEqual());
		//8.添加大于等于查询条件
		putCriteria(CriteriaType.GreaterthanOrEqual.name(),()->new Greater().orEqual());
		//9.添加集合查询条件
		putCriteria(CriteriaType.In.name(), ()->new In());
		//10.添加 is null 条件
		putCriteria(CriteriaType.IsNull.name(), ()->new IsNull());
		//11.添加 is not null 条件
		putCriteria(CriteriaType.IsNotNull.name(), ()->new IsNull().not());
		//12.添加区间查询条件
		putCriteria(CriteriaType.Between.name(), ()->new Between());
		//13.添加区间查询条件
		putCriteria(CriteriaType.NotBetween.name(), ()->new Between().not());
		//14.添加集合反向查询条件
		putCriteria(CriteriaType.NotIn.name(), ()->new In().not());
		//15.添加批量eq查询条件
		putCriteria(CriteriaType.EqualAll.name(), ()->new EqualAll());
		//16.左模糊查询条件
		putCriteria(CriteriaType.LikeLeft.name(), ()->new Like().left());
		//17.右模糊查询条件
		putCriteria(CriteriaType.LikeRight.name(), ()->new Like().right());
		//18.非左模糊查询条件
		putCriteria(CriteriaType.NotLikeLeft.name(), ()->new Like().left().not());
		//19.非右模糊查询条件
		putCriteria(CriteriaType.NotLikeRight.name(), ()->new Like().right().not());
		//20.in子查询条件
		putCriteria(CriteriaType.SubQuery.name(), ()->new SubQuery());
	}

	/**
	 * 外部扩展查询条件，无法覆盖预定义操作
	 *
	 * @param key   扩展键
	 * @param value 扩展参数
	 * @return boolean
	 */
	public static boolean putCriteria(String key, Supplier<IGenCriteria> value) {
		return Optional.ofNullable(value).map(v -> criteriaMap.putIfAbsent(key, v).equals(v)).orElse(false);
	}

	/**
	 * 翻译查询条件
	 *
	 * @param wrapper 构建查询条件
	 * @return QueryWrapper
	 */
	public static QueryWrapper translate(QueryWrapper wrapper) {
		return Optional.ofNullable(criteriaMap.get(getType().name())).map(e -> e.get().translate(wrapper, this)).orElse(null);
	}
}
