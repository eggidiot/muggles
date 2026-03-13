package com.muggles.fun.tools.core.bean;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 方法增强工具（ByteBuddy实现）。
 * 功能对标 {@link MethodExtUtil}（cglib实现），用法完全对称。
 */
@Slf4j
@UtilityClass
public class MethodEnhancer {

	/**
	 * ByteBuddy实例复用（线程安全）
	 */
	private static final ByteBuddy BYTE_BUDDY = new ByteBuddy();

	/**
	 * 方法增强内置工厂（ByteBuddy实现）
	 */
	@Slf4j
	@Data
	@Accessors(chain = true)
	@RequiredArgsConstructor
	public static class BuddyProxyFactory implements InvocationHandler {
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
		 * @return Object
		 */
		public Object getObject() {
			try {
				Class<?> proxyClass = BYTE_BUDDY.subclass(target.getClass())
						.method(ElementMatchers.any())
						.intercept(InvocationHandlerAdapter.of(this))
						.make()
						.load(target.getClass().getClassLoader())
						.getLoaded();
				return proxyClass.getDeclaredConstructor().newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Failed to create ByteBuddy proxy", e);
			}
		}

		/**
		 * 代理方法调用，执行前置/后置拦截逻辑
		 *
		 * @param proxy  代理对象
		 * @param method 被调用的方法
		 * @param args   方法参数
		 * @return 方法返回值
		 * @throws Throwable 任意异常
		 */
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			// 将代理类的Method解析为原始类的Method，确保与注册时的key匹配
			Method originalMethod = resolveOriginalMethod(method);
			//1.获取对应的前置拦截器
			IEnhanceBefore before = beforeMap.get(originalMethod);
			//2.获取对应的后置拦截器
			IEnhanceAfter after = afterMap.get(originalMethod);
			//3.拦截器处理逻辑
			Assert.isTrue(before == null || before.before(proxy, originalMethod, args), () -> new IllegalStateException("前置处理异常"));
			if (after != null) {
				return after.after(proxy, originalMethod, args, method.invoke(target, args));
			}
			return method.invoke(target, args);
		}

		/**
		 * 将代理类的Method解析为原始类上对应的Method
		 */
		private Method resolveOriginalMethod(Method proxyMethod) {
			try {
				return target.getClass().getMethod(proxyMethod.getName(), proxyMethod.getParameterTypes());
			} catch (NoSuchMethodException e) {
				return proxyMethod;
			}
		}

