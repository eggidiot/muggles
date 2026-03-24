package com.muggles.fun.repo.basic.scene;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.muggles.fun.basic.model.IMugglePage;
import com.muggles.fun.repo.basic.model.Muggle;
import com.muggles.fun.repo.basic.model.MugglePage;
import com.muggles.fun.repo.basic.service.IMuggleService;
import lombok.Data;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * BO表示业务对象
 * 业务Service组件父类，填充所有单实体操作组件Service
 */
@Data
public abstract class AbsBizService implements IBizLayerService {

	/**
	 * service默认后缀
	 */
	protected static String suffix = "ServiceImpl";

	/**
	 * 保存实体
	 *
	 * @param entity 实体对象
	 * @return Boolean
	 */
	@Override
	public <T>Boolean saveDo(T entity) {
		IMuggleService<T> service = getService((Class<T>)entity.getClass());
		if (service != null) {
			return service.save(entity);
		}
		return false;
	}

	/**
	 * 更新实体
	 *
	 * @param entity 实体对象
	 * @return Boolean
	 */
	@Override
	public <T>Boolean updateDo(T entity) {
		IMuggleService<T> service = getService((Class<T>) entity.getClass());
		if (service != null) {
			return service.updateById(entity);
		}
		return false;
	}

	/**
	 * 根据主键获取实体对象
	 *
	 * @param id     主键
	 * @param tClass 实体类型
	 * @param <T>    对象类型
	 * @return T
	 */
	@Override
	public <T> T getDo(Long id, Class<T> tClass) {
		IMuggleService<T> service = getService(tClass);
		if (service != null) {
			return (T) service.getById(id);
		}
		return null;
	}

	/**
	 * 获取集合列表
	 *
	 * @param tClass 实体类型
	 * @param <T>    对象类型
	 * @return List<T>
	 */
	@Override
	public <T> List<T> listDos(Class<T> tClass) {
		IMuggleService<T> service = getService(tClass);
		if (service != null) {
			return service.list();
		}
		return Collections.emptyList();
	}

	/**
	 * 分页查询方法
	 *
	 * @param param  查询条件
	 * @param tClass 查询对象类型
	 * @param <T>    实体类型
	 * @return FlinePage<T>
	 */
	@Override
	public <T> IMugglePage<T> pageDos(Muggle<T> param, Class<T> tClass) {
		IMuggleService<T> service = getService(tClass);
		if (service != null) {
			return service.page(param);
		}
		return new MugglePage<>();
	}

	/**
	 * 根据实体类型获取crud组件
	 * 静态方法，全局都可以调用，待优化
	 *
	 * @param serviceMap crud组件集合
	 * @param tClass     实体类型
	 * @param <T>        实体类型
	 * @return IFlineService<T>
	 */
	public static <T> IMuggleService<T> service(Map<String, IMuggleService<?>> serviceMap, Class<T> tClass) {
		for (String key : serviceMap.keySet()) {
			if (StrUtil.equalsIgnoreCase(tClass.getSimpleName() + suffix, key)) {
				return (IMuggleService<T>) serviceMap.get(key);
			}
		}
		return null;
	}

	/**
	 * 根据对象类型和主键删除对象
	 *
	 * @param id     主键
	 * @param tClass 对象类型
	 * @return Boolean
	 */
	@Override
	public <T> Boolean removeDo(Long id, Class<T> tClass) {
		IMuggleService<T> service = getService(tClass);
		if (service != null) {
			return service.removeById(id);
		}
		return false;
	}

	/**
	 * 根据类型获取相应的查询条件生成方法
	 *
	 * @param tClass 数据类型
	 * @param <T>    实体类型
	 * @return FlineParam<T>
	 */
	@Override
	public <T> Muggle<T> where(Class<T> tClass) {
		return getService(tClass).where();
	}

	/**
	 * 根据类型和查询条件获取集合查询结果
	 *
	 * @param param  查询参数
	 * @param tClass 查询类型
	 * @return List<T>
	 */
	@Override
	public <T> List<T> listDos(Muggle<T> param, Class<T> tClass) {
		return pageDos(param.setCurrent(1L).setSize(Integer.MAX_VALUE), tClass).getRecords();
	}

	/**
	 * 根据查询条件获取单条查询结果
	 *
	 * @param param  查询参数
	 * @param tClass 对象类型
	 * @return T
	 */
	@Override
	public <T> T oneDo(Muggle<T> param, Class<T> tClass) {
		return CollUtil.get(listDos(param, tClass), 0);
	}
}
