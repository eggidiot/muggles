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
		List<String> selectColumns = buildSelectColumns(muggle.getFields(), muggle.getExcludes(), muggle.getEntityClass(), muggle.getAlias());
		// 递归收集联表的查询字段
		collectJoinSelectColumns(muggle.getJoins(), selectColumns);
		muggle.setSelectColumns(selectColumns);
		muggle.setGroupByColumns(muggle.getGroupBys());
		List<String> whereConditions = CollUtil.newArrayList();
		buildWhereConditions(muggle, whereConditions);
		muggle.setWhereConditions(whereConditions);
		return muggle;
	}

	/**
	 * 构建包含联表的子查询SQL
	 * <p>
	 * 用于子查询Muggle包含join时，组装完整的子查询SQL语句。
	 * 调用前需先通过 {@link #buildJoinParam(Muggle)} 填充联表参数。
	 *
	 * @param muggle 已通过buildJoinParam处理过的查询参数
	 * @return 完整的子查询SQL
	 */
	public <T> String buildJoinSubQuerySql(Muggle<T> muggle) {
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		sb.append(String.join(",", muggle.getSelectColumns()));
		sb.append(" from ");
		sb.append(muggle.getJoinSql());
		sb.append(" where 1=1 ");
		for (String condition : muggle.getWhereConditions()) {
			sb.append(condition);
		}
		if (CollUtil.isNotEmpty(muggle.getGroupByColumns())) {
			sb.append(" group by ");
			sb.append(String.join(",", muggle.getGroupByColumns()));
		}
		return sb.toString();
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
		// 递归展开所有联表，传入主表别名作为第一层join的父别名
		appendJoinClauses(muggle.getJoins(), muggle.getAlias(), sb);
		return sb.toString();
	}

	/**
	 * 递归拼接联表SQL子句
	 *
	 * @param joins       联表列表
	 * @param parentAlias 父表（驱动表）的别名
	 * @param sb          SQL构建器
	 */
	private void appendJoinClauses(List<Muggle<?>> joins, String parentAlias, StringBuilder sb) {
		for (Muggle<?> join : joins) {
			TableInfo joinTableInfo = TableInfoHelper.getTableInfo(join.getEntityClass());
			Assert.notNull(joinTableInfo, () -> new MugglesBizException("未找到表信息，请检查实体类是否使用@TableName注解"));
			sb.append(BLANK);
			sb.append(toJoinKeyword(join.getJoin())).append(BLANK);
			sb.append(joinTableInfo.getTableName()).append(BLANK).append(join.getAlias());
			sb.append(BLANK).append(ON).append(BLANK);
			// ON条件：使用parentAlias作为驱动表别名，在SQL生成时确定，不依赖outAlias的设置时机
			List<String> onConditions = new ArrayList<>();
			for (OnCriteria onCriteria : join.getOn()) {
				String attr1 = parentAlias + POINT + onCriteria.getAttr1();
				String attr2 = join.getAlias() + POINT + onCriteria.getAttr2();
				onConditions.add(attr1 + EQ + attr2);
			}
			// 联表逻辑删除条件作为ON条件统一拼接
			String logicDeleteOnCondition = buildLogicDeleteOnCondition(join.getEntityClass(), join.getAlias());
			if (StrUtil.isNotBlank(logicDeleteOnCondition)) {
				onConditions.add(logicDeleteOnCondition);
			}
			// ON条件为空时使用 1=1 兜底，避免生成 "ON WHERE" 语法错误
			sb.append(CollUtil.isEmpty(onConditions) ? "1=1" : String.join(AND, onConditions));
			// 递归处理子联表，子联表的父别名是当前联表的别名
			if (CollUtil.isNotEmpty(join.getJoins())) {
				appendJoinClauses(join.getJoins(), join.getAlias(), sb);
			}
		}
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
	 * 构建逻辑删除条件（不带AND前缀，用于ON条件列表拼接）
	 * @param clazz 实体类型
	 * @param alias 表别名
	 * @return 逻辑删除条件SQL，如 "t2.delete_flag=0"
	 */
	private String buildLogicDeleteOnCondition(Class<?> clazz, String alias) {
		TableInfo tableInfo = TableInfoHelper.getTableInfo(clazz);
		Assert.notNull(tableInfo, () -> new MugglesBizException("未找到表信息，请检查实体类是否使用@TableName注解"));

		TableFieldInfo logicDeleteFieldInfo = tableInfo.getLogicDeleteFieldInfo();
		if (logicDeleteFieldInfo != null) {
			return alias + POINT + logicDeleteFieldInfo.getColumn() + EQ
				+ logicDeleteFieldInfo.getLogicNotDeleteValue();
		}
		return "";
	}

	/**
	 * 构建逻辑删除条件（WHERE子句，包含OR IS NULL判断）
	 *
	 * @param clazz 实体类型
	 * @param alias 表别名
	 * @return 逻辑删除条件SQL
	 */
	private String buildLogicDeleteConditionOnWhere(Class<?> clazz, String alias) {
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
	 * 当用户未指定查询字段时，默认查询主表所有列（带别名前缀），避免JOIN时同名列覆盖
	 *
	 * @param columns     用户指定的列
	 * @param excludes    用户排除的列（列名，下划线格式）
	 * @param entityClass 主表实体类型
	 * @param alias       主表别名
	 * @return 查询字段列表
	 */
	private List<String> buildSelectColumns(List<String> columns, List<String> excludes, Class<?> entityClass, String alias) {
		if (CollUtil.isNotEmpty(columns)) {
			return columns;
		}
		// 排除字段集合（列名格式）
		Set<String> excludeSet = CollUtil.isEmpty(excludes) ? Collections.emptySet() : new HashSet<>(excludes);
		// 未指定字段时，根据主表实体生成 alias.column AS column 格式，避免JOIN同名列覆盖且保证MyBatis能正确映射
		TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
		if (tableInfo != null) {
			List<String> result = new ArrayList<>();
			String keyColumn = tableInfo.getKeyColumn();
			if (!excludeSet.contains(keyColumn)) {
				result.add(alias + POINT + keyColumn + " AS " + keyColumn);
			}
			for (TableFieldInfo field : tableInfo.getFieldList()) {
				if (field.isSelect() && !excludeSet.contains(field.getColumn())) {
					result.add(alias + POINT + field.getColumn() + " AS " + field.getColumn());
				}
			}
			return result;
		}
		return CollUtil.newArrayList(SELECT_ALL);
	}

	/**
	 * 递归收集联表的查询字段（通过selectAs设置的字段）
	 *
	 * @param joins         联表列表
	 * @param selectColumns 查询字段列表（结果累加到此列表）
	 */
	private void collectJoinSelectColumns(List<Muggle<?>> joins, List<String> selectColumns) {
		for (Muggle<?> join : joins) {
			if (CollUtil.isNotEmpty(join.getFields())) {
				selectColumns.addAll(join.getFields());
			}
			if (CollUtil.isNotEmpty(join.getJoins())) {
				collectJoinSelectColumns(join.getJoins(), selectColumns);
			}
		}
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
		// 递归收集所有联表的WHERE条件
		collectJoinWhereConditions(muggle.getJoins(), whereConditions);
	}

	/**
	 * 递归收集联表的WHERE查询条件
	 *
	 * @param joins           联表列表
	 * @param whereConditions WHERE条件列表
	 */
	private void collectJoinWhereConditions(List<Muggle<?>> joins, List<String> whereConditions) {
		for (Muggle<?> join : joins) {
			QueryWrapper<?> joinWrapper = WrapperTranslator.translate(join);
			// join表的逻辑删除已在ON子句中处理，WHERE中只生成用户条件（不再追加逻辑删除）
			String joinCondition = generateJoinWhereCondition(joinWrapper, join.getAlias(), join.getEntityClass());
			if (StrUtil.isNotBlank(joinCondition)) {
				whereConditions.add(joinCondition);
			}
			// 递归处理子联表
			if (CollUtil.isNotEmpty(join.getJoins())) {
				collectJoinWhereConditions(join.getJoins(), whereConditions);
			}
		}
	}

	/**
	 * 生成联表的WHERE查询条件（不含逻辑删除，逻辑删除已在ON子句中处理）
	 *
	 * @param queryWrapper 查询条件封装
	 * @param alias        表别名
	 * @param clazz        实体类型
	 * @return 拼接后的条件SQL
	 */
	private String generateJoinWhereCondition(QueryWrapper<?> queryWrapper, String alias, Class<?> clazz) {
		String sqlSegment = queryWrapper.getExpression().getNormal().getSqlSegment();
		if (StrUtil.isBlank(sqlSegment)) {
			return "";
		}
		String whereCondition = AND + sqlSegment;
		List<String> subQueries = CollUtil.newArrayList();
		whereCondition = fillSqlParam(whereCondition, queryWrapper.getParamNameValuePairs());
		whereCondition = protectNestedSubQueries(whereCondition, subQueries);
		whereCondition = addColumnAlias(whereCondition, alias, clazz);
		return restoreSubQueries(whereCondition, subQueries);
	}

	/**
	 * 生成WHERE查询条件
	 *
	 * @param queryWrapper 查询条件封装
	 * @param alias        表别名
	 * @return 拼接后的条件SQL
	 */
	private String generateWhereCondition(QueryWrapper<?> queryWrapper, String alias,Class<?> clazz) {
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
		// 基于实体元数据给无别名的列添加表别名前缀
		whereCondition = addColumnAlias(whereCondition, alias, clazz);
		// 恢复子查询
		return restoreSubQueries(whereCondition, subQueries);
	}

	/**
	 * 基于实体元数据，给WHERE条件中无别名前缀的列添加表别名
	 *
	 * @param sql   WHERE条件SQL
	 * @param alias 表别名
	 * @param clazz 实体类型
	 * @return 添加别名后的SQL
	 */
	private String addColumnAlias(String sql, String alias, Class<?> clazz) {
		if (StrUtil.isBlank(sql) || StrUtil.isBlank(alias)) {
			return sql;
		}
		TableInfo tableInfo = TableInfoHelper.getTableInfo(clazz);
		if (tableInfo == null) {
			return sql;
		}
		// 收集实体所有列名（按长度倒序，优先替换长列名避免部分匹配）
		List<String> columns = new ArrayList<>();
		if (StrUtil.isNotBlank(tableInfo.getKeyColumn())) {
			columns.add(tableInfo.getKeyColumn());
		}
		tableInfo.getFieldList().forEach(f -> columns.add(f.getColumn()));
		columns.sort((a, b) -> b.length() - a.length());

		String prefix = alias + POINT;
		for (String col : columns) {
			// 替换未带别名前缀的列：前面不能是字母、数字、下划线或点，后面不能是字母、数字或下划线
			sql = sql.replaceAll("(?<![\\w.])" + col + "(?!\\w)", prefix + col);
		}
		return sql;
	}

	/**
	 * 使用JSQLParser进行字段别名替换
	 *
	 * @param sql   原始SQL
	 * @param alias 表的别名
	 * @return 替换别名后的SQL
	 */
	private String replaceColumnAliasesWithJSQLParser(String sql, String alias) {
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
	private String restoreSubQueries(String sql, List<String> subQueries) {
		for (int i = 0; i < subQueries.size(); i++) {
			sql = sql.replace(subqueryPlaceholder + i, "(" + subQueries.get(i) + ")");
		}
		return sql;
	}

	/**
	 * 保护子查询部分，避免对其进行别名替换
	 */
	private String protectNestedSubQueries(String sql, List<String> subQueries) {
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
        switch (obj) {
            case null -> {
                return null;
            }
            case String s -> {
                return StrUtil.wrap(StringEscape.escapeRawString(obj.toString()), "'");
            }
            case LocalDateTime localDateTime -> {
                String dateStr = LocalDateTimeUtil.formatNormal(localDateTime);
                return StrUtil.wrap(StringEscape.escapeRawString(dateStr), "'");
            }
            case LocalDate localDate -> {
                String dateStr = LocalDateTimeUtil.formatNormal(localDate);
                return StrUtil.wrap(StringEscape.escapeRawString(dateStr), "'");
            }
            case Date date -> {
                String dateStr = DateUtil.formatDateTime(date);
                return StrUtil.wrap(StringEscape.escapeRawString(dateStr), "'");
            }
            default -> {
                return obj.toString();
            }
        }
    }
}
