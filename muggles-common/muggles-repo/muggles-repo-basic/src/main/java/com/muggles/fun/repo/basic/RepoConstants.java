package com.muggles.fun.repo.basic;

import com.muggles.fun.basic.Constants;

/**
 * 持久层数据字典类
 */
public interface RepoConstants extends Constants {

	/**
	 * 排序逆序
	 */
	Integer DESC = 0;
	/**
	 * 排序正序
	 */
	Integer ASC = 1;
	/**
	 * 默认数值大小为1
	 */
	Long DEFAULT_SIZE = 1L;
	/**
	 * 默认查询一条的时候查询单页大小
	 */
	Long DEFAULT_PAGESIZE = 10L;
	/**
	 * 默认查询一条的时候查询页码
	 */
	Long DEFAULT_CURRENT = DEFAULT_SIZE;
	/**
	 * 聚合函数包含ifnull函数模板
	 */
	String FUNC_TEMPLATE_IFNULL = "IFNULL( %s(%s),0) as %s";
	/**
	 * 普通据韩函数模板字符串
	 */
	String FUNC_TEMPLATE = "%s(%s) as %s";

	/**
	 * 函数枚举类型
	 */
    enum FuncType {
		/**
		 * 统计
		 */
		COUNT,
		/**
		 * 求和
		 */
		SUM,
		/**
		 * 最小值
		 */
		MIN,
		/**
		 * 最大值
		 */
		MAX,
		/**
		 * 平均数
		 */
		AVG
	}
	/**
	 * 函数枚举类型
	 */
	enum JoinType {
		/**
		 * 内连接
		 */
		INNER,
		/**
		 * 左连接
		 */
		LEFT,
		/**
		 * 右连接
		 */
		RIGHT,
		/**
		 * 全连接
		 */
		FULL
	}

	/**
	 * 查询字段策略，默认忽略不存在字段
	 */
	enum FieldStrategy{
		/**
		 * 忽略
		 */
		IGNORE,
		/**
		 * 报错
		 */
		ERROR
	}

	/**
	 * 集合查询时集合为空的处理策略
	 */
	enum InCondiStrategy{
		/**
		 * 忽略
		 */
		IGNORE,
		/**
		 * 拼接一个1=2的查询条件
		 */
		ONE_EQ_TWO
	}
}
