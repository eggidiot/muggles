package com.muggles.fun.tools.core.copier.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

import com.muggles.fun.tools.core.copier.process.AutoCopyProcessor;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;

import javafx.scene.Parent;
import sun.misc.Unsafe;

/**
 * 获取javacProcessingEnv的相关工具方法
 *
 * @author tanghao
 * @date 2024/2/23 16:16
 */
public class JavacProcessingEnvUtil {
    /**
     * 将给定的处理环境强制转换为JavacProcessingEnvironment。 在Gradle增量编译的情况下，返回Gradle包装器的委托处理环境。
     */
    public static JavacProcessingEnvironment getJavacProcessingEnvironment(Object procEnv,Messager messager) {
        addOpensForLombok();
        if (procEnv instanceof JavacProcessingEnvironment) {
            return (JavacProcessingEnvironment)procEnv;
        }

        // 尝试在对象中找到“delegate”字段，并使用它获取JavacProcessingEnvironment
        for (Class<?> procEnvClass = procEnv.getClass(); procEnvClass != null;
            procEnvClass = procEnvClass.getSuperclass()) {
            Object delegate = tryGetDelegateField(procEnvClass, procEnv);
            if (delegate == null) {
                delegate = tryGetProxyDelegateToField(procEnvClass, procEnv);
            }
            if (delegate == null) {
                delegate = tryGetProcessingEnvField(procEnvClass, procEnv);
            }

            if (delegate != null) {
                return getJavacProcessingEnvironment(delegate,messager);
            }
            // 未找到delegate字段，尝试在超类上查找
        }

        messager.printMessage(Diagnostic.Kind.WARNING,
            "Can't get the delegate of the gradle IncrementalProcessingEnvironment. AutoCopy won't work.");
        return null;
    }

    /**
     * 尝试通过代理获取委托对象。这在处理由代理封装的环境时特别有用， 比如在IntelliJ IDEA 2020.3及更高版本中使用。
     */
    private static Object tryGetProxyDelegateToField(Class<?> delegateClass, Object instance) {
        try {
            InvocationHandler handler = Proxy.getInvocationHandler(instance);
            return Permit.getField(handler.getClass(), "val$delegateTo").get(handler);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 尝试获取由Kotlin增量编译过程中使用的filer字段。
     */
    private Object tryGetFilerField(Class<?> delegateClass, Object instance) {
        try {
            return Permit.getField(delegateClass, "filer").get(instance);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 尝试获取由Kotlin增量编译过程中使用的processingEnv字段。
     */
    private static Object tryGetProcessingEnvField(Class<?> delegateClass, Object instance) {
        try {
            return Permit.getField(delegateClass, "processingEnv").get(instance);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 尝试获取Gradle增量编译过程中使用的delegate字段。
     */
    private static Object tryGetDelegateField(Class<?> delegateClass, Object instance) {
        try {
            return Permit.getField(delegateClass, "delegate").get(instance);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从JDK9开始有用，从JDK16开始变为必需。此代码旨在在JDK8及以下版本上优雅地无操作， 因为在那里不需要这项操作。
     */
    private static void addOpensForLombok() {
        Class<?> cModule;
        try {
            cModule = Class.forName("java.lang.Module");
        } catch (ClassNotFoundException e) {
            // JDK8及以下版本，不需要此操作。
            return;
        }

        Unsafe unsafe = getUnsafe();
        Object jdkCompilerModule = getJdkCompilerModule();
        Object ownModule = getOwnModule();
        String[] allPkgs = {"com.sun.tools.javac.code", "com.sun.tools.javac.comp", "com.sun.tools.javac.file",
            "com.sun.tools.javac.main", "com.sun.tools.javac.model", "com.sun.tools.javac.parser",
            "com.sun.tools.javac.processing", "com.sun.tools.javac.tree", "com.sun.tools.javac.util",
            "com.sun.tools.javac.jvm",};

        try {
            Method m = cModule.getDeclaredMethod("implAddOpens", String.class, cModule);
            long firstFieldOffset = getFirstFieldOffset(unsafe);
            unsafe.putBooleanVolatile(m, firstFieldOffset, true);
            for (String p : allPkgs) {
                m.invoke(jdkCompilerModule, p, ownModule);
            }
        } catch (Exception ignore) {
        }
    }

    /**
     * 获取JDK编译器模块。这是通过反射调用JDK内部API来实现的， 主要用于支持在高版本JDK中使用反射访问编译器内部模块。
     */
    private static Object getJdkCompilerModule() {
        /* call public api: ModuleLayer.boot().findModule("jdk.compiler").get();
           but use reflection because we don't want this code to crash on jdk1.7 and below.
           In that case, none of this stuff was needed in the first place, so we just exit via
           the catch block and do nothing.
         */

        try {
            Class<?> cModuleLayer = Class.forName("java.lang.ModuleLayer");
            Method mBoot = cModuleLayer.getDeclaredMethod("boot");
            Object bootLayer = mBoot.invoke(null);
            Class<?> cOptional = Class.forName("java.util.Optional");
            Method mFindModule = cModuleLayer.getDeclaredMethod("findModule", String.class);
            Object oCompilerO = mFindModule.invoke(bootLayer, "jdk.compiler");
            return cOptional.getDeclaredMethod("get").invoke(oCompilerO);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当前类所在的模块。这在模块化Java应用中特别重要， 用于确保可以访问特定模块内的类或包。
     */
    private static Object getOwnModule() {
        try {
            Method m = Permit.getMethod(Class.class, "getModule");
            return m.invoke(AutoCopyProcessor.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取Unsafe类中第一个字段的偏移量。这通常用于低级操作， 比如直接内存访问或修改字段，绕过安全检查。
     */
    private static long getFirstFieldOffset(Unsafe unsafe) {
        try {
            return unsafe.objectFieldOffset(Parent.class.getDeclaredField("first"));
        } catch (NoSuchFieldException e) {
            // can't happen.
            throw new RuntimeException(e);
        } catch (SecurityException e) {
            // can't happen
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取Unsafe实例。Unsafe提供了一种绕过Java语言安全性限制的方式， 允许直接内存访问等操作，通常用于高性能或低级内存操作。
     */
    private static Unsafe getUnsafe() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe)theUnsafe.get(null);
        } catch (Exception e) {
            return null;
        }
    }
}
