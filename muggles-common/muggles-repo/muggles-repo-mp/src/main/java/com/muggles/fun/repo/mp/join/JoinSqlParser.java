package com.muggles.fun.repo.mp.join;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.sql.StringEscape;
import com.muggles.fun.basic.exception.MugglesBizException;
import com.muggles.fun.repo.basic.criteria.OnCriteria;
import com.muggles.fun.repo.basic.model.Muggle;
import com.muggles.fun.repo.mp.criteria.WrapperTranslator;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.muggles.fun.repo.basic.RepoConstants.JoinType;

/**
 * 关联表sql解析器
 * <p>
 * 负责生成联表查询的SQL语句，并处理查询条件及参数替换。
 */
@Slf4j
@UtilityClass
public class JoinSqlParser {

	/**
	 * 参数替换前缀
	 */
	private static final String PARAM_PREFIX = "ew.paramNameValuePairs.";
	/**
	 * 默认sql模板
	 */
	private static final String DEFAULT_SQL = "SELECT * FROM dummy_table {} WHERE 1 = 1 {}";

	/**
	 * 空白字符串
	 */
	private static final String BLANK = " ";
	/**
	 * 查询所有字段
	 */
	private static final String SELECT_ALL = " * ";
	/**
	 * 点
	 */
	private static final String POINT = ".";
	/**
	 * 等号
	 */
	private static final String EQ = "=";
	/**
	 * 联表条件关键字
	 */
	private static final String ON = "ON";
	/**
	 * and条件符号
	 */
	private static final String AND = " AND ";
	/**
	 * or条件符号
	 */
	private static final String OR = " OR ";
	/**
	 * 为空条件
	 */
	private static final String IS_NULL = " is null ";
	/**
	 * select 查询符号
	 */
	private static final String SELECT = "SELECT";
	/**
	 * 子查询占位符
	 */
	private static final String subqueryPlaceholder = "subquery_placeholder_";

	/**
	 * 构建JoinParam对象，解析联表SQL
	 *
	 * @param muggle 查询参数对象
	 * @return Muggle<T>
	 */
	public <T> Muggle<T> buildJoinParam(Muggle<T> muggle) {
		muggle.setJoinSql(buildJoinSql(muggle));
		muggle.setSelectColumns(buildSelectColumns(muggle.getFields()));
		muggle.setGroupByColumns(muggle.getGroupBys());
		List<String> whereConditions = CollUtil.newArrayList();
		buildWhereConditions(muggle, whereConditions);
		muggle.setWhereConditions(whereConditions);
		return muggle;
	}

	/**
	 * 构建联表SQL
	 *
	 * @param muggle 查询参数对象
	 * @return 拼接后的联表SQL
	 */
	private <T> String buildJoinSql(Muggle<T> muggle) {
		StringBuilder sb = new StringBuilder();
		// 主表
		TableInfo masterInfo = TableInfoHelper.getTableInfo(muggle.getEntityClass());
		Assert.notNull(masterInfo, () -> new MugglesBizException("未找到表信息，请检查实体类是否使用@TableName注解"));
		sb.append(masterInfo.getTableName()).append(BLANK).append(muggle.getAlias());
		// 联表
		for (Muggle<?> join : muggle.getJoins()) {
			TableInfo joinTableInfo = TableInfoHelper.getTableInfo(join.getEntityClass());
			Assert.notNull(joinTableInfo, () -> new MugglesBizException("未找到表信息，请检查实体类是否使用@TableName注解"));
			sb.append(BLANK);
			sb.append(toJoinKeyword(join.getJoin())).append(BLANK);
			sb.append(joinTableInfo.getTableName()).append(BLANK).append(join.getAlias());
			sb.append(BLANK).append(ON).append(BLANK);
			// ON条件
			List<String> onConditions = new ArrayList<>();
			for (OnCriteria onCriteria : join.getOn()) {
				onConditions.add(onCriteria.getAttr1() + EQ + onCriteria.getAttr2());
			}
			sb.append(String.join(AND, onConditions));
			// 联表逻辑删除条件
			sb.append(buildLogicDeleteCondition(join.getEntityClass(), join.getAlias()));
		}
		return sb.toString();
	}

