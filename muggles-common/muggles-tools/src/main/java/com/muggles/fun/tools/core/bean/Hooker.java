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
    List<Holder<T>> holders = CollUtil.newArrayList();
    /**
     * 劫持方法持有
     */
    public static class Holder<T> {
        /**
         * 方法引用
         */
        WeakReference<Method> method;
        /**
         * 劫持代理对象弱引用
         */
        WeakReference<Hooker<T>> hooker;
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
         * @param method    被劫持方法
         */
        private Holder(Method method, Hooker<T> hooker) {
            this.method = new WeakReference<>(method);
            this.hooker = new WeakReference<>(hooker);
        }
        /**
         * 前置处理器
         * @param before    前置处理器
         * @return          Holder<T>
         */
        public Holder<T> bofore(IEnhanceBefore before) {
            this.before.put(before);
            return this;
        }

        /**
         * 前置处理器
         * @param before    前置处理器
         * @param index     插入位置
         * @return          Holder<T>
         */
        public Holder<T> bofore(IEnhanceBefore before, int index) {
            this.before.put(before,index);
            return this;
        }

        /**
         * 后置处理器
         * @param after     后置处理器
         * @return          Holder<T>
         */
        public Holder<T> after(IEnhanceAfter after) {
            this.after.put(after);
            return this;
        }

        /**
         * 后置处理器
         * @param after     后置处理器
         * @param index     插入位置
         * @return          Holder<T>
         */
        public Holder<T> after(IEnhanceAfter after, int index) {
            this.after.put(after,index);
            return this;
        }

        /**
         * 返回当前所有的前置过滤器
         * @return  前置过滤器链集合
         */
        public List<IEnhanceBefore> befores(){
            return this.before.getBeforeChain();
        }

        /**
         * 返回当前所有的后置过滤器
         * @return  前置过滤器链集合
         */
        public List<IEnhanceAfter> afters(){
            return this.after.getAfterChain();
        }

        /**
         * 清理前置处理器
         * @return  Holder<T>
         */
        public Holder<T> clearBefores(){
            this.before.getBeforeChain().clear();
            return this;
        }

        /**
         * 清理指定位置前置处理器
         * @return  Holder<T>
         */
        public Holder<T> clearBefores(int pos){
            if (this.before.getBeforeChain().isEmpty()) {
                return this;
            }
            if (pos <  0) {
                pos = 0;
            }
            if (pos > this.before.getBeforeChain().size()) {
                pos = this.before.getBeforeChain().size() - 1;
            }
            this.before.getBeforeChain().remove(pos);
            return this;
        }


        /**
         * 清理后置处理器
         * @return  Holder<T>
         */
        public Holder<T> clearAfters(){
            this.after.getAfterChain().clear();
            return this;
        }

        /**
         * 清理指定位置前置处理器
         * @return  Holder<T>
         */
        public Holder<T> clearAfters(int pos){
            if (this.after.getBeforeChain().isEmpty()) {
                return this;
            }
            if (pos <  0) {
                pos = 0;
            }
            if (pos > this.after.getBeforeChain().size()) {
                pos = this.after.getBeforeChain().size() - 1;
            }
            this.after.getBeforeChain().remove(pos);
            return this;
        }

        /**
         * 确认方法是否被当前Holder对象持有
         * @param method    方法
         * @return          boolean
         */
        public boolean check(Method method) {
            return method.equals(this.method.get());
        }

        /**
         * 单个holder设置完毕，返回代理对象
         * @return  Hooker<T>
         */
        public Hooker<T> done(){
            return this.hooker.get();
        }
    }

    /**
     * 构造器
     * @param t     被劫持对象
     */
    private Hooker(T t) {
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
         *
         * @param before 前置处理器
         * @param index  插入位置
         */
        public void put(IEnhanceBefore before, int index) {
            if (index < 0) {
                index = 0;
            }
            if (index > beforeChain.size()) {
                index = beforeChain.size();
            }
            beforeChain.add(index,before);
        }
        /**
         * 在指定位置插入前置处理器
         *
         * @param before 前置处理器
         */
        public void put(IEnhanceBefore before) {
            beforeChain.add(before);
        }

        /**
         * 在指定位置插入前置处理器
         *
         * @param after 后置处理器
         * @param index 插入位置
         */
        public void put(IEnhanceAfter after, int index) {
            if (index < 0) {
                index = 0;
            }
            if (index > afterChain.size()) {
                index = afterChain.size();
            }
            afterChain.add(index,after);
        }

        /**
         * 在指定位置插入前置处理器
         *
         * @param after 后置处理器
         */
        public void put(IEnhanceAfter after) {
            afterChain.add(after);
        }
    }

    /**
     * 设置劫持方法对象
     * @param method    被劫持方法
     * @return          Holder<T>
     */
    public Holder<T> method(Method method) {
        if (method == null) {
            return null;
        }
        Holder<T> hh = null;
        for (Holder<T> h:holders) {
            if (h.check(method)) {
                hh = h;
            }
        }
        if (hh == null) {
            hh = new Holder<>(method, this);
            this.holders.add(hh);
        }
        return hh;
    }

    /**
     * 设置劫持方法对象
     * @param method    被劫持方法
     * @return          Holder<T>
     */
    public Holder<T> method(String method) {
        Method m = MethodEnhanceUtil.findMethod(t.get(),method);
        return method(m);
    }

    /**
     * 设置劫持方法对象
     * @param method    被劫持方法
     * @return          Holder<T>
     */
    public Holder<T> method(String method, Class<?>... paramTypes) {
        Method m = MethodEnhanceUtil.findMethod(t.get(),method,paramTypes);
        return method(m);
    }

    /**
     * 设置劫持方法对象
     * @param method    被劫持方法
     * @return          Holder<T>
     */
    public Holder<T> method(Func1<T,?> method) {
        String methodName = LambdaUtil.getMethodName(method);
        return method(methodName);
    }

    /**
     * 劫持方法
     * @param t             劫持实体对象
     * @param method        劫持方法对象
     * @param paramTypes    方法参数
     * @return              劫持代理
     */
    public static <T>Holder<T> hooked(T t, String method, Class<?>... paramTypes) {
        Method m = MethodEnhanceUtil.findMethod(t,method,paramTypes);
        return hooked(t,m);
    }

    /**
     * 劫持方法
     * @param t             劫持实体对象
     * @param method        劫持方法对象
     * @return              劫持代理
     */
    public static <T>Holder<T> hooked(T t, Method method) {
        Hooker<T> hh = new Hooker<>(t);
        return hh.method(method);
    }

    /**
     * 劫持方法
     * @param t             劫持实体对象
     * @return              劫持代理
     * @param <T>           泛型
     */
    public static <T>Hooker<T> hooked(T t) {
        return new Hooker<>(t);
    }

    /**
     * 获取劫持以后对象
     * @return  T
     */
    public T target(){
        if (CollUtil.isEmpty(holders)) {
            return t.get();
        }
        MethodEnhanceUtil.CglibProxyFactory factory = new MethodEnhanceUtil.CglibProxyFactory(t.get());
        for (Holder<T> h: holders) {
            factory.enhance(h.method.get(),h.before,h.after);
        }
        return factory.get();
    }
}