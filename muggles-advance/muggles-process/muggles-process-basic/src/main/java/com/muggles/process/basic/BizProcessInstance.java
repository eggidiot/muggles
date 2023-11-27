package com.muggles.process.basic;


/**
 * {@link BizProcess}Instance 流程实例抽象接口
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 0.9.0
 */
public interface BizProcessInstance extends BizProcess {

    /**
     * 流程实例Id
     *
     * @return String
     */
    String getProcessInstanceId();

    /**
     * 流程定义KEY
     *
     * @return String
     */
    String getProcessDefKey();

    /**
     * 业务对象id
     *
     * @return Long
     */
    Long getBizId();

}