	/**
	 * 将JoinType转换为SQL关键字
	 *
	 * @param type 连接类型
	 * @return SQL连接关键字
	 */
	private String toJoinKeyword(JoinType type) {
        return switch (type) {
            case LEFT -> "LEFT JOIN";
            case RIGHT -> "RIGHT JOIN";
            case FULL -> "FULL JOIN";
            default -> "INNER JOIN";
        };
	}

	/**
	 * 构建逻辑删除条件
	 * @param clazz 实体类型
	 * @param alias 表别名
	 * @return 逻辑删除条件SQL
	 */
	private static String buildLogicDeleteCondition(Class<?> clazz, String alias) {
		StringBuilder sb = new StringBuilder();
		TableInfo tableInfo = TableInfoHelper.getTableInfo(clazz);
		Assert.notNull(tableInfo, () -> new MugglesBizException("未找到表信息，请检查实体类是否使用@TableName注解"));

		TableFieldInfo logicDeleteFieldInfo = tableInfo.getLogicDeleteFieldInfo();
		if (logicDeleteFieldInfo != null) {
			sb.append(AND).append(alias).append(POINT);
			sb.append(logicDeleteFieldInfo.getColumn()).append(EQ)
				.append(logicDeleteFieldInfo.getLogicNotDeleteValue());
		}
		return sb.toString();
	}

	/**
	 * 构建逻辑删除条件（WHERE子句，包含OR IS NULL判断）
	 *
	 * @param clazz 实体类型
	 * @param alias 表别名
	 * @return 逻辑删除条件SQL
	 */
	private static String buildLogicDeleteConditionOnWhere(Class<?> clazz, String alias) {
		StringBuilder sb = new StringBuilder();
		TableInfo tableInfo = TableInfoHelper.getTableInfo(clazz);
		Assert.notNull(tableInfo, () -> new MugglesBizException("未找到表信息，请检查实体类是否使用@TableName注解"));

		TableFieldInfo logicDeleteFieldInfo = tableInfo.getLogicDeleteFieldInfo();
		if (logicDeleteFieldInfo != null) {
			sb.append(AND).append("(").append(alias).append(POINT);
			sb.append(logicDeleteFieldInfo.getColumn()).append(EQ)
				.append(logicDeleteFieldInfo.getLogicNotDeleteValue())
				.append(OR).append(alias).append(POINT).append(logicDeleteFieldInfo.getColumn())
				.append(IS_NULL).append(")");
		}
		return sb.toString();
	}

	/**
	 * 构建查询字段列表
	 *
	 * @param columns 选择的列
	 * @return 查询字段列表
	 */
	private static List<String> buildSelectColumns(List<String> columns) {
		return CollUtil.isEmpty(columns) ? CollUtil.newArrayList(SELECT_ALL) : columns;
	}

	/**
	 * 构建WHERE查询条件
	 *
	 * @param muggle          主表查询参数
	 * @param whereConditions WHERE条件列表
	 */
	private <T> void buildWhereConditions(Muggle<T> muggle, List<String> whereConditions) {
		// 主表条件
		QueryWrapper<T> masterWrapper = WrapperTranslator.translate(muggle);
		String masterCondition = generateWhereCondition(masterWrapper, muggle.getAlias(), muggle.getEntityClass());
		if (StrUtil.isNotBlank(masterCondition)) {
			whereConditions.add(masterCondition);
		}
		// 联表条件
		for (Muggle<?> join : muggle.getJoins()) {
			QueryWrapper<?> joinWrapper = WrapperTranslator.translate(join);
			String joinCondition = generateWhereCondition(joinWrapper, join.getAlias(), join.getEntityClass());
			if (StrUtil.isNotBlank(joinCondition)) {
				whereConditions.add(joinCondition);
			}
		}
	}

	/**
	 * 生成WHERE查询条件
	 *
	 * @param queryWrapper 查询条件封装
	 * @param alias        表别名
	 * @return 拼接后的条件SQL
	 */
	private static String generateWhereCondition(QueryWrapper<?> queryWrapper, String alias,Class<?> clazz) {

		StringBuilder sb = new StringBuilder();
		sb.append(buildLogicDeleteConditionOnWhere(clazz, alias));
		// 只获取WHERE条件部分，排除GROUP BY和ORDER BY
		String sqlSegment = queryWrapper.getExpression().getNormal().getSqlSegment();
		if (StrUtil.isNotBlank(sqlSegment)) {
			sb.append(AND).append(sqlSegment);
		}
		// 定义子查询集合
		List<String> subQueries = CollUtil.newArrayList();
		// 填充sql占位符
		String whereCondition = fillSqlParam(sb.toString(), queryWrapper.getParamNameValuePairs());
		// 保护子查询sql
		whereCondition = protectNestedSubQueries(whereCondition, subQueries);

		// 使用JSQLParser进行SQL解析和字段别名替换
		whereCondition = replaceColumnAliasesWithJSQLParser(whereCondition, alias);
		// 恢复子查询
		return restoreSubQueries(whereCondition, subQueries);
	}

