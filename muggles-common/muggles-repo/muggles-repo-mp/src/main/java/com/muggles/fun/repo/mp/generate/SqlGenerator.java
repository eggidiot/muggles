package com.muggles.fun.repo.mp.generate;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.toolkit.JdbcUtils;
import com.baomidou.mybatisplus.generator.IFill;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.Controller;
import com.baomidou.mybatisplus.generator.config.builder.Entity;
import com.baomidou.mybatisplus.generator.config.builder.Mapper;
import com.baomidou.mybatisplus.generator.config.builder.Service;
import com.baomidou.mybatisplus.generator.config.po.LikeTable;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.querys.DbQueryRegistry;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.function.ConverterFileName;
import com.baomidou.mybatisplus.generator.keywords.BaseKeyWordsHandler;
import com.baomidou.mybatisplus.generator.keywords.H2KeyWordsHandler;
import com.baomidou.mybatisplus.generator.keywords.MySqlKeyWordsHandler;
import com.baomidou.mybatisplus.generator.keywords.PostgreSqlKeyWordsHandler;
import com.muggles.fun.basic.Constants;
import com.muggles.fun.repo.mp.generate.convert.MpMysqlTypeConvert;
import com.muggles.fun.repo.mp.generate.convert.MpPostgreSqlTypeConvert;
import lombok.experimental.UtilityClass;
import org.apache.ibatis.cache.Cache;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * 实体生成器 根据数据库表信息，字段信息，生成对应的实体和相应的其他文件
 *
 * @author Lenovo
 */
@UtilityClass
public class SqlGenerator {

	/**
	 * 数据库配置(DataSourceConfig) 基础配置
	 *
	 * @param url      jdbc 路径
	 * @param username 数据库账号
	 * @param password 数据库密码
	 * @return	DataSourceConfig
	 */
	public DataSourceConfig db(String url, String username, String password) {
		DbQueryRegistry dbQueryRegistry = new DbQueryRegistry();
		DbType dbType = JdbcUtils.getDbType(url);
		IDbQuery dbQuery = dbQueryRegistry.getDbQuery(dbType);
		return dbAll(url, username, password, dbQuery, null, typeConvertChose(dbType), keyWordsHandlerChose(dbType));
	}

	/**
	 * 自定义类型转换选择
	 *
	 * @param dbType	数据库类型
	 * @return	ITypeConvert
	 */
	private ITypeConvert typeConvertChose(DbType dbType) {
        return switch (dbType) {
            case MYSQL -> MpMysqlTypeConvert.INSTANCE;
            case POSTGRE_SQL -> MpPostgreSqlTypeConvert.INSTANCE;
            default -> null;
        };
	}

	/**
	 * 选择KeyWordsHandler
	 *
	 * @param dbType	数据库类型
	 * @return	BaseKeyWordsHandler
	 */
	private BaseKeyWordsHandler keyWordsHandlerChose(DbType dbType) {
        return switch (dbType) {
            case POSTGRE_SQL -> new PostgreSqlKeyWordsHandler();
            case MYSQL -> new MySqlKeyWordsHandler();
            case H2 -> new H2KeyWordsHandler();
            default -> null;
        };
	}

	/**
	 * 数据库配置(DataSourceConfig)
	 *
	 * @param url             jdbc 路径
	 * @param username        数据库账号
	 * @param password        数据库密码
	 * @param dbQuery         数据库查询
	 * @param schema          数据库 schema(部分数据库适用)
	 * @param typeConvert     数据库类型转换器
	 * @param keyWordsHandler 数据库关键字处理器
	 * @return DataSourceConfig
	 */
	public DataSourceConfig dbAll(String url, String username, String password, IDbQuery dbQuery, String schema,
										 ITypeConvert typeConvert, IKeyWordsHandler keyWordsHandler) {
		return new DataSourceConfig.Builder(url, username, password).dbQuery(dbQuery).schema(schema)
			.typeConvert(typeConvert).keyWordsHandler(keyWordsHandler).build();
	}

	/**
	 * 全局配置(GlobalConfig)
	 *
	 * @param output 指定输出目录
	 * @param author 作者名
	 * @return GlobalConfig
	 */
	public GlobalConfig global(String output, String author) {
		return globalAll(true, output, author, false, true, DateType.TIME_PACK, Constants.DATE_FORMAT);
	}

