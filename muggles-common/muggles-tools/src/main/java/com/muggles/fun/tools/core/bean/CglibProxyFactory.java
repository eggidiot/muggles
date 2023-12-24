package com.muggles.fun.tools.core.bean;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 方法增强内置工厂
 */
@Slf4j
@Data
@Accessors(chain = true)
@RequiredArgsConstructor
public class CglibProxyFactory implements MethodInterceptor {
    /**
     * 维护目标对象
     */
    private final Object target;
    /**
     * 方法前置拦截器集合
     */
    private Map<Method, IEnhanceBefore> beforeMap = MapUtil.newConcurrentHashMap();
    /**
     * 方法后置拦截器集合
     */
    private Map<Method, IEnhanceAfter> afterMap = MapUtil.newConcurrentHashMap();
    /**
     * 当前操作方法，满足链式API调用设计成员属性
     */
    private Method currentHolder;

    /**
     * 获得目标类的代理对象
     *
     * @return  Object
     */
    public Object getObject() {
        //1.工具类
        Enhancer enhancer = new Enhancer();
        //2.设置父类
        enhancer.setSuperclass(target.getClass());
        //3.设置回调
        enhancer.setCallback(this);
        //4.创建代理类
        return enhancer.create();
    }

    /**
     * All generated proxied methods call this method instead of the original method.
     * The original method may either be invoked by normal reflection using the Method object,
     * or by using the MethodProxy (faster).
     *
     * @param obj    "this", the enhanced object
     * @param method intercepted Method
     * @param args   argument array; primitive types are wrapped
     * @param proxy  used to invoke super (non-intercepted method); may be called
     *               as many times as needed
     * @return any value compatible with the signature of the proxied method. Method returning void will ignore this value.
     * @throws Throwable any exception may be thrown; if so, super method will not be invoked
     * @see MethodProxy
     */
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        //1.获取对应的前置拦截器
        IEnhanceBefore before = beforeMap.get(method);
        //2.获取对应的后置拦截器
        IEnhanceAfter after = afterMap.get(method);
        //3.拦截器处理逻辑
        Assert.isTrue(before == null || before.before(obj, method, args), () -> new IllegalStateException("前置处理异常"));
        if (after != null) {
            return after.after(obj, method, args, method.invoke(target, args));
        }
        return method.invoke(target, args);
    }

    /**
     * 增强某个方法
     *
     * @param method 指定方法
     * @param before 前置处理器
     * @param after  后置处理器
     * @return 代理对象
     */
    public CglibProxyFactory enhance(Method method, IEnhanceBefore before, IEnhanceAfter after) {
        Assert.notNull(method, () -> new IllegalStateException("指定方法不存在"));
        if (before != null) {
            getBeforeMap().put(method, before);
        }
        if (after != null) {
            getAfterMap().put(method, after);
        }
        return this;
    }

    /**
     * 增强某个方法
     *
     * @param method 指定方法名称
     * @param before 前置处理器
     * @param after  后置处理器
     * @return 代理工厂对象
     */
    public CglibProxyFactory enhance(String method, IEnhanceBefore before, IEnhanceAfter after) {
        Method m = MethodEnhanceUtil.findMethod(getTarget(), method);
        if (m != null) {
            enhance(m, before, after);
        }
        return this;
    }

    /**
     * 前置增强某个方法
     *
     * @param method 指定方法
     * @param before 前置处理器
     * @return 代理工厂对象
     */
    public CglibProxyFactory enhanceBefore(Method method, IEnhanceBefore before) {
        return enhance(method, before, null);
    }

    /**
     * 增强某个方法
     *
     * @param method 指定方法
     * @param before 前置处理器
     * @return 代理工厂对象
     */
    public CglibProxyFactory enhanceBefore(String method, IEnhanceBefore before) {
        return enhance(method, before, null);
    }

    /**
     * 后置增强某个方法
     *
     * @param method 指定方法
     * @param after  后置处理器
     * @return 代理工厂对象
     */
    public CglibProxyFactory enhanceAfter(Method method, IEnhanceAfter after) {
        return enhance(method, null, after);
    }

    /**
     * 后置增强某个方法
     *
     * @param method 指定方法
     * @param after  后置处理器
     * @return 代理工厂对象
     */
    public CglibProxyFactory enhanceAfter(String method, IEnhanceAfter after) {
        return enhance(method, null, after);
    }

    /**
     * 返回增强对象
     *
     * @param <T>   泛型
     * @return      增强后对象
     */
    public <T> T get() {
        return (T) getObject();
    }
}
