package com.muggles.fun.core.handler.view;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReflectUtil;
import com.muggles.fun.basic.anno.ViewModel;
import com.muggles.fun.basic.converter.IViewConverter;
import com.muggles.fun.basic.exception.MugglesBizException;
import com.muggles.fun.basic.handler.IValueHandle;
import com.muggles.fun.basic.model.IMugglePage;
import com.muggles.fun.tools.core.spel.SpelUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * 视图模型处理器，负责处理视图模型转换
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ViewModelhandler extends AbstractViewModelHandler {

    /**
     * 视图对象注解
     */
    ViewModel toVo;

    /**
     * 转换方法
     * @param o the function argument
     * @return  Object
     */
    @Override
    public Object apply(Object o) {
        toVo = (ViewModel) getAnno();
        assert toVo != null;
        Class<?> converterClass = toVo.converter();
        Assert.notEquals(converterClass, IViewConverter.class,()->new MugglesBizException("没有设置正确的视图转换器"));

        Object data = SpelUtil.dataInfo(toVo.dataKey(), o);

        //1.通过转换器转换视图对象
        IViewConverter<?,?> func = (IViewConverter<?,?>) ReflectUtil.newInstance(converterClass);
        return convert(data, toVo.dataKey(), func);
    }

    /**
     * 返回视图转换以后的对象
     * @param data		原始对象
     * @param spel		spel表达式
     * @param converter	转换器
     * @return	Object
     */
    Object convert(Object data,String spel, IViewConverter converter) {
        if (List.class.isAssignableFrom(data.getClass())) {
            return SpelUtil.wrapperInfo(spel, data, converter.applyList((List) data));
        } else if (IMugglePage.class.isAssignableFrom(data.getClass())) {
            return SpelUtil.wrapperInfo(spel, data, converter.applyPage((IMugglePage) data));
        } else {
            return SpelUtil.wrapperInfo(spel, data, converter.apply(data));
        }
    }

    /**
     * 获取当前处理器对应的注解
     * @return  Class<? extends Annotation>
     */
    @Override
    public Class<? extends Annotation> getAnnotation() {
        return ViewModel.class;
    }
}