	/**
	 * 全局配置(GlobalConfig)
	 *
	 * @param disableOpenDir 禁止打开输出目录 默认值:true
	 * @param output         指定输出目录
	 * @param author         作者名
	 * @param enableKotlin   开启 kotlin模式 默认值:false
	 * @param enableSwagger  开启 swagger模式 默认值:false
	 * @param type           时间策略
	 * @param format         注释日期 默认值: yyyy-MM-dd HH:mm:ss
	 * @return	GlobalConfig
	 */
	public GlobalConfig globalAll(boolean disableOpenDir, String output, String author, boolean enableKotlin,
										 boolean enableSwagger, DateType type, String format) {
		GlobalConfig.Builder builder =
			new GlobalConfig.Builder().outputDir(output).author(author).dateType(type).commentDate(format);
		// 2.是否禁止打开输出目录
		if (disableOpenDir) {
			builder.disableOpenDir();
		}
		// 3.是否开启KOTLIN模式
		if (enableKotlin) {
			builder.enableKotlin();
		}
		// 4.是否开启swagger模式
		if (enableSwagger) {
			builder.enableSwagger();
		}
		return builder.build();
	}

	/**
	 * 父级包名
	 *
	 * @param parent   父包名
	 * @param pathInfo 路径配置信息
	 * @return PackageConfig
	 */
	public static PackageConfig pack(String parent, Map<OutputFile, String> pathInfo) {
		return packAll(parent, null, "entity", "service", "service.impl", "mapper", "/" + parent.replaceAll("\\.", "/"),
			"controller", "other", pathInfo);
	}

	/**
	 * 父级包名
	 *
	 * @param parent     父包名
	 * @param pathInfo   路径配置信息
	 * @param moduleName 父模块名
	 * @return PackageConfig
	 */
	public static PackageConfig pack(String parent, Map<OutputFile, String> pathInfo, String moduleName) {
		return packAll(parent, moduleName, "entity", "service", "service.impl", "mapper",
			"/" + parent.replaceAll("\\.", "/"), "controller", "other", pathInfo);
	}

	/**
	 * 包配置(PackageConfig)
	 *
	 * @param parent      父包名
	 * @param moduleName  父包模块名
	 * @param entity      Entity 包名 默认值:entity
	 * @param service     Service 包名 默认值:service
	 * @param serviceImpl Service Impl 包名 默认值:service.impl
	 * @param mapper      Mapper 包名 默认值:mapper
	 * @param xml         Mapper XML 包名 默认值:mapper.xml
	 * @param controller  Controller 包名 默认值:controller
	 * @param other       自定义文件包名 输出自定义文件时所用到的包名
	 * @param pathInfo    路径配置信息
	 * @return PackageConfig
	 */
	public static PackageConfig packAll(String parent, String moduleName, String entity, String service,
										String serviceImpl, String mapper, String xml, String controller, String other,
										Map<OutputFile, String> pathInfo) {
		return new PackageConfig.Builder().parent(parent).moduleName(moduleName).entity(entity).service(service)
			.serviceImpl(serviceImpl).mapper(mapper).xml(xml).controller(controller).pathInfo(pathInfo).build();
	}

	/**
	 * 模板配置(TemplateConfig)，默认不生成Controller层代码
	 *
	 * @return TemplateConfig
	 */
	public TemplateConfig template() {
		return templateAll(ConstVal.TEMPLATE_ENTITY_JAVA, ConstVal.TEMPLATE_SERVICE, ConstVal.TEMPLATE_SERVICE_IMPL,
			ConstVal.TEMPLATE_MAPPER, ConstVal.TEMPLATE_XML, ConstVal.TEMPLATE_CONTROLLER, false, (TemplateType) null);
	}

	/**
	 * 模板配置(TemplateConfig)
	 *
	 * @param entity        设置实体模板路径(JAVA)
	 * @param service       设置 service 模板路径
	 * @param serviceImpl   设置 serviceImpl 模板路径
	 * @param mapper        设置 mapper 模板路径
	 * @param xml           设置 mapperXml 模板路径
	 * @param controller    设置 controller 模板路径
	 * @param disable       是否禁止所有的模板
	 * @param templateTypes 禁止指定的模板
	 * @return TemplateConfig
	 */
	public static TemplateConfig templateAll(String entity, String service, String serviceImpl, String mapper,
											 String xml, String controller, boolean disable, TemplateType... templateTypes) {
		TemplateConfig.Builder builder = new TemplateConfig.Builder().entity(entity).service(service)
			.serviceImpl(serviceImpl).mapper(mapper).xml(xml).controller(controller);
		// 1.是否禁止模板
		if (disable) {
			builder.disable();
		}
		// 2.禁止传入的模板
		if (templateTypes != null && templateTypes.length != 0) {
			builder.disable(templateTypes);
		}
		return builder.build();
	}

