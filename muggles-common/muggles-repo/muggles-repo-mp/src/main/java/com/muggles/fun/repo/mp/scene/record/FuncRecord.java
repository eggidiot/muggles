package com.muggles.fun.repo.mp.scene.record;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Getter;

/**
 * 功能性模型，主要实现逻辑删除和乐观锁
 *
 * @param <T>
 */
public class FuncRecord<T extends FuncRecord<T>> extends IdRecord<T> {

	/**
	 * 版本号
	 */
	@TableField(value = "version", fill = FieldFill.INSERT)
	@Version
	@Getter
	protected Integer version;
	/**
	 * 逻辑删除标志，状态1表示记录被删除，0表示正常记录
	 */
	@TableField(value = "delete_flag", fill = FieldFill.INSERT)
	@TableLogic
	@Getter
	protected Integer deleteFlag;

	/**
	 * 当前实体乐观锁版本
	 *
	 * @param version 版本值
	 * @return
	 */
	public T setVersion(Integer version) {
		this.version = version;
		return (T) this;
	}

	/**
	 * 设置当前实体逻辑删除标志
	 *
	 * @param deleteFlag 删除标志
	 * @return
	 */
	public T setDeleteFlag(Integer deleteFlag) {
		this.deleteFlag = deleteFlag;
		return (T) this;
	}
}
