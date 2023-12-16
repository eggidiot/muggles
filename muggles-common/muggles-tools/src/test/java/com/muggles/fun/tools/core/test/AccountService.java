package com.muggles.fun.tools.core.test;

import lombok.Data;

@Data
public class AccountService {
    private int amount = 100;

    public void deductMoney(int money){
        System.out.println("开始扣款，请稍等。。。");
        amount  = amount-money;
    }

    public int deductMoney(int money,int rate){
        System.out.println("开始折扣扣款，请稍等。。。");
        amount  = amount -(money *rate);
        return getAmount();
    }

    public int deductMoney1(int money,int rate){
        System.out.println("开始折扣扣款，请稍等。。。");
        amount  = amount -(money *rate);
        return getAmount();
    }
}