	/**
	 * 注入配置(InjectionConfig)
	 *
	 * @param biConsumer 输出文件之前消费者
	 * @param customMap  自定义配置 Map 对象
	 * @param customFile 自定义配置模板文件
	 * @return InjectionConfig
	 */
	public static InjectionConfig inject(BiConsumer<TableInfo, Map<String, Object>> biConsumer,
										 Map<String, Object> customMap, Map<String, String> customFile) {
		return new InjectionConfig.Builder().beforeOutputFile(biConsumer).customMap(customMap).customFile(customFile)
			.build();
	}

	/**
	 * 策略配置(StrategyConfig)
	 *
	 * @param capital          是否开启大写命名 默认值:false
	 * @param skipView         是否开启跳过视图 默认值:false
	 * @param disableSqlFilter 是否禁用 sql 过滤 默认值:true，语法不能支持使用 sql 过滤表的话，可以考虑关闭此开关
	 * @param enableSchema     是否 启用 schema 默认值:false，多 schema 场景的时候打开
	 * @param likeTable        模糊表匹配(sql 过滤) likeTable 与 notLikeTable 只能配置一项
	 * @param notLikeTable     模糊表排除(sql 过滤) likeTable 与 notLikeTable 只能配置一项
	 * @param includes         增加表匹配(内存过滤) include 与 exclude 只能配置一项
	 * @param excludes         增加表排除匹配(内存过滤) include 与 exclude 只能配置一项
	 * @param tablePrefix      增加过滤表前缀
	 * @param tableSuffix      增加过滤表后缀
	 * @param fieldPrefix      增加过滤字段前缀
	 * @param fieldSuffix      增加过滤字段后缀
	 * @return StrategyConfig
	 */
	public static StrategyConfig strategy(boolean capital, boolean skipView, boolean disableSqlFilter,
										  boolean enableSchema, LikeTable likeTable, LikeTable notLikeTable, List<String> includes, List<String> excludes,
										  List<String> tablePrefix, List<String> tableSuffix, List<String> fieldPrefix, List<String> fieldSuffix) {
		StrategyConfig.Builder builder = new StrategyConfig.Builder();
		// 1.是否开启大写命名
		if (capital) {
			builder.enableCapitalMode();
		}
		// 2.是否开启跳过视图
		if (skipView) {
			builder.enableSkipView();
		}
		// 3.禁用 sql 过滤
		if (disableSqlFilter) {
			builder.disableSqlFilter();
		}
		// 4.启用 schema
		if (enableSchema) {
			builder.enableSchema();
		}
		// 5.模糊表匹配(sql 过滤)
		if (likeTable != null) {
			builder.likeTable(likeTable);
		}
		// 6.模糊表排除(sql 过滤)
		if (notLikeTable != null) {
			builder.notLikeTable(notLikeTable);
		}
		// 7.添加包含的表
		if (CollUtil.isNotEmpty(includes)) {
			builder.addInclude(includes.toArray(new String[0]));
		}
		// 8.添加排除的表
		if (CollUtil.isNotEmpty(excludes)) {
			builder.addExclude(excludes.toArray(new String[0]));
		}
		// 9.增加过滤表前缀
		if (CollUtil.isNotEmpty(tablePrefix)) {
			builder.addTablePrefix(tablePrefix.toArray(new String[0]));
		}
		// 10.增加过滤表后缀
		if (CollUtil.isNotEmpty(tableSuffix)) {
			builder.addTableSuffix(tableSuffix.toArray(new String[0]));
		}
		// 11.增加过滤字段前缀
		if (CollUtil.isNotEmpty(fieldPrefix)) {
			builder.addFieldPrefix(fieldPrefix.toArray(new String[0]));
		}
		// 12.增加过滤字段后缀
		if (CollUtil.isNotEmpty(fieldSuffix)) {
			builder.addFieldSuffix(fieldSuffix.toArray(new String[fieldSuffix.size()]));
		}
		return builder.build();
	}

