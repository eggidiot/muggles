package com.muggles.fun.cache.core.aop;

import com.muggles.fun.muggles.cache.basic.ICacheService;
import com.muggles.fun.muggles.cache.basic.anno.MuggleCacheEvict;
import com.muggles.fun.muggles.cache.basic.anno.MuggleCachePut;
import com.muggles.fun.muggles.cache.basic.anno.MuggleCacheable;
import com.muggles.fun.muggles.cache.basic.anno.MuggleCaching;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Muggle 缓存注解切面。
 * <p>该切面负责把方法上的缓存注解转换为 {@link ICacheService} 调用。
 */
@Aspect
@RequiredArgsConstructor
public class MuggleCacheAspect {

	private static final String RESULT_VARIABLE = "result";

	private final ICacheService cacheService;

	private final ExpressionParser expressionParser = new SpelExpressionParser();

	private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

	/**
	 * 处理方法或类上的缓存注解。
	 *
	 * @param joinPoint 方法调用上下文
	 * @return 方法返回值
	 * @throws Throwable 原始方法异常
	 */
	@Around("@annotation(com.muggles.fun.muggles.cache.basic.anno.MuggleCacheable)"
			+ " || @annotation(com.muggles.fun.muggles.cache.basic.anno.MuggleCachePut)"
			+ " || @annotation(com.muggles.fun.muggles.cache.basic.anno.MuggleCacheEvict)"
			+ " || @annotation(com.muggles.fun.muggles.cache.basic.anno.MuggleCaching)"
			+ " || @within(com.muggles.fun.muggles.cache.basic.anno.MuggleCacheable)"
			+ " || @within(com.muggles.fun.muggles.cache.basic.anno.MuggleCachePut)"
			+ " || @within(com.muggles.fun.muggles.cache.basic.anno.MuggleCacheEvict)"
			+ " || @within(com.muggles.fun.muggles.cache.basic.anno.MuggleCaching)")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		Method method = resolveMethod(joinPoint);
		CacheOperationContext context = new CacheOperationContext(joinPoint, method);
		List<MuggleCacheable> cacheableAnnotations = cacheableAnnotations(method);
		for (MuggleCacheable cacheable : cacheableAnnotations) {
			Serializable key = resolveKey(cacheable.key(), context, null);
			Object value = cacheService.get(key, method.getGenericReturnType());
			if (value != null || containsKey(key)) {
				return value;
			}
		}
		Object result = joinPoint.proceed();
		writeCacheableResults(cacheableAnnotations, context, result);
		writePutResults(putAnnotations(method), context, result);
		evictCaches(evictAnnotations(method), context, result);
		return result;
	}

	/**
	 * 写入 cacheable 缓存结果。
	 *
	 * @param annotations 缓存注解
	 * @param context     操作上下文
	 * @param result      方法返回值
	 */
	private void writeCacheableResults(List<MuggleCacheable> annotations, CacheOperationContext context, Object result) {
		for (MuggleCacheable cacheable : annotations) {
			Serializable key = resolveKey(cacheable.key(), context, result);
			cacheService.put(key, result, cacheable.expire(), cacheable.timeUnit());
		}
	}

	/**
	 * 写入 cache put 结果。
	 *
	 * @param annotations 缓存更新注解
	 * @param context     操作上下文
	 * @param result      方法返回值
	 */
	private void writePutResults(List<MuggleCachePut> annotations, CacheOperationContext context, Object result) {
		for (MuggleCachePut cachePut : annotations) {
			Serializable key = resolveKey(cachePut.key(), context, result);
			cacheService.put(key, result, cachePut.expire(), cachePut.timeUnit());
		}
	}

	/**
	 * 删除缓存。
	 *
	 * @param annotations 缓存删除注解
	 * @param context     操作上下文
	 * @param result      方法返回值
	 */
	private void evictCaches(List<MuggleCacheEvict> annotations, CacheOperationContext context, Object result) {
		for (MuggleCacheEvict evict : annotations) {
			if (evict.allEntries()) {
				cacheService.clear();
				continue;
			}
			Serializable key = resolveKey(evict.key(), context, result);
			cacheService.remove(key);
		}
	}

	/**
	 * 获取可缓存注解。
	 *
	 * @param method 目标方法
	 * @return 可缓存注解集合
	 */
	private List<MuggleCacheable> cacheableAnnotations(Method method) {
		List<MuggleCacheable> annotations = new ArrayList<>();
		MuggleCacheable direct = findMergedAnnotation(method, MuggleCacheable.class);
		if (direct != null) {
			annotations.add(direct);
		}
		MuggleCaching caching = findMergedAnnotation(method, MuggleCaching.class);
		if (caching != null) {
			annotations.addAll(Arrays.asList(caching.cacheable()));
		}
		return annotations;
	}

	/**
	 * 获取缓存写入注解。
	 *
	 * @param method 目标方法
	 * @return 缓存写入注解集合
	 */
	private List<MuggleCachePut> putAnnotations(Method method) {
		List<MuggleCachePut> annotations = new ArrayList<>();
		MuggleCachePut direct = findMergedAnnotation(method, MuggleCachePut.class);
		if (direct != null) {
			annotations.add(direct);
		}
		MuggleCaching caching = findMergedAnnotation(method, MuggleCaching.class);
		if (caching != null) {
			annotations.addAll(Arrays.asList(caching.put()));
		}
		return annotations;
	}

	/**
	 * 获取缓存删除注解。
	 *
	 * @param method 目标方法
	 * @return 缓存删除注解集合
	 */
	private List<MuggleCacheEvict> evictAnnotations(Method method) {
		List<MuggleCacheEvict> annotations = new ArrayList<>();
		MuggleCacheEvict direct = findMergedAnnotation(method, MuggleCacheEvict.class);
		if (direct != null) {
			annotations.add(direct);
		}
		MuggleCaching caching = findMergedAnnotation(method, MuggleCaching.class);
		if (caching != null) {
			annotations.addAll(Arrays.asList(caching.evict()));
		}
		return annotations;
	}

	/**
	 * 查找方法或类上的合并注解。
	 *
	 * @param method         目标方法
	 * @param annotationType 注解类型
	 * @param <A>            注解类型
	 * @return 注解实例
	 */
	private <A extends java.lang.annotation.Annotation> A findMergedAnnotation(Method method, Class<A> annotationType) {
		A annotation = AnnotatedElementUtils.findMergedAnnotation(method, annotationType);
		if (annotation != null) {
			return annotation;
		}
		return AnnotatedElementUtils.findMergedAnnotation(method.getDeclaringClass(), annotationType);
	}

	/**
	 * 解析缓存 key。
	 *
	 * @param keyExpression key 表达式
	 * @param context       操作上下文
	 * @param result        方法结果
	 * @return 缓存 key
	 */
	private Serializable resolveKey(String keyExpression, CacheOperationContext context, Object result) {
		if (keyExpression == null || keyExpression.isBlank()) {
			return defaultKey(context);
		}
		StandardEvaluationContext evaluationContext = evaluationContext(context, result);
		Object key = expressionParser.parseExpression(keyExpression).getValue(evaluationContext);
		if (key instanceof Serializable serializable) {
			return serializable;
		}
		return String.valueOf(key);
	}

	/**
	 * 构建 SpEL 上下文。
	 *
	 * @param context 操作上下文
	 * @param result  方法结果
	 * @return SpEL 上下文
	 */
	private StandardEvaluationContext evaluationContext(CacheOperationContext context, Object result) {
		StandardEvaluationContext evaluationContext = new StandardEvaluationContext(context.target());
		evaluationContext.setVariable("root", context);
		evaluationContext.setVariable("method", context.method());
		evaluationContext.setVariable("methodName", context.method().getName());
		evaluationContext.setVariable("target", context.target());
		evaluationContext.setVariable("targetClass", context.target().getClass());
		evaluationContext.setVariable("args", context.args());
		evaluationContext.setVariable(RESULT_VARIABLE, result);
		String[] parameterNames = parameterNameDiscoverer.getParameterNames(context.method());
		for (int i = 0; i < context.args().length; i++) {
			evaluationContext.setVariable("p" + i, context.args()[i]);
			evaluationContext.setVariable("a" + i, context.args()[i]);
			if (parameterNames != null && i < parameterNames.length) {
				evaluationContext.setVariable(parameterNames[i], context.args()[i]);
			}
		}
		return evaluationContext;
	}

	/**
	 * 构建默认缓存 key。
	 *
	 * @param context 操作上下文
	 * @return 默认缓存 key
	 */
	private Serializable defaultKey(CacheOperationContext context) {
		return context.target().getClass().getName()
				+ "#"
				+ context.method().getName()
				+ ":"
				+ Arrays.deepHashCode(context.args());
	}

	/**
	 * 判断缓存是否包含 key。
	 * <p>当前 ICacheService 未提供 contains 方法，因此通过 list 临时判断。
	 *
	 * @param key 缓存 key
	 * @return true 表示已缓存该 key
	 */
	private boolean containsKey(Serializable key) {
		return cacheService.list().stream().anyMatch(entry -> key.equals(entry.getKey()));
	}

	/**
	 * 解析真实目标方法。
	 *
	 * @param joinPoint 方法调用上下文
	 * @return 真实目标方法
	 * @throws NoSuchMethodException 方法不存在
	 */
	private Method resolveMethod(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		return joinPoint.getTarget().getClass().getMethod(method.getName(), method.getParameterTypes());
	}

	/**
	 * 缓存操作上下文。
	 *
	 * @param joinPoint 调用点
	 * @param method    目标方法
	 */
	private record CacheOperationContext(ProceedingJoinPoint joinPoint, Method method) {

		/**
		 * 目标对象。
		 *
		 * @return 目标对象
		 */
		Object target() {
			return joinPoint.getTarget();
		}

		/**
		 * 方法参数。
		 *
		 * @return 方法参数
		 */
		Object[] args() {
			return joinPoint.getArgs();
		}

		/**
		 * 方法返回类型。
		 *
		 * @return 方法返回类型
		 */
		Type returnType() {
			return method.getGenericReturnType();
		}
	}
}
