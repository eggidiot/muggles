package com.muggles.fun.repo.basic.converter;

import com.muggles.fun.basic.converter.IViewConverter;
import com.muggles.fun.basic.model.IMugglePage;
import com.muggles.fun.repo.basic.model.MugglePage;

/**
 * 带分页功能的视图类型转换器
 * @param <T>
 * @param <R>
 */
public interface IPageViewConverter<T, R> extends IViewConverter<T, R> {
    /**
     * 转化分页对象
     *
     * @param page 分页查询
     * @return IFlinePage<R>
     */
    default IMugglePage<R> applyPage(IMugglePage<T> page) {
        MugglePage<R> p = new MugglePage<R>().setCurrent(page.getCurrent()).setSize(page.getSize());
        p.setTotal(page.getTotal()).setPages(page.getPages());
        p.setRecords(applyList(page.getRecords()));
        return p;
    }
}
