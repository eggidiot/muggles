package com.muggles.fun.tools.core.test;

import com.muggles.fun.tools.core.bean.Hooker;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestEnhance {


    public static void main(String[] args) {
        AccountService a1 = new AccountService();
//        MethodEnhanceUtil.enhancerBefore(a1,"deductMoney1",(obj,method,params)->{
//            log.info("前置切面对象:{},方法:{},参数:{}", obj, method.getName(), params);
//            return true;
//        }).deductMoney1(5,4);
        AccountService res = Hooker.hooked(a1).method("deductMoney1").bofore((obj, method, params) -> {
                    log.info("前置切面对象22:{},方法:{},参数:{}", obj, method.getName(), params);
                    params[1] = 66;
                    return true;
                }).done().method("deductMoney1").bofore((obj, method, params) -> {
                    log.info("前置切面对象33:{},方法:{},参数:{}", obj, method.getName(), params);

                    return true;
                }).done()
                .target();
        log.info("结果:{}", res.deductMoney1(2,2));
    }
}
