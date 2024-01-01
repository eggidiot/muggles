package com.muggles.fun.repo.mp.service;

import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
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
	 * 实体对象类型
	 */
	public Class<T> getTClass() {

		Class<T> entityClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
		return entityClass;
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
	 * 根据 ID 查询
	 *
	 * @param id 主键ID
	 */
	@Override
	public T getById(Serializable id) {
		return mapper().selectById(id);
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
		return saveOrUpdateBatch(entityList,100);
	}
}
