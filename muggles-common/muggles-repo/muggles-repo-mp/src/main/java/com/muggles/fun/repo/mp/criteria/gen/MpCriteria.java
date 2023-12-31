package com.muggles.fun.repo.mp.criteria.gen;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.muggles.fun.repo.basic.criteria.CriteriaType;
import com.muggles.fun.repo.basic.criteria.ICriteria;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 查询条件生成mybtis-plus方式
 */
public abstract class MpCriteria implements ICriteria<Object> {

	/**
	 * 查询条件生成容器
	 */
	static Map<String, IGenCriteria> criteriaMap = new HashMap<>();

	/**
	 * 初始化成员变量
	 */
	static {
		//1.添加相等条件
		criteriaMap.put(CriteriaType.Equal.name(), new Equal());
		//2.添加不等条件
		criteriaMap.put(CriteriaType.NotEqual.name(), new NotEqual());
		//3.添加模糊查询条件
		criteriaMap.put(CriteriaType.Like.name(), new Like());
		//4.添加模糊查询条件
		criteriaMap.put(CriteriaType.NotLike.name(), new Like());
		//5.添加大于查询条件
		criteriaMap.put(CriteriaType.Greaterthan.name(), new Greaterthan());
		//6.添加小于查询条件
		criteriaMap.put(CriteriaType.Lessthan.name(), new Lessthan());
		//7.添加小于等于查询条件
		criteriaMap.put(CriteriaType.LessthanOrEqual.name(), new LessthanOrEqual());
		//8.添加大于等于查询条件
		criteriaMap.put(CriteriaType.GreaterthanOrEqual.name(), new GreaterthanOrEqual());
		//9.添加集合查询条件
		criteriaMap.put(CriteriaType.In.name(), new In());
		//10.添加 is null 条件
		criteriaMap.put(CriteriaType.IsNull.name(), new IsNull());
		//11.添加 is not null 条件
		criteriaMap.put(CriteriaType.IsNotNull.name(), new IsNotNull());
		//12.添加区间查询条件
		criteriaMap.put(CriteriaType.Between.name(), new Between());
		//13.添加集合反向查询条件
		criteriaMap.put(CriteriaType.NotIn.name(), new NotIn());
		//14.添加批量eq查询条件
		criteriaMap.put(CriteriaType.EqualAll.name(), new EqualAll());
		//15.左模糊查询条件
		criteriaMap.put(CriteriaType.LikeLeft.name(), new LikeLeft());
		//16.右模糊查询条件
		criteriaMap.put(CriteriaType.LikeRight.name(), new LikeRight());
		//17.左模糊查询条件
		criteriaMap.put(CriteriaType.NotLikeLeft.name(), new LikeLeft());
		//18.右模糊查询条件
		criteriaMap.put(CriteriaType.NotLikeRight.name(), new LikeRight());
		//15.in子查询条件
		criteriaMap.put(CriteriaType.SubQuery.name(), new SubQuery());


		//18.批量equals对象非空属性
		criteriaMap.put(CriteriaType.EqualAll.name(), new EqualAll());
	}

	/**
	 * 外部扩展查询条件，无法覆盖预定义操作
	 *
	 * @param key   扩展键
	 * @param value 扩展参数
	 * @return boolean
	 */
	public static boolean putCriteria(String key, IGenCriteria value) {
		return Optional.ofNullable(value).map(v -> criteriaMap.putIfAbsent(key, v).equals(v)).orElse(false);
	}

	/**
	 * 翻译查询条件
	 *
	 * @param wrapper 构建查询条件
	 * @return QueryWrapper
	 */
	public QueryWrapper translate(QueryWrapper wrapper) {
		return Optional.ofNullable(criteriaMap.get(getType().name())).map(e -> e.translate(wrapper, this)).orElse(null);
	}
}
