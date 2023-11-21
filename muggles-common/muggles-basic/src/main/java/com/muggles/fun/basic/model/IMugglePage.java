package com.muggles.fun.basic.model;

import java.util.List;

/**
 * 麻瓜分页接口
 */
public interface IMugglePage<T> {
    /**
     * 获取页面尺寸
     * @return long
     */
    long getSize();

    /**
     * 获取当前页面
     * @return long
     */
    long getCurrent();

    /**
     * 获取记录
     * @return List
     */
    List<T> getRecords();

    /**
     * 获取总条数
     * @return long
     */
    long getTotal();

    /**
     * 获取总页数
     * @return long
     */
    long getPages();

}
