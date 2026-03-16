package com.muggles.fun.core.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.core.GenericTypeResolver;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.type.TypeFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 处理参数值HANDLER
 */
@Data
@Accessors(chain = true)
public abstract class ParamValueHandler {

    /**
     * jackson书写对象
     */
    protected JsonMapper jsonMapper;

    /**
     * 将{@link Type} 转化为Jackson需要的{com.fasterxml.jackson.databind.JavaType}
     */
    public JavaType getJavaType(Type type, Class<?> contextClass) {
        //MAPPER这个可以使用ObjectMapperUtils中ObjectMapper
        TypeFactory typeFactory = jsonMapper.getTypeFactory();
        //这种是处理public <T extends User> T testEnvV3(@JsonParam("users") List<T> user) 这种类型。
        return typeFactory.constructType(GenericTypeResolver.resolveType(type, contextClass));
    }

    /**
     * 将Object对象转换为具体的对象类型（支持泛型）
     */
    public <T> T value(String rawValue, JavaType javaType){
        return jsonMapper.readValue(rawValue, javaType);
    }

    /**
     * 获取request的核心参数字符串
     *
     * @param request	http请求
     * @return	String
     * @throws IOException	IO异常
     */
    public String getJson(HttpServletRequest request) throws IOException {
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();

        char[] buf = new char[1024];
        int rd;
        while ((rd = reader.read(buf)) != -1) {
            sb.append(buf, 0, rd);
        }
        return sb.toString();
    }
}
