package com.muggles.fun.tools.core.collection.vo;

import java.io.Serializable;

import com.muggles.fun.tools.core.collection.JoinProperty;
import com.muggles.fun.tools.core.collection.dto.Organization;
import com.muggles.fun.tools.core.collection.dto.People;

import lombok.Data;

/**
 * 人的展示对象
 *
 * @author tanghao
 * @date 2024/1/5 10:04
 */
@Data
@JoinProperty(classType = People.class)
public class PersonVo implements Serializable {

    private Long id;
    @JoinProperty(classType = People.class)
    private String name;
    @JoinProperty(alias = "aa", fieldName = "address")
    private String addressInfo;
    @JoinProperty(classType = Organization.class)
    private String orgName;

    @JoinProperty(classType = Organization.class,fieldName = "orgName")
    public String getName() {
        return name;
    }
}
