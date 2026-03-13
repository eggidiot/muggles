package com.muggles.fun.tools.core.bean;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

/**
 * 动态属性增强工具（ByteBuddy实现），可为对象添加任意类型的额外属性。
 * 功能对标 {@link DynamicBean}（cglib实现），用于性能对比。
 */
public class DynamicPropertyEnhancer {

    /**
     * 为对象动态扩展属性（便捷方法），自动推断属性类型。
     *
     * @param source        原始对象
     * @param addProperties 附加属性名 -> 值映射
     * @param <T>           原始类型
     * @return 增强后的对象（原始类的子类实例）
     */
    public static <T> T enhance(T source, Map<String, Object> addProperties) throws Exception {
        if (addProperties == null || addProperties.isEmpty()) {
            return source;
        }
        // 收集原始对象已有的属性类型
        PropertyDescriptor[] descriptors = BeanUtil.getPropertyDescriptors(ClassUtil.getClass(source));
        Map<String, Class<?>> propertyMap = new HashMap<>();
        for (PropertyDescriptor d : descriptors) {
            if (!"class".equalsIgnoreCase(d.getName())) {
                propertyMap.put(d.getName(), d.getPropertyType());
            }
        }
        // 合并新增属性类型
        addProperties.forEach((k, v) -> propertyMap.put(k, v == null ? Object.class : v.getClass()));

        return enhance(source, propertyMap, addProperties);
    }

    /**
     * 增强对象：为原始对象添加额外属性，生成真实字段和对应的 getter/setter。
     *
     * @param source        原始对象
     * @param propertyTypes 所有属性名 -> 属性类型映射（含原始+新增）
     * @param addProperties 新增属性名 -> 初始值映射
     * @param <T>           原始类型
     * @return 增强后的对象（原始类的子类实例）
     */
    public static <T> T enhance(T source,
                                Map<String, Class<?>> propertyTypes,
                                Map<String, Object> addProperties) throws Exception {
        if (source == null) {
            return null;
        }
        Class<?> originalClass = source.getClass();

        // 1. 动态创建子类，为每个新增属性定义真实字段 + getter/setter
        net.bytebuddy.dynamic.DynamicType.Builder<?> builder = new ByteBuddy().subclass(originalClass);

        for (Map.Entry<String, Class<?>> entry : propertyTypes.entrySet()) {
            String propName = entry.getKey();
            Class<?> propType = entry.getValue();
            // 跳过父类已有的属性，只为新增属性生成字段
            if (ReflectUtil.hasField(originalClass, propName)) {
                continue;
            }
            String capitalized = propName.substring(0, 1).toUpperCase() + propName.substring(1);
            String getterName = (propType == boolean.class ? "is" : "get") + capitalized;
            String setterName = "set" + capitalized;

            // 定义真实字段 + getter/setter，由 FieldAccessor 直接读写字段
            builder = builder
                    .defineField(propName, propType, Visibility.PRIVATE)
                    .defineMethod(getterName, propType, Visibility.PUBLIC)
                    .intercept(FieldAccessor.ofField(propName))
                    .defineMethod(setterName, void.class, Visibility.PUBLIC)
                    .withParameters(propType)
                    .intercept(FieldAccessor.ofField(propName));
        }

        // 2. 创建增强类并加载到同一个类加载器
        Class<?> enhancedClass = builder.make()
                .load(originalClass.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded();

        // 3. 创建实例
        T enhanced = (T) enhancedClass.getDeclaredConstructor().newInstance();

        // 4. 复制原始对象的属性值
        PropertyDescriptor[] descriptors = BeanUtil.getPropertyDescriptors(originalClass);
        for (PropertyDescriptor d : descriptors) {
            if (!"class".equalsIgnoreCase(d.getName())) {
                Object value = ReflectUtil.getFieldValue(source, d.getName());
                ReflectUtil.setFieldValue(enhanced, d.getName(), value);
            }
        }

        // 5. 设置新增属性的值
        if (addProperties != null) {
            addProperties.forEach((k, v) -> ReflectUtil.setFieldValue(enhanced, k, v));
        }

        return enhanced;
    }

    public static void main(String[] args) throws Exception {
        Person original = new Person("Alice", 30);

        Map<String, Object> values = new HashMap<>();
        values.put("address", "123 Main St");
        values.put("employed", true);
        values.put("salary", 75000.0);

        Person enhanced = DynamicPropertyEnhancer.enhance(original, values);
        System.out.println("result = " + JSONUtil.toJsonStr(enhanced));
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class Person {
    private String name;
    private int age;
}
