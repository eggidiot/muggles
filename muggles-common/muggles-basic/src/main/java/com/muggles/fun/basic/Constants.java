package com.muggles.fun.basic;

/**
 * 常见常量定义
 *
 * @author kay
 */
public interface Constants {
    //=======================HTTP配置信息=========================
    /**
     * header租户ID标识
     */
    String TENANT_ID = "TENANT_ID";
    /**
     * header中的设备编码标识
     */
    String DEVICE_CODE = "DEVICE_CODE";
    /**
     * 编码
     */
    String UTF8 = "UTF-8";
    /**
     * 成功标记
     */
    Integer SUCCESS = 0;
    /**
     * 初始化标记
     */
    Integer INIT = 0;
    /**
     * 删除标记
     */
    Integer DELETE = 1;
    /**
     * 失败标记
     */
    Integer FAIL = 1;
    /**
     * 默认操作
     */
    Integer DEFAULT_OPT = 1;

	/**
	 * 在字符串中的空json对象
	 */
	String EMPTY_JSON_IN_STRING = "\\{\\}";

	/**
	 * 字符串占位符
	 */
	String STR_PLACEHOLDER = "%s";


    //=======================格式配置信息=========================
    /**
     * 默认日期格式
     */
    String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 只有日期的时间格式
	 */
    String ONLY_DATE_FORMAT = "yyyy-MM-dd";

	/**
	 * 默认框架组件ORDER值
	 */
	int DEFAULT_ORDER = 100;
}
