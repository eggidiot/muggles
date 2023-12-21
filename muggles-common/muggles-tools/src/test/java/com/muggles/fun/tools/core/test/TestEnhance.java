package com.muggles.fun.tools.core.test;

import cn.hutool.json.JSONUtil;
import com.muggles.fun.tools.core.bean.BeanExtUtil;
import com.muggles.fun.tools.core.bean.MethodEnhanceUtil;


public class TestEnhance {



    public static void main(String[] args) {
        AccountService a1 = new AccountService();
        MethodEnhanceUtil.enhancerBefore(a1,"aa",null).deductMoney1(5,4);
//        MethodEnhanceUtil.hooked(a1).method(null).bofore(null).bofore().after().after().get();
        System.out.println("目标类对象accountService的余额：");
        System.out.println(a1.getAmount());


        a1 = BeanExtUtil.addProp(a1,"name","张三");
        System.out.println("args = " + JSONUtil.toJsonStr(a1));
    }
}
