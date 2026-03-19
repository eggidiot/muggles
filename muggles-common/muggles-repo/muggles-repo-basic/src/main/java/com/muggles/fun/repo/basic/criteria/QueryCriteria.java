package com.muggles.fun.repo.basic.criteria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import static com.muggles.fun.basic.Constants.RelationType;

/**
 * 查询参数
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class QueryCriteria implements ICriteria<Object> {

	/**
	 * 查询属性名称
	 */
	protected String attribute;
	/**
	 * 查询属性值
	 */
	protected Object value;
	/**
	 * 查询类型，IN BETWEEN,LIKE,EQ,ISNULL,ISNOTNULL,LESSTHAN,GREATTHAN,LESSEQUALTHAN,GREATEQUALTHAN,PrimaryKey,ForeignKey,NotIn,SubQuery
	 */
	protected CriteriaType type;
	/**
	 * and连接
	 */
	protected RelationType relation;

	/**
	 * 兼容旧版构造器
	 *
	 * @param attribute 查询属性
	 * @param value     查询值
	 * @param type      查询类型
	 */
	public QueryCriteria(String attribute, Object value, CriteriaType type) {
		this(attribute, value, type, RelationType.AND);
	}
}
