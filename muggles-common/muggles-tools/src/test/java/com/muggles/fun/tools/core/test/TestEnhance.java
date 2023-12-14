package com.muggles.fun.tools.core.test;

import com.muggles.fun.tools.core.bean.MethodEnhanceUtil;

import java.lang.reflect.Method;
import java.util.Arrays;


public class TestEnhance {

    public static void main(String[] args) {
        AccountService a1 = new AccountService();
        Method m = MethodEnhanceUtil.findMethod(a1,"deductMoney",int.class,int.class);
        MethodEnhanceUtil.enhancer(a1,m,(obj,method, args1) -> {
            AccountService a = (AccountService) obj;
            a.setAmount(a.getAmount() * 2);
            System.out.println("清理缓存");
            return  true;
        },null).deductMoney(5,4);
        System.out.println("目标类对象accountService的余额：");
        System.out.println(a1.getAmount());
    }
}
