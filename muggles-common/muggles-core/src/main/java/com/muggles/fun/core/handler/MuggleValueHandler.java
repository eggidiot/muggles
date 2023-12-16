package com.muggles.fun.core.handler;

import org.springframework.core.MethodParameter;

/**
 * 返回值处理器
 */
public class MuggleValueHandler extends ReturnValueHandler{

    /**
     * Whether the given {@linkplain MethodParameter method return type} is
     * supported by this handler.
     *
     * @param returnType the method return type to check
     * @return {@code true} if this handler supports the supplied return type;
     * {@code false} otherwise
     */
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return false;
    }
}
