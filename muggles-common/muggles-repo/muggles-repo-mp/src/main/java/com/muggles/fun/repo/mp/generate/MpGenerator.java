package com.muggles.fun.repo.mp.generate;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.IFill;
import com.baomidou.mybatisplus.generator.config.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.po.LikeTable;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.fill.Property;
import com.muggles.fun.basic.Constants;
import com.muggles.fun.repo.basic.service.IMuggleService;
import com.muggles.fun.repo.mp.mapper.CommonMapper;
import com.muggles.fun.repo.mp.scene.record.IdRecord;
import com.muggles.fun.repo.mp.service.MpServiceImpl;
import com.muggles.fun.repo.mp.template.MpTemplateEngine;
import org.apache.ibatis.cache.Cache;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;


public class MpGenerator {

	/**
	 * 生成表前缀
	 */
	List<String> prefixs = new ArrayList<>();
	/**
	 * 是否覆盖文件
	 */
	boolean fileOverride = true;

	/**
	 * 数据库表生成实体
	 *
	 * @param includes    当前库反射需要的表名
	 * @param excludes    当前库反射排除的表名
	 * @param url         数据库连接
	 * @param root        用户名
	 * @param password    密码
	 * @param output      输出目录
	 * @param author      作者
	 * @param packageName 父包名
	 * @param pathInfo    路径配置信息
	 * @return	boolean
	 */
	public boolean gen(List<String> includes, List<String> excludes, String url, String root, String password,
					   String output, String author, String packageName, Map<OutputFile, String> pathInfo) {
		return gen(includes, excludes, url, root, password, output, author, packageName, pathInfo, IdRecord.class);
	}

	/**
	 * 数据库表生成实体
	 *
	 * @param includes         当前库反射需要的表名
	 * @param excludes         当前库反射排除的表名
	 * @param url              数据库连接
	 * @param root             用户名
	 * @param password         密码
	 * @param output           输出目录
	 * @param author           作者
	 * @param packageName      父包名
	 * @param pathInfo         路径配置信息
	 * @param entitySuperClass 实体指定父类
	 * @return	boolean
	 */
	public boolean gen(List<String> includes, List<String> excludes, String url, String root, String password,
					   String output, String author, String packageName, Map<OutputFile, String> pathInfo, Class entitySuperClass) {
		Field[] fs = ReflectUtil.getFields(entitySuperClass);
		List<IFill> fills = getCommons(Arrays.stream(fs).map(Field::getName).collect(Collectors.toList()));
		return gen(includes, excludes, fills, url, root, password, output, author, packageName, pathInfo,
			entitySuperClass);
	}

	/**
	 * 数据库表生成实体
	 *
	 * @param includes         当前库反射需要的表名
	 * @param excludes         当前库反射排除的表名
	 * @param fills            默认填充公共字段
	 * @param url              数据库连接
	 * @param root             用户名
	 * @param password         密码
	 * @param output           输出目录
	 * @param author           作者
	 * @param packageName      父包名
	 * @param pathInfo         路径配置信息
	 * @param entitySuperClass 实体指定父类
	 * @return	boolean
	 */
	public boolean gen(List<String> includes, List<String> excludes, List<IFill> fills, String url, String root,
					   String password, String output, String author, String packageName, Map<OutputFile, String> pathInfo,
					   Class<?> entitySuperClass) {
		return gen(includes, excludes, fills, url, root, password, output, author, packageName, pathInfo,
			entitySuperClass, IMuggleService.class, MpServiceImpl.class, CommonMapper.class, null);
	}

