package com.muggles.fun.repo.mp.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * 通用Mapper
 */
public interface CommonMapper<T> extends BaseMapper<T> {

    /**
     * 根据 entity 条件，查询一条记录，并锁定
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
    T selectOneForUpdate(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);


    /**
     * 根据 entity 条件，查询全部记录，并锁定
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
    List<T> selectListForUpdate(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

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
}

