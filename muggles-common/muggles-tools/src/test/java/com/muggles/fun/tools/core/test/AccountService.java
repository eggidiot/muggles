package com.muggles.fun.tools.core.test;


public class AccountService {
    private int amount = 100;


    public AccountService(Object o) {
    }

    public AccountService deductMoney2(int money){
        System.out.println("开始扣款，请稍等。。。");
        amount  = amount-money;
        return this;
    }

    public int deductMoney(int money,int rate){
        System.out.println("开始折扣扣款，请稍等。。。");
        amount  = amount -(money *rate);
        return getAmount();
    }

    public int deductMoney1(int money,int rate){
        return deductMoney(money,rate);
    }
}
