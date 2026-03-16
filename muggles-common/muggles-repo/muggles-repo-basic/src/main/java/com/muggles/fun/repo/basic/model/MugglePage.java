package com.muggles.fun.repo.basic.model;

import com.muggles.fun.basic.model.IMugglePage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 默认分页对象
 * @param <T>
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class MugglePage<T> implements IMugglePage<T> {
    /**
     * 每页显示条数，默认 10
     */
    private long size = 10;
    /**
     * 当前页
     */
    private long current = 1;
    /**
     * 总页数
     */
    private long pages = 1;
    /**
     * 总记录数
     */
    private long total = 0;
    /**
     * 查询记录
     */
    private List<T> records;
}
