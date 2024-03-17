package com.muggles.fun.dynamic;

import lombok.Getter;

import java.math.BigDecimal;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

import static java.sql.Types.LONGVARCHAR;

/**
 * 属性类型枚举
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 0.9.0
 */
@Getter
public enum AttrType implements Dict<String, AttrType> {
    /**
     * 基础类型
     */
    INT_8("8位整型", true, 1, null, Types.TINYINT, "TINYINT", Byte.class),
    INT_16("16位整型", true, 4, null, Types.SMALLINT, "SMALLINT", Short.class),
    INT_32("32位整型", true, 11, null, Types.INTEGER, "INT", Integer.class),
    INT_64("64位整型", true, 20, null, Types.BIGINT, "BIGINT", Long.class),

    BOOLEAN("是否型", true, 1, null, Types.BIT, "TINYINT", Byte.class),

    STR("字符串", true, 255, null, Types.VARCHAR, "VARCHAR", String.class),
    STR_255("255位字符串", true, 255, null, Types.VARCHAR, "VARCHAR", String.class),
    STR_511("511位字符串", true, 511, null, Types.VARCHAR, "VARCHAR", String.class),
    STR_127("127位字符串", true, 127, null, Types.VARCHAR, "VARCHAR", String.class),
    TEXT("文本", false, null, null, 192, "TEXT", String.class,
            LONGVARCHAR),

    LOCAL_DATE_TIME("LocalDateTime", false, null, null,
            191, "DATETIME", LocalDateTime.class, Types.TIMESTAMP),

    BIG_DECIMAL("数字(10,4)", true, 10, 4,
            Types.DECIMAL, "DECIMAL", BigDecimal.class),

    /**
     * 引用类类型
     */
    DICT("字典", true, 255, null, Types.VARCHAR, "VARCHAR", String.class),
    PARENT_ID("父节点ID", true, 20, null, Types.BIGINT, "BIGINT", Long.class),
    ;

    /**
     * 类型说明
     */
    public final String text;

    /**
     * 是否需要长度属性
     */
    public final boolean hasLength;

    /**
     * 长度
     */
    public final Integer length;

    /**
     * 属性标度
     */
    public final Integer scale;

    /**
     * {@link java.sql}
     */
    public final Integer sqlTypeCode;

    /**
     * 匹配的sql类型
     */
    public final Integer[] matchSqlCodes;

    /**
     * 属性对应数据库名称
     */
    public final String sqlTypeName;

    /**
     * Java Class
     */
    public final Class<?> clazz;

    /**
     * @param text
     * @param hasLength
     * @param length
     * @param scale
     * @param sqlTypeCode
     * @param sqlTypeName
     * @param clazz
     * @param matchSqlCodes
     */
    AttrType(String text, boolean hasLength, Integer length, Integer scale, Integer sqlTypeCode, String sqlTypeName, Class<?> clazz, Integer... matchSqlCodes) {
        this.text = text;
        this.hasLength = hasLength;
        this.length = length;
        this.scale = scale;
        this.sqlTypeCode = sqlTypeCode;
        this.sqlTypeName = sqlTypeName;
        this.clazz = clazz;
        if(matchSqlCodes == null) {
            this.matchSqlCodes = new Integer[]{sqlTypeCode};
        } else {
            this.matchSqlCodes = new Integer[matchSqlCodes.length + 1];
            for (int i = 0; i < matchSqlCodes.length; i++) {
                this.matchSqlCodes[i] = matchSqlCodes[i];
            }
            this.matchSqlCodes[matchSqlCodes.length] = sqlTypeCode;
        }
        register();
    }

    public static Class<?> getJavaType(String attrTypeName) {
        return Arrays.stream(AttrType.values()).filter(x -> x.name().equals(attrTypeName))
                .findFirst().map(AttrType::getClazz).orElse(null);

    }

    public static AttrType getAttrType(String attrTypeName) {
        return Arrays.stream(AttrType.values()).filter(x -> x.name().equals(attrTypeName))
                .findFirst().orElse(null);

    }

    public static AttrType matchAttrTypeBySqlCode(int sqlTypeCode) {
        return Arrays.stream(AttrType.values()).filter(x -> Arrays.stream(x.getMatchSqlCodes()).anyMatch(c -> c == sqlTypeCode))
                .findFirst().orElse(null);
    }

    public static String toNameString() {
       return Arrays.stream(AttrType.values()).map(AttrType::name).collect(Collectors.joining(","));
    }

    /**
     * 字典名称
     *
     * @return String
     */
    @Override
    public String getName() {
        return "attrType";
    }

    /**
     * 字典项关键字
     *
     * @return K
     */
    @Override
    public String getItemKey() {
        return name();
    }

    /**
     * 字典项现实文本
     *
     * @return String
     */
    @Override
    public String getItemText() {
        return getText();
    }

    /**
     * 字典项对象
     *
     * @return String
     */
    @Override
    public AttrType getItem() {
        return this;
    }
}
