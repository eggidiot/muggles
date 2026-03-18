package com.muggles.fun.tools.core.test;

import cn.hutool.json.JSONUtil;
import com.muggles.fun.tools.core.bean.BeanEnhancer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class Person{
    String name;
}


public class TestEnhancer {

    public static void main(String[] args) {
        Person p = new Person("张三");
        Person p1 = BeanEnhancer.addProp(p,"age",11);
        Person p2 = BeanEnhancer.addProp(p1,"email","kay@365yunbao.com");
        System.out.println("args = " + JSONUtil.toJsonStr(p2));
    }
}
