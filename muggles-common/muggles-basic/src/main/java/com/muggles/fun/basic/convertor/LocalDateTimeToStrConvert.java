package com.muggles.fun.basic.convertor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 把localDateTime转换成HH:MM:dd HH:MM:ss格式字符串
 *
 * @author tanghao
 * @date 2024/2/18 14:11
 */
public class LocalDateTimeToStrConvert implements ITypeConvertor<LocalDateTime, String> {
    /**
     * 把原始类型转换成目标类型
     *
     * @param source
     * @return
     */
    @Override
    public String apply(LocalDateTime source) {
        if (source == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return source.format(formatter);
    }
}
