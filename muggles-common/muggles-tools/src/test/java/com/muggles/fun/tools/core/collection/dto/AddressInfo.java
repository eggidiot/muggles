package com.muggles.fun.tools.core.collection.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 地址信息
 *
 * @author tanghao
 * @date 2024/1/4 17:13
 */
@Data
@Accessors(chain = true)
public class AddressInfo implements Serializable {
    /**
     * 地址id
     */
    private Long id;
    /**
     * 地址信息
     */
    private String address;
    /**
     * 组织机构编码
     */
    private String orgCode;
}
