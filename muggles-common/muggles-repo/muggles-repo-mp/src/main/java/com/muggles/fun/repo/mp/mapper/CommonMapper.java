package com.muggles.fun.repo.mp.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muggles.fun.repo.basic.model.Muggle;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * 通用Mapper
 */
public interface CommonMapper<T> extends BaseMapper<T> {

    /**
     * 根据条件自增更新
     * @param table     表名
     * @param field     字段键值
     * @param wrapper   查询条件
     * @return 更新的记录条数
     */
    @Update("<script>"
            + "update ${table} set "
            + "     <foreach collection=\"field.keys\" item=\"key\" separator=\",\">"
            + "         ${key} = ${key} + #{field[${key}]}"
            + "     </foreach>"
            + " ${ew.customSqlSegment}"
            + "</script>")
    int updateFieldSelf(@Param("table")String table, @Param("field")Map<String, Object> field, @Param(Constants.WRAPPER) QueryWrapper<T> wrapper);

	/**
	 * 根据条件设置字段为null
	 * @param table     表名
	 * @param fields    字段键值
	 * @param wrapper   查询条件
	 * @return	更新的记录条数
	 */
	@Update("<script>"
		+ "update ${table} set "
		+ "     <foreach collection=\"fields\" item=\"key\" separator=\",\">"
		+ "         ${key} = null"
		+ "     </foreach>"
		+ " ${ew.customSqlSegment}"
		+ "</script>")
	int updateFieldNUll(@Param("table")String table, @Param("fields") List<String> fields, @Param(Constants.WRAPPER) QueryWrapper<T> wrapper);

	/**
     * 纯物理删除
     * @param table     表名
     * @param wrapper   删除条件
     * @return	删除的记录条数
     */
    @Delete("<script>"
            + "delete from ${table}"
            + " ${ew.customSqlSegment}"
            + "</script>")
    int deletePhy(@Param("table")String table, @Param(Constants.WRAPPER) QueryWrapper<T> wrapper);

	/**
	 * 纯物理删除
	 * @param table     表名
	 * @param wrapper   删除条件
	 * @return	删除的记录条数
	 */
	@Delete("<script>"
			+ "update ${table} set ${field} = 0"
			+ " ${ew.customSqlSegment}"
			+ "</script>")
	int recovery(@Param("table")String table,@Param("field") String field, @Param(Constants.WRAPPER) QueryWrapper<T> wrapper);

	/**
	 * 联表查询列表
	 * @param param	查询参数
	 * @return	List<T>
	 */
	@Select("<script>" + "SELECT " + "<foreach collection='joiner.selectColumns' item='item' separator=','>"
			+ "${item} " + "</foreach>" + "FROM ${joiner.joinSql}" + " WHERE 1=1 "
			+ "<foreach collection='joiner.whereConditions' item='condition' separator=''>" + "${condition}" + "</foreach>"
			+ "<foreach collection='joiner.groupByColumns' item='column' separator=',' open=' group by '>" + "${column}" + "</foreach>"
			+ "</script>")
	List<T> queryJoinList(@Param("joiner") Muggle<T> param);

	/**
	 * 联表查询列表（返回Map，用于复合对象查询）
	 * @param param	查询参数
	 * @return	List<Map<String, Object>>
	 */
	@Select("<script>" + "SELECT " + "<foreach collection='joiner.selectColumns' item='item' separator=','>"
			+ "${item} " + "</foreach>" + "FROM ${joiner.joinSql}" + " WHERE 1=1 "
			+ "<foreach collection='joiner.whereConditions' item='condition' separator=''>" + "${condition}" + "</foreach>"
			+ "<foreach collection='joiner.groupByColumns' item='column' separator=',' open=' group by '>" + "${column}" + "</foreach>"
			+ "</script>")
	List<Map<String, Object>> queryJoinMaps(@Param("joiner") Muggle<?> param);

	/**
	 * 联表查询分页
	 *
	 * @param page		分页对象
	 * @param param		分页参数
	 */
	@Select("<script>" + "SELECT " + "<foreach collection='joiner.selectColumns' item='item' separator=','>"
			+ "${item} " + "</foreach>" + "FROM ${joiner.joinSql}" + " WHERE 1=1 "
			+ "<foreach collection='joiner.whereConditions' item='condition' separator=''>" + "${condition}" + "</foreach>"
			+ "<foreach collection='joiner.groupByColumns' item='column' separator=',' open=' group by '>" + "${column}" + "</foreach>"
			+ "</script>")
	Page<T> queryJoinPage(Page<T> page, @Param("joiner") Muggle<T> param);

	/**
	 * 联表查询分页（返回Map，用于复合对象查询）
	 *
	 * @param page		分页对象
	 * @param param		分页参数
	 * @return	Page<Map<String, Object>>
	 */
	@Select("<script>" + "SELECT " + "<foreach collection='joiner.selectColumns' item='item' separator=','>"
			+ "${item} " + "</foreach>" + "FROM ${joiner.joinSql}" + " WHERE 1=1 "
			+ "<foreach collection='joiner.whereConditions' item='condition' separator=''>" + "${condition}" + "</foreach>"
			+ "<foreach collection='joiner.groupByColumns' item='column' separator=',' open=' group by '>" + "${column}" + "</foreach>"
			+ "</script>")
	Page<Map<String, Object>> queryJoinPageMaps(Page<Map<String, Object>> page, @Param("joiner") Muggle<?> param);
}

