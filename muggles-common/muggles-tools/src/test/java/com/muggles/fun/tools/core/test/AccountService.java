package com.muggles.fun.tools.core.test;

import lombok.Data;
import lombok.experimental.Accessors;

public class AccountService {
    private int amount = 100;

    public int deductMoney2(){
        System.out.println("开始扣款，请稍等。。。");
        amount  = amount-6;
        return amount;
    }

    public int deductMoney1111(int money,int rate){
        System.out.println("开始折扣扣款，请稍等。。。");
        amount  = amount -(money *rate);
        return getAmount();
    }

    public int deductMoney1(int money,int rate){
        return deductMoney1111(money,rate);
    }

    public int getAmount() {
        return amount;
    }
    public void setAmount(int amount) {
        this.amount = amount;
    }
}
