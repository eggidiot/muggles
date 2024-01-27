package com.muggles.fun.tools.core.collection.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 人
 *
 * @author tanghao
 * @date 2024/1/4 17:12
 */
@Data
@Accessors(chain = true)
public class People implements Serializable {
    private Long id;
    /**
     * 姓名
     */
    private String name;
    /**
     * 年龄
     */
    private Integer age;
    /**
     * 地址id
     */
    private Long addressId;
}