	/**
	 * 数据库表生成实体
	 *
	 * @param includes              当前库反射需要的表名
	 * @param excludes              当前库反射排除的表名
	 * @param fills                 默认填充公共字段
	 * @param url                   数据库连接
	 * @param root                  用户名
	 * @param password              密码
	 * @param output                输出目录
	 * @param author                作者
	 * @param packageName           父包名
	 * @param pathInfo              路径配置信息
	 * @param entitySuperClass      实体指定父类
	 * @param serviceSuperClass     Service指定父类
	 * @param serviceImplSuperClass ServiceImpl实现类指定父类
	 * @param mapperSuperClass      Dao指定父类
	 * @return	boolean
	 */
	public boolean gen(List<String> includes, List<String> excludes, List<IFill> fills, String url, String root,
					   String password, String output, String author, String packageName, Map<OutputFile, String> pathInfo,
					   Class<?> entitySuperClass, Class<?> serviceSuperClass, Class<?> serviceImplSuperClass, Class<?> mapperSuperClass,
					   Class<?> controllerSuperClass) {
		AutoGenerator generator = new AutoGenerator(SqlGenerator.db(url, root, password))
			.global(
				SqlGenerator.globalAll(true, output, author, false, true, DateType.TIME_PACK, Constants.DATE_FORMAT))
			.template(
				SqlGenerator.templateAll("/flines/entity.java", "/flines/fservice.java", "/flines/fserviceImpl.java",
					"/flines/fmapper.java", "/flines/mapper.xml", "/flines/controller.java", false, null))
			.packageInfo(SqlGenerator.pack(packageName, pathInfo)).injection(customConfig())
			.strategy(config(fileOverride, includes, excludes, fills, controllerSuperClass, serviceSuperClass,
				serviceImplSuperClass, mapperSuperClass, entitySuperClass, null));
		generator.execute(new MpTemplateEngine());
		return true;
	}

	/**
	 * 数据库表生成实体
	 *
	 * @param includes         当前库反射需要的表名
	 * @param excludes         当前库反射排除的表名
	 * @param commonFields     公共字段
	 * @param url              数据库连接
	 * @param root             用户名
	 * @param password         密码
	 * @param output           输出目录
	 * @param author           作者
	 * @param packageName      父包名
	 * @param pathInfo         路径配置信息
	 * @param entitySuperClass 实体指定父类
	 * @return	boolean
	 */
	public boolean gen(List<String> includes, List<String> excludes, String[] commonFields, String url, String root,
					   String password, String output, String author, String packageName, Map<OutputFile, String> pathInfo,
					   Class<?> entitySuperClass) {
		List<IFill> fills = new ArrayList<>();
		if (ArrayUtil.isNotEmpty(commonFields)) {
			fills = Arrays.stream(commonFields).map(f -> new Property(f, FieldFill.INSERT))
				.collect(Collectors.toList());
		}
		return gen(includes, excludes, fills, url, root, password, output, author, packageName, pathInfo,
			entitySuperClass);
	}

	/**
	 * 数据库表生成实体
	 *
	 * @param url         数据库连接
	 * @param root        用户名
	 * @param password    密码
	 * @param output      输出目录
	 * @param author      作者
	 * @param packageName 父包名
	 * @param pathInfo    路径配置信息
	 * @return	boolean
	 */
	public boolean gen(String url, String root, String password, String output, String author, String packageName,
					   Map<OutputFile, String> pathInfo) {
		List<String> excludes = new ArrayList<String>() {
			{
				add("");
			}
		};
		return gen(null, excludes, url, root, password, output, author, packageName, pathInfo);
	}

	/**
	 * 数据库表生成实体,默认将JAVA相关文件输出到当前项目的src/main/java中，XML文件输出/src/main/resources/$packagename/repo/persistence下
	 *
	 * @param url         数据库连接
	 * @param root        用户名
	 * @param password    密码
	 * @param author      作者
	 * @param packageName 父包名
	 * @return	boolean
	 */
	public boolean gen(String url, String root, String password, String author, String packageName) {
		return gen(url, root, password, author, packageName, null);
	}

	/**
	 * 数据库表生成实体,默认将JAVA相关文件输出到当前项目的src/main/java中，XML文件输出/src/main/resources/$packagename/repo/persistence下
	 *
	 * @param url         数据库连接
	 * @param root        用户名
	 * @param password    密码
	 * @param author      作者
	 * @param packageName 父包名
	 * @param moduleName  模块相对根模块路径名称
	 * @return	boolean
	 */
	public boolean gen(String url, String root, String password, String author, String packageName, String moduleName) {
		return gen(null, null, url, root, password, author, packageName, moduleName);
	}

	/**
	 * 数据库表生成实体,默认将JAVA相关文件输出到当前项目的src/main/java中，XML文件输出/src/main/resources/$packagename/repo/persistence下
	 *
	 * @param includes    当前库反射需要的表名
	 * @param excludes    当前库反射排除的表名
	 * @param url         数据库连接
	 * @param root        用户名
	 * @param password    密码
	 * @param author      作者
	 * @param packageName 父包名
	 * @param moduleName  模块相对根模块路径名称
	 * @return	boolean
	 */
	public boolean gen(List<String> includes, List<String> excludes, String url, String root, String password,
					   String author, String packageName, String moduleName) {
		return gen(includes, excludes, url, root, password, author, packageName, moduleName, IdRecord.class);
	}

