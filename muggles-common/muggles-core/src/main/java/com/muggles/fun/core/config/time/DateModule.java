package com.muggles.fun.core.config.time;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import com.muggles.fun.basic.Constants;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.json.PackageVersion;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.module.SimpleModule;

import java.time.LocalDate;

/**
 * JACKSON 接口层时间参数序列化插件
 *
 * @author lenovo
 */
@Accessors(chain = true)
public class DateModule extends SimpleModule {
    /**
     * 构造方法，设置默认的时间序列化、反序列化组件
     */
    public DateModule(String format) {
        super(PackageVersion.VERSION);
        this.addSerializer(LocalDate.class, new TsLocalDateSerializer(format));
        this.addDeserializer(LocalDate.class, new TsLocalDateDeserializer(format));
    }

    /**
     * 构造方法，设置默认的时间序列化、反序列化组件
     */
    public DateModule() {
        this(Constants.DATE_FORMAT);
    }

    /**
     * 默认采用字符串形式格式化时间
     */
    @RequiredArgsConstructor
    public static class TsLocalDateSerializer extends ValueSerializer<LocalDate> {

        /**
         * 日期格式
         */
        final String format;

        /**
         * 序列化时间方法
         *
         * @param value     时间对象值
         * @param gen       JSON对象生成器
         * @param ctxt      可提供默认包含的可序列化组件的提供对象
         */
        @Override
        public void serialize(LocalDate value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
            //1.为Null值则不序列化操作
            if (ObjectUtil.isNull(value)) {
                return;
            }
            //2.只能使用字符串形式输出
            gen.writeString(LocalDateTimeUtil.format(value, format));
        }
    }

    /**
     * 默认将字符串时间输出成LocalDateTime
     */
    @RequiredArgsConstructor
    public static class TsLocalDateDeserializer extends ValueDeserializer<LocalDate> {
        /**
         * 日期格式
         */
        final String format;
        /**
         * 将字符串反序列化成时间对象
         *
         * @param p     JSON对象解析器
         * @param ctxt  需要反序列化对象的上下文
         * @return LocalDateTime
         */
        @Override
        public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
            //1.自定义格式
            String time = p.getValueAsString();
            return LocalDateTimeUtil.parseDate(time, format);
        }
    }
}
