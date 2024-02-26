package com.muggles.fun.repo.mp.service;

import com.muggles.fun.repo.basic.service.IMuggleService;
import com.muggles.fun.repo.mp.mapper.CommonMapper;

public class MpServiceImpl<M extends CommonMapper<T>, T> extends CommonServiceImpl<M, T> implements IMuggleService<T> {
}
