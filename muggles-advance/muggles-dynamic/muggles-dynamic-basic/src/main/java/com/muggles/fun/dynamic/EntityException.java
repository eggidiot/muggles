package com.muggles.fun.dynamic;


import cn.hutool.core.util.StrUtil;
import com.muggles.fun.basic.exception.MugglesBizException;

/**
 * 实体建模异常
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 0.9.0
 */
public class EntityException extends MugglesBizException {

    public EntityException(Throwable cause) {
        super("", cause);
    }

    public EntityException(String message) {
        super(message);
    }

    public EntityException(String messageFormat, Object... args) {
        super(StrUtil.format(messageFormat, args));
    }

    public EntityException(String message, Throwable cause) {
        super(message, cause);
    }

}
