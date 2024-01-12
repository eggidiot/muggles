package com.muggles.fun.tools.core.collection.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 组织机构
 *
 * @author tanghao
 * @date 2024/1/4 17:16
 */
@Data
@Accessors(chain = true)
public class Organization implements Serializable {

    private Long id;
    /**
     * 组织机构名称
     */
    private String orgName;
    /**
     * 组织机构编码
     */
    private String orgCode;
}
