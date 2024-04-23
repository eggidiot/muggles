package com.muggles.fun.dynamic.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.sf.json.JSONNull;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 兼容的ObjectMapper工具
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 0.9.0
 */
public class CompatibleObjectMapper extends ObjectMapper {

    public final static CompatibleObjectMapper INSTANCE = new CompatibleObjectMapper();

    private CompatibleObjectMapper() {
        super();
        // fix jsr310
        this.registerModule(new JavaTimeModule());
        // 解决：net.sf.json.JSONException: Object is null
        SimpleModule netSfJsonModule = new SimpleModule("net.sf.json");
        netSfJsonModule.addSerializer(JSONNull.class, NullSerializer.instance);
        this.enable(SerializationFeature.INDENT_OUTPUT);
        this.registerModule(netSfJsonModule);
    }

    @Override
    public byte[] writeValueAsBytes(Object value) throws JsonProcessingException {
        // LocalDateTime ToString 不行，需要转换为DateTime
        if (value instanceof LocalDateTime) {
            value = Date.from(((LocalDateTime) value).atZone(ZoneId.systemDefault()).toInstant());
        }
        return super.writeValueAsBytes(value);
    }

}