	/**
	 * Entity 策略配置
	 *
	 * @param config                     策略配置
	 * @param fileOverride               是否覆盖文件
	 * @param nameConvert                名称转换实现
	 * @param superClass                 设置父类
	 * @param superClazz                 设置父类，类名字符串
	 * @param disableSerialVersionUID    是否禁用生成 serialVersionUID 默认值:true
	 * @param enableColumnConstant       是否开启生成字段常量 默认值:false
	 * @param enableChainModel           是否开启链式模型 默认值:false
	 * @param enableLombok               是否开启 lombok 模型 默认值:false
	 * @param enableRemoveIsPrefix       是否开启 Boolean 类型字段移除 is 前缀 默认值:false
	 * @param enableTableFieldAnnotation 是否开启生成实体时生成字段注解 默认值:false
	 * @param enableActiveRecord         是否开启 ActiveRecord 模型 默认值:false
	 * @param versionColumnName          乐观锁字段名(数据库)
	 * @param versionPropertyName        乐观锁属性名(实体)
	 * @param logicDeleteColumnName      逻辑删除字段名(数据库)
	 * @param logicDeletePropertyName    逻辑删除属性名(实体)
	 * @param naming                     数据库表映射到实体的命名策略 默认下划线转驼峰命
	 * @param columnNaming               数据库表字段映射到实体的命名策略 默认为 null
	 * @param addSuperEntityColumns      添加父类公共字段
	 * @param addIgnoreColumns           添加忽略字段
	 * @param addTableFills              添加表字段填充
	 * @param type                       全局主键类型
	 * @param ConverterFileName          转换文件名称
	 * @param formatFileName             格式化文件名称
	 * @return StrategyConfig
	 */
	public StrategyConfig entity(StrategyConfig config, boolean fileOverride, String entityTemplate, INameConvert nameConvert,
										Class<?> superClass, String superClazz, boolean disableSerialVersionUID, boolean enableColumnConstant,
										boolean enableChainModel, boolean enableLombok, boolean enableRemoveIsPrefix,
										boolean enableTableFieldAnnotation, boolean enableActiveRecord, String versionColumnName,
										String versionPropertyName, String logicDeleteColumnName, String logicDeletePropertyName, NamingStrategy naming,
										NamingStrategy columnNaming, List<String> addSuperEntityColumns, List<String> addIgnoreColumns,
										List<IFill> addTableFills, IdType type, ConverterFileName ConverterFileName, String formatFileName) {
		Entity.Builder eb = config.entityBuilder();
		eb.versionColumnName(versionColumnName).versionPropertyName(versionPropertyName)
			.logicDeleteColumnName(logicDeleteColumnName).logicDeletePropertyName(logicDeletePropertyName)
			.naming(naming).columnNaming(columnNaming).idType(type).convertFileName(ConverterFileName)
			.formatFileName(formatFileName);
		// 0.设置名称转换实现
		if (nameConvert != null) {
			eb.nameConvert(nameConvert);
		}
		// 1.设置父类
		if (StrUtil.isNotBlank(superClazz)) {
			eb.superClass(superClazz);
		}
		if (superClass != null) {
			eb.superClass(superClass);
		}
		// 2.禁用生成 serialVersionUID
		if (disableSerialVersionUID) {
			eb.disableSerialVersionUID();
		}
		// 3.开启生成字段常量
		if (enableColumnConstant) {
			eb.enableColumnConstant();
		}
		// 4.开启链式模型
		if (enableChainModel) {
			eb.enableChainModel();
		}
		// 5.开启 lombok 模型
		if (enableLombok) {
			eb.enableLombok();
		}
		// 6.类型字段移除 is 前缀
		if (enableRemoveIsPrefix) {
			eb.enableRemoveIsPrefix();
		}
		// 7.开启生成实体时生成字段注解
		if (enableTableFieldAnnotation) {
			eb.enableTableFieldAnnotation();
		}
		// 8.开启 ActiveRecord 模型
		if (enableActiveRecord) {
			eb.enableActiveRecord();
		}
		// 9.添加父类公共字段
		if (CollUtil.isNotEmpty(addSuperEntityColumns)) {
			eb.addSuperEntityColumns(addSuperEntityColumns.toArray(new String[0]));
		}
		// 10.添加忽略字段
		if (CollUtil.isNotEmpty(addIgnoreColumns)) {
			eb.addIgnoreColumns(addIgnoreColumns.toArray(new String[0]));
		}
		// 10.添加忽略字段
		if (CollUtil.isNotEmpty(addTableFills)) {
			eb.addTableFills(addTableFills.toArray(new IFill[0]));
		}
		if (fileOverride) {
			eb.enableFileOverride();
		}
		if (StrUtil.isNotBlank(entityTemplate)) {
			eb.javaTemplate(entityTemplate);
		}
		eb.build();
		return config;
	}

