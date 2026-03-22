package com.muggles.fun.repo.mp.record.scene;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;

/**
 * 操作人记录，包含当前记录操作人信息
 */
public class OperatorRecord<T extends OperatorRecord<T>> extends LifecycleRecord<T> {
	/**
	 * 记录生成人的ID
	 */
	@TableField(value = "creator", fill = FieldFill.INSERT)
	@Getter
	protected Long creator;
	/**
	 * 最后一个更新人的ID
	 */
	@TableField(value = "updator", fill = FieldFill.INSERT_UPDATE)
	@Getter
	protected Long updator;

	/**
	 * 设置记录创建者ID
	 *
	 * @param creator	创建人
	 * @return	T
	 */
	public T setCreator(Long creator) {
		this.creator = creator;
		return (T) this;
	}

	/**
	 * 设置记录更新者ID
	 *
	 * @param updator	最后更新人
	 * @return	T
	 */
	public T setUpdator(Long updator) {
		this.updator = updator;
		return (T) this;
	}
}
