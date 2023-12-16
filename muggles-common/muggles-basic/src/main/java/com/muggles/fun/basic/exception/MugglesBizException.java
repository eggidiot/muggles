package com.muggles.fun.basic.exception;

import com.muggles.fun.basic.Constants;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务基本异常，一般用于定义非正常业务流程状态
 *
 * @author Lenovo
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MugglesBizException extends RuntimeException {

	/**
	 * 错误提示编码
	 */
	private int code = Constants.FAIL;

	/**
	 * @param message 错误提示信息构造方法
	 */
	public MugglesBizException(String message) {
		super(message);
	}

	/**
	 * @param message 错误提示信息构造方法 占位符用{}的方式提供
	 * @param args    参数字符串数组
	 */
	public MugglesBizException(String message, String... args) {
		super(fillMessage(message, args));

	}

	/**
	 * message占位符的方式创建FlineBizException对象
	 *
	 * @param message 错误提示信息构造方法 占位符用{}的方式提供
	 * @param args    参数字符串数组
	 * @return	MugglesBizException
	 */
	public static MugglesBizException messageOf(String message, String... args) {
		return new MugglesBizException(message, args);
	}

	/**
	 * 包装其他异常信息
	 *
	 * @param message	异常文本
	 * @param ex		运行时异常
	 */
	public MugglesBizException(String message, Throwable ex) {
		super(message, ex);
	}

	/**
	 * @param code    错误提示编码
	 * @param message 错误提示信息构造方法
	 */
	public MugglesBizException(int code, String message) {
		this(message);
		this.code = code;
	}

	/**
	 * 填充字符串占位符
	 * <p>
	 * 如果占位符数量和参数数量对应不上，则会抛IllegalFormatConversionException 异常
	 *
	 * @param message	异常文本
	 * @param strArgs	异常参数
	 * @return			异常信息
	 */
	private static String fillMessage(String message, String... strArgs) {
		if (message == null || message.isEmpty()) {
			return message;
		}
		message = message.replaceAll(Constants.EMPTY_JSON_IN_STRING, Constants.STR_PLACEHOLDER);
		return String.format(message, strArgs);
	}

}
