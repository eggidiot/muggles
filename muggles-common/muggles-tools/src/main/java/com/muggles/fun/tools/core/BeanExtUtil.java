package com.muggles.fun.tools.core;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;

import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Bean对象属性扩展工具
 */
public class BeanExtUtil {

	/**
	 * 私有化构造器避免外部调用
	 */
	private BeanExtUtil(){}

    /**
     * 为对象动态扩展属性
     *
     * @param dest			目标对象
     * @param addProperties	附加属性
     * @return	Object
     */
    public static Object getObject(Object dest, Map<String, Object> addProperties) {
        PropertyDescriptor[] descriptors = BeanUtil.getPropertyDescriptors(ClassUtil.getClass(dest));
        Map<String, Class<?>> propertyMap = MapUtil.newHashMap();
        for (PropertyDescriptor d : descriptors) {
            if (!"class".equalsIgnoreCase(d.getName())) {
                propertyMap.put(d.getName(), d.getPropertyType());
            }
        }
        addProperties.forEach((k, v) -> propertyMap.put(k, v.getClass()));
        DynamicBean dynamicBean = new DynamicBean(dest.getClass(), propertyMap);
        propertyMap.forEach((k, v) -> {
            if (!addProperties.containsKey(k)) {
                dynamicBean.setValue(k, ReflectUtil.getFieldValue(dest, k));
            }
        });
        addProperties.forEach(dynamicBean::setValue);
        return dynamicBean.getTarget();
    }

	/**
	 * 动态扩展属性后转换类型
	 * @param dest			目标对象
	 * @param addProperties	附加属性
	 * @return	T
	 * @param <T>	泛型
	 */
	public static <T>T addExtra(T dest, Map<String, Object> addProperties) {
		return (T) getObject(dest,addProperties);
	}

	/**
	 * 根据字段映射装饰成树形结构
	 * @param list			集合对象
	 * @param prop			子级对象通过关联的属性名称，get方法
	 * @param parentProp	子级对象关联父级对象的属性名称.get方法
	 * @return	List<T>
	 * @param <T>	泛型
	 */
	public static <T>List<T> decorateTree(List<T> list, Function<T,?> prop,Function<T,?> parentProp,String childField) {
		if (CollUtil.isEmpty(list)) {
			return CollUtil.newArrayList();
		}
		Map<Object, List<T>> group = MapUtil.newHashMap();
		//1.更新树形列表
		List<T> decorastors = list.stream().map(t->{
			//1.1.检查是否需要添加子集集合字段
			if (ReflectUtil.hasField(t.getClass(),childField)){
				//1.1.1要求对象有一个子集集合的属性，方便起见，指定是List类型的集合，不接受Set，Collect等集合
				Class<?> clz = ReflectUtil.getField(t.getClass(),childField).getType();
				if (!List.class.isAssignableFrom(clz)){
					throw new IllegalArgumentException("指定字段名称绑定了非list类型成员");
				}
				//1.1.2子集集合若未初始化，进行初始化
				if (ReflectUtil.getFieldValue(t,childField) == null) {
					ReflectUtil.setFieldValue(t, childField, CollUtil.newArrayList());
				}
				//1.1.3将子集集合字段和关联映射字段做成K-V形式，方便后续归档
				Object key = prop.apply(t);
				if (key != null) {
					group.put(key, (List<T>) ReflectUtil.getFieldValue(t,childField));
				}
				return t;
			} else {//1.2.需要添加子集集合字段，采用插桩技术，动态添加字段
				List<T> children = CollUtil.newArrayList();
				Map<String, Object> map = MapUtil.newHashMap();
				map.put(childField, children);
				Object key = prop.apply(t);
				//1.2.1将子集集合字段和关联映射字段做成K-V形式，方便后续归档
				if (key != null) {
					group.put(key, children);
				}
				//1.2.2返回插桩后的字段
				return addExtra(t, map);
			}
		}).collect(Collectors.toList());
		//2.归档子节点集合
		Map<?,List<T>> childrenGroup =  decorastors.stream().filter(t->parentProp.apply(t) != null).collect(Collectors.groupingBy(parentProp));
		decorastors.forEach(t->{
			Object key = prop.apply(t);
			if (key != null) {
				List<T> children = childrenGroup.get(key);
				if (CollUtil.isNotEmpty(children)) {
					List<T> fields = group.get(key);
					CollUtil.addAll(fields, children);
				}
			}
		});
		return decorastors;
	}

	/**
	 * 默认使用children作为子节点集合的字段名称
	 * @param list			集合对象
	 * @param prop			子级对象通过关联的属性名称，get方法
	 * @param parentProp	子级对象关联父级对象的属性名称.get方法
	 * @return	List<T>
	 * @param <T>	泛型
	 */
	public static <T>List<T> decorateTree(List<T> list, Function<T,?> prop,Function<T,?> parentProp) {
		return decorateTree(list,prop,parentProp,"children");
	}
}
