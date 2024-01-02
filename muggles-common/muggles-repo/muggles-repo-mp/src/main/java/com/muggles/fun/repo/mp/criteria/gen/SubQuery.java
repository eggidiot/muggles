package com.muggles.fun.repo.mp.criteria.gen;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.muggles.fun.repo.basic.criteria.CriteriaType;
import com.muggles.fun.repo.basic.model.Muggle;
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
	public QueryWrapper<?> translate(QueryWrapper<?> wrapper, MpCriteria criteria) {
		if (ObjectUtil.isNotNull(criteria.getValue())) {
			Muggle<R> param = (Muggle<R>) criteria.getValue();
			String realSql = BaseSubQuery.getSubQuerySql(criteria);
			switch (param.getSubQuery()){
				case In:
					wrapper.inSql()
					break;
				case NotIn:
					break;
			}
			// 表示in类型子查询
			if (param.getSubQuery() == CriteriaType.In) {
				wrapper.inSql(StrUtil.toUnderlineCase(criteria.getAttribute()), realSql);
			} else if (param.getSubQuery() == CriteriaType.NotIn) {
				// not in 类型子查询
				wrapper.notInSql(StrUtil.toUnderlineCase(criteria.getAttribute()), realSql);
			}
		}
		return wrapper;
	}
}
