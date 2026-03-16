package com.muggles.fun.core.encrypt;

import lombok.Data;
import lombok.experimental.Accessors;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

/**
 * 默认编码器
 */
@Data
@Accessors(chain = true)
public class DefaultEncryptor implements StringEncryptor {

    /**
     * 标准PBE字符串编码器
     */
    private StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();

    /**
     * 加密方法
     *
     * @param s	加密属性
     * @return	String
     */
    @Override
    public String encrypt(String s) {
        return standardPBEStringEncryptor.encrypt(s);
    }

    /**
     * 解密方法
     *
     * @param s	加密属性
     * @return	String
     */
    @Override
    public String decrypt(String s) {
        return standardPBEStringEncryptor.decrypt(s);
    }
}
