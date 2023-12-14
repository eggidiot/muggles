package com.muggles.fun.tools.core.test;

import com.muggles.fun.tools.core.bean.MethodEnhanceUtil;


public class TestEnhance {

    public static void main(String[] args) {
        AccountService a1 = new AccountService();
        MethodEnhanceUtil.enhancer(a1).deductMoney(5,4);
        System.out.println("目标类对象accountService的余额：");
        System.out.println(a1.getAmount());
    }
}
