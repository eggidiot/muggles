package com.muggles.fun.tools.core.bean;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.reflect.MethodHandleUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 方法增强工具
 */
@UtilityClass
public class MethodEnhanceUtil {

    /**
     * 方法增强内置工厂
     */
    @Slf4j
    @Data
    @Accessors(chain = true)
    public static class CglibProxyFactory implements MethodInterceptor {
        /**
         * 维护目标对象
         */
        private Object target;
        /**
         * 前置处理器
         */
        private IEnhanceBefore before = (method, args) -> {
            log.info("前置切面对象:{},参数：{}",target,args);
            return true;
        };
        /**
         * 后置处理器
         */
        private IEnhanceAfter after = (method, args, returnValue) -> {
            log.info("后置切面对象:{},参数：{}",target,args);
            return returnValue;
        };

        public CglibProxyFactory(Object target) {
            this.target = target;
        }
        /**
         * 获得目标类的代理对象
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
            Assert.isTrue(before == null || before.before(method, args),()->new IllegalStateException("前置处理异常"));
            if (after != null){
                return after.after(method,args,method.invoke(target, args));
            }
            return method.invoke(target, args);
        }
    }

    /**
     * 返回增强对象
     * @param t     目标对象
     * @return      增强对象
     * @param <T>   泛型
     */
    public <T>T enhancer(T t,IEnhanceBefore before,IEnhanceAfter after){
        CglibProxyFactory cglibProxyFactory = new CglibProxyFactory(t).setBefore(before).setAfter(after);
        return  (T) cglibProxyFactory.getObject();
    }

    /**
     * 返回增强对象
     * @param t     目标对象
     * @return      增强对象
     * @param <T>   泛型
     */
    public <T>T enhancer(T t){
        CglibProxyFactory cglibProxyFactory = new CglibProxyFactory(t);
        return  (T) cglibProxyFactory.getObject();
    }

    /**
     * 设置某个方法前置拦截，插入业务逻辑
     * @param t
     * @param method
     * @param before
     * @return
     * @param <T>
     */
    public <T>T enhancerBefore(T t, String method,IEnhanceBefore before){
        CglibProxyFactory cglibProxyFactory = new CglibProxyFactory(t);
        return  (T) cglibProxyFactory.getObject();
    }

    /**
     * 设置某个方法前置拦截，插入业务逻辑
     * @param t
     * @param method
     * @param before
     * @return
     * @param <T>
     */
    public <T>T enhancerBefore(T t, Method method,IEnhanceBefore before){
        CglibProxyFactory cglibProxyFactory = new CglibProxyFactory(t);
        return  (T) cglibProxyFactory.getObject();
    }

    /**
     * 设置某个方法后置拦截，插入业务逻辑
     * @param t
     * @param method
     * @param after
     * @return
     * @param <T>
     */
    public <T>T enhancerAfter(T t, String method,IEnhanceAfter after){
        CglibProxyFactory cglibProxyFactory = new CglibProxyFactory(t);
        return  (T) cglibProxyFactory.getObject();
    }

    /**
     * 设置某个方法后置拦截，插入业务逻辑
     * @param t
     * @param method
     * @param after
     * @return
     * @param <T>
     */
    public <T>T enhancerAfter(T t, Method method,IEnhanceAfter after){
        CglibProxyFactory cglibProxyFactory = new CglibProxyFactory(t);
        return  (T) cglibProxyFactory.getObject();
    }
}
