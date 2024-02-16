package com.muggles.fun.repo.mp.criteria;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.sql.StringEscape;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.muggles.fun.repo.basic.criteria.QueryCriteria;
import com.muggles.fun.repo.basic.model.Muggle;
import com.muggles.fun.repo.mp.criteria.gen.MpCriteria;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 子查询条件基本构建工具
 */
@UtilityClass
public class BaseSubQuery {
	/**
	 * 子查询select
	 */
	private static final String SELECT = "select ";
	/**
	 * 子查询from
	 */
	private static final String FROM = " from ";
	/**
	 * 子查询where条件默认1=1
	 */
	private static final String WHERE = " where 1 = 1";
	/**
	 * 子查询and连接符
	 */
	private static final String AND = " and ";
	/**
	 * 子查询逻辑删除
	 */
	private static final String LOGIC_DELETE_FIELD_NAME = " delete_flag = 0";
	/**
	 * 参数替换前缀
	 */
	private static final String PARAM_PREFIX = "ew.paramNameValuePairs.";


	/**
	 * 根据查询条件和MP对象生成子查询sql
	 *
	 * @param criteria 查询条件参数
	 * @return String
	 */
	@SneakyThrows
	public <R> String getSubQuerySql(QueryCriteria criteria) {
		Muggle<R> param = (Muggle<R>) criteria.getValue();
		QueryWrapper<R> queryWrapper = WrapperTranslator.translate(param);
		// 把子查询queryWrapper转换成sql
		String sql = buildSubQuerySql(queryWrapper, param.getEntityClass(), CollUtil.isNotEmpty(param.getCriterias()));
		Map<String, Object> paramMap = queryWrapper.getParamNameValuePairs();
		return fillSqlParam(sql, paramMap);
	}

	/**
	 * 根据查询条件拼接子查询sql
	 *
	 * @param queryWrapper 查询条件
	 * @param clazz        子查询实体类型
	 * @param <R>          子查询表
	 * @return String
	 */
	private <R> String buildSubQuerySql(QueryWrapper<R> queryWrapper, Class<R> clazz, boolean hasCondition) {
		// 获取查询字段
		String columns = queryWrapper.getSqlSelect();
		String whereClause = queryWrapper.getSqlSegment();
		StringBuilder sb = new StringBuilder();
		sb.append(SELECT);
		sb.append(columns);
		sb.append(FROM);
		String tableName = SqlHelper.table(clazz).getTableName();
		sb.append(tableName);
		sb.append(WHERE);
		//处理逻辑删除
		List<Field> fields = TableInfoHelper.getAllFields(clazz);
		//1.处理逻辑删除
		fields.stream().filter(field -> field.isAnnotationPresent(TableLogic.class)).forEach(field -> {
			sb.append(AND);
			sb.append(LOGIC_DELETE_FIELD_NAME);
		});
		if (StrUtil.isNotBlank(whereClause)) {
			if (hasCondition) {
				sb.append(AND);
			}
			sb.append(whereClause);
		}
		return sb.toString();
	}

	/**
	 * 填充sql中的参数
	 *
	 * @param sql      拼接的sql
	 * @param paramMap 参数映射
	 * @return String
	 */
	private String fillSqlParam(String sql, Map<String, Object> paramMap) {
		Map<String, Object> realParamMap = new HashMap<>();
		paramMap.forEach((key, value) -> realParamMap.put(PARAM_PREFIX + key, convertValueByType(value)));
		return StrUtil.format(StrUtil.replace(sql, "#", ""), realParamMap);
	}

	/**
	 * 根据参数类型转换对应的sql
	 *
	 * @param obj 转换对象
	 * @return String
	 */
	private String convertValueByType(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof String) {
			return StrUtil.wrap(StringEscape.escapeRawString(obj.toString()), "'");
		} else if (obj instanceof LocalDateTime) {
			String dateStr = LocalDateTimeUtil.formatNormal((LocalDateTime) obj);
			return StrUtil.wrap(StringEscape.escapeRawString(dateStr), "'");
		} else if (obj instanceof LocalDate) {
			String dateStr = LocalDateTimeUtil.formatNormal((LocalDate) obj);
			return StrUtil.wrap(StringEscape.escapeRawString(dateStr), "'");
		} else if (obj instanceof Date) {
			String dateStr = DateUtil.formatDateTime((Date) obj);
			return StrUtil.wrap(StringEscape.escapeRawString(dateStr), "'");
		} else {
			return obj.toString();
		}
	}
}
