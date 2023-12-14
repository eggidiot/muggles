package com.muggles.fun.tools.core.bean;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 方法增强工具
 */
@Slf4j
@UtilityClass
public class MethodEnhanceUtil {

    /**
     * 方法增强内置工厂
     */
    @Slf4j
    @Data
    @Accessors(chain = true)
    @RequiredArgsConstructor
    public static class CglibProxyFactory implements MethodInterceptor {
        /**
         * 维护目标对象
         */
        private final Object target;
        /**
         * 方法前置拦截器集合
         */
        private Map<Method, IEnhanceBefore> beforeMap = MapUtil.newHashMap();
        /**
         * 方法后置拦截器集合
         */
        private Map<Method, IEnhanceAfter> afterMap = MapUtil.newHashMap();

        /**
         * 获得目标类的代理对象
         *
         * @return
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
            Assert.isTrue(before == null || before.before(method, args), () -> new IllegalStateException("前置处理异常"));
            if (after != null) {
                return after.after(method, args, method.invoke(target, args));
            }
            return method.invoke(target, args);
        }

        /**
         * 增强某个方法
         *
         * @param method 指定方法
         * @param before 前置处理器
         * @param after
         * @return
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
            Method m = findMethod(getTarget(), method);
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
         * @param <T>
         * @return
         */
        public <T> T get() {
            return (T) getObject();
        }
    }

    /**
     * 返回增强对象，默认方式不指定方法与增强方式，会给类声明的所有方法添加默认前后置处理器
     *
     * @param t   目标对象
     * @param <T> 泛型
     * @return 增强对象
     */
    public <T> T enhancer(T t) {
        CglibProxyFactory factory = new CglibProxyFactory(t);
        Method[] methods = t.getClass().getDeclaredMethods();
        for (Method m: methods) {
            factory.enhance(m,(method, args) -> {
                    log.info("前置切面对象:{},方法:{},参数:{}",t,method.getName(),args);
                    return true;
                },(method,args,value)->{
                    log.info("前置切面对象:{},方法:{},参数:{},返回值:{}",t,method.getName(),args,value);
                    return value;
                }
            );
        }
        return factory.get();
    }
    /**
     * 返回增强对象
     *
     * @param t   目标对象
     * @param <T> 泛型
     * @return 增强对象
     */
    public <T> T enhancer(T t, String method, IEnhanceBefore before, IEnhanceAfter after) {
        CglibProxyFactory factory = new CglibProxyFactory(t);
        factory.enhance(method,before,after);
        return factory.get();
    }

    /**
     * 返回增强对象
     *
     * @param t   目标对象
     * @param <T> 泛型
     * @return 增强对象
     */
    public <T> T enhancer(T t, Method method, IEnhanceBefore before, IEnhanceAfter after) {
        CglibProxyFactory cglibProxyFactory = new CglibProxyFactory(t);
        return (T) cglibProxyFactory.getObject();
    }

    /**
     * 设置某个方法前置拦截，插入业务逻辑
     *
     * @param before
     * @param t
     * @param method
     * @param <T>
     * @return
     */
    public <T> T enhancerBefore(IEnhanceBefore before, T t, String method) {
        CglibProxyFactory cglibProxyFactory = new CglibProxyFactory(t);
        return (T) cglibProxyFactory.getObject();
    }

    /**
     * 设置某个方法前置拦截，插入业务逻辑
     *
     * @param before
     * @param t
     * @param method
     * @param <T>
     * @return
     */
    public <T> T enhancerBefore(IEnhanceBefore before, T t, Method method) {
        CglibProxyFactory cglibProxyFactory = new CglibProxyFactory(t);
        cglibProxyFactory.getBeforeMap().put(method, before);
        return (T) cglibProxyFactory.getObject();
    }

    /**
     * 设置某个方法后置拦截，插入业务逻辑
     *
     * @param t
     * @param method
     * @param after
     * @param <T>
     * @return
     */
    public <T> T enhancerAfter(T t, String method, IEnhanceAfter after) {
        CglibProxyFactory cglibProxyFactory = new CglibProxyFactory(t);
        return (T) cglibProxyFactory.getObject();
    }

    /**
     * 设置某个方法后置拦截，插入业务逻辑
     *
     * @param t
     * @param method
     * @param after
     * @param <T>
     * @return
     */
    public <T> T enhancerAfter(T t, Method method, IEnhanceAfter after) {
        CglibProxyFactory cglibProxyFactory = new CglibProxyFactory(t);
        return (T) cglibProxyFactory.getObject();
    }

    /**
     * 返回与方法名匹配的第一个方法，要正确使用，要求被查找的方法名没有被重写
     *
     * @param t      对象
     * @param method 方法
     * @param <T>    对象泛型
     * @return 目标方法
     */
    public <T> Method findMethod(T t, String method) {
        Assert.notNull(t, () -> new IllegalStateException("对象不能为Null"));
        return findMethod(t.getClass(), method);
    }

    /**
     * 返回与方法名匹配的第一个方法，要正确使用，要求被查找的方法名没有被重写
     *
     * @param tClass 指定类型
     * @param method 方法
     * @param <T>    对象泛型
     * @return 目标方法
     */
    public <T> Method findMethod(Class<T> tClass, String method) {
        Assert.notNull(tClass, () -> new IllegalStateException("指定class不能为null"));
        Method[] methods = tClass.getMethods();
        for (Method m : methods) {
            if (m.getName().equals(method)) {
                return m;
            }
        }
        return null;
    }

    /**
     * 根据方法名找对象方法
     *
     * @param t          对象
     * @param method     方法
     * @param paramTypes 参数类型，可变数组
     * @param <T>        对象泛型
     * @return 目标方法
     */
    public <T> Method findMethod(T t, String method, Class<?>... paramTypes) {
        Assert.notNull(t, () -> new IllegalStateException("对象不能为Null"));
        return findMethod(t.getClass(), method, paramTypes);
    }

    /**
     * 根据方法名找对象方法
     *
     * @param tClass     指定类型
     * @param method     方法
     * @param paramTypes 参数类型，可变数组
     * @param <T>        对象泛型
     * @return 目标方法
     */
    public <T> Method findMethod(Class<T> tClass, String method, Class<?>... paramTypes) {
        Assert.notNull(tClass, () -> new IllegalStateException("指定class不能为null"));
        try {
            return tClass.getMethod(method, paramTypes);
        } catch (NoSuchMethodException e) {
            log.error("获取指定方法出错:{}", e);
            return null;
        }
    }
}