		/**
		 * 增强某个方法
		 *
		 * @param method 指定方法
		 * @param before 前置处理器
		 * @param after  后置处理器
		 * @return BuddyProxyFactory
		 */
		public BuddyProxyFactory enhance(Method method, IEnhanceBefore before, IEnhanceAfter after) {
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
		 * 增强某个方法（按名称匹配所有同名重载方法）
		 *
		 * @param method 指定方法名称
		 * @param before 前置处理器
		 * @param after  后置处理器
		 * @return 代理工厂对象
		 */
		public BuddyProxyFactory enhance(String method, IEnhanceBefore before, IEnhanceAfter after) {
			for (Method m : target.getClass().getMethods()) {
				if (m.getName().equals(method)) {
					enhance(m, before, after);
				}
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
		public BuddyProxyFactory enhanceBefore(Method method, IEnhanceBefore before) {
			return enhance(method, before, null);
		}

		/**
		 * 前置增强某个方法
		 *
		 * @param method 指定方法名称
		 * @param before 前置处理器
		 * @return 代理工厂对象
		 */
		public BuddyProxyFactory enhanceBefore(String method, IEnhanceBefore before) {
			return enhance(method, before, null);
		}

		/**
		 * 后置增强某个方法
		 *
		 * @param method 指定方法
		 * @param after  后置处理器
		 * @return 代理工厂对象
		 */
		public BuddyProxyFactory enhanceAfter(Method method, IEnhanceAfter after) {
			return enhance(method, null, after);
		}

		/**
		 * 后置增强某个方法
		 *
		 * @param method 指定方法名称
		 * @param after  后置处理器
		 * @return 代理工厂对象
		 */
		public BuddyProxyFactory enhanceAfter(String method, IEnhanceAfter after) {
			return enhance(method, null, after);
		}

		/**
		 * 返回增强对象
		 *
		 * @param <T> 泛型类型
		 * @return T
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
		BuddyProxyFactory factory = new BuddyProxyFactory(t);
		Method[] methods = t.getClass().getDeclaredMethods();
		for (Method m : methods) {
			factory.enhance(m, (obj, method, args) -> {
				log.info("前置切面对象:{},方法:{},参数:{}", t, method.getName(), args);
				return true;
			}, (obj, method, args, value) -> {
				log.info("后置切面对象:{},方法:{},参数:{},返回值:{}", t, method.getName(), args, value);
				return value;
			});
		}
		return factory.get();
	}

	/**
	 * 返回增强对象
	 *
	 * @param t      目标对象
	 * @param method 方法名称
	 * @param before 前置处理器
	 * @param after  后置处理器
	 * @param <T>    泛型
	 * @return 增强对象
	 */
	public <T> T enhancer(T t, String method, IEnhanceBefore before, IEnhanceAfter after) {
		Method m = findMethod(t, method);
		return enhancer(t, m, before, after);
	}

	/**
	 * 返回增强对象
	 *
	 * @param t      目标对象
	 * @param method 方法
	 * @param before 前置处理器
	 * @param after  后置处理器
	 * @param <T>    泛型
	 * @return 增强对象
	 */
	public <T> T enhancer(T t, Method method, IEnhanceBefore before, IEnhanceAfter after) {
		Assert.notNull(t, () -> new IllegalStateException("指定对象不能为Null"));
		Assert.notNull(method, () -> new IllegalStateException("被增强的指定方法不能为Null"));
		BuddyProxyFactory factory = new BuddyProxyFactory(t);
		factory.enhance(method, before, after);
		return factory.get();
	}

	/**
	 * 设置某个方法前置拦截，插入业务逻辑
	 *
	 * @param before 前置处理器
	 * @param t      原始对象
	 * @param method 拦截方法名称
	 * @param <T>    泛型类型
	 * @return T
	 */
	public <T> T enhancerBefore(IEnhanceBefore before, T t, String method) {
		return enhancer(t, method, before, null);
	}

	/**
	 * 设置某个方法前置拦截，插入业务逻辑
	 *
	 * @param before 前置处理器
	 * @param t      原始对象
	 * @param method 拦截方法
	 * @param <T>    泛型类型
	 * @return T
	 */
	public <T> T enhancerBefore(IEnhanceBefore before, T t, Method method) {
		return enhancer(t, method, before, null);
	}

	/**
	 * 设置某个方法前置拦截，支持方法引用（类::方法名）
	 *
	 * @param before 前置处理器
	 * @param t      原始对象
	 * @param method 方法引用，如 AccountService::getAmount
	 * @param <T>    泛型类型
	 * @return T
	 */
	public <T> T enhancerBefore(IEnhanceBefore before, T t, SFunction<T, ?> method) {
		return enhancer(t, resolveMethodName(method), before, null);
	}

	/**
	 * 设置某个方法后置拦截，插入业务逻辑
	 *
	 * @param t      原始对象
	 * @param method 拦截方法名称
	 * @param after  后置处理器
	 * @param <T>    泛型类型
	 * @return T
	 */
	public <T> T enhancerAfter(T t, String method, IEnhanceAfter after) {
		return enhancer(t, method, null, after);
	}
	/**
	 * 设置某个方法后置拦截，插入业务逻辑
	 *
	 * @param t      原始对象
	 * @param method 拦截方法名称
	 * @param after  后置处理器
	 * @param <T>    泛型类型
	 * @return T
	 */
	public <T> T enhancerAfter(T t, SFunction<T, ?> method, IEnhanceAfter after) {
		return enhancer(t, resolveMethodName(method), null, after);
	}

	/**
	 * 设置某个方法后置拦截，插入业务逻辑
	 *
	 * @param t      原始对象
	 * @param method 拦截方法
	 * @param after  后置处理器
	 * @param <T>    泛型类型
	 * @return T
	 */
	public <T> T enhancerAfter(T t, Method method, IEnhanceAfter after) {
		return enhancer(t, method, null, after);
	}

	/**
	 * 返回与方法名匹配的第一个方法，要正确使用，要求被查找的方法名没有被重写
	 *
	 * @param t      对象
	 * @param method 方法名
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
	 * @param method 方法名
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
	 * @param method     方法名
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
	 * @param method     方法名
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
	 * 从SFunction方法引用中提取方法名
	 *
	 * @param func 方法引用，如 AccountService::getAmount
	 * @param <T>  泛型类型
	 * @return 方法名
	 */
	private <T> String resolveMethodName(SFunction<T, ?> func) {
		try {
			Method writeReplace = func.getClass().getDeclaredMethod("writeReplace");
			writeReplace.setAccessible(true);
			SerializedLambda lambda = (SerializedLambda) writeReplace.invoke(func);
			return lambda.getImplMethodName();
		} catch (Exception e) {
			throw new RuntimeException("无法从方法引用中提取方法名", e);
		}
	}
}
