package com.muggles.fun.repo.mp.record;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Getter;

/**
 * 记录包含的最基本的Id字段，Id类型为long
 * activerecord模式实体，具备持久层能力
 *
 * @param <T>
 */
public class IdRecord<T extends IdRecord<T>> extends Model<T> {
	/**
	 * 主键
	 * 采用AUTO类型时会忽略插入Id的值
	 */
	@TableId(value = "id", type = IdType.AUTO)
	@Getter
	protected Long id;


	/**
	 * 设置主键
	 *
	 * @param id 主键值
	 * @return
	 */
	public T setId(Long id) {
		this.id = id;
		return (T) this;
	}

}
