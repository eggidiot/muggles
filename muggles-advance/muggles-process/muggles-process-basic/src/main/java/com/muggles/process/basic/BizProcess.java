package com.muggles.process.basic;

import java.io.Serializable;

/**
 * 业务流程抽象接口，代表某一种业务
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 0.9.0
 */
public interface BizProcess extends Serializable {

    /**
     * 流程的业务关键字
     *
     * @return String
     */
    default String getBizKey() {
        return StringUtils.lowerFirst(getBizClass().getSimpleName());
    }

    /**
     * 流程业务实体类 <br/>
     * <ul>
     *     <li>1. 当实体继承该接口实现流程时，流程业务的实体类就是该类自身（默认）</li>
     *     <li>2. 当实体继承该接口实现了一个流程中间表的时候，流程业务的类就不是自身，需要自行实现</li>
     * </ul>
     *
     * @return Class<?>
     */
    default Class<?> getBizClass() {
        return this.getClass();
    }
}
