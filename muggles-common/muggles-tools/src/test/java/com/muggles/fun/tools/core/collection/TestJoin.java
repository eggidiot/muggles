package com.muggles.fun.tools.core.collection;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.muggles.fun.tools.core.collection.dto.AddressInfo;
import com.muggles.fun.tools.core.collection.dto.Organization;
import com.muggles.fun.tools.core.collection.dto.People;
import com.muggles.fun.tools.core.collection.vo.PersonVo;

/**
 * @author tanghao
 * @date 2024/1/4 17:07
 */
public class TestJoin {
    private List<AddressInfo> addressInfoList;
    private List<Organization> organizationList;
    private List<People> peopleList;

    @Before
    public void before() {
        this.addressInfoList = buildAddressInfoList();
        this.organizationList = buildOrganizationList();
        this.peopleList = buildPeopleList();
    }

    @Test
    public void testJoin() {
        // List<Object> maps =
        // MultiJoiner.create()
        // .join(peopleList, addressInfoList, (a, b) -> a.getAddressId().equals(b.getId()))
        // .join(addressInfoList, organizationList, (a, b) -> a.getOrgCode().equals(b.getOrgCode()))
        // .execute().where(People.class,a -> a.getAge()> 20)
        // .getByClassType(People.class);
        // System.out.println(JSON.toJSONString(maps, SerializerFeature.PrettyFormat));


        List<PersonVo> maps = MultiJoiner.create()
            .leftJoin(
                MultiJoiner.<People, AddressInfo>buildOperation()
                    .left(peopleList)
                    .right(addressInfoList)
                    .on((a, b) -> a.getAddressId().equals(b.getId()))
                    .build())
            .join(
                MultiJoiner.<AddressInfo, Organization>buildOperation()
                    .left(AddressInfo.class)
                    .right(organizationList)
                    .on((a, b) -> a.getOrgCode().equals(b.getOrgCode())).build())
            .execute()
            .convert(PersonVo.class);
        System.out.println(maps);
    }

    /**
     * 构建地址集合
     * 
     * @return
     */
    private List<AddressInfo> buildAddressInfoList() {
        List<AddressInfo> result = new ArrayList<>();
        result.add(new AddressInfo().setId(1L).setAddress("文三路193号").setOrgCode("aaaa"));
        result.add(new AddressInfo().setId(2L).setAddress("学院路25号").setOrgCode("bbbb"));
        result.add(new AddressInfo().setId(3L).setAddress("文三路192号").setOrgCode("aaaa"));
        result.add(new AddressInfo().setId(4L).setAddress("教工路12号").setOrgCode("cccc"));
        return result;
    }

    /**
     * 构建组织机构集合
     * 
     * @return
     */
    private List<Organization> buildOrganizationList() {
        List<Organization> result = new ArrayList<>();
        result.add(new Organization().setId(1L).setOrgName("文三街道").setOrgCode("aaaa"));
        result.add(new Organization().setId(2L).setOrgName("翠苑街道").setOrgCode("bbbb"));
        result.add(new Organization().setId(3L).setOrgName("西三旗街道").setOrgCode("cccc"));
        result.add(new Organization().setId(4L).setOrgName("庆春街道").setOrgCode("dddd"));
        return result;
    }

    /**
     * 构建人的集合
     * 
     * @return
     */
    private List<People> buildPeopleList() {
        List<People> result = new ArrayList<>();
        result.add(new People().setId(1L).setName("张三").setAge(18).setAddressId(1L));
        result.add(new People().setId(2L).setName("李四").setAge(19).setAddressId(3L));
        result.add(new People().setId(3L).setName("王五").setAge(29).setAddressId(2L));
        return result;
    }
}
