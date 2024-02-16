package com.muggles.fun.repo.mp.criteria.gen;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.muggles.fun.repo.basic.criteria.CriteriaType;
import com.muggles.fun.repo.basic.criteria.QueryCriteria;
import com.muggles.fun.repo.basic.model.Muggle;
import com.muggles.fun.repo.mp.criteria.BaseSubQuery;
import com.muggles.fun.repo.mp.criteria.WrapperTranslator;
import lombok.SneakyThrows;

/**
 * 子查询语句生成
 */
class SubQuery<R> implements IGenCriteria {

	/**
	 * 根据查询条件和MP对象生成MP查询条件
	 *
	 * @param wrapper  MP查询条件
	 * @param criteria 查询条件参数
	 * @return QueryWrapper
	 */
	@SneakyThrows
	@Override
	public QueryWrapper<?> translate(QueryWrapper<?> wrapper, QueryCriteria criteria) {
		if (ObjectUtil.isNotNull(criteria.getValue())) {
			Muggle<R> param = (Muggle<R>) criteria.getValue();
			String realSql = BaseSubQuery.getSubQuerySql(criteria);
			switch (param.getSubQuery()){
				case Equal:
					wrapper.apply(criteria.getAttribute() + " = (" + realSql + ")");
					break;
				case NotEqual:
					wrapper.apply(criteria.getAttribute() + " <> (" + realSql + ")");
					break;
				case In:
					wrapper.inSql(criteria.getAttribute(), realSql);
					break;
				case NotIn:
					wrapper.notInSql(criteria.getAttribute(), realSql);
					break;
			}
		}
		return wrapper;
	}
}
