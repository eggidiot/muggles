package com.muggles.fun.core.context;

import cn.hutool.cron.CronUtil;
import com.alibaba.ttl.threadpool.TtlExecutors;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * 任务处理器
 */
@UtilityClass
public class TaskProcessor {

    /**
     * 默认线程池
     */
    static ExecutorService es = TtlExecutors.getTtlScheduledExecutorService(new ScheduledThreadPoolExecutor(10));

    /**
     * 任务ID号
     */
    static List<String> taskIds = new ArrayList<>();

    /**
     * 提交任务
     *
     * @param cornExpress	定时任务corn表达式
     * @param runnable		定时任务
     * @return	String
     */
    public String submit(String cornExpress, Runnable runnable) {
        String id = CronUtil.schedule(cornExpress, runnable);
        taskIds.add(id);
        return id;
    }

    /**
     * 停止所有任务
     */
    public void stop() {
        CronUtil.stop();
    }

    /**
     * 取消任务
     *
     * @param id	定时任务id
     * @return	boolean
     */
    public boolean cancel(String id) {
        boolean res = CronUtil.remove(id);
        CronUtil.restart();
        return res;
    }

    /**
     * 启动任务
     */
    public void start() {
        if (CronUtil.getScheduler().isStarted()) {
            return;
        }
        CronUtil.getScheduler().setThreadExecutor(es);
        // 支持秒级别定时任务
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }
}