	/**
	 * Controller 策略配置
	 *
	 * @param config            策略配置
	 * @param fileOverride      是否覆盖文件
	 * @param converterFileName 文件模板路径
	 * @param superClass        设置父类
	 * @param superClazz        设置父类，类名字符串
	 * @param enableHyphenStyle 开启驼峰转连字符 默认值:false
	 * @param enableRestStyle   开启生成@RestController 控制器 默认值:false
	 * @param converterFileName 转换文件名称
	 * @param format            格式化文件名称
	 * @return StrategyConfig
	 */
	public StrategyConfig controller(@NotNull StrategyConfig config, boolean fileOverride, String controllerTemplate, Class<?> superClass,
											String superClazz, boolean enableHyphenStyle, boolean enableRestStyle, ConverterFileName converterFileName,
											String format) {
		Controller.Builder cb = config.controllerBuilder();
		cb.convertFileName(converterFileName).formatFileName(format);

		// 1.设置父类
		if (StrUtil.isNotBlank(superClazz)) {
			cb.superClass(superClazz);
		}
		if (superClass != null) {
			cb.superClass(superClass);
		}
		// 2.开启驼峰转连字符
		if (enableHyphenStyle) {
			cb.enableHyphenStyle();
		}
		// 3.开启生成@RestController 控制器
		if (enableRestStyle) {
			cb.enableRestStyle();
		}
		// 4.文件是否覆盖
		if (fileOverride) {
			cb.enableFileOverride();
		}
		if (StrUtil.isNotBlank(controllerTemplate)) {
			cb.template(controllerTemplate);
		}
		cb.build();
		return config;
	}

	/**
	 * Service 策略配置
	 *
	 * @param config                策略配置
	 * @param fileOverride          是否覆盖文件
	 * @param superClass            设置 service 接口父类
	 * @param superClazz            设置 service 接口父类,类名字符串
	 * @param superImplClass        设置 service 实现类父类
	 * @param superImplClazz        设置 service 实现类父类，类名字符串
	 * @param converterFileName     转换 service 接口文件名称
	 * @param converterImplFileName 转换 service 实现类文件名称
	 * @param format                格式化 service 接口文件名称
	 * @param formatImpl            格式化 service 实现类文件名称
	 * @return StrategyConfig
	 */
	public static StrategyConfig service(@NotNull StrategyConfig config, boolean fileOverride, String serviceTemplate, String serviceImplTemplate, Class<?> superClass,
										 String superClazz, Class<?> superImplClass, String superImplClazz, ConverterFileName converterFileName,
										 ConverterFileName converterImplFileName, String format, String formatImpl) {
		Service.Builder sb = config.serviceBuilder();
		// 1.设置接口父类
		if (StrUtil.isNotBlank(superClazz)) {
			sb.superServiceClass(superClazz);
		}
		if (superClass != null) {
			sb.superServiceClass(superClass);
		}
		// 2.设置实现类父类
		if (StrUtil.isNotBlank(superImplClazz)) {
			sb.superServiceImplClass(superImplClazz);
		}
		if (superImplClass != null) {
			sb.superServiceImplClass(superImplClass);
		}
		// 3.转换接口和实现类文件名称
		if (converterFileName != null) {
			sb.convertServiceFileName(converterFileName);
		}
		if (converterImplFileName != null) {
			sb.convertServiceImplFileName(converterImplFileName);
		}
		// 4.格式化接口和实现类文件名称
		if (StrUtil.isNotBlank(format)) {
			sb.formatServiceFileName(format);
		}
		if (StrUtil.isNotBlank(formatImpl)) {
			sb.formatServiceImplFileName(formatImpl);
		}
		if (fileOverride) {
			sb.enableFileOverride();
		}
		if (StrUtil.isNotBlank(serviceTemplate)) {
			sb.serviceTemplate(serviceTemplate);
		}
		if (StrUtil.isNotBlank(serviceImplTemplate)) {
			sb.serviceImplTemplate(serviceImplTemplate);
		}
		sb.build();
		return config;
	}

