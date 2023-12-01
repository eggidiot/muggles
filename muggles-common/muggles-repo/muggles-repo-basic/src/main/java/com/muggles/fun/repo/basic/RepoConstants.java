package com.muggles.fun.repo.basic;

/**
 * 持久层数据字典类
 */
public interface RepoConstants {

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
}
