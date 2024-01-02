package com.muggles.fun.repo.mp.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.muggles.fun.basic.model.IMugglePage;
import com.muggles.fun.basic.model.MuggleParam;
import com.muggles.fun.repo.basic.model.Muggle;
import com.muggles.fun.repo.basic.service.ICommonService;
import com.muggles.fun.repo.mp.mapper.CommonMapper;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;


/**
 * 通过用Service
 *
 * @param <M>
 * @param <T>
 */
public class CommonServiceImpl<M extends CommonMapper<T>, T> extends ServiceImpl<M, T> implements ICommonService<T> {
	/**
	 * 根据 entity 条件，查询一条记录，并锁定
	 *
	 * @param muggle 实体对象封装操作类（可以为 null）
	 * @return T
	 */
	@Override
	public T oneForUpdate(Muggle<T> muggle) {
		return null;
	}

	/**
	 * 根据 entity 条件，查询全部记录，并锁定
	 *
	 * @param muggle 实体对象封装操作类（可以为 null）
	 * @return List<T>
	 */
	@Override
	public List<T> listForUpdate(Muggle<T> muggle) {
		return null;
	}

	/**
	 * 根据ID更新字为null
	 *
	 * @param fields 置NULL字段集合
	 * @param id     实体主键
	 * @return boolean
	 */
	@Override
	public boolean updateFieldNullById(List<String> fields, Serializable id) {
		return false;
	}

	/**
	 * 根据ID更新字段自增，会忽略非数字类型字段值
	 *
	 * @param entity  实体
	 * @param postive 是否添加非负判断
	 * @return boolean
	 */
	@Override
	public boolean updateFieldSelfById(T entity, boolean postive) {
		return false;
	}

	/**
	 * 实体对象类型
	 */
	public Class<T> getTClass() {

		Class<T> entityClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
		return entityClass;
	}

	/**
	 * 获取实体对象对应的表名
	 *
	 * @return String
	 */
	@Override
	public String tableName() {
		return null;
	}

	/**
	 * 物理删除
	 *
	 * @param id 主键
	 * @return boolean
	 */
	@Override
	public boolean removePhyById(Serializable id) {
		return false;
	}

	/**
	 * 根据主键恢复某个逻辑删的数据
	 *
	 * @param id 记录主键
	 * @return boolean
	 */
	@Override
	public boolean recoverById(Serializable id) {
		return false;
	}

	/**
	 * 获取mapper对象
	 *
	 * @return CommonMapper<T>
	 */
	public M mapper() {
		return baseMapper;
	}

	/**
	 * 插入一条记录（选择字段，策略插入）
	 *
	 * @param entity 实体对象
	 * @return boolean
	 */
	@Override
	public boolean save(T entity) {
		return SqlHelper.retBool(mapper().insert(entity));
	}

	/**
	 * 插入（批量）
	 *
	 * @param entityList 实体对象集合
	 */
	@Override
	public boolean saveBatch(Collection<T> entityList) {
		return super.saveBatch(entityList,100);
	}

	/**
	 * 根据ID 批量更新
	 *
	 * @param entityList 实体对象集合
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateBatchById(Collection<T> entityList) {
		return updateBatchById(entityList, DEFAULT_BATCH_SIZE);
	}


	/**
	 * 根据 ID 选择修改
	 *
	 * @param entity 实体对象
	 */
	@Override
	public boolean updateById(T entity) {
		return SqlHelper.retBool(mapper().updateById(entity));
	}

	/**
	 * 将指定条件的记录更新诚实体非null字段
	 *
	 * @param t     实体记录
	 * @param param 更新条件
	 * @return Boolean
	 */
	@Override
	public <C extends MuggleParam<T, C>> Boolean update(T t, C param) {
		return null;
	}

	/**
	 * 根据 ID 查询
	 *
	 * @param id 主键ID
	 */
	@Override
	public T getById(Serializable id) {
		return mapper().selectById(id);
	}

	/**
	 * 根据查询条件查询实体第一条记录
	 *
	 * @param param 查询条件
	 * @return T
	 */
	@Override
	public <C extends MuggleParam<T, C>> T one(C param) {
		return null;
	}

	/**
	 * 根据查询条件查询实体集合
	 *
	 * @param param 查询条件
	 * @return T
	 */
	@Override
	public <C extends MuggleParam<T, C>> List<T> list(C param) {
		return null;
	}

	/**
	 * 根据查询条件查询实体分页集合
	 *
	 * @param param 查询条件
	 * @return T
	 */
	@Override
	public <C extends MuggleParam<T, C>> IMugglePage<T> page(C param) {
		return null;
	}

	/**
	 * 根据 ID 删除
	 *
	 * @param id 主键ID
	 */
	@Override
	public boolean removeById(Serializable id) {
		return SqlHelper.retBool(mapper().deleteById(id));
	}

	/**
	 * 批量保存实体
	 *
	 * @param list 实体记录
	 * @return Boolean
	 */
	@Override
	public boolean saveBatch(List<T> list) {
		return false;
	}

	/**
	 * 根据实体id批量更新实体
	 *
	 * @param list 实体记录
	 * @return Boolean
	 */
	@Override
	public boolean updateBatchById(List<T> list) {
		return false;
	}

	/**
	 * 将指定条件的记录批量更新诚实体非null字段
	 *
	 * @param list  实体记录
	 * @param param 更新条件
	 * @return Boolean
	 */
	@Override
	public <C extends MuggleParam<T, C>> Boolean updateBatch(List<T> list, C param) {
		return null;
	}

	/**
	 * 根据实体id批量插入或者更新实体
	 *
	 * @param list 实体记录
	 * @return Boolean
	 */
	@Override
	public boolean saveOrUpdateBatchById(List<T> list) {
		return super.saveOrUpdateBatch(list, DEFAULT_BATCH_SIZE);
	}

	/**
	 * 根据id集合批量删除记录
	 *
	 * @param ids 实体id集合
	 * @return Boolean
	 */
	@Override
	public boolean removeBatchById(List<Serializable> ids) {
		return false;
	}

	/**
	 * 根据条件删除实体记录
	 *
	 * @param param 通用查询参数
	 * @return C
	 */
	@Override
	public <C extends MuggleParam<T, C>> Boolean remove(C param) {
		return null;
	}

	/**
	 * 批量删除(jdbc批量提交)
	 *
	 * @param list 主键ID或实体列表(主键ID类型必须与实体类型字段保持一致)
	 * @return 删除结果
	 * @since 3.5.0
	 */
	@Override
	public boolean removeBatchByIds(Collection<?> list) {
		if (CollUtil.isEmpty(list)) {
			return false;
		}
		return removeBatchByIds(list, 100);
	}

	/**
	 * 查询所有
	 *
	 * @see Wrappers#emptyWrapper()
	 */
	@Override
	public List<T> list() {
		return mapper().selectList(Wrappers.emptyWrapper());
	}

	/**
	 * 批量修改插入
	 *
	 * @param entityList 实体对象集合
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean saveOrUpdateBatch(Collection<T> entityList) {
		return saveOrUpdateBatch(entityList,DEFAULT_BATCH_SIZE);
	}
}
