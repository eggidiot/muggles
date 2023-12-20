package com.muggles.fun.repo.basic.convert;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.muggles.fun.basic.Constants;
import com.muggles.fun.repo.basic.criteria.CriteriaType;
import com.muggles.fun.repo.basic.criteria.QueryCriteria;
import lombok.Setter;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Map;

/**
 * Http Api接收Map参数时转换成Criteria集合
 */
@UtilityClass
public class ParamsConverter {

	/**
	 * 分隔符，设置set方法表示可以配置
	 */
	@Setter
	private static String SPLITER = "_";

	/**
	 * 将Map参数转成参数列表
	 *
	 * @param params 接口Map参数
	 * @return List<QueryCriteria>
	 */
	public List<QueryCriteria> conertMap2Criterias(Map<String, Object> params) {
		List<QueryCriteria> criterias = CollUtil.newArrayList();
		if (CollUtil.isNotEmpty(params)) {
			params.forEach((k, v) -> {
				QueryCriteria c = genCriteria(k, v);
				if (c != null) {
					criterias.add(c);
				}
			});
		}
		return criterias;
	}

	/**
	 * 根据参数形式拼装参数结果
	 *
	 * @param key   键
	 * @param value 值
	 * @return QueryCriteria
	 */
	QueryCriteria genCriteria(String key, Object value) {
		QueryCriteria qc = null;
		//1.默认构成equal等值条件
		if (!key.contains(SPLITER)) {
			qc = new QueryCriteria(key, value, CriteriaType.Equal);
		}
		//2.拆分查询条件字符串
		String[] keys = key.split(SPLITER);
		if (ArrayUtil.length(keys) == 2) {
			Constants.RelationType relationType = Constants.RelationType.getNameIgnoreCase(keys[0]);
			if (relationType != null) {
				qc = new QueryCriteria(keys[1], value, CriteriaType.Equal, relationType);
			} else {
				//2.1根据查询条件获取查询类型
				CriteriaType type = CriteriaType.getNameIgnoreCase(keys[1]);
				qc = new QueryCriteria(keys[0], value, type);
			}
		}
		//3.如果分隔三个操作符，第三个表示连接符
		if (ArrayUtil.length(keys) == 3) {
			//2.1根据查询条件获取查询类型
			CriteriaType type = CriteriaType.getNameIgnoreCase(keys[2]);
			Constants.RelationType relationType = Constants.RelationType.getNameIgnoreCase(keys[0]);
			qc = new QueryCriteria(keys[1], value, type, relationType);
		}
		return qc;
	}
}