	/**
	 * 数据库表生成实体,默认将JAVA相关文件输出到当前项目的src/main/java中，XML文件输出/src/main/resources/$packagename/repo/persistence下
	 *
	 * @param includes         当前库反射需要的表名
	 * @param excludes         当前库反射排除的表名
	 * @param url              数据库连接
	 * @param root             用户名
	 * @param password         密码
	 * @param author           作者
	 * @param packageName      父包名
	 * @param moduleName       模块相对根模块路径名称
	 * @param entitySuperClass 实体指定父类
	 * @return	boolean
	 */
	public boolean gen(List<String> includes, List<String> excludes, String url, String root, String password,
					   String author, String packageName, String moduleName, Class entitySuperClass) {
		return gen(includes, excludes, url, root, password, output(moduleName), author, packageName,
			Collections.singletonMap(OutputFile.xml, path(moduleName, packageName)), entitySuperClass);
	}

	/**
	 * 数据库表生成实体,默认将JAVA相关文件输出到当前项目的src/main/java中，XML文件输出/src/main/resources/$packagename/repo/persistence下
	 *
	 * @param includes         当前库反射需要的表名
	 * @param excludes         当前库反射排除的表名
	 * @param commonFields     公共字段
	 * @param url              数据库连接
	 * @param root             用户名
	 * @param password         密码
	 * @param author           作者
	 * @param packageName      父包名
	 * @param moduleName       模块相对根模块路径名称
	 * @param entitySuperClass 实体指定父类
	 * @return	boolean
	 */
	public boolean gen(List<String> includes, List<String> excludes, String[] commonFields, String url, String root,
					   String password, String author, String packageName, String moduleName, Class entitySuperClass) {
		return gen(includes, excludes, commonFields, url, root, password, output(moduleName), author, packageName,
			Collections.singletonMap(OutputFile.xml, path(moduleName, packageName)), entitySuperClass);
	}

	/**
	 * 数据库表生成实体,默认将JAVA相关文件输出到当前项目的src/main/java中，XML文件输出/src/main/resources/$packagename/repo/persistence下
	 *
	 * @param includes         当前库反射需要的表名
	 * @param excludes         当前库反射排除的表名
	 * @param fills            默认填充公共字段
	 * @param url              数据库连接
	 * @param root             用户名
	 * @param password         密码
	 * @param author           作者
	 * @param packageName      父包名
	 * @param moduleName       模块相对根模块路径名称
	 * @param entitySuperClass 实体指定父类
	 * @return	boolean
	 */
	public boolean gen(List<String> includes, List<String> excludes, List<IFill> fills, String url, String root,
					   String password, String author, String packageName, String moduleName, Class entitySuperClass) {
		return gen(includes, excludes, fills, url, root, password, output(moduleName), author, packageName,
			Collections.singletonMap(OutputFile.xml, path(moduleName, packageName)), entitySuperClass);
	}

	/**
	 * 根据模块名称和报名获取文件路径
	 *
	 * @param moduleName  模块名称
	 * @param packageName 包名
	 * @return	String
	 */
	private String path(String moduleName, String packageName) {
		String path = "src/main/resources/" + packageName.replaceAll("\\.", "/") + "/repo/persistence";
		if (StrUtil.isNotBlank(moduleName)) {
			path = moduleName + "/" + path;
		}
		return path;
	}

	/**
	 * 根据模块名称h获取输出路径
	 *
	 * @param moduleName 模块路径
	 * @return	String
	 */
	private String output(String moduleName) {
		String output = "src/main/java";
		if (StrUtil.isNotBlank(moduleName)) {
			output = moduleName + "/" + output;
		}
		return output;
	}

