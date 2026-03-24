package com.muggles.fun.repo.mp.generate;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 包路径生成器
 */
@UtilityClass
public class PackageOutputPathGenerator {
	/**
	 * 持久层模块后缀
	 */
	private static final String REPO_SUFFIX = "-repo";
	/**
	 * controller层模块后缀
	 */
	private static final String CONTROLLER_SUFFIX = "-controller";
	/**
	 * 服务层后缀
	 */
	private static final String SERVICE_SUFFIX = "-service";
	/**
	 * 服务实现层后缀
	 */
	private static final String SERVICE_IMPL_SUFFIX = "-service-impl";

	/**
	 * java文件的标准输出路径
	 */
	private static final String JAVA_FILE_BASE_PATH = "{basePath}src{separator}main{separator}java{separator}{packageName}{separator}{typePackageName}";
	/**
	 * xml文件的基础输出路径
	 */
	private static final String XML_FILE_BASE_PATH = "{basePath}src{separator}main{separator}resources{separator}{packageName}{separator}repo{separator}persistence";
	/**
	 * 基础路径字符串占位符
	 */
	private static final String REPLACE_HOLDER_BATH_PATH = "basePath";
	/**
	 * 分隔符占位符
	 */
	private static final String REPLACE_HOLDER_SEPARATOR = "separator";
	/**
	 * 包路径占位符
	 */
	private static final String REPLACE_HOLDER_PACKAGE_NAME = "packageName";
	/**
	 * 包类型占位符
	 */
	private static final String REPLACE_HOLDER_TYPE_PACKAGE_NAME = "typePackageName";

	/**
	 * 实体类包名
	 */
	private static final String ENTITY_PACKAGE_NAME = "entity";

	/**
	 * mapper类包名
	 */
	private static final String MAPPER_PACKAGE_NAME = "mapper";

	/**
	 * service类包名
	 */
	private static final String SERVICE_PACKAGE_NAME = "service";

	/**
	 * service实现类包名
	 */
	private static final String SERVICE_IMPL_PACKAGE_NAME = "service{}impl";

	/**
	 * 控制器类包名
	 */
	private static final String CONTROLLER_PACKAGE_NAME = "controller";

	/**
	 * 模块名分割标识符
	 */
	private static final String MODULE_SPLIT = "-";
	/**
	 * 包名分隔符
	 */
	private static final String PACKAGE_SPLIT = "\\.";

	/**
	 * 获取项目根目录绝对路径
	 *
	 * @return	String
	 */
	public String getRootPath() {
		return System.getProperty("user.dir");
	}


	/**
	 * 获取java文件输出路径
	 *
	 * @param appName 应用名
	 * @param suffix  所属模块后缀
	 * @return	String
	 */
	private String buildJavaOutputPath(String appName, String suffix, String packageName, String packageType) {
		return buildJavaOutputPath(appName, null, suffix, packageName, packageType);
	}

	/**
	 * 获取java文件输出路径
	 *
	 * @param appName 应用名
	 * @param suffix  所属模块后缀
	 * @return	String
	 */
	private String buildJavaOutputPath(String appName, String moduleName, String suffix, String packageName, String packageType) {
		StringBuilder sb = new StringBuilder(getRootPath());
		sb.append(File.separator);
		if (StrUtil.isNotBlank(moduleName)) {
			sb.append(appName);
			sb.append(MODULE_SPLIT);
			sb.append(moduleName.replaceAll(PACKAGE_SPLIT, MODULE_SPLIT));
			sb.append(File.separator);
		}
		sb.append(appName);
		if (StrUtil.isNotBlank(moduleName)) {
			sb.append(MODULE_SPLIT);
			sb.append(moduleName.replaceAll(PACKAGE_SPLIT, MODULE_SPLIT));
		}
		sb.append(suffix);
		sb.append(File.separator);
		Map<String, String> pathParam = new HashMap<>();
		pathParam.put(REPLACE_HOLDER_BATH_PATH, sb.toString());
		pathParam.put(REPLACE_HOLDER_SEPARATOR, File.separator);
		pathParam.put(REPLACE_HOLDER_PACKAGE_NAME, packageName.concat(File.separator).concat(moduleName).replaceAll(PACKAGE_SPLIT, File.separator));
		pathParam.put(REPLACE_HOLDER_TYPE_PACKAGE_NAME, packageType);
		return StrUtil.format(JAVA_FILE_BASE_PATH, pathParam);
	}

	/**
	 * 初始化输出文件路径
	 *
	 * @param appName     应用名
	 * @param packageName 包路径
	 * @return	Map<OutputFile, String>
	 */
	public Map<OutputFile, String> initOutputPath(String appName, String packageName) {
		return initOutputPath(appName, null, packageName);
	}

	/**
	 * 初始化输出文件路径
	 *
	 * @param appName     应用名
	 * @param packageName 包路径
	 * @return Map<OutputFile, String>
	 */
	public Map<OutputFile, String> initOutputPath(String appName, String moduleName, String packageName) {
		Map<OutputFile, String> outputMap = new HashMap<>();
		outputMap.put(OutputFile.controller, buildJavaOutputPath(appName, moduleName, CONTROLLER_SUFFIX, packageName, CONTROLLER_PACKAGE_NAME));
		outputMap.put(OutputFile.service, buildJavaOutputPath(appName, moduleName, SERVICE_SUFFIX, packageName, SERVICE_PACKAGE_NAME));
		outputMap.put(OutputFile.serviceImpl, buildJavaOutputPath(appName, moduleName, SERVICE_IMPL_SUFFIX, packageName, StrFormatter.format(SERVICE_IMPL_PACKAGE_NAME, File.separator)));
		outputMap.put(OutputFile.entity, buildJavaOutputPath(appName, moduleName, REPO_SUFFIX, packageName, ENTITY_PACKAGE_NAME));
		outputMap.put(OutputFile.mapper, buildJavaOutputPath(appName, moduleName, REPO_SUFFIX, packageName, MAPPER_PACKAGE_NAME));
		outputMap.put(OutputFile.xml, buildXmlOutputPath(appName, moduleName, packageName));
		return outputMap;
	}

	/**
	 * 获取xml文件输出路径
	 *
	 * @param appName     应用名
	 * @param packageName 包路径
	 * @return	String
	 */
	public String buildXmlOutputPath(String appName, String packageName) {
		return buildXmlOutputPath(appName, null, packageName);
	}

	/**
	 * 获取xml文件输出路径
	 *
	 * @param appName     应用名
	 * @param moduleName  模块名
	 * @param packageName 包路径
	 * @return String
	 */
	public String buildXmlOutputPath(String appName, String moduleName, String packageName) {
		StringBuilder sb = new StringBuilder(getRootPath());
		sb.append(File.separator);
		if (StrUtil.isNotBlank(moduleName)) {
			sb.append(appName);
			sb.append(MODULE_SPLIT);
			sb.append(moduleName);
			sb.append(File.separator);
		}
		sb.append(appName);
		if (StrUtil.isNotBlank(moduleName)) {
			sb.append(MODULE_SPLIT);
			sb.append(moduleName);
		}
		sb.append(REPO_SUFFIX);
		sb.append(File.separator);
		Map<String, String> pathParam = new HashMap<>();
		pathParam.put(REPLACE_HOLDER_BATH_PATH, sb.toString());
		pathParam.put(REPLACE_HOLDER_SEPARATOR, File.separator);
		pathParam.put(REPLACE_HOLDER_PACKAGE_NAME, packageName.replaceAll(PACKAGE_SPLIT, File.separator));
		return StrUtil.format(XML_FILE_BASE_PATH, pathParam);
	}

}
