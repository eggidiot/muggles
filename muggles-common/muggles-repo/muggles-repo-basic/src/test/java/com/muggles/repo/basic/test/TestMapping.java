package com.muggles.repo.basic.test;

import com.muggles.fun.repo.basic.IFieldMapping;
import com.muggles.fun.repo.basic.model.DefaultMapping;

public class TestMapping {

    public static void main(String[] args) {
        // 使用方法引用获取属性名
        IFieldMapping mapping = new DefaultMapping();
        System.out.println(mapping.fieldMappingColum(Person::getName)); // 输出: name
    }
}

class Person {
    private String name;
    private int age;
    // getter/setter 略
    public String getName() { return name; }
    public int getAge() { return age; }
}