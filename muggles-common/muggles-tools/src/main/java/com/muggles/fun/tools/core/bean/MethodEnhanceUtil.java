package com.muggles.fun.tools.core.bean;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.map.MapUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.List;
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
        private Map<Method, EnhanceChain> beforeMap = MapUtil.newConcurrentHashMap();
        /**
         * 方法后置拦截器集合
         */
        private Map<Method, EnhanceChain> afterMap = MapUtil.newConcurrentHashMap();
        /**
         * 当前操作方法，满足链式API调用设计成员属性
         */
        private Method currentHolder;

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
            Assert.isTrue(before == null || before.before(obj,method, args), () -> new IllegalStateException("前置处理异常"));
            if (after != null) {
                return after.after(obj,method, args, method.invoke(target, args));
            }
            return method.invoke(target, args);
        }

        /**
         * 增强某个方法
         *
         * @param method    指定方法
         * @param before    前置处理器
         * @param after     后置处理器
         * @return          代理对象
         */
        public CglibProxyFactory enhance(Method method, IEnhanceBefore before, IEnhanceAfter after) {
            Assert.notNull(method, () -> new IllegalStateException("指定方法不存在"));
            if (before != null) {
                put(method,before);
            }
            if (after != null) {
                put(method, after);
            }
            return this;
        }

        /**
         * 设置前置拦截器方法
         * @param method    拦截的方法对象
         * @param before    前置拦截器
         * @return          代理对象
         */
        public CglibProxyFactory put(Method method,IEnhanceBefore before){
            return put(method,before,Integer.MAX_VALUE);
        }

        /**
         * 设置前置拦截器方法
         * @param method    拦截的方法对象
         * @param before    前置拦截器
         * @param index     插入位置
         * @return          代理对象
         */
        public CglibProxyFactory put(Method method,IEnhanceBefore before,int index){
            EnhanceChain beChain = beforeMap.get(method);
            if (beChain == null) {
                beChain = new EnhanceChain();
                beforeMap.put(method,beChain);
            }
            beChain.put(before,index);
            return this;
        }

        /**
         * 设置后置拦截器方法
         * @param method    拦截的方法对象
         * @param after     后置拦截器
         * @return          代理对象
         */
        public CglibProxyFactory put(Method method,IEnhanceAfter after){
            return put(method,after,Integer.MAX_VALUE);
        }

        /**
         * 设置后置拦截器方法
         * @param method    拦截的方法对象
         * @param after     后置处理器
         * @param index     插入位置
         * @return          代理对象
         */
        public CglibProxyFactory put(Method method,IEnhanceAfter after,int index){
            EnhanceChain afterChain = afterMap.get(method);
            if (afterChain == null) {
                afterChain = new EnhanceChain();
                afterMap.put(method,afterChain);
            }
            afterChain.put(after,index);
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
     * 链条形过滤器
     */
    @Data
    @Accessors(chain = true)
    static class EnhanceChain implements IEnhanceBefore,IEnhanceAfter{
        /**
         * 前置过滤器链
         */
        List<IEnhanceBefore> beforeChain = CollUtil.newArrayList();
        /**
         * 后置过滤器链
         */
        List<IEnhanceAfter> afterChain = CollUtil.newArrayList();
        /**
         * 返回值拦截
         *
         * @param obj         当前this对象
         * @param method      执行方法
         * @param args        执行参数
         * @param returnValue 返回值
         * @return 方法返回值, 建议返回原值
         */
        @Override
        public Object after(Object obj, Method method, Object[] args, Object returnValue) {
            if (CollUtil.isNotEmpty(afterChain)) {
                //1.此处体现半责任链方式，每个节点处理返回值会往下传递
                for (IEnhanceAfter after:afterChain) {
                    if (after != null) {
                        returnValue = after.after(obj,method,args,returnValue);
                    }
                }
            }
            return returnValue;
        }

        /**
         * 执行拦截逻辑
         *
         * @param obj    当前this对象
         * @param method 执行方法
         * @param args   执行参数
         * @return 方法返回值, 建议返回原值
         */
        @Override
        public boolean before(Object obj, Method method, Object[] args) {
            if (CollUtil.isNotEmpty(beforeChain)) {
                for (IEnhanceBefore before:beforeChain) {
                    if (before != null && !before.before(obj,method,args)) {
                        return false;
                    }
                }
            }
            return true;
        }

        /**
         * 在指定位置插入前置处理器
         * @param before    前置处理器
         * @param index     插入位置
         * @return
         */
        public EnhanceChain put(IEnhanceBefore before,int index) {
            if (index < 0) {
                index = 0;
            }
            if (index > beforeChain.size()) {
                index = beforeChain.size();
            }
            beforeChain.add(index,before);
            return this;
        }
        /**
         * 在指定位置插入前置处理器
         * @param before    前置处理器
         * @return
         */
        public EnhanceChain put(IEnhanceBefore before) {
            beforeChain.add(before);
            return this;
        }

        /**
         * 在指定位置插入前置处理器
         * @param after     后置处理器
         * @param index     插入位置
         * @return
         */
        public EnhanceChain put(IEnhanceAfter after,int index) {
            if (index < 0) {
                index = 0;
            }
            if (index > afterChain.size()) {
                index = afterChain.size();
            }
            afterChain.add(index,after);
            return this;
        }

        /**
         * 在指定位置插入前置处理器
         * @param after     后置处理器
         * @return
         */
        public EnhanceChain put(IEnhanceAfter after) {
            afterChain.add(after);
            return this;
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
            factory.enhance(m,(obj,method, args) -> {
                    log.info("前置切面对象:{},方法:{},参数:{}",t,method.getName(),args);
                    return true;
                },(obj,method,args,value)->{
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
        Method m = findMethod(t,method);
        return enhancer(t,m,before,after);
    }

    /**
     * 返回增强对象
     *
     * @param t   目标对象
     * @param <T> 泛型
     * @return 增强对象
     */
    public <T> T enhancer(T t, Method method, IEnhanceBefore before, IEnhanceAfter after) {
        Assert.notNull(t,()->new IllegalStateException("指定对象不能为Null"));
        Assert.notNull(t,()->new IllegalStateException("被增强的指定方法不能为Null"));
        CglibProxyFactory factory = new CglibProxyFactory(t);
        factory.enhance(method,before,after);
        return factory.get();
    }

    /**
     * 设置某个方法前置拦截，插入业务逻辑
     *
     * @param t         增强对象
     * @param method    增强方法
     * @param before    前置处理器
     * @param <T>       泛型
     * @return          增强对象
     */
    public <T> T enhancerBefore(T t, String method,IEnhanceBefore before) {
        return enhancer(t,method,before,null);
    }

    /**
     * 设置某个方法前置拦截，插入业务逻辑
     *
     * @param t         增强对象
     * @param method    增强方法
     * @param before    前置处理器
     * @param <T>       泛型
     * @return          增强对象
     */
    public <T> T enhancerBefore(T t, Method method,IEnhanceBefore before) {
        return enhancer(t,method,before,null);
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
        return enhancer(t,method,null,after);
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
        return enhancer(t,method,null,after);
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
            return null;
        }
    }

    /**
     * 链式调用holder
     */
    public static class HookHolder {
        /**
         * 弱引用
         */
        WeakReference<CglibProxyFactory> factory;
        /**
         * 方法引用
         */
        WeakReference<Method> method;
        /**
         * 构造器
         * @param factory
         */
        public HookHolder(CglibProxyFactory factory,Method method) {
            this.factory = new WeakReference<>(factory);
            this.method = new WeakReference<>(method);
        }

        /**
         * 构造器
         * @param factory
         */
        public HookHolder(CglibProxyFactory factory) {
            this.factory = new WeakReference<>(factory);
        }

        /**
         * 设置劫持方法对象
         * @param method    被劫持方法
         * @return
         */
        public HookHolder method(Method method) {
            this.method = new WeakReference<>(method);
            return this;
        }

        /**
         * 前置处理器
         * @param before    前置处理器
         * @return
         */
        public HookHolder bofore(IEnhanceBefore before) {
            this.factory.get().put(this.method.get(),before);
            return this;
        }

        /**
         * 前置处理器
         * @param before    前置处理器
         * @param index     插入位置
         * @return
         */
        public HookHolder bofore(IEnhanceBefore before,int index) {
            this.factory.get().put(this.method.get(),before,index);
            return this;
        }

        /**
         * 后置处理器
         * @param after     后置处理器
         * @return
         */
        public HookHolder after(IEnhanceAfter after) {
            this.factory.get().put(this.method.get(),after);
            return this;
        }

        /**
         * 后置处理器
         * @param after     后置处理器
         * @param index     插入位置
         * @return
         */
        public HookHolder after(IEnhanceAfter after,int index) {
            this.factory.get().put(this.method.get(),after,index);
            return this;
        }

        /**
         * 返回增强对象
         *
         * @param <T>
         * @return
         */
        public <T> T get() {
            return (T) this.factory.get().getObject();
        }
    }

    /**
     * 劫持方法
     * @param t             劫持实体对象
     * @param method        劫持方法对象
     * @param paramTypes    方法参数
     * @return              劫持代理
     * @param <T>           泛型
     */
    public <T> HookHolder hooked(T t, Func1<T,?> method, Class<?>... paramTypes) {
        CglibProxyFactory factory = new CglibProxyFactory(t);
        String methodName = LambdaUtil.getMethodName(method);
        Method m = findMethod(t,methodName,paramTypes);
        HookHolder hh = new HookHolder(factory,m);
        return hh;
    }

    /**
     * 劫持方法
     * @param t             劫持实体对象
     * @param method        劫持方法对象
     * @return              劫持代理
     * @param <T>           泛型
     */
    public <T> HookHolder hooked(T t, Func1<T,?> method) {
        CglibProxyFactory factory = new CglibProxyFactory(t);
        String methodName = LambdaUtil.getMethodName(method);
        Method m = findMethod(t,methodName);
        HookHolder hh = new HookHolder(factory,m);
        return hh;
    }

    /**
     * 劫持方法
     * @param t             劫持实体对象
     * @return              劫持代理
     * @param <T>           泛型
     */
    public <T> HookHolder hooked(T t) {
        CglibProxyFactory factory = new CglibProxyFactory(t);
        HookHolder hh = new HookHolder(factory);
        return hh;
    }
}
