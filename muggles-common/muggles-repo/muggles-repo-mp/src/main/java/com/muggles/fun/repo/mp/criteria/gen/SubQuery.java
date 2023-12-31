package com.muggles.fun.repo.mp.criteria.gen;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fline.tp.repo.wrapper.criteria.subquery.BaseSubQuery;
import com.fline.tp.repo.wrapper.criteria.subquery.SubQueryTypeEnum;
import com.fline.tp.repo.wrapper.criteria.subquery.SubqueryParam;
import com.fline.tp.tools.core.util.ObjectUtil;
import com.fline.tp.tools.core.util.StrUtil;
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
			SubqueryParam<R> param = (SubqueryParam<R>) criteria.getValue();
			String realSql = BaseSubQuery.getSubQuerySql(criteria);
			// 表示in类型子查询
			if (param.getSubQueryType() == SubQueryTypeEnum.IN) {
				wrapper.inSql(StrUtil.toUnderlineCase(criteria.getAttribute()), realSql);
			} else if (param.getSubQueryType() == SubQueryTypeEnum.NOT_IN) {
				// not in 类型子查询
				wrapper.notInSql(StrUtil.toUnderlineCase(criteria.getAttribute()), realSql);
			}
		}
		return wrapper;
	}
}
