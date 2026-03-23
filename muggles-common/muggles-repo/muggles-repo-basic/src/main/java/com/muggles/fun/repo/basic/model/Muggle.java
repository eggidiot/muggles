package com.muggles.fun.repo.basic.model;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.muggles.fun.basic.Constants;
import com.muggles.fun.basic.exception.MugglesBizException;
import com.muggles.fun.basic.model.MuggleParam;
import com.muggles.fun.repo.basic.IFieldMapping;
import com.muggles.fun.repo.basic.RepoConstants;
import com.muggles.fun.repo.basic.criteria.CriteriaType;
import com.muggles.fun.repo.basic.criteria.OnCriteria;
import com.muggles.fun.repo.basic.criteria.QueryCriteria;
import com.muggles.fun.repo.basic.criteria.between.BetweenParam;
import com.muggles.fun.tools.core.bean.LambdaFunction;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.muggles.fun.repo.basic.RepoConstants.FuncType;
import static com.muggles.fun.basic.Constants.RelationType;
import static com.muggles.fun.repo.basic.RepoConstants.JoinType;

/**
 * 核心查询数据结构，用于默认通用查询，和特定查询方案
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class Muggle<T> extends MuggleParam<T, Muggle<T>> {
    /**
     * 取别名
     */
    protected String alias = "t";
    /**
     * 默认别名前缀
     */
    private static final String DEFAULT_ALIAS = "t";
    /**
     * 作为连接被驱动表时，保存连接驱动表的别名，不用接受外部输入
     */
    @Accessors(fluent = true)
    protected String outAlias;
    /**
     * 子查询时外部默认方式
     */
    protected CriteriaType subQuery = CriteriaType.In;
    /**
     * 默认内连接
     */
    protected JoinType join = JoinType.INNER;
    /**
     * 查询条件，数组形式，父类属性中的params最终也将翻译到该属性
     */
    protected List<QueryCriteria> criterias = new ArrayList<>();
    /**
     * 适用于关联查询的集合
     */
    protected List<Muggle<?>> joins = new ArrayList<>();
    /**
     * 连表条件，数组形式，父类属性中的params最终也将翻译到该属性
     */
    protected List<OnCriteria> on = new ArrayList<>();
    /**
     * 指定实体类型
     */
    protected Class<T> entityClass;
    /**
     * 属性映射字段映射器类文件对象
     */
    private Class<? extends IFieldMapping> mappingClass = DefaultMapping.class;
    /**
     * 属性映射字段映射器
     */
    private IFieldMapping mapping;
    /**
     * 分页时是否执行count操作
     */
    private Boolean searchCount = true;
    /**
     * 默认使用的函数带有ifnull函数模板
     */
    @Getter
    @Setter
    protected static String func = RepoConstants.FUNC_TEMPLATE;
    /**
     * 联表SQL
     */
    private String joinSql;
    /**
     * 联表查询字段列表
     */
    private List<String> selectColumns = new ArrayList<>();
    /**
     * 联表WHERE查询条件列表
     */
    private List<String> whereConditions = new ArrayList<>();
    /**
     * 联表分组字段列表
     */
    private List<String> groupByColumns = new ArrayList<>();
    /**
     * 指定查询字段
     *
     * @param fields 字段名边长数组
     * @return Muggle<T>
     */
    @SafeVarargs
    public final Muggle<T> select(LambdaFunction<T, ?>... fields) {
        List<LambdaFunction<T, ?>> list = Arrays.asList(fields);
        String[] array = ArrayUtil.toArray(list.stream().filter(Objects::nonNull).map(this::columnsToString).collect(Collectors.toList()),String.class);
        return select(array);
    }
    /**
     * 指定查询字段并设置别名，表别名会在联表解析时自动添加
     * 适用于联表查询时同名字段需要区分的场景
     * 示例：selectAs(SysRole::getName, "role_name") → 解析后 "www.name AS role_name"
     *
     * @param field 字段Lambda
     * @param alias 别名
     * @return Muggle<T>
     */
    public <R> Muggle<T> selectAs(LambdaFunction<R, ?> field, String alias) {
        String col = columnsToString(field);
        String prefix = StrUtil.isNotBlank(this.alias) ? this.alias + "." : "";
        return select(prefix + col + " AS " + alias);
    }

    /**
     * 添加排序字段
     *
     * @param fields 排序字段
     * @return Muggle<T>
     */
    @SafeVarargs
    public final Muggle<T> orderBy(LambdaFunction<T, ?>... fields) {
        Arrays.asList(fields).forEach(f -> orderBy(f, Constants.ASC));
        return this;
    }
    /**
     * 排除查询字段
     *
     * @param fields 字段边长数组
     * @return Muggle<T>
     */
    @SafeVarargs
    public final Muggle<T> excludes(LambdaFunction<T, ?>... fields) {
        List<LambdaFunction<T, ?>> list = Arrays.asList(fields);
        String[] array = ArrayUtil.toArray(list.stream().filter(Objects::nonNull).map(this::columnsToString).collect(Collectors.toList()),String.class);
        return excludes(array);
    }

    /**
     * 添加排序字段
     *
     * @param groups 分组字段
     * @return Muggle<T>
     */
    @SafeVarargs
    public final Muggle<T> groupBy(LambdaFunction<T, ?>... groups) {
        Assert.notNull(groups, () -> new MugglesBizException("分组字段不能传null值"));
        List<LambdaFunction<T, ?>> list = Arrays.asList(groups);
        String[] array = ArrayUtil.toArray(list.stream().map(this::columnsToString).collect(Collectors.toList()),String.class);
        return groupBy(array);
    }

    /**
     * 添加排序字段
     *
     * @param field 字段
     * @param asc   排序方式
     * @return Muggle<T>
     */
    public Muggle<T> orderBy(LambdaFunction<T, ?> field, int asc) {
        return orderBy(columnsToString(field),asc);
    }

    /**
     * 添加相等查询参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> eq(String attribute, Object value) {
        criterias.add(new QueryCriteria(attribute, value, CriteriaType.Equal, type()));
        return this;
    }

    /**
     * 添加不等参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> notEq(String attribute, Object value) {
        criterias.add(new QueryCriteria(attribute, value, CriteriaType.NotEqual, type()));
        return this;
    }

    /**
     * 添加模糊参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> like(String attribute, Object value) {
        criterias.add(new QueryCriteria(attribute, value, CriteriaType.Like, type()));
        return this;
    }

    /**
     * 添加模糊取反参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> notLike(String attribute, Object value) {
        criterias.add(new QueryCriteria(attribute, value, CriteriaType.NotLike, type()));
        return this;
    }

    /**
     * 添加左模糊参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> likeLeft(String attribute, Object value) {
        criterias.add(new QueryCriteria(attribute, value, CriteriaType.LikeLeft, type()));
        return this;
    }

    /**
     * 添加左模糊取反参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> notLikeLeft(String attribute, Object value) {
        criterias.add(new QueryCriteria(attribute, value, CriteriaType.NotLikeLeft, type()));
        return this;
    }

    /**
     * 添加右模糊参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> likeRight(String attribute, Object value) {
        criterias.add(new QueryCriteria(attribute, value, CriteriaType.LikeRight, type()));
        return this;
    }

    /**
     * 添加右模糊取反参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> notLikeRight(String attribute, Object value) {
        criterias.add(new QueryCriteria(attribute, value, CriteriaType.NotLikeRight, type()));
        return this;
    }

    /**
     * 添加大于参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> greaterthan(String attribute, Object value) {
        criterias.add(new QueryCriteria(attribute, value, CriteriaType.Greaterthan, type()));
        return this;
    }

    /**
     * 添加小于参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> lessthan(String attribute, Object value) {
        criterias.add(new QueryCriteria(attribute, value, CriteriaType.Lessthan, type()));
        return this;
    }

    /**
     * 添加小于等于参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> lessthanOrEqual(String attribute, Object value) {
        criterias.add(new QueryCriteria(attribute, value, CriteriaType.LessthanOrEqual, type()));
        return this;
    }

    /**
     * 添加大于等于参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> greaterthanOrEqual(String attribute, Object value) {
        criterias.add(new QueryCriteria(attribute, value, CriteriaType.GreaterthanOrEqual, type()));
        return this;
    }

    /**
     * 添加范围参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> in(String attribute, Collection<?> value) {
        criterias.add(new QueryCriteria(attribute, value, CriteriaType.In, type()));
        return this;
    }
    /**
     * 添加范围反向参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> notIn(String attribute, Collection<?> value) {
        criterias.add(new QueryCriteria(attribute, value, CriteriaType.NotIn, type()));
        return this;
    }

    /**
     * 添加null参数
     *
     * @param attribute 参数名
     * @return Muggle<T>
     */
    public Muggle<T> isNull(String attribute) {
        criterias.add(new QueryCriteria(attribute, null, CriteriaType.IsNull, type()));
        return this;
    }

    /**
     * 添加非null参数
     *
     * @param attribute 参数名
     * @return Muggle<T>
     */
    public Muggle<T> isNotNull(String attribute) {
        criterias.add(new QueryCriteria(attribute, null, CriteriaType.IsNotNull, type()));
        return this;
    }

    /**
     * 添加范围参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> between(String attribute, BetweenParam value) {
        criterias.add(new QueryCriteria(attribute, value, CriteriaType.Between, type()));
        return this;
    }


    /**
     * 批量添加eq查询条件，会把entity不为null的属性都用eq方式查询
     *
     * @param entity – 查询对象
     * @return  Muggle<T>
     */
    public Muggle<T> eqAll(T entity) {
        criterias.add(new QueryCriteria(null, entity, CriteriaType.EqualAll, getType()));
        return setEntityClass((Class<T>) entity.getClass());
    }
    /**
     * 添加相等查询参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> eq(LambdaFunction<T, ?> attribute, Object value) {
        return eq(columnsToString(attribute), value);
    }

    /**
     * 添加不等参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> notEq(LambdaFunction<T, ?> attribute, Object value) {
        return notEq(columnsToString(attribute), value);
    }

    /**
     * 添加模糊取反参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> notLike(LambdaFunction<T, ?> attribute, Object value) {
        return notLike(columnsToString(attribute), value);
    }

    /**
     * 添加模糊参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> like(LambdaFunction<T, ?> attribute, Object value) {
        return like(columnsToString(attribute), value);
    }

    /**
     * 添加左模糊取反参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> notLikeLeft(LambdaFunction<T, ?> attribute, Object value) {
        return notLikeLeft(columnsToString(attribute), value);
    }

    /**
     * 添加左模糊参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> likeLeft(LambdaFunction<T, ?> attribute, Object value) {
        return likeLeft(columnsToString(attribute), value);
    }

    /**
     * 添加右模糊取反参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> notLikeRight(LambdaFunction<T, ?> attribute, Object value) {
        return notLikeRight(columnsToString(attribute), value);
    }

    /**
     * 添加右模糊参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> likeRight(LambdaFunction<T, ?> attribute, Object value) {
        return likeRight(columnsToString(attribute), value);
    }

    /**
     * 添加大于参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> greaterthan(LambdaFunction<T, ?> attribute, Object value) {
        return greaterthan(columnsToString(attribute), value);
    }

    /**
     * 添加小于参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> lessthan(LambdaFunction<T, ?> attribute, Object value) {
        return lessthan(columnsToString(attribute), value);
    }

    /**
     * 添加小于等于参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> lessthanOrEqual(LambdaFunction<T, ?> attribute, Object value) {
        return lessthanOrEqual(columnsToString(attribute), value);
    }

    /**
     * 添加大于等于参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> greaterthanOrEqual(LambdaFunction<T, ?> attribute, Object value) {
        return greaterthanOrEqual(columnsToString(attribute), value);
    }

    /**
     * 添加范围参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> in(LambdaFunction<T, ?> attribute, Collection<?> value) {
        return in(columnsToString(attribute), value);
    }

    /**
     * 添加null参数
     *
     * @param attribute 参数名
     * @return Muggle<T>
     */
    public Muggle<T> isNull(LambdaFunction<T, ?> attribute) {
        return isNull(columnsToString(attribute));
    }

    /**
     * 添加非null参数
     *
     * @param attribute 参数名
     * @return Muggle<T>
     */
    public Muggle<T> isNotNull(LambdaFunction<T, ?> attribute) {
        return isNotNull(columnsToString(attribute));
    }

    /**
     * 添加范围参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> between(LambdaFunction<T, ?> attribute, BetweenParam value) {
        return between(columnsToString(attribute), value);
    }

    /**
     * 添加范围反向参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public Muggle<T> notIn(LambdaFunction<T, ?> attribute, Collection<?> value) {
        return notIn(columnsToString(attribute), value);
    }


    /**
     * 获取实体对象的字段名称
     *
     * @param attribute 字段属性名称
     * @return String
     */
    public <T>String columnsToString(LambdaFunction<T, ?> attribute) {
        if (mapping == null) {
            mapping = ReflectUtil.newInstance(mappingClass);
        }
        return mapping.fieldMappingColum(attribute);
    }

    //======================================================连接条件组==============================================
    /**
     * 修改or连接符
     *
     * @return Muggle<T>
     */
    public Muggle<T> or() {
        nextRelation.set(false);
        return this;
    }
    /**
     * 修改or连接符
     *
     * @return Muggle<T>
     */
    public Muggle<T> and() {
        nextRelation.set(true);
        return this;
    }
    /**
     * 使用连接符框定一组条件
     *
     * @return Muggle<T>
     */
    public Muggle<T> relation(Consumer<Muggle<T>> consumer, RelationType type) {
        Muggle<T> relation = new Muggle<T>().setType(type);
        relations.add(relation);
        consumer.accept(relation);
        return this;
    }
    /**
     * or方式连接內联一组条件
     *
     * @param consumer 执行函数
     * @return Muggle<T>
     */
    public Muggle<T> or(Consumer<Muggle<T>> consumer) {
        return relation(consumer, RelationType.OR);
    }

    /**
     * and方式连接內联一组条件
     *
     * @param consumer 执行函数
     * @return Muggle<T>
     */
    public Muggle<T> and(Consumer<Muggle<T>> consumer) {
        return relation(consumer, RelationType.AND);
    }

    /**
     * 纯嵌套內联一组条件，一般用于第一组嵌套条件
     *
     * @param consumer 执行函数
     * @return Muggle<T>
     */
    public Muggle<T> nested(Consumer<Muggle<T>> consumer) {
        return relation(consumer, RelationType.NESTED);
    }
    //======================================================子查询=================================================
    /**
     * 添加范围notin子查询
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public <R> Muggle<T> notIn(String attribute, Muggle<R> value) {
        value.setSubQuery(CriteriaType.NotIn);
        criterias.add(new QueryCriteria(attribute, value, CriteriaType.SubQuery, type()));
        return this;
    }
    /**
     * 添加范围notin子查询
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public <R> Muggle<T> notIn(LambdaFunction<T, ?> attribute, Muggle<R> value) {
        return notIn(columnsToString(attribute), value);
    }
    /**
     * 添加范围参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public <R> Muggle<T> in(LambdaFunction<T, ?> attribute, Muggle<R> value) {
        return in(columnsToString(attribute), value);
    }
    /**
     * 添加范围参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public <R> Muggle<T> in(String attribute, Muggle<R> value) {
        value.setSubQuery(CriteriaType.In);
        criterias.add(new QueryCriteria(attribute, value, CriteriaType.SubQuery, type()));
        return this;
    }
    /**
     * 添加范围参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public <R> Muggle<T> eq(String attribute, Muggle<R> value) {
        value.setSubQuery(CriteriaType.Equal);
        criterias.add(new QueryCriteria(attribute, value, CriteriaType.SubQuery, type()));
        return this;
    }
    /**
     * 添加范围参数
     *
     * @param attribute 参数名
     * @param value     参数值
     * @return Muggle<T>
     */
    public <R> Muggle<T> notEq(String attribute, Muggle<R> value) {
        value.setSubQuery(CriteriaType.NotEqual);
        criterias.add(new QueryCriteria(attribute, value, CriteriaType.SubQuery, type()));
        return this;
    }
    //=====================================================连表查询=================================================
    /**
     * 统一添加联表，自动分配默认别名（若未手动设置）
     * 别名规则：t1, t2, t3... 按加入顺序递增
     *
     * @param joinInfo 联表查询信息
     * @param type     连表类型
     */
    private <R> void addJoin(Muggle<R> joinInfo, JoinType type) {
        if (DEFAULT_ALIAS.equals(joinInfo.getAlias())) {
            joinInfo.setAlias(DEFAULT_ALIAS + (this.joins.size() + 1));
        }
        this.joins.add(joinInfo.setJoin(type).outAlias(getAlias()));
    }
    /**
     * 设置连表查询条件
     * @param type      连表类型
     * @param joinInfo  连接信息
     * @return  Muggle<T>
     * @param <R>   外连对象的泛型
     */
    public <R>Muggle<T> join(JoinType type,Muggle<R> joinInfo){
        addJoin(joinInfo, type);
        return this;
    }
    /**
     * 设置连表查询条件
     * @param joinInfo  连接信息
     * @return  Muggle<T>
     * @param <R>   泛型类型
     */
    public <R>Muggle<T> join(Muggle<R> joinInfo){
        addJoin(joinInfo, JoinType.INNER);
        return this;
    }
    /**
     * 设置左连表查询条件
     * @param joinInfo  连接信息
     * @return  Muggle<T>
     * @param <R>   泛型类型
     */
    public <R>Muggle<T> leftJoin(Muggle<R> joinInfo){
        addJoin(joinInfo, JoinType.LEFT);
        return this;
    }
    /**
     * 设置右连表查询条件
     * @param joinInfo  连接信息
     * @return  Muggle<T>
     * @param <R>   泛型类型
     */
    public <R>Muggle<T> rightJoin(Muggle<R> joinInfo){
        addJoin(joinInfo, JoinType.RIGHT);
        return this;
    }
    /**
     * 设置全连接表查询条件
     * @param joinInfo  连接信息
     * @return  Muggle<T>
     * @param <R>   泛型类型
     */
    public <R>Muggle<T> fullJoin(Muggle<R> joinInfo){
        addJoin(joinInfo, JoinType.FULL);
        return this;
    }
    /**
     * 设置连表查询条件，使用Consumer配置连接信息
     * @param type      连表类型
     * @param clazz     连接实体类型
     * @param consumer  连接信息配置
     * @return  Muggle<T>
     * @param <R>   外连对象的泛型
     */
    public <R> Muggle<T> join(JoinType type, Class<R> clazz, Consumer<Muggle<R>> consumer) {
        Muggle<R> joinInfo = new Muggle<R>().setEntityClass(clazz);
        consumer.accept(joinInfo);
        addJoin(joinInfo, type);
        return this;
    }
    /**
     * 内连接查询，使用Consumer配置连接信息
     * @param clazz     连接实体类型
     * @param consumer  连接信息配置
     * @return  Muggle<T>
     * @param <R>   泛型类型
     */
    public <R> Muggle<T> join(Class<R> clazz, Consumer<Muggle<R>> consumer) {
        return join(JoinType.INNER, clazz, consumer);
    }
    /**
     * 左连接查询，使用Consumer配置连接信息
     * @param clazz     连接实体类型
     * @param consumer  连接信息配置
     * @return  Muggle<T>
     * @param <R>   泛型类型
     */
    public <R> Muggle<T> leftJoin(Class<R> clazz, Consumer<Muggle<R>> consumer) {
        return join(JoinType.LEFT, clazz, consumer);
    }
    /**
     * 右连接查询，使用Consumer配置连接信息
     * @param clazz     连接实体类型
     * @param consumer  连接信息配置
     * @return  Muggle<T>
     * @param <R>   泛型类型
     */
    public <R> Muggle<T> rightJoin(Class<R> clazz, Consumer<Muggle<R>> consumer) {
        return join(JoinType.RIGHT, clazz, consumer);
    }
    /**
     * 全连接查询，使用Consumer配置连接信息
     * @param clazz     连接实体类型
     * @param consumer  连接信息配置
     * @return  Muggle<T>
     * @param <R>   泛型类型
     */
    public <R> Muggle<T> fullJoin(Class<R> clazz, Consumer<Muggle<R>> consumer) {
        return join(JoinType.FULL, clazz, consumer);
    }
    /**
     * 设置连表on条件，作为子表查询时用
     * @param attr1 驱动表属性
     * @param attr2 被驱动表属性
     * @return  Muggle<T>
     * @param <R>   驱动表泛型
     */
    public <R>Muggle<T> on(LambdaFunction<R,?> attr1,LambdaFunction<T,?> attr2) {
        return on(attr1,attr2,RelationType.AND);
    }
    /**
     * 设置连表on条件，作为子表查询时用
     * @param attr1 驱动表属性
     * @param attr2 被驱动表属性
     * @return  Muggle<T>
     */
    public Muggle<T> on(String attr1,String attr2) {
        return on(attr1,attr2,RelationType.AND);
    }
    /**
     * 设置连表on条件，作为子表查询时用
     * @param attr1 驱动表属性
     * @param attr2 被驱动表属性
     * @return  Muggle<T>
     */
    public Muggle<T> on(String attr1,String attr2,RelationType type) {
        this.on.add(new OnCriteria(attr1,attr2,type));
        return this;
    }
    /**
     * 设置连表on条件，作为子表查询时用
     * @param attr1 驱动表属性
     * @param attr2 被驱动表属性
     * @return  Muggle<T>
     */
    public <R>Muggle<T> on(LambdaFunction<R,?> attr1,LambdaFunction<T,?> attr2,RelationType type) {
        return on(columnsToString(attr1),columnsToString(attr2),type);
    }

    //======================================================聚合函数================================================

    /**
     * 函数调用
     *
     * @param type  调用类型
     * @param field 字段名
     * @param alias 别名
     * @return SqlFunc 当前对象本身
     */
    public Muggle<T> func(FuncType type, String field, String alias) {
        //1.使用聚合函数时默认不添加排序字段
        if (StrUtil.isBlank(alias)) {
            alias = field;
        }
        Assert.notNull(type, () -> new MugglesBizException("未知函数名称"));
        field = String.format(Muggle.getFunc(), type.name().toLowerCase(), field, alias);
        this.fields.add(field);
        return this;
    }

    /**
     * 函数调用
     *
     * @param type  调用类型
     * @param field 字段名
     * @param alias 别名
     * @return SqlFunc 当前对象本身
     */
    public Muggle<T> func(FuncType type, LambdaFunction<T, ?> field, String alias) {
        return func(type, columnsToString(field), alias);
    }

    /**
     * 最大值函数调用
     *
     * @param field 字段名
     * @param alias 别名
     * @return SqlFunc 当前对象本身
     */
    public Muggle<T> max(String field, String alias) {
        return func(FuncType.MAX, field, alias);
    }

    /**
     * 最大值函数调用
     *
     * @param field 字段名
     * @return SqlFunc 当前对象本身
     */
    public Muggle<T> max(String field) {
        return func(FuncType.MAX, field, field);
    }

    /**
     * 最大值函数调用
     *
     * @param field 字段名
     * @param alias 别名
     * @return SqlFunc 当前对象本身
     */
    public Muggle<T> max(LambdaFunction<T, ?> field, String alias) {
        return func(FuncType.MAX, columnsToString(field), alias);
    }

    /**
     * 最大值函数调用
     *
     * @param field 字段名
     * @return SqlFunc 当前对象本身
     */
    public Muggle<T> max(LambdaFunction<T, ?> field) {
        String fieldName = columnsToString(field);
        return func(FuncType.MAX, fieldName, fieldName);
    }

    /**
     * 最小值函数调用
     *
     * @param field 字段名
     * @param alias 别名
     * @return SqlFunc 当前对象本身
     */
    public Muggle<T> min(String field, String alias) {
        return func(FuncType.MIN, field, alias);
    }

    /**
     * 最小值函数调用
     *
     * @param field 字段名
     * @return SqlFunc 当前对象本身
     */
    public Muggle<T> min(String field) {
        return func(FuncType.MIN, field, field);
    }

    /**
     * 最小值函数调用
     *
     * @param field 字段名
     * @param alias 别名
     * @return SqlFunc 当前对象本身
     */
    public Muggle<T> min(LambdaFunction<T, ?> field, String alias) {
        return func(FuncType.MIN, columnsToString(field), alias);
    }

    /**
     * 最小值函数调用
     *
     * @param field 字段名
     * @return SqlFunc 当前对象本身
     */
    public Muggle<T> min(LambdaFunction<T, ?> field) {
        String fieldName = columnsToString(field);
        return func(FuncType.MIN, fieldName, fieldName);
    }

    /**
     * 平均值函数调用
     *
     * @param field 字段名
     * @param alias 别名
     * @return SqlFunc 当前对象本身
     */
    public Muggle<T> avg(String field, String alias) {
        return func(FuncType.AVG, field, alias);
    }

    /**
     * 平均值函数调用
     *
     * @param field 字段名
     * @return SqlFunc 当前对象本身
     */
    public Muggle<T> avg(String field) {
        return func(FuncType.AVG, field, field);
    }

    /**
     * 平均值函数调用
     *
     * @param field 字段名
     * @param alias 别名
     * @return SqlFunc 当前对象本身
     */
    public Muggle<T> avg(LambdaFunction<T, ?> field, String alias) {
        return func(FuncType.AVG, columnsToString(field), alias);
    }

    /**
     * 平均值函数调用
     *
     * @param field 字段名
     * @return SqlFunc 当前对象本身
     */
    public Muggle<T> avg(LambdaFunction<T, ?> field) {
        String fieldName = columnsToString(field);
        return func(FuncType.AVG, fieldName, fieldName);
    }

    /**
     * 平均值函数调用
     *
     * @param field 字段名
     * @param alias 别名
     * @return SqlFunc 当前对象本身
     */
    public Muggle<T> sum(String field, String alias) {
        return func(FuncType.SUM, field, alias);
    }

    /**
     * 平均值函数调用
     *
     * @param field 字段名
     * @return SqlFunc 当前对象本身
     */
    public Muggle<T> sum(String field) {
        return func(FuncType.SUM, field, field);
    }

    /**
     * 平均值函数调用
     *
     * @param field 字段名
     * @param alias 别名
     * @return SqlFunc 当前对象本身
     */
    public Muggle<T> sum(LambdaFunction<T, ?> field, String alias) {
        return func(FuncType.SUM, columnsToString(field), alias);
    }

    /**
     * 平均值函数调用
     *
     * @param field 字段名
     * @return SqlFunc 当前对象本身
     */
    public Muggle<T> sum(LambdaFunction<T, ?> field) {
        String fieldName = columnsToString(field);
        return func(FuncType.SUM, fieldName, fieldName);
    }

    /**
     * 平均值函数调用
     *
     * @param field 字段名
     * @param alias 别名
     * @return SqlFunc 当前对象本身
     */
    public Muggle<T> count(String field, String alias) {
        return func(FuncType.COUNT, field, alias);
    }

    /**
     * 平均值函数调用
     *
     * @param field 字段名
     * @return SqlFunc 当前对象本身
     */
    public Muggle<T> count(String field) {
        return func(FuncType.COUNT, field, field);
    }

    /**
     * 平均值函数调用
     *
     * @param field 字段名
     * @param alias 别名
     * @return SqlFunc 当前对象本身
     */
    public Muggle<T> count(LambdaFunction<T, ?> field, String alias) {
        return func(FuncType.COUNT, columnsToString(field), alias);
    }

    /**
     * 平均值函数调用
     *
     * @param field 字段名
     * @return SqlFunc 当前对象本身
     */
    public Muggle<T> count(LambdaFunction<T, ?> field) {
        String fieldName = columnsToString(field);
        return func(FuncType.COUNT, fieldName, fieldName);
    }

    /**
     * 查询对象字段限定
     *
     * @param selector  指定查询对象
     */
    @Override
    public Muggle<T> setSelector(T selector) {
        super.setSelector(selector);
        if (selector != null) {
            return setEntityClass((Class<T>) selector.getClass());
        }
        return this;
    }
}