	/**
	 * Mapper 策略配置
	 *
	 * @param config                 策略配置
	 * @param fileOverride           是否覆盖文件
	 * @param superClass             设置父类
	 * @param superClazz             设置父类，类名字符串
	 * @param enableMapperAnnotation 是否开启 @Mapper 注解 默认值:false
	 * @param enableBaseResultMap    是否启用 BaseResultMap 生成 默认值:false
	 * @param enableBaseColumnList   是否启用 BaseColumnList 默认值:false
	 * @param cache                  设置缓存实现类
	 * @param convertMapperFileName  转换 mapper 类文件名称
	 * @param convertXmlFileName     转换 xml 文件名称
	 * @param formatMapperFileName   格式化 mapper 文件名称
	 * @param formatXmlFileName      格式化 xml 实现类文件名称
	 * @return StrategyConfig
	 */
	public static StrategyConfig mapper(@NotNull StrategyConfig config, boolean fileOverride, String mapperTemplate, String xmlTemplate, Class<?> superClass,
										String superClazz, boolean enableMapperAnnotation, boolean enableBaseResultMap, boolean enableBaseColumnList,
										Class<? extends Cache> cache, ConverterFileName convertMapperFileName, ConverterFileName convertXmlFileName,
										String formatMapperFileName, String formatXmlFileName) {
		Mapper.Builder mb = config.mapperBuilder();
		// 1.设置父类
		if (StrUtil.isNotBlank(superClazz)) {
			mb.superClass(superClazz);
		}
		if (superClass != null) {
			mb.superClass(superClass);
		}
		// 2.开启 @Mapper 注解
		if (enableMapperAnnotation) {
			mb.enableMapperAnnotation();
		}
		// 3.启用 BaseResultMap 生成
		if (enableBaseResultMap) {
			mb.enableBaseResultMap();
		}
		if (enableBaseColumnList) {
			mb.enableBaseColumnList();
		}
		if (cache != null) {
			mb.cache(cache);
		}
		// 3.转换Mapper和XML文件名称
		if (convertMapperFileName != null) {
			mb.convertMapperFileName(convertMapperFileName);
		}
		if (convertXmlFileName != null) {
			mb.convertXmlFileName(convertXmlFileName);
		}
		// 4.格式化Mapper和XML文件名称
		if (StrUtil.isNotBlank(formatMapperFileName)) {
			mb.formatMapperFileName(formatMapperFileName);
		}
		if (StrUtil.isNotBlank(formatXmlFileName)) {
			mb.formatXmlFileName(formatXmlFileName);
		}
		if (fileOverride) {
			mb.enableFileOverride();
		}
		if (StrUtil.isNotBlank(mapperTemplate)) {
			mb.mapperTemplate(mapperTemplate);
		}
		if (StrUtil.isNotBlank(xmlTemplate)) {
			mb.mapperXmlTemplate(xmlTemplate);
		}
		mb.build();
		return config;
	}

	/**
	 * 快速策略配置
	 *
	 * @param fileOverride          是否覆盖文件
	 * @param includes              包含表名
	 * @param exludes               排除表名
	 * @param fills                 自动填充字段
	 * @param controllerSuperClass  控制接口父类
	 * @param serviceSuperClass     业务接口父类
	 * @param serviceImplSuperClass 业务实现类父类
	 * @param mapperSuperClass      Mapper接口父类
	 * @param entitySupperClass     实体父类
	 * @param cacheClass            缓存实现类
	 * @return StrategyConfig
	 */
	public static StrategyConfig config(boolean fileOverride, List<String> includes, List<String> exludes,
										List<IFill> fills, Class<?> controllerSuperClass, Class<?> serviceSuperClass, Class<?> serviceImplSuperClass,
										Class<?> mapperSuperClass, Class<?> entitySupperClass, Class<? extends Cache> cacheClass) {

		// 1.装配策略配置
		StrategyConfig config =
			strategy(false, false, true, false, null, null, includes, exludes, null, null, null, null);
		// 2.实体默认配置
		entity(config, fileOverride, "/flines/entity.java", null, entitySupperClass, null, false, false, true, true, false, true, true,
			"version", "version", "delete_flag", "deleteFlag", NamingStrategy.underline_to_camel,
			NamingStrategy.underline_to_camel, null, null, fills, IdType.AUTO, null, "%s");
		// 3.控制层默认配置
		controller(config, fileOverride, "/flines/controller.java", controllerSuperClass, null, false, true, null, "%sController");
		// 4.业务层默认配置
		service(config, fileOverride, "/flines/fservice.java", "/flines/fserviceImpl.java", serviceSuperClass, null, serviceImplSuperClass, null, null, null, "I%sService",
			"%sServiceImpl");
		// 5.映射层模式配置
		mapper(config, fileOverride, "/flines/fmapper.java", "/flines/mapper.xml", mapperSuperClass, null, true, true, false, cacheClass, null, null, "%sDao", "%s");

		return config;
	}
}
