package com.muggles.fun.tools.core.bean;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.func.LambdaUtil;
import lombok.Data;
import lombok.experimental.Accessors;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 链式调用holder
 */
public class Hooker<T> {
    /**
     * 指定方法
     */
    WeakReference<T> t;
    /**
     * 方法持有链
     */
    List<Holder> holders = CollUtil.newArrayList();
    /**
     * 劫持方法持有
     */
    public static class Holder<T> {
        /**
         * 方法引用
         */
        WeakReference<Method> method;
        /**
         * 默认前置处理器链
         */
        EnhanceChain before = new EnhanceChain();
        /**
         * 默认后置处理器链
         */
        EnhanceChain after = new EnhanceChain();

        /**
         * 构造器
         * @param method
         */
        public Holder(Method method) {
            this.method = new WeakReference<>(method);
        }

        /**
         * 设置劫持方法对象
         * @param method    被劫持方法
         * @return
         */
        public Holder methods(Func1<T,?> method) {
            return this;
        }
        /**
         * 前置处理器
         * @param before    前置处理器
         * @return
         */
        public Holder bofore(IEnhanceBefore before) {
            this.before.put(before);
            return this;
        }

        /**
         * 前置处理器
         * @param before    前置处理器
         * @param index     插入位置
         * @return
         */
        public Holder bofore(IEnhanceBefore before, int index) {
            this.before.put(before,index);
            return this;
        }

        /**
         * 后置处理器
         * @param after     后置处理器
         * @return
         */
        public Holder after(IEnhanceAfter after) {
            this.after.put(after);
            return this;
        }

        /**
         * 后置处理器
         * @param after     后置处理器
         * @param index     插入位置
         * @return
         */
        public Holder after(IEnhanceAfter after, int index) {
            this.after.put(after,index);
            return this;
        }
    }
    /**
     * 构造器
     * @param t         被劫持对象
     * @param method    被劫持方法
     */
    public Hooker(T t, Method method) {
        this.t = new WeakReference<>(t);
        Holder holder = new Holder(method);
        holders.add(holder);
    }

    /**
     * 构造器
     * @param t     被劫持对象
     */
    public Hooker(T t) {
        this.t = new WeakReference<>(t);
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
     * 设置劫持方法对象
     * @param method    被劫持方法
     * @return
     */
    public Holder method(Method method) {
        Holder holder = new Holder(method);
        holders.add(holder);
        return holder;
    }

    /**
     * 劫持方法
     * @param t             劫持实体对象
     * @param method        劫持方法对象
     * @param paramTypes    方法参数
     * @return              劫持代理
     * @param <T>           泛型
     */
    public <T> Hooker hooked(T t, Func1<T,?> method, Class<?>... paramTypes) {

        Hooker hh = new Hooker(t,null);
        return hh;
    }

    /**
     * 劫持方法
     * @param t             劫持实体对象
     * @param method        劫持方法对象
     * @return              劫持代理
     * @param <T>           泛型
     */
    public <T> Hooker hooked(T t, Func1<T,?> method) {
        MethodEnhanceUtil.CglibProxyFactory factory = new MethodEnhanceUtil.CglibProxyFactory(t);
        String methodName = LambdaUtil.getMethodName(method);
        Method m = MethodEnhanceUtil.findMethod(t,methodName);
        Hooker hh = new Hooker(factory,m);
        return hh;
    }

    /**
     * 劫持方法
     * @param t             劫持实体对象
     * @return              劫持代理
     * @param <T>           泛型
     */
    public <T> Hooker hooked(T t) {
        MethodEnhanceUtil.CglibProxyFactory factory = new MethodEnhanceUtil.CglibProxyFactory(t);
        Hooker hh = new Hooker(factory);
        return hh;
    }
}