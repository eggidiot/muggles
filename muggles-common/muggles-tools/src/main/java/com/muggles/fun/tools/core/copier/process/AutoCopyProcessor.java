package com.muggles.fun.tools.core.copier.process;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import com.muggles.fun.tools.core.copier.annotation.AutoCopy;
import com.muggles.fun.tools.core.copier.annotation.Mapping;
import com.muggles.fun.tools.core.copier.util.JavacProcessingEnvUtil;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Names;

@SupportedAnnotationTypes("com.fline.tp.tools.autocopy.annotation.AutoCopy")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AutoCopyProcessor extends AbstractProcessor {
    /**
     * 用于标识注解处理的round
     */
    private static int round;
    /**
     * 打印日志工具
     */
    private Messager messager;
    /**
     * Java语法树的工具类
     */
    private JavacTrees trees;
    /**
     * 创建语法树节点的工厂类
     */
    private TreeMaker treeMaker;
    /**
     * 元素工具
     */
    private JavacElements elementUtils;
    /**
     * 类型工具
     */
    private Types typeUtils;

    /**
     * 空转换器
     */
    private TypeMirror nullType;
    /**
     * object类型
     */
    private TypeMirror objectType;

    /**
     * 注解处理器的初始化方法。在此方法中，我们解包ProcessingEnvironment， 并使用它来初始化需要该环境的其他组件。
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
        // 解包后的ProcessingEnvironment，用于访问原始的处理环境
        ProcessingEnvironment unwrappedprocessingEnv = jbUnwrap(ProcessingEnvironment.class, processingEnv);
        // 创建TreeMaker和Names所需的上下文
        Context context = (JavacProcessingEnvUtil.getJavacProcessingEnvironment(unwrappedprocessingEnv, messager).getContext());
        this.trees = JavacTrees.instance(unwrappedprocessingEnv); // 注意这里改为使用解包后的环境
        this.treeMaker = TreeMaker.instance(context);
        // 编译器名称表的访问，提供了一些标准的名称和创建新名称的方法
        Names names = Names.instance(context);
        this.elementUtils = (JavacElements) unwrappedprocessingEnv.getElementUtils();
        this.typeUtils = unwrappedprocessingEnv.getTypeUtils();
        this.nullType =
            unwrappedprocessingEnv.getElementUtils().getTypeElement("com.fline.tp.core.convertor.Null").asType();
        this.objectType = elementUtils.getTypeElement("java.lang.Object").asType();
    }

    /**
     * 自定义方法，用于解包可能被封装的ProcessingEnvironment对象。 利用反射调用org.jetbrains.jps.javac.APIWrappers的unwrap方法。
     */
    private static <T> T jbUnwrap(Class<? extends T> iface, T wrapper) {
        T unwrapped = null;
        try {
            final Class<?> apiWrappers =
                wrapper.getClass().getClassLoader().loadClass("org.jetbrains.jps.javac.APIWrappers");
            final Method unwrapMethod = apiWrappers.getDeclaredMethod("unwrap", Class.class, Object.class);
            unwrapped = iface.cast(unwrapMethod.invoke(null, iface, wrapper));
        } catch (Throwable ignored) {
        }
        return unwrapped != null ? unwrapped : wrapper;
    }


    /**
     * 注解处理器的核心逻辑，处理标有@AutoCopy注解的元素。 遍历所有被@AutoCopy注解的元素，并对每个元素执行相应的处理。
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        messager.printMessage(Diagnostic.Kind.NOTE, AutoCopyProcessor.class.getSimpleName() + " round " + (++round));
        for (Element element : roundEnv.getElementsAnnotatedWith(AutoCopy.class)) {
            JCTree jcTree = trees.getTree(element);
            jcTree.accept(new TreeTranslator() {
                /**
                 * 访问方法定义，特别是处理构造方法上的@AutoCopy注解。 对于每个构造方法，分析其注解并生成相应的代码。
                 */
                @Override
                public void visitMethodDef(JCTree.JCMethodDecl methodDecl) {
                    if (element.getKind() == ElementKind.CONSTRUCTOR) {
                        // 处理构造方法上的注解
                        handlerConstructorAnnotation(methodDecl, element);
                    }
                }
            });
        }
        return true;
    }

    /**
     * 获取元素上@AutoCopy注解定义的映射关系。
     */
    private Map<String, String> getMappings(Element element) {
        AutoCopy autoCopy = element.getAnnotation(AutoCopy.class);
        Map<String, String> fieldMap = new HashMap<>();
        if (autoCopy.value() != null && autoCopy.value().length > 0) {
            Mapping[] mappings = autoCopy.value();
            for (Mapping mapping : mappings) {
                if ((mapping.name() != null && !mapping.name().equals("")) && mapping.sourceName() != null
                    && !mapping.sourceName().equals("")) {
                    fieldMap.put(mapping.name(), mapping.sourceName());
                }
            }
        }
        return fieldMap;
    }

    /**
     * 从@Mapping注解中获取转换器类。
     */
    public TypeMirror getHandler(Mapping mapping) {
        Class<?> handlerClass;
        try {
            handlerClass = mapping.handler();
        } catch (MirroredTypeException mte) {
            TypeMirror handlerTypeMirror = mte.getTypeMirror();
            return handlerTypeMirror;
        }
        return null;
    }

    /**
     * 检查字段是否需要使用"is"前缀作为getter方法的名称。
     */
    private boolean checkIsMethod(TypeMirror typeMirror, String fieldName, Element element) {
        return typeUtils.isSameType(typeMirror, typeUtils.getPrimitiveType(TypeKind.BOOLEAN));
    }

    /**
     * 从@AutoCopy注解中提取并返回自定义转换器。
     */
    private Map<String, TypeMirror> getConvert(Element element) {
        AutoCopy autoCopy = element.getAnnotation(AutoCopy.class);
        Map<String, TypeMirror> fieldMap = new HashMap<>();
        if (autoCopy.value() != null && autoCopy.value().length > 0) {
            Mapping[] mappings = autoCopy.value();
            for (Mapping mapping : mappings) {
                TypeMirror handler = getHandler(mapping);
                if (handler != null && !typeUtils.isSameType(handler, nullType)) {
                    fieldMap.put(mapping.sourceName(), handler);
                }
            }
        }
        return fieldMap;
    }

    /**
     * 处理构造方法上的@AutoCopy注解，生成代码以拷贝属性。
     */
    private void handlerConstructorAnnotation(JCTree.JCMethodDecl methodDecl, Element element) {
        if (!methodDecl.params.isEmpty()) {
            // 获取入参属性变量名
            JCTree.JCVariableDecl param = methodDecl.params.head;
            String typeName = param.vartype.type.toString(); // 获取类型名称
            TypeElement typeElement = elementUtils.getTypeElement(typeName);
            // 获取入参属性set集合
            Map<String, VariableElement> fieldNameSet = getAllFields(typeElement);
            // 生成类拷贝代码
            genCopyCode(methodDecl, param, fieldNameSet, element);
        }
        // Debug print the modified constructor
        try {
            messager.printMessage(Diagnostic.Kind.NOTE, trees.getTree(element).toString());
        } catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Error printing modified constructor: " + e.getMessage());
        }
    }

    /**
     * 递归获取包括父类在内的所有字段。
     */
    private Map<String, VariableElement> getAllFields(TypeElement typeElement) {
        Map<String, VariableElement> fields = new HashMap<>();
        if (typeUtils.isSameType(typeElement.asType(), objectType)) {
            return fields;
        }

        // 获取当前类的字段
        for (Element enclosed : typeElement.getEnclosedElements()) {
            if (enclosed.getKind() == ElementKind.FIELD) {
                VariableElement variableElement = (VariableElement)enclosed;
                Set<Modifier> modifiers = variableElement.getModifiers();
                String fieldName = variableElement.getSimpleName().toString();
                if (!fields.containsKey(fieldName) && !modifiers.contains(Modifier.FINAL)
                    && !modifiers.contains(Modifier.STATIC)) {
                    fields.put(fieldName, variableElement);
                }
            }
        }
        // 获取父类并递归处理
        TypeMirror superclass = typeElement.getSuperclass();
        if (superclass.getKind() != TypeKind.NONE) {
            TypeElement superElement = (TypeElement)typeUtils.asElement(superclass);
            fields.putAll(getAllFields(superElement)); // 递归调用
        }
        return fields;
    }

    /**
     * 生成字段拷贝的代码，包括对自定义转换器的支持。
     */
    private void genCopyCode(JCTree.JCMethodDecl methodDecl, JCTree.JCVariableDecl param,
        Map<String, VariableElement> fieldMap, Element element) {
        ListBuffer<JCTree.JCStatement> newStatements = new ListBuffer<>();
        Map<String, VariableElement> targetClassField = getAllFields((TypeElement)element.getEnclosingElement());

        // 获取字段映射关系key为当前类字段名 value为来源类的字段名
        Map<String, String> mappings = getMappings(element);
        // 获取字段转换器
        Map<String, TypeMirror> convertMap = getConvert(element);
        for (Map.Entry<String, VariableElement> entry : targetClassField.entrySet()) {
            String fieldName = entry.getKey();
            String targetFieldName = mappings.get(fieldName);
            String sourceFieldName = targetFieldName != null ? targetFieldName : fieldName;
            // 如果变量名和当前类的变量对应不是则直接跳过
            if (!fieldMap.containsKey(sourceFieldName)) {
                continue;
            }
            TypeMirror typeMirror = fieldMap.get(sourceFieldName).asType();
            TypeMirror type = entry.getValue().asType();
            // 获取自定义转换处理类
            TypeMirror sourceConvert = convertMap.get(sourceFieldName);
            // 如果类型不同则直接跳过
            if (!typeUtils.isSameType(typeMirror, type)
                && (sourceConvert == null || typeUtils.isSameType(sourceConvert, nullType))) {
                continue;
            }
            if (sourceConvert != null && !typeUtils.isSameType(sourceConvert, nullType)) {
                genCopyCodeWithConvert(param, newStatements, fieldName, sourceFieldName, sourceConvert,
                    checkIsMethod(typeMirror, sourceFieldName, element));
            } else {
                genSimpleCopyCode(param, newStatements, fieldName, sourceFieldName,
                    checkIsMethod(typeMirror, sourceFieldName, element));
            }
        }
        methodDecl.body.stats = newStatements.toList().appendList(methodDecl.body.stats);
    }

    /**
     * 生成简单的字段拷贝代码，不涉及转换器。
     */
    private void genSimpleCopyCode(JCTree.JCVariableDecl param, ListBuffer<JCTree.JCStatement> newStatements,
        String fieldName, String sourceFieldName, boolean isPrefix) {
        String getterName = getterNameGen(sourceFieldName, isPrefix);
        // 假设存在get方法
        JCTree.JCFieldAccess getterSelect =
            treeMaker.Select(treeMaker.Ident(elementUtils.getName(param.name)), elementUtils.getName(getterName));
        JCTree.JCMethodInvocation getterCall = treeMaker.Apply(List.nil(), getterSelect, List.nil());
        String setterName = setMethodNameGen(isPrefix, fieldName);
        // 创建setter方法的调用，假设setter方法遵循标准JavaBean命名规范
        JCTree.JCExpression setterMethod =
            treeMaker.Select(treeMaker.Ident(elementUtils.getName("this")), elementUtils.getName(setterName));
        JCTree.JCMethodInvocation setterCall = treeMaker.Apply(List.nil(), setterMethod, List.of(getterCall));

        newStatements.add(treeMaker.Exec(setterCall));
    }

    /**
     * 生成包含转换器的字段拷贝代码。
     */
    private void genCopyCodeWithConvert(JCTree.JCVariableDecl param, ListBuffer<JCTree.JCStatement> newStatements,
        String fieldName, String sourceFieldName, TypeMirror sourceConvert, boolean isPrefix) {
        Symbol.TypeSymbol typeSymbol = (Symbol.TypeSymbol)typeUtils.asElement(sourceConvert);
        JCTree.JCExpression convertHandlerFactoryClassExpression =
            treeMaker.QualIdent(elementUtils.getTypeElement("com.fline.tp.core.convertor.ConvertHandlerFactory"));
        JCTree.JCFieldAccess convertMethod =
            treeMaker.Select(convertHandlerFactoryClassExpression, elementUtils.getName("getHandler"));
        JCTree.JCLiteral classNameLiteral = treeMaker.Literal(typeSymbol.type.toString());
        JCTree.JCMethodInvocation factoryCall = treeMaker.Apply(List.nil(), convertMethod, List.of(classNameLiteral));
        JCTree.JCExpression targetType = treeMaker.QualIdent(elementUtils.getTypeElement(typeSymbol.type.toString()));
        JCTree.JCExpression castedFactoryCall = treeMaker.TypeCast(targetType, factoryCall);

        String getterName = getterNameGen(sourceFieldName, isPrefix);

        // 假设存在get方法
        JCTree.JCFieldAccess getterSelect =
            treeMaker.Select(treeMaker.Ident(elementUtils.getName(param.name)), elementUtils.getName(getterName));
        JCTree.JCMethodInvocation getterCall = treeMaker.Apply(List.nil(), getterSelect, List.nil());

        JCTree.JCMethodInvocation convertCall = treeMaker.Apply(List.nil(),
            treeMaker.Select(castedFactoryCall, elementUtils.getName("apply")), List.of(getterCall));
        String setterName = setMethodNameGen(isPrefix, fieldName);
        // 创建setter方法的调用，假设setter方法遵循标准JavaBean命名规范
        JCTree.JCExpression setterMethod =
            treeMaker.Select(treeMaker.Ident(elementUtils.getName("this")), elementUtils.getName(setterName));
        JCTree.JCMethodInvocation setterCall = treeMaker.Apply(List.nil(), setterMethod, List.of(convertCall));

        newStatements.add(treeMaker.Exec(setterCall));
    }

    /**
     * 生成getter方法的名称。
     */
    private String getterNameGen(String sourceFieldName, boolean isPrefix) {
        String getterName;
        if (sourceFieldName.startsWith("is")) {
            getterName = sourceFieldName;
        } else {
            getterName = (isPrefix ? "is" : "get") + Character.toUpperCase(sourceFieldName.charAt(0))
                + sourceFieldName.substring(1);
        }
        return getterName;
    }

    /**
     * 生成setter方法的名称。
     */
    private String setMethodNameGen(boolean isPrefix, String fieldName) {
        String setterName;
        if (isPrefix && fieldName.startsWith("is")) {
            setterName = "set" + Character.toUpperCase(fieldName.charAt(2)) + fieldName.substring(3);
        } else {
            setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        }
        return setterName;
    }

}
