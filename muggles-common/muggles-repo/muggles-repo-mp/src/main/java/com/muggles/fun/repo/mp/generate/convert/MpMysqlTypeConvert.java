package com.muggles.fun.repo.mp.generate.convert;

import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.ITypeConvert;
import com.baomidou.mybatisplus.generator.config.converts.select.BranchBuilder;
import com.baomidou.mybatisplus.generator.config.converts.select.Selector;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import org.jetbrains.annotations.NotNull;

import static com.baomidou.mybatisplus.generator.config.rules.DbColumnType.*;

/**
 * Mysqll 字段类型转换
 */
public class MpMysqlTypeConvert implements ITypeConvert {
	public static final MpMysqlTypeConvert INSTANCE = new MpMysqlTypeConvert();

	/**
	 * @inheritDoc
	 */
	@Override
	public IColumnType processTypeConvert(@NotNull GlobalConfig config, @NotNull String fieldType) {
		return use(fieldType).test(containsAny("char", "text", "json", "enum").then(STRING))
			.test(contains("bigint").then(LONG))
			.test(contains("tinyint").then(INTEGER))
			.test(contains("bit").then(BYTE))
			.test(contains("int").then(INTEGER))
			.test(contains("decimal").then(BIG_DECIMAL))
			.test(contains("clob").then(STRING))
			.test(contains("blob").then(BYTE_ARRAY))
			.test(contains("binary").then(BYTE_ARRAY))
			.test(contains("float").then(FLOAT))
			.test(contains("double").then(DOUBLE))
			.test(containsAny("date", "time", "year").then(t -> toDateType(config, t))).or(STRING);
	}

	/**
	 * 转换为日期类型
	 *
	 * @param config 配置信息
	 * @param type   类型
	 * @return 返回对应的列类型
	 */
	public static IColumnType toDateType(GlobalConfig config, String type) {
		String dateType = type.replaceAll("\\(\\d+\\)", "");
        return switch (config.getDateType()) {
            case ONLY_DATE -> DATE;
            case SQL_PACK -> switch (dateType) {
                case "date", "year" -> DATE_SQL;
                case "time" -> TIME;
                default -> TIMESTAMP;
            };
            case TIME_PACK -> switch (dateType) {
                case "date" -> LOCAL_DATE;
                case "time" -> LOCAL_TIME;
                case "year" -> YEAR;
                default -> LOCAL_DATE_TIME;
            };
            default -> STRING;
        };
	}

	/**
	 * 使用指定参数构建一个选择器
	 *
	 * @param param 参数
	 * @return 返回选择器
	 */
	static Selector<String, IColumnType> use(String param) {
		return new Selector<>(param.toLowerCase());
	}

	/**
	 * 这个分支构建器用于构建用于支持 {@link String#contains(CharSequence)} 的分支
	 *
	 * @param value 分支的值
	 * @return 返回分支构建器
	 * @see #containsAny(CharSequence...)
	 */
	static BranchBuilder<String, IColumnType> contains(CharSequence value) {
		return BranchBuilder.of(s -> s.contains(value));
	}

	/**
	 * @see #contains(CharSequence)
	 */
	static BranchBuilder<String, IColumnType> containsAny(CharSequence... values) {
		return BranchBuilder.of(s -> {
			for (CharSequence value : values) {
				if (s.contains(value)) {
					return true;
				}
			}
			return false;
		});
	}
}
