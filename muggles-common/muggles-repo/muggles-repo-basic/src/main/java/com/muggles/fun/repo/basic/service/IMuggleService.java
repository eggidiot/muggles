package com.muggles.fun.repo.basic.service;

import com.muggles.fun.repo.basic.model.Muggle;

/**
 * 默认的muggle需要提供业务能力的service
 *
 * @param <T>
 */
public interface IMuggleService<T> extends ICommonService<T, Muggle<T>>{
}
