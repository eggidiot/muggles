package com.muggles.fun.tools.core.test;

import com.muggles.fun.tools.core.bean.MethodEnhanceUtil;

import java.util.Arrays;


public class TestEnhance {

    public static void main(String[] args) {
        AccountService a1 = new AccountService();
        MethodEnhanceUtil.enhancer(a1,"deductMoney",(obj,method, args1) -> {
            AccountService a = (AccountService) obj;
            a.setAmount(a.getAmount() * 2);
            System.out.println("清理缓存");
            return  true;
        },null).deductMoney(5);
        System.out.println("目标类对象accountService的余额：");
        System.out.println(a1.getAmount());
    }
}
