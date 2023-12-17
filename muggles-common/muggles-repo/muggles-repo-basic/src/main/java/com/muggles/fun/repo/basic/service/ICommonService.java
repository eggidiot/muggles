package com.muggles.fun.repo.basic.service;

import com.muggles.fun.basic.IMuggleService;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 通用Service继承接口
 *
 * @param <T>
 */
public interface ICommonService<T> extends IMuggleService<T> {

	/**
	 * 获取mapper对象
	 *
	 * @return CommonMapper<T>
	 */
	<M extends CommonMapper<T>> M mapper();

	/**
	 * 根据 entity 条件，查询一条记录，并锁定
	 *
	 * @param queryWrapper 实体对象封装操作类（可以为 null）
	 * @return T
	 */
	default T getOneForUpdate(Wrapper<T> queryWrapper) {
		return mapper().selectOneForUpdate(queryWrapper);
	}

	/**
	 * 根据 entity 条件，查询全部记录，并锁定
	 *
	 * @param queryWrapper 实体对象封装操作类（可以为 null）
	 * @return List<T>
	 */
	default List<T> listForUpdate(Wrapper<T> queryWrapper) {
		return mapper().selectListForUpdate(queryWrapper);
	}

	/**
	 * 根据ID更新字为null
	 *
	 * @param fields 置NULL字段集合
	 * @param id     实体主键
	 * @return	boolean
	 */
	default boolean updateFieldNullById(List<String> fields, Serializable id) {
		QueryWrapper<T> wrapper = Wrappers.query();
		//1.更新条件为空则不更新
		if (CollUtil.isEmpty(fields)) {
			return false;
		}
		//2.ID为NULL则不允许更新
		Assert.notNull(id, () -> new FlineBizException("id不能为空"));

		//3.操作数据库update语句
		return SqlHelper.retBool(mapper().updateFieldNUll(tableName(), fields, wrapper.eq("id", id)));

	}

	/**
	 * 根据ID更新字段自增
	 *
	 * @param entity 需要更新的自增实体，会忽略非数字类型字段值，对于自增负数会添加条件判断
	 * @return	boolean
	 */
	default boolean updateFieldSelfById(T entity) {
		return updateFieldSelfById(entity, true);
	}

	/**
	 * 根据ID更新字段自增，会忽略非数字类型字段值
	 *
	 * @param entity  实体
	 * @param postive 是否添加非负判断
	 * @return	boolean
	 */
	default boolean updateFieldSelfById(T entity, boolean postive) {
		Object id = ReflectUtil.getFieldValue(entity, "id");
		//1.ID为NULL则不允许更新
		Assert.notNull(id, () -> new FlineBizException("id不能为空"));
		ReflectUtil.setFieldValue(entity, "id", null);

		QueryWrapper<T> wrapper = Wrappers.<T>query().eq("id", id);
		Map<String, Object> res = toMap(wrapper, entity, true, postive);
		//2.更新条件为空则不更新
		if (CollectionUtil.isEmpty(res)) {
			return false;
		}

		//3.判断逻辑删除和乐观锁
		return SqlHelper.retBool(mapper().updateFieldSelf(tableName(), res, wrapper));
	}

	/**
	 * 实体对象类型
	 * @return	Class<T>
	 */
	Class<T> getTClass();

	/**
	 * 获取实体对象对应的表名
	 *
	 * @return String
	 */
	default String tableName() {
		return TableInfoHelper.getTableInfo(getTClass()).getTableName();
	}

