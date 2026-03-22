package com.muggles.fun.repo.mp.record.scene;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 包含生命周期记录和功能性记录的属性
 */
public class LifecycleFuncRecord<T extends LifecycleFuncRecord<T>> extends FuncRecord<T> {
	/**
	 * 创建时间
	 */
	@TableField(value = "create_date", fill = FieldFill.INSERT)
	@Getter
	protected LocalDateTime createDate;
	/**
	 * 更新时间
	 */
	@TableField(value = "update_date", fill = FieldFill.INSERT_UPDATE)
	@Getter
	protected LocalDateTime updateDate;

	/**
	 * 当前实体创建时间
	 *
	 * @param createDate 时间值
	 * @return
	 */
	public T setCreateDate(LocalDateTime createDate) {
		this.createDate = createDate;
		return (T) this;
	}

	/**
	 * 当前实体更新时间
	 *
	 * @param updateDate 时间值
	 * @return
	 */
	public T setUpdateDate(LocalDateTime updateDate) {
		this.updateDate = updateDate;
		return (T) this;
	}
}