	/**
	 * 自定义公共服务类
	 *
	 * @return	InjectionConfig
	 */
	InjectionConfig customConfig() {
		Map<String, String> customMap = new HashMap<>();
		customMap.put(MpTemplateEngine.customService, "/flines/customService.java.ftl");
		customMap.put(MpTemplateEngine.customImplService, "/flines/customServiceImpl.java.ftl");
		customMap.put(MpTemplateEngine.bizService, "/flines/bizService.java.ftl");
		customMap.put(MpTemplateEngine.AbstractBizService, "/flines/AbstractBizService.java.ftl");
		customMap.put(MpTemplateEngine.customMapper, "/flines/customMapper.java.ftl");
		customMap.put(MpTemplateEngine.recordMask, "/flines/recordMask.java.ftl");
		return new InjectionConfig.Builder().beforeOutputFile((tableInfo, objectMap) -> {
				System.out.println("tableInfo: " + tableInfo.getEntityName() + " objectMap: " + objectMap.size());
			}).customMap(Collections.singletonMap("kay", "kay"))
			.customFile(customMap)
			.build();
	}

	/**
	 * 设置公共字段策略
	 *
	 * @return	Map<String, FieldFill>
	 */
	protected Map<String, FieldFill> properties() {
		return new HashMap<String, FieldFill>() {
			{
				put("name", FieldFill.DEFAULT);
				put("code", FieldFill.DEFAULT);
				put("deleteFlag", FieldFill.INSERT);
				put("status", FieldFill.INSERT);
				put("updateTime", FieldFill.INSERT);
				put("updateDate", FieldFill.INSERT_UPDATE);
				put("createDate", FieldFill.INSERT);
				put("version", FieldFill.INSERT);
				put("memo", FieldFill.DEFAULT);
				put("creator", FieldFill.INSERT);
				put("updator", FieldFill.INSERT);
			}
		};
	}

	/**
	 * 获取支持的公有字段
	 *
	 * @param commons	公共字段列表
	 * @return	List<IFill>
	 */
	List<IFill> getCommons(List<String> commons) {
		Map<String, FieldFill> properties = properties();
		return properties.entrySet().stream().filter(it -> commons.contains(it.getKey()))
			.map(it -> new Property(it.getKey(), it.getValue())).collect(Collectors.toList());
	}

	/**
	 * 设置表前缀
	 *
	 * @param prefixs	公共前缀
	 * @return	MpGenerator
	 */
	public MpGenerator prefixs(List<String> prefixs) {
		this.prefixs = prefixs;
		return this;
	}

	/**
	 * 设置文件是否可以覆盖
	 *
	 * @param fileOverride	是否覆盖旧文件
	 * @return	MpGenerator
	 */
	public MpGenerator fileOverride(boolean fileOverride) {
		this.fileOverride = fileOverride;
		return this;
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
	 * @return	StrategyConfig
	 */
	public StrategyConfig config(boolean fileOverride, List<String> includes, List<String> exludes, List<IFill> fills,
								 Class<?> controllerSuperClass, Class<?> serviceSuperClass, Class<?> serviceImplSuperClass,
								 Class<?> mapperSuperClass, Class<?> entitySupperClass, Class<? extends Cache> cacheClass) {

		LikeTable like = null;
		boolean sqlFilter = true;
		if (CollUtil.isEmpty(includes) && CollUtil.isEmpty(exludes) && CollUtil.isNotEmpty(prefixs)) {
			like = new LikeTable(prefixs.getFirst());
			sqlFilter = false;
		}
		// 1.装配策略配置
		StrategyConfig config =
				SqlGenerator.strategy(false, false, sqlFilter, false, like, null, includes, exludes, prefixs, null, null, null);
		// 2.实体默认配置
		SqlGenerator.entity(config, fileOverride, "/flines/entity.java", null, entitySupperClass, null, false, false, true, true, false, true, true,
			"version", "version", "delete_flag", "deleteFlag", NamingStrategy.underline_to_camel,
			NamingStrategy.underline_to_camel, null, null, fills, IdType.AUTO, null, "%s");
		// 3.控制层默认配置
		SqlGenerator.controller(config, fileOverride, "/flines/controller.java", controllerSuperClass, null, true, true, null, "%sController");
		// 4.业务层默认配置
		SqlGenerator.service(config, fileOverride, "/flines/fservice.java", "/flines/fserviceImpl.java", serviceSuperClass, null, serviceImplSuperClass, null, null, null, "I%sService",
			"%sServiceImpl");
		// 5.映射层模式配置
		SqlGenerator.mapper(config, fileOverride, "/flines/fmapper.java", "/flines/mapper.xml", mapperSuperClass, null, true, true, false, cacheClass, null, null, "%sDao", "%s");

		return config;
	}
}