	/**
	 * 获取实体属性键值对，同时更新条件
	 *
	 * @param wrapper         更新条件
	 * @param entity          实体属性
	 * @param ignoreNotNumber 忽略非数字类型
	 * @return	Map<String, Object>
	 */
	default Map<String, Object> toMap(QueryWrapper<T> wrapper, T entity, boolean ignoreNotNumber, boolean postive) {
		//1.生成属性键值对
		Map<String, Object> result = MapUtil.newHashMap();
		List<Field> fields = TableInfoHelper.getAllFields(entity.getClass());
		//1.处理逻辑删除
		fields.stream().filter(field -> field.isAnnotationPresent(TableLogic.class)).forEach(field -> {
			wrapper.eq(StrUtil.toUnderlineCase(field.getName()), Constants.INIT);
		});
		//2.处理属性字段以及乐观锁
		fields.stream()
			.filter(field -> ObjectUtil.isNotNull(ReflectUtil.getFieldValue(entity, field)))
			.filter(field -> !ignoreNotNumber || Number.class.isAssignableFrom(field.getType()))
			.forEach(field -> {
				Object v = ReflectUtil.getFieldValue(entity, field);
				if (field.isAnnotationPresent(Version.class)) {
					wrapper.eq(StrUtil.toUnderlineCase(field.getName()), v);
					//2.1版本号每次只能涨一个单位
					result.put(StrUtil.toUnderlineCase(field.getName()), Constants.DEFAULT_OPT);
					return;
				}
				//2.2非负判定
				if (postive && (new BigDecimal(v.toString()).compareTo(new BigDecimal(0)) < 0)) {
					wrapper.ge(StrUtil.toUnderlineCase(field.getName()), new BigDecimal(v.toString()).abs());
				}
				result.put(StrUtil.toUnderlineCase(field.getName()), v);
			});
		return result;
	}

	/**
	 * 物理删除
	 *
	 * @param id 主键
	 * @return	boolean
	 */
	default boolean deletePhyById(Serializable id) {
		//1.ID为NULL则不允许删除
		Assert.notNull(id, () -> new FlineBizException("id不能为空"));
		return SqlHelper.retBool(mapper().deletePhy(tableName(), Wrappers.<T>query().eq("id", id)));
	}

	/**
	 * 插入一条记录（选择字段，策略插入）
	 *
	 * @param entity 实体对象
	 * @return	boolean
	 */
	default boolean save(T entity) {
		return SqlHelper.retBool(mapper().insert(entity));
	}

	/**
	 * 根据 ID 选择修改
	 *
	 * @param entity 实体对象
	 * @return	boolean
	 */
	default boolean updateById(T entity) {
		return SqlHelper.retBool(mapper().updateById(entity));
	}

	/**
	 * TableId 注解存在更新记录，否插入一条记录
	 *
	 * @param entity 实体对象
	 * @return	boolean
	 */
	boolean saveOrUpdate(T entity);

	/**
	 * 根据 ID 查询
	 *
	 * @param id 主键ID
	 * @return	T
	 */
	default T getById(Serializable id) {
		return mapper().selectById(id);
	}

	/**
	 * 根据 ID 删除
	 *
	 * @param id 主键ID
	 * @return	boolean
	 */
	default boolean removeById(Serializable id) {
		return SqlHelper.retBool(mapper().deleteById(id));
	}

	/**
	 * 查询所有
	 *
	 * @see Wrappers#emptyWrapper()
	 * @return	List<T>
	 */
	default List<T> list() {
		return mapper().selectList(Wrappers.emptyWrapper());
	}

	/**
	 * 插入（批量）
	 *
	 * @param entityList 实体对象集合
	 * @return	boolean
	 */
	boolean saveBatch(Collection<T> entityList);

	/**
	 * 批量修改插入
	 *
	 * @param entityList 实体对象集合
	 * @return	boolean
	 */
	boolean saveOrUpdateBatch(Collection<T> entityList);

	/**
	 * 根据ID 批量更新
	 *
	 * @param entityList 实体对象集合
	 * @return	boolean
	 */
	boolean updateBatchById(Collection<T> entityList);

	/**
	 * 批量删除(jdbc批量提交)
	 *
	 * @param list 主键ID或实体列表(主键ID类型必须与实体类型字段保持一致)
	 * @return	boolean
	 * @since 3.5.0
	 */
	boolean removeBatchByIds(Collection<?> list);
}
