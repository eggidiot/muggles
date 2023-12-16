package com.muggles.fun.tools.core.bean;

import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.beans.BeanMap;

import java.util.Map;

/**
 * 动态代理对象，推荐只代理简单的pojo
 */
public class DynamicBean {
	/**
	 * 目标对象
	 */
    private final Object target;
	/**
	 * 对象属性集合
	 */
    private final BeanMap beanMap;

	/**
	 * 使用类反射的构造器
	 * @param superclass	父类
	 * @param propertyMap	属性集合
	 */
    public DynamicBean(Class<?> superclass, Map<String,Class<?>> propertyMap) {
        this.target = generateBean(superclass, propertyMap);
        this.beanMap = BeanMap.create(this.target);
    }

	/**
	 * 设置新的属性
	 * @param property	属性名称
	 * @param value		属性值
	 */
    public void setValue(String property, Object value) {
        beanMap.put(property, value);
    }

	/**
	 * 获取目标属性值
	 * @param property	属性名称
	 * @return	Object
	 */
    public Object getValue(String property) {
        return beanMap.get(property);
    }

	/**
	 * 获取目标对象
	 * @return	目标对象
	 */
    public Object getTarget() {
        return this.target;
    }

    /**
     * 根据属性生成对象
	 * @return	目标对象
     */
    private Object generateBean(Class<?> superclass, Map<String,Class<?>> propertyMap) {
        BeanGenerator generator = new BeanGenerator();
        if (null != superclass) {
            generator.setSuperclass(superclass);
        }
        BeanGenerator.addProperties(generator, propertyMap);
        return generator.create();
    }
}
