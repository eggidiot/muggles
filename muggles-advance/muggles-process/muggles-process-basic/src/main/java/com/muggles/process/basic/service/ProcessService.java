package com.muggles.process.basic.service;


import com.muggles.process.basic.BizProcessInstance;

/**
 * 流程服务
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 0.9.0
 */
public interface ProcessService<T extends BizProcessInstance> {

    // ---------------------------------------- 流程定义 ----------------------------------------

    /**
     * 加载流程数据
     */
    String loadContentByKey(String processDefKey);

    /**
     * 部署一个新流程
     *
     * @param processDefKey 流程定义key
     * @return boolean
     */
    boolean deploy(String processDefKey, String content);


    // ---------------------------------------- 流程实例查询 ----------------------------------------

    /**
     * 查询待办事项
     */
    PageRec<T> pageTodo(Long userId, String businessKey, QueryParam<T> param);

    /**
     * 查询已办事项
     */
    PageRec<T> pageDone(Long userId, String businessKey, QueryParam<T> param);

    /**
     * 查询办结事项
     */
    PageRec<T> pageComplete(Long userId, String businessKey, QueryParam<T> param);

    /**
     * 查询全部事项
     */
    PageRec<T> pageAll(Long userId, String businessKey, QueryParam<T> param);

    /**
     * 统计流程办理情况 - 待办、已办、完结、全部
     *
     * @param userId
     * @param bizKey 流程业务编码
     * @return
     */
    ProcessInstanceStat statistics(Long userId, String bizKey);



}
