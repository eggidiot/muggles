package com.muggles.fun.tools.core.bean;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import lombok.experimental.UtilityClass;

import java.beans.PropertyDescriptor;
import java.util.Map;

/**
 * Bean对象属性扩展工具（ByteBuddy实现）。
 * 功能对标 {@link BeanExtUtil}（cglib实现），用法完全对称。
 *
 * @see DynamicBuddy
 * @see BeanExtUtil
 */
@UtilityClass
public class BeanEnhancer {

	/**
	 * 为对象动态扩展属性
	 *
	 * @param source		目标对象
	 * @param addProperties	附加属性
	 * @return	T
	 * @param <T>	泛型
	 */
	public <T> T enhance(T source, Map<String, Object> addProperties) {
		PropertyDescriptor[] descriptors = BeanUtil.getPropertyDescriptors(ClassUtil.getClass(source));
		Map<String, Class<?>> propertyMap = MapUtil.newHashMap();
		for (PropertyDescriptor d : descriptors) {
			if (!"class".equalsIgnoreCase(d.getName())) {
				propertyMap.put(d.getName(), d.getPropertyType());
			}
		}
		addProperties.forEach((k, v) -> propertyMap.put(k, v == null ? Object.class : v.getClass()));
		DynamicBuddy<T> dynamicBuddy = new DynamicBuddy(source.getClass(), propertyMap);
		propertyMap.forEach((k, v) -> {
			if (!addProperties.containsKey(k)) {
				dynamicBuddy.setValue(k, ReflectUtil.getFieldValue(source, k));
			}
		});
		addProperties.forEach(dynamicBuddy::setValue);
		return dynamicBuddy.getTarget();
	}

	/**
	 * 为对象动态添加属性
	 * @param dest		目标对象
	 * @param field		属性名称
	 * @param value		属性值
	 * @return	T
	 * @param <T>		泛型
	 */
	public <T> T addProp(T dest, String field, Object value) {
		Map<String, Object> propFields = MapUtil.newHashMap();
		propFields.put(field, value);
		return enhance(dest, propFields);
	}
}
