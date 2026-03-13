package com.muggles.fun.tools.core.test;

import cn.hutool.json.JSONUtil;
import com.muggles.fun.tools.core.bean.BeanEnhancer;
import com.muggles.fun.tools.core.bean.MethodEnhancer;


public class TestEnhance {


    public static void main(String[] args) {
        AccountService a1 = new AccountService();
        // 通过方法引用增强 getAmount 方法（SFunction方式：类::方法名）
        AccountService proxy = MethodEnhancer.enhancerBefore((obj, method, args1) -> {
            System.out.println("[before] 拦截到 " + method.getName() + " 调用");
            return true;
        }, a1, AccountService::getAmount);
        // 调用被增强的方法，触发拦截器
        System.out.println("余额：" + proxy.getAmount());

        System.out.println();

        // 通过字符串方式增强 deductMoney 方法（增强所有同名重载）
        AccountService a2 = new AccountService();
        MethodEnhancer.enhancer(a2, "deductMoney", (obj, method, args1) -> {
            AccountService a = (AccountService) obj;
            a.setAmount(a.getAmount() * 2);
            System.out.println("[before] 清理缓存，余额翻倍");
            return true;
        }, null).deductMoney(5);
        System.out.println("目标类对象accountService的余额：" + a2.getAmount());

        System.out.println();

        // ByteBuddy属性增强
        a2 = BeanEnhancer.addProp(a2, "name", "张三");
        System.out.println("args = " + JSONUtil.toJsonStr(a2));
    }
}
