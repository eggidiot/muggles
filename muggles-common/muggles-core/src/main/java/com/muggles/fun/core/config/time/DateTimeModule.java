package com.muggles.fun.core.config.time;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.muggles.fun.basic.Constants;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.PackageVersion;

import java.io.IOException;
import java.time.LocalDateTime;


/**
 * JACKSON 接口层时间参数序列化插件
 *
 * @author lenovo
 */
@Accessors(chain = true)
public class DateTimeModule extends SimpleModule {
    /**
     * 构造方法，设置默认的时间序列化、反序列化组件
     */
    public DateTimeModule(String format) {
        this(format, false);
    }

    /**
     * 构造方法，设置默认的时间序列化、反序列化组件
     */
    public DateTimeModule() {
        this(Constants.DATE_FORMAT, false);
    }

    /**
     * 构造方法，设置默认的时间序列化、反序列化组件
     */
    public DateTimeModule(Boolean timestamp) {
        this(Constants.DATE_FORMAT, timestamp);
    }

    /**
     * 构造方法，设置默认的时间序列化、反序列化组件
     */
    public DateTimeModule(String format, Boolean timestamp) {
        super(PackageVersion.VERSION);
        this.addSerializer(LocalDateTime.class, new TsLocalDateTimeSerializer(format, timestamp));
        this.addDeserializer(LocalDateTime.class, new TsLocalDateDeserializer(format, timestamp));
    }

    /**
     * 默认采用字符串形式格式化时间
     */
    @RequiredArgsConstructor
    public static class TsLocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

        /**
         * 日期格式
         */
        final String format;
        /**
         * 是否返回时间戳
         */
        final Boolean timestamp;

        /**
         * 序列化时间方法
         *
         * @param value    时间对象值
         * @param g        JSON对象生成器
         * @param provider 可提供默认包含的可序列化组件的提供对象
         */
        @Override
        public void serialize(LocalDateTime value, JsonGenerator g, SerializerProvider provider) throws IOException {
            //1.为Null值则不序列化操作
            if (ObjectUtil.isNull(value)) {
                return;
            }
            //2.判断是否时间戳方式序列化
            if (Boolean.TRUE.equals(timestamp)) {
                g.writeObject(LocalDateTimeUtil.toEpochMilli(value));
            } else {
                g.writeString(LocalDateTimeUtil.format(value, format));
            }
        }
    }

    /**
     * 默认将字符串时间输出成LocalDateTime
     */
    @RequiredArgsConstructor
    public static class TsLocalDateDeserializer extends JsonDeserializer<LocalDateTime> {
        /**
         * 日期格式
         */
        final String format;
        /**
         * 是否返回时间戳
         */
        final Boolean timestamp;

        /**
         * 将字符串反序列化成时间对象
         *
         * @param p    JSON对象解析器
         * @param ctxt 需要反序列化对象的上下文
         * @return  LocalDateTime
         */
        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            //1.时间戳格式
            if (Boolean.TRUE.equals(timestamp)) {
                return LocalDateTimeUtil.of(p.getLongValue());
            }
            //2.自定义格式
            String time = p.getValueAsString();
            return LocalDateTimeUtil.parse(time, format);
        }
    }

}