	/**
	 * 使用JSQLParser进行字段别名替换
	 *
	 * @param sql   原始SQL
	 * @param alias 表的别名
	 * @return 替换别名后的SQL
	 */
	private static String replaceColumnAliasesWithJSQLParser(String sql, String alias) {
		try {
			String trimmedSql = sql.trim();
			// 将WHERE条件包装成完整的SELECT语句，以便JSQLParser解析
			String dummySql = StrUtil.format(DEFAULT_SQL, alias, trimmedSql);
			Statement statement = CCJSqlParserUtil.parse(dummySql);

			if (statement instanceof PlainSelect plainSelect) {
                // 处理WHERE子句中的列
				if (plainSelect.getWhere() != null) {
					plainSelect.getWhere().accept(new ExpressionVisitorAdapter() {
						@Override
						public void visit(Column column) {
							// 仅在列未被别名化时添加别名
							if (StrUtil.isNotEmpty(column.getColumnName())
								&& !StrUtil.contains(column.getColumnName(), subqueryPlaceholder)) {
								column.setTable(new net.sf.jsqlparser.schema.Table());
								column.getTable().setName(alias);
							}
						}
					});
				}

				// 提取修改后的WHERE子句
				Expression modifiedWhere = plainSelect.getWhere();

				// 将WHERE子句转换为字符串
                return StrUtil.removePrefixIgnoreCase(modifiedWhere.toString(), "1 = 1");
			}
		} catch (Exception e) {
			log.error("SQL解析错误：", e);
		}
		// 如果解析失败，返回原始SQL
		return sql;
	}

	/**
	 * 恢复子查询部分，替换占位符
	 */
	private static String restoreSubQueries(String sql, List<String> subQueries) {
		for (int i = 0; i < subQueries.size(); i++) {
			sql = sql.replace(subqueryPlaceholder + i, "(" + subQueries.get(i) + ")");
		}
		return sql;
	}

	/**
	 * 保护子查询部分，避免对其进行别名替换
	 */
	private static String protectNestedSubQueries(String sql, List<String> subQueries) {
		Deque<Integer> stack = new ArrayDeque<>();
		StringBuilder sb = new StringBuilder(sql);
		int start = -1;

		// 遍历整个 SQL 字符串，找到所有的子查询并替换为占位符
		for (int i = 0; i < sb.length(); i++) {
			char c = sb.charAt(i);
			if (c == '(') {
				// 遇到左括号时，记录位置
				stack.push(i);
			} else if (c == ')' && !stack.isEmpty()) {
				// 遇到右括号时，匹配之前的左括号，处理括号内的内容
				start = stack.pop();
				String potentialSubQuery = sb.substring(start + 1, i).trim();

				// 确认是否为子查询（可以加其他子查询判断逻辑）
				if (isSubQuery(potentialSubQuery)) {
					subQueries.add(potentialSubQuery);
					// 替换子查询为占位符，并确保占位符不会被加别名
					sb.replace(start + 1, i, subqueryPlaceholder + (subQueries.size() - 1));
					// 调整当前索引以跳过新插入的占位符
					i = start + (subqueryPlaceholder + (subQueries.size() - 1)).length();
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 判断是否为子查询的逻辑
	 *
	 * @param query	查询条件
	 * @return boolean
	 */
	private boolean isSubQuery(String query) {
		// 简单判断子查询是否包含 'SELECT' 关键字，实际可根据需要扩展
		return query.toUpperCase().contains(SELECT);
	}

	/**
	 * 填充sql中的参数
	 *
	 * @param sql      拼接的sql
	 * @param paramMap 参数映射
	 * @return String
	 */
	public String fillSqlParam(String sql, Map<String, Object> paramMap) {
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
	public String convertValueByType(Object obj) {
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
