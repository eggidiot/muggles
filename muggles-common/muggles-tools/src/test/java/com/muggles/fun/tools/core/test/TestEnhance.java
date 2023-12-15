package com.muggles.fun.tools.core.test;

import cn.hutool.json.JSONUtil;
import com.muggles.fun.tools.core.bean.BeanExtUtil;
import com.muggles.fun.tools.core.bean.MethodEnhanceUtil;


public class TestEnhance {



    public static void main(String[] args) {
        AccountService a1 = new AccountService();
        MethodEnhanceUtil.enhancer(a1,"deductMoney1",(obj,method, args1) -> {
            AccountService a = (AccountService) obj;
            a.setAmount(a.getAmount() * 2);
            System.out.println("清理缓存");
            return  true;
        },null).deductMoney(5,4);
        System.out.println("目标类对象accountService的余额：");


        System.out.println(a1.getAmount());
        a1 = BeanExtUtil.addProp(a1,"name","张三");
        System.out.println("args = " + JSONUtil.toJsonStr(a1));
    }
}
