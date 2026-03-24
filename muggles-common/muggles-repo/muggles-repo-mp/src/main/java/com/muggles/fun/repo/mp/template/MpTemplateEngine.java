package com.muggles.fun.repo.mp.template;

import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 自定义模板引擎
 */
public class MpTemplateEngine extends FreemarkerTemplateEngine {

	/**
	 * 项目定制组件父类模板
	 */
	public static String customService = "ICustomService.java";
	/**
	 * 项目定制组件父类实现类模板
	 */
	public static String customImplService = "CustomServiceImpl.java";
	/**
	 * 项目定制业务父类模板
	 */
	public static String bizService = "IBizService.java";
	/**
	 * 项目定制业务父类抽象类模板
	 */
	public static String AbstractBizService = "AbstractBizService.java";
	/**
	 * 项目定制mapper父类模版
	 */
	public static String customMapper = "ICustomMapper.java";
	/**
	 * 字段遮掩
	 */
	public static String recordMask = "RecordMask.java";

	/**
	 * 输出自定义模板文件
	 *
	 * @param customFiles 自定义配置模板文件信息
	 * @param tableInfo   表信息
	 * @param objectMap   渲染数据
	 * @since 3.5.1
	 */
	@Override
	protected void outputCustomFile(@NotNull List<CustomFile> customFiles, @NotNull TableInfo tableInfo, @NotNull Map<String, Object> objectMap) {
		String entityName = tableInfo.getEntityName();
		String otherPath = getPathInfo(OutputFile.xml);
		AtomicBoolean customServiceJavaDone = new AtomicBoolean(false);
		AtomicBoolean customServiceImplJavaDone = new AtomicBoolean(false);
		AtomicBoolean bizServiceJavaDone = new AtomicBoolean(false);
		AtomicBoolean abstractBizServiceJavaDone = new AtomicBoolean(false);
		AtomicBoolean customMapperJavaDone = new AtomicBoolean(false);
		AtomicBoolean recodeMaskJavaDone = new AtomicBoolean(false);
		customFiles.forEach(custom -> {
			if (custom.getFileName().equals(customService)) {
				if (customServiceJavaDone.get()) {
					return;
				}
				String customPath = getPathInfo(OutputFile.service) + File.separator + "base";
				String fileName = String.format((customPath + File.separator + "%s"), custom.getFileName());
				outputFile(new File(fileName), objectMap, custom.getTemplatePath(), custom.isFileOverride());
				customServiceJavaDone.set(true);
			} else if (custom.getFileName().equals(customImplService)) {
				if (customServiceImplJavaDone.get()) {
					return;
				}
				String customPath = getPathInfo(OutputFile.service) + File.separator + "base";
				String fileName = String.format((customPath + File.separator + "%s"), custom.getFileName());
				outputFile(new File(fileName), objectMap, custom.getTemplatePath(), custom.isFileOverride());
				customServiceImplJavaDone.set(true);
			} else if (custom.getFileName().equals(bizService)) {
				if (bizServiceJavaDone.get()) {
					return;
				}
				String customPath = getPathInfo(OutputFile.service) + File.separator + "base";
				String fileName = String.format((customPath + File.separator + "%s"), custom.getFileName());
				outputFile(new File(fileName), objectMap, custom.getTemplatePath(), custom.isFileOverride());
				bizServiceJavaDone.set(true);
			} else if (custom.getFileName().equals(AbstractBizService)) {
				if (abstractBizServiceJavaDone.get()) {
					return;
				}
				String customPath = getPathInfo(OutputFile.service) + File.separator + "base";
				String fileName = String.format((customPath + File.separator + "%s"), custom.getFileName());
				outputFile(new File(fileName), objectMap, custom.getTemplatePath(), custom.isFileOverride());
				abstractBizServiceJavaDone.set(true);
			} else if (custom.getFileName().equals(customMapper)) {
				if (customMapperJavaDone.get()) {
					return;
				}
				String customPath = getPathInfo(OutputFile.mapper) + File.separator + "base";
				String fileName = String.format((customPath + File.separator + "%s"), custom.getFileName());
				outputFile(new File(fileName), objectMap, custom.getTemplatePath(), custom.isFileOverride());
				customMapperJavaDone.set(true);
			} else if (custom.getFileName().equals(recordMask)) {
				if (recodeMaskJavaDone.get()) {
					return;
				}
				String customPath = getPathInfo(OutputFile.service) + File.separator + "base";
				String fileName = String.format((customPath + File.separator + "%s"), custom.getFileName());
				outputFile(new File(fileName), objectMap, custom.getTemplatePath(), custom.isFileOverride());
				recodeMaskJavaDone.set(true);
			} else {
				String fileName = String.format((otherPath + File.separator + entityName + File.separator + "%s"), custom.getFileName());
				outputFile(new File(fileName), objectMap, custom.getTemplatePath(), custom.isFileOverride());
			}
		});
	}
}
