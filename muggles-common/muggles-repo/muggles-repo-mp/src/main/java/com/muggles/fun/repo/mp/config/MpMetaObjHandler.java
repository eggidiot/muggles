package com.muggles.fun.repo.mp.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.muggles.fun.basic.Constants;
import com.muggles.fun.core.context.UserContext;
import com.muggles.fun.repo.mp.record.IdRecord;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

/**
 * 处理公共字段
 */
@Data
@Accessors(chain = true)
public class MpMetaObjHandler implements MetaObjectHandler {

	/**
	 * 插入默认值填充
	 * @param metaObject 元对象
	 */
	@Override
	public void insertFill(MetaObject metaObject) {
		//1.创建对象默认设置版本为1
		Object version = getFieldValByName("version", metaObject);
		if (null == version && metaObject.hasSetter("version")) {
			strictInsertFill(metaObject, "version", Integer.class, Constants.DEFAULT_OPT);
		}
		//2.创建对象设置默认状态为1
		Object status = getFieldValByName("status", metaObject);
		if (null == status && metaObject.hasSetter("status")) {
			strictInsertFill(metaObject, "status", Integer.class, Constants.DEFAULT_OPT);
		}
		//3.创建对象设置默认删除标记为删除默认值
		Object deleteFlag = getFieldValByName("deleteFlag", metaObject);
		if (null == deleteFlag && metaObject.hasSetter("deleteFlag")) {
			strictInsertFill(metaObject, "deleteFlag", Integer.class, Constants.INIT);
		}

		//4.创建对象设置默认创建时间为系统当前时间
		Object tVal = getFieldValByName("createDate", metaObject);
		LocalDateTime t = LocalDateTime.now();
		if (null == tVal && metaObject.hasSetter("createDate")) {
			strictInsertFill(metaObject, "createDate", LocalDateTime.class, t);
		}
		//5.创建对象设置默认更新时间为系统当前时间
		tVal = getFieldValByName("updateDate", metaObject);
		if (null == tVal && metaObject.hasSetter("updateDate")) {
			strictInsertFill(metaObject, "updateDate", LocalDateTime.class, t);
		}

		//6.设置创建人ID
		IdRecord<?> record = UserContext.getUser();
		Long userId = record == null ? null : record.getId();
		if (metaObject.hasSetter("creator")) {
			strictInsertFill(metaObject, "creator", Long.class, userId);
		}

		//7.设置更新人ID
		if (metaObject.hasSetter("updator")) {
			strictInsertFill(metaObject, "updator", Long.class, userId);
		}
	}

	/**
	 * 更新默认值填充
	 * @param metaObject 元对象
	 */
	@Override
	public void updateFill(MetaObject metaObject) {
		//1.创建对象设置默认创建时间为系统当前时间
		Object tVal = getFieldValByName("updateDate", metaObject);
		if (null == tVal && metaObject.hasSetter("updateDate")) {
			strictUpdateFill(metaObject, "updateDate", LocalDateTime.class, LocalDateTime.now());
		}

		//2.设置更新人ID
		IdRecord<?> record = UserContext.getUser();
		Long userId = record == null ? null : record.getId();
		if (metaObject.hasSetter("updator")) {
			strictInsertFill(metaObject, "updator", Long.class, userId);
		}
	}
}
