package com.muggles.fun.dynamic;

import java.nio.charset.StandardCharsets;

/**
 * 动态表单常量
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 0.9.0
 */
public interface Const {

    /**
     * 查询最大上限（5w），避免全量查询
     */
    Integer EXPORT_QUERY_UPPER_LIMIT = 5 * 10000;

    /**
     * csv 字符处理
     */
    byte[] BYTES_ENTER = "\n".getBytes(StandardCharsets.UTF_8);

    byte[] BYTES_SEPARATOR = ",".getBytes(StandardCharsets.UTF_8);

    /**
     * 特殊字符
     */
    String UFEFF = "\ufeff";

    String ATTR_ID = "id";
    String COLUMN_ID = "id";
    String ATTR_VERSION = "version";
    String COLUMN_VERSION = "version";
    String ATTR_DELETE_FLAG = "deleteFlag";
    String COLUMN_DELETE_FLAG = "delete_flag";
    String ATTR_CREATE_TIME = "createTime";
    String COLUMN_CREATE_TIME = "create_time";
    String ATTR_UPDATE_TIME = "updateTime";
    String COLUMN_UPDATE_TIME = "update_time";
    String ATTR_CREATOR_ID = "creatorId";
    String COLUMN_CREATOR_ID = "creator_id";
    String ATTR_UPDATER_ID = "updaterId";
    String COLUMN_UPDATER_ID = "updater_id";
    String ATTR_TENANT_ID = "tenantId";
    String COLUMN_TENANT_ID = "tenant_id";

}
