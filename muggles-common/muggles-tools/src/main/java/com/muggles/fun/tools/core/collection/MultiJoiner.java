package com.muggles.fun.tools.core.collection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 多集合关联类
 * 
 * @author haotang
 */
public class MultiJoiner {
    /**
     * 存储多表连接操作的列表
     */
    private List<JoinOperation<?, ?>> joinOperations = new ArrayList<>();

    /**
     * 创建一个实例
     * 
     * @return
     */
    public static MultiJoiner create() {
        return new MultiJoiner();
    }

    /**
     * 基础的join方法，用于添加连接操作到列表
     * 
     * @param leftSet 左集合列表
     * @param rightSet 右集合列表
     * @param joinCondition 关联逻辑
     * @param joinType 关联类型
     */
    private <T, U> MultiJoiner baseJoin(List<T> leftSet, List<U> rightSet, BiPredicate<T, U> joinCondition,
        JoinType joinType) {
        validateSets(leftSet, rightSet);
        String leftAlias = getAliasFromSet(leftSet);
        String rightAlias = getAliasFromSet(rightSet);
        joinOperations
            .add(new JoinOperation<>(leftSet, leftAlias, rightSet, rightAlias, joinCondition).joinType(joinType));
        return this;
    }

    /**
     * 重载的baseJoin方法，允许指定别名
     * 
     * @param leftSet 左集合列表
     * @param leftAlias 左集合别名
     * @param rightSet 右集合列表
     * @param rightAlias 右集合别名
     * @param joinCondition 关联逻辑
     * @param joinType 关联类型
     * @return
     * @param <T>
     * @param <U>
     */
    private <T, U> MultiJoiner baseJoin(List<T> leftSet, String leftAlias, List<U> rightSet, String rightAlias,
        BiPredicate<T, U> joinCondition, JoinType joinType) {
        validateSets(leftSet, rightSet);
        joinOperations
            .add(new JoinOperation<>(leftSet, leftAlias, rightSet, rightAlias, joinCondition).joinType(joinType));
        return this;
    }

    /**
     * 提供对外的join基础方法，用于内部连接
     * 
     * @param joinOperation 关联信息
     * @param joinType 关联类型
     * @return
     * @param <T>
     * @param <U>
     */
    private <T, U> MultiJoiner baseJoin(JoinOperation<T, U> joinOperation, JoinType joinType) {
        joinOperations.add(joinOperation.joinType(joinType));
        return this;
    }

    /**
     * join 联表
     * 
     * @param leftSet 左集合
     * @param rightSet 右集合
     * @param joinCondition 关联逻辑
     */
    public <T, U> MultiJoiner join(List<T> leftSet, List<U> rightSet, BiPredicate<T, U> joinCondition) {
        return baseJoin(leftSet, rightSet, joinCondition, JoinType.INNER_JOIN);
    }

    /**
     * join 联表
     * 
     * @param leftSet 左集合
     * @param leftAlias 左集合别名
     * @param rightSet 右集合
     * @param rightAlias 右集合别名
     * @param joinCondition 关联逻辑
     */
    public <T, U> MultiJoiner join(List<T> leftSet, String leftAlias, List<U> rightSet, String rightAlias,
        BiPredicate<T, U> joinCondition) {
        return baseJoin(leftSet, leftAlias, rightSet, rightAlias, joinCondition, JoinType.INNER_JOIN);
    }

    /**
     * join 联表
     * 
     * @param joinOperation 联表操作对象
     * @return
     */
    public MultiJoiner join(JoinOperation joinOperation) {
        return baseJoin(joinOperation, JoinType.INNER_JOIN);
    }

    /**
     * left join 联表
     * 
     * @param leftSet 左集合
     * @param rightSet 右集合
     * @param joinCondition 关联逻辑
     */
    public <T, U> MultiJoiner leftJoin(List<T> leftSet, List<U> rightSet, BiPredicate<T, U> joinCondition) {
        return baseJoin(leftSet, rightSet, joinCondition, JoinType.LEFT_JOIN);
    }

    /**
     * left join 联表
     * 
     * @param leftSet 左集合
     * @param leftAlias 左集合别名
     * @param rightSet 右集合
     * @param rightAlias 右集合别名
     * @param joinCondition 关联逻辑
     */
    public <T, U> MultiJoiner leftJoin(List<T> leftSet, String leftAlias, List<U> rightSet, String rightAlias,
        BiPredicate<T, U> joinCondition) {
        return baseJoin(leftSet, leftAlias, rightSet, rightAlias, joinCondition, JoinType.LEFT_JOIN);
    }

    /**
     * left join 联表
     * 
     * @param joinOperation 联表操作对象
     * @return
     */
    public MultiJoiner leftJoin(JoinOperation joinOperation) {
        return baseJoin(joinOperation, JoinType.LEFT_JOIN);
    }

    /**
     * right join 联表
     * 
     * @param leftSet 左集合
     * @param rightSet 右集合
     * @param joinCondition 关联逻辑
     */
    public <T, U> MultiJoiner rightJoin(List<T> leftSet, List<U> rightSet, BiPredicate<T, U> joinCondition) {
        return baseJoin(leftSet, rightSet, joinCondition, JoinType.RIGHT_JOIN);
    }

    /**
     * right join 联表
     * 
     * @param leftSet 左集合
     * @param leftAlias 左集合别名
     * @param rightSet 右集合
     * @param rightAlias 右集合别名
     * @param joinCondition 关联逻辑
     */
    public <T, U> MultiJoiner rightJoin(List<T> leftSet, String leftAlias, List<U> rightSet, String rightAlias,
        BiPredicate<T, U> joinCondition) {
        return baseJoin(leftSet, leftAlias, rightSet, rightAlias, joinCondition, JoinType.RIGHT_JOIN);
    }

    /**
     * right join 联表
     * 
     * @param joinOperation 联表操作对象
     * @return
     */
    public MultiJoiner rightJoin(JoinOperation joinOperation) {
        return baseJoin(joinOperation, JoinType.RIGHT_JOIN);
    }

    /**
     * full join 联表
     * 
     * @param leftSet 左集合
     * @param rightSet 右集合
     * @param joinCondition 关联逻辑
     */
    public <T, U> MultiJoiner fullJoin(List<T> leftSet, List<U> rightSet, BiPredicate<T, U> joinCondition) {
        return baseJoin(leftSet, rightSet, joinCondition, JoinType.FULL_JOIN);
    }

    /**
     * full join 联表
     * 
     * @param leftSet 左集合
     * @param leftAlias 左集合别名
     * @param rightSet 右集合
     * @param rightAlias 右集合别名
     * @param joinCondition 关联逻辑
     */
    public <T, U> MultiJoiner fullJoin(List<T> leftSet, String leftAlias, List<U> rightSet, String rightAlias,
        BiPredicate<T, U> joinCondition) {
        return baseJoin(leftSet, leftAlias, rightSet, rightAlias, joinCondition, JoinType.FULL_JOIN);
    }

    /**
     * full join 联表
     * 
     * @param joinOperation 联表操作对象
     * @return
     */
    public MultiJoiner fullJoin(JoinOperation joinOperation) {
        return baseJoin(joinOperation, JoinType.FULL_JOIN);
    }

    /**
     * 创建联表操作对象
     */
    public static <T, U> JoinOperation.Builder<T, U> buildOperation() {
        return new JoinOperation.Builder<>();
    }

    /**
     * 根据集合类型获取别名
     * 
     * @param set
     * @return
     * @param <T>
     */
    private <T> String getAliasFromSet(List<T> set) {
        if (set == null || set.isEmpty()) {
            return "";
        }
        return set.get(0).getClass().getSimpleName();
    }

    /**
     * 校验集合对象
     * 
     * @param leftSet
     * @param rightSet
     */
    private void validateSets(List<?> leftSet, List<?> rightSet) {
        if (leftSet == null || leftSet.isEmpty()) {
            throw new IllegalArgumentException("左侧集合不能为空");
        }
        if (rightSet == null || rightSet.isEmpty()) {
            throw new IllegalArgumentException("右侧集合不能为空");
        }
    }

    /**
     * 执行多表连接操作，返回连接结果
     * 
     * @return
     */
    public Result execute() {
        List<MultiJoinResult> currentResults = new ArrayList<>();

        for (JoinOperation<?, ?> operation : joinOperations) {
            currentResults = performJoin(currentResults, operation, operation.joinType);
        }
        return new Result(currentResults);
    }

    /**
     * 执行联接操作的方法。根据连接类型（内连接、左连接、右连接、全连接）来处理连接逻辑
     * 
     * @param previousResult 结果集对象列表
     * @param operation 联表描述对象
     * @param joinType 联表类型
     */
    private <T, U> List<MultiJoinResult> performJoin(List<MultiJoinResult> previousResult,
        JoinOperation<T, U> operation, JoinType joinType) {
        List<MultiJoinResult> result = new ArrayList<>();
        if (CollUtil.isEmpty(previousResult)) {
            // 第一次关联
            performInitialJoin(operation, result, joinType);
        } else {
            // 后续关联
            performSubsequentJoin(previousResult, operation, result, joinType);
        }
        return result;
    }

    /**
     * 执行初始连接操作。在连接操作的第一阶段被调用，用于处理第一个连接操作
     * 
     * @param operation 联表描述对象
     * @param results 结果集对象列表
     * @param joinType 联表类型
     */
    private <T, U> void performInitialJoin(JoinOperation<T, U> operation, List<MultiJoinResult> results,
        JoinType joinType) {
        if (CollUtil.isEmpty(operation.leftSet.getValue()) || CollUtil.isEmpty(operation.rightSet.getValue())) {
            return;
        }
        for (T leftItem : operation.leftSet.getValue()) {
            boolean matched = false;
            for (U rightItem : operation.rightSet.getValue()) {
                if (operation.condition.test(leftItem, rightItem)) {
                    addJoinResult(results, leftItem, rightItem, operation.leftSet.getKey(),
                        operation.rightSet.getKey());
                    matched = true;
                    // 由于每个键只对应一个值，找到匹配项后即可停止
                    break;
                }
            }
            if (!matched && (joinType == JoinType.LEFT_JOIN || joinType == JoinType.FULL_JOIN)) {
                addJoinResult(results, leftItem, null, operation.leftSet.getKey(), operation.rightSet.getKey());
            }
        }
    }

    /**
     * 向结果列表中添加一个连接结果。根据给定的左侧和右侧元素以及它们的别名构造一个新的 MultiJoinResult
     * 
     * @param results 结果集对象列表
     * @param leftItem 左对象
     * @param rightItem 右对象
     * @param leftAlias 左集合别名
     * @param rightAlias 右集合别名
     */
    private void addJoinResult(List<MultiJoinResult> results, Object leftItem, Object rightItem, String leftAlias,
        String rightAlias) {
        MultiJoinResult joinResult = new MultiJoinResult();
        joinResult.put(leftAlias, leftItem);
        joinResult.put(rightAlias, rightItem);
        results.add(joinResult);
    }

    /**
     * 执行后续连接操作。用于处理第一个连接操作之后的所有连接操作
     * 
     * @param previousResults 前一次联表的结果集对象列表
     * @param operation 联表描述对象
     * @param results 用于存放联表结果的对象容器
     * @param joinType 联表类型
     */
    private <T, U> void performSubsequentJoin(List<MultiJoinResult> previousResults, JoinOperation<T, U> operation,
        List<MultiJoinResult> results, JoinType joinType) {
        for (MultiJoinResult prevResult : previousResults) {
            // T leftItem = (T)prevResult.get(operation.leftSet.getKey(),
            // operation.leftSet.getValue().get(0).getClass());
            T leftItem = prevResult.get(operation.leftSet.getKey());

            boolean matchedForThisLeftItem = false;
            for (U rightItem : operation.rightSet.getValue()) {
                if (leftItem != null && operation.condition.test(leftItem, rightItem)) {
                    MultiJoinResult joinResult = new MultiJoinResult(prevResult);
                    joinResult.put(operation.rightSet.getKey(), rightItem);
                    results.add(joinResult);
                    matchedForThisLeftItem = true;
                }
            }

            if (!matchedForThisLeftItem && (joinType == JoinType.LEFT_JOIN || joinType == JoinType.FULL_JOIN)) {
                MultiJoinResult joinResult = new MultiJoinResult(prevResult);
                joinResult.put(operation.rightSet.getKey(), null);
                results.add(joinResult);
            }
        }

        if (joinType == JoinType.RIGHT_JOIN || joinType == JoinType.FULL_JOIN) {
            for (U rightItem : operation.rightSet.getValue()) {
                boolean matchedForThisRightItem = false;
                for (MultiJoinResult prevResult : previousResults) {
                    T leftItem = prevResult.get(operation.leftSet.getKey());
                    if (leftItem != null && operation.condition.test(leftItem, rightItem)) {
                        matchedForThisRightItem = true;
                        break;
                    }
                }
                if (!matchedForThisRightItem) {
                    MultiJoinResult joinResult = new MultiJoinResult();
                    joinResult.put(operation.leftSet.getKey(), null);
                    joinResult.put(operation.rightSet.getKey(), rightItem);
                    results.add(joinResult);
                }
            }
        }
    }

    /**
     * 联表结果对象
     */
    @AllArgsConstructor
    public static class Result {
        /**
         * 存储多表连接结果的列表
         */
        private List<MultiJoinResult> dataList;

        /**
         * 获取列数据的方法
         * 
         * @return
         */
        public List<Map<String, Object>> getColumns() {
            List<Map<String, Object>> columnList =
                dataList.stream().filter(a -> a != null).map(a -> a.getColumns()).collect(Collectors.toList());
            return columnList;
        }

        /**
         * 将结果转换为带别名的Map列表
         * 
         * @return
         */
        public List<Map<String, Object>> toAliasedMapList() {
            List<Map<String, Object>> aliasedMapList = new ArrayList<>();

            for (MultiJoinResult multiJoinResult : dataList) {
                Map<String, Object> aliasedMap = new HashMap<>();
                for (Map.Entry<String, Object> entry : multiJoinResult.getColumns().entrySet()) {
                    String alias = entry.getKey();
                    Object value = entry.getValue();

                    if (value != null) {
                        // 使用反射来访问对象的字段
                        Field[] fields = value.getClass().getDeclaredFields();
                        for (Field field : fields) {
                            // 设置私有字段可访问
                            field.setAccessible(true);
                            try {
                                if (StrUtil.containsAny(field.getName(), "serialVersionUID")) {
                                    continue;
                                }
                                Object fieldValue = field.get(value);
                                String aliasedKey = alias + "." + field.getName();
                                aliasedMap.put(aliasedKey, fieldValue);
                            } catch (IllegalAccessException e) {
                                // 处理或记录异常
                                e.printStackTrace();
                            }
                        }
                    }
                }
                aliasedMapList.add(aliasedMap);
            }

            return aliasedMapList;
        }

        /**
         * 根据别名获取数据的方法
         * 
         * @param alias 表别名
         * @return
         */
        public List<Object> getByAlias(String alias) {
            Assert.notEmpty(alias, () -> new IllegalArgumentException("集合别名不能为空"));
            return dataList.stream().filter(a -> a != null && a.getColumns() != null)
                .map(a -> a.getColumns().get(alias)).collect(Collectors.toList());
        }

        /**
         * 根据类类型获取数据的方法
         * 
         * @param clazz 集合类型
         * @return
         */
        public List<Object> getByClassType(Class clazz) {
            Assert.notNull(clazz, () -> new IllegalArgumentException("目标集合对象类型不能为空"));
            return dataList.stream().filter(a -> a != null && a.getColumns() != null)
                .map(a -> a.getColumns().get(clazz.getSimpleName())).collect(Collectors.toList());
        }

        /**
         * 根据条件过滤数据的方法
         * 
         * @param alias 表别名
         * @param predicate 过滤条件
         */
        public <T> Result where(String alias, Predicate<T> predicate) {
            Assert.notEmpty(alias, () -> new IllegalArgumentException("集合别名不能为空"));
            List<MultiJoinResult> filteredList = dataList.stream().filter(a -> {
                if (a != null && a.getColumns() != null && a.getColumns().containsKey(alias)) {
                    T value = (T)a.getColumns().get(alias);
                    if (value == null) {
                        return false;
                    }
                    return predicate.test(value);
                }
                return false;
            }).collect(Collectors.toList());
            this.dataList = filteredList;
            return this;
        }

        /**
         * 根据条件过滤数据的方法
         *
         * @param classType 集合类型
         * @param predicate 过滤条件
         */
        public <T> Result where(Class<T> classType, Predicate<T> predicate) {
            Assert.notNull(classType, () -> new IllegalArgumentException("集合类型不能为空"));
            String alias = classType.getSimpleName();
            return where(alias, predicate);
        }

        /**
         * 把结果集转换成目标对象集合
         * 
         * @param targetClass
         */
        public <T> List<T> convert(Class<T> targetClass) {
            Assert.notNull(targetClass, () -> new IllegalArgumentException("目标对象类型不能为空"));
            List<T> result = new ArrayList<>();
            for (MultiJoinResult multiJoinResult : dataList) {
                try {
                    T targetObject = targetClass.getDeclaredConstructor().newInstance();
                    fillByField(targetClass, multiJoinResult, targetObject);
                    fillByMethod(targetClass, multiJoinResult, targetObject);
                    result.add(targetObject);
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException
                    | InvocationTargetException e) {
                    e.printStackTrace();
                    throw new IllegalArgumentException("集合结果集转换为"+targetClass.getSimpleName()+"对象时异常" );
                }
            }
            return result;
        }

        /**
         * 填充字段注解信息
         * 
         * @param targetClass 目标类类型
         * @param multiJoinResult 结果对象
         * @param targetObject 目标类实例
         */
        private <T> void fillByField(Class<T> targetClass, MultiJoinResult multiJoinResult, T targetObject)
            throws IllegalAccessException {
            JoinProperty clazzAnno = targetClass.getAnnotation(JoinProperty.class);
            String clazzAlias = "";
            if (clazzAnno != null) {
                clazzAlias = getAliasByJoinProperty(clazzAnno);
            }
            for (Field field : targetClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(JoinProperty.class)) {
                    JoinProperty annotation = field.getAnnotation(JoinProperty.class);
                    Object v = getValueByJoinProperty(annotation, multiJoinResult, field);
                    ReflectUtil.setFieldValue(targetObject, field, v);
                } else if (StrUtil.isNotBlank(clazzAlias)) {
                    Object defaultValue = multiJoinResult.getColumns().get(clazzAlias);
                    Object v = ReflectUtil.getFieldValue(defaultValue, field.getName());
                    ReflectUtil.setFieldValue(targetObject, field, v);
                }
            }
        }

        /**
         * 获取根据注解信息获取属性值
         * 
         * @param multiJoinResult 结果数据
         * @param field 字段信息
         */
        private Object getValueByJoinProperty(JoinProperty joinProperty, MultiJoinResult multiJoinResult, Field field) {
            String alias = getAliasByJoinProperty(joinProperty);
            Object value = multiJoinResult.getColumns().get(alias);
            String joinFieldName = joinProperty.fieldName();
            Object v;
            if (StrUtil.isBlank(joinFieldName)) {
                v = ReflectUtil.getFieldValue(value, field.getName());
            } else {
                v = ReflectUtil.getFieldValue(value, joinFieldName);
            }
            return v;
        }

        /**
         * 根据方法上的注解信息填充对象属性
         * 
         * @param targetClass
         * @param multiJoinResult
         * @param targetObject
         * @param <T>
         */
        private <T> void fillByMethod(Class<T> targetClass, MultiJoinResult multiJoinResult, T targetObject) {
            Method[] methods = targetClass.getMethods();
            for (Method method : methods) {
                if (isGetter(method)) {
                    JoinProperty joinProperty = method.getAnnotation(JoinProperty.class);
                    if (joinProperty == null) {
                        continue;
                    }
                    String fieldName = getterToFieldName(method.getName());
                    Field field = ReflectUtil.getField(targetClass, fieldName);
                    if (field == null) {
                        continue;
                    }
                    Object v = getValueByJoinProperty(joinProperty, multiJoinResult, field);
                    ReflectUtil.setFieldValue(targetObject, field, v);
                }
            }
        }

        /**
         * 判断当前方法是不是get方法
         * 
         * @param method
         * @return
         */
        private boolean isGetter(Method method) {
            if (!method.getName().startsWith("get")) {
                return false;
            }
            if (method.getParameterTypes().length != 0) {
                return false;
            }
            return method.getReturnType() != void.class;
        }

        /**
         * 根据get方法名获取对应属性名
         * 
         * @param getterName
         * @return
         */
        private String getterToFieldName(String getterName) {
            // 去除 "get" 并将首字母小写
            return Character.toLowerCase(getterName.charAt(3)) + getterName.substring(4);
        }

        /**
         * 根据注解信息获取别名
         * 
         * @param joinProperty
         * @return
         */
        private String getAliasByJoinProperty(JoinProperty joinProperty) {
            String alias = joinProperty.alias();
            if (StrUtil.isNotBlank(alias)) {
                return alias;
            }
            Class classType = joinProperty.classType();
            if (classType != null) {
                return classType.getSimpleName();
            }
            return "";
        }

    }

    /**
     * 枚举类型，定义连接类型
     */
    public enum JoinType {
        /**
         * 内连接
         */
        INNER_JOIN,
        /**
         * 左连接
         */
        LEFT_JOIN,
        /**
         * 右连接
         */
        RIGHT_JOIN,
        /**
         * 全连接 返回左表右表所有行
         */
        FULL_JOIN
    }

    /**
     * 联表描述对象
     * 
     * @param <T>
     * @param <U>
     */
    @Data
    private static class JoinOperation<T, U> {
        /**
         * 左集合及其别名
         */
        private Pair<String, List<T>> leftSet;
        /**
         * 右集合及其别名
         */
        private Pair<String, List<U>> rightSet;
        /**
         * 集合对象关联逻辑
         */
        private BiPredicate<T, U> condition;

        /**
         * 联表类型
         */
        private JoinType joinType = JoinType.INNER_JOIN;

        /**
         * 联表描述对象 构造方法
         * 
         * @param leftSet 左集合
         * @param leftAlias 左集合别名
         * @param rightSet 右集合
         * @param rightAlias 右结合别名
         * @param condition 联表条件
         */
        JoinOperation(List<T> leftSet, String leftAlias, List<U> rightSet, String rightAlias,
            BiPredicate<T, U> condition) {
            this.leftSet = new Pair<>(leftAlias, leftSet);
            this.rightSet = new Pair<>(rightAlias, rightSet);
            this.condition = condition;
        }

        /**
         * 根据联表描述buider对象创建联表描述对象的构造方法
         * 
         * @param builder 联表描述的builder对象
         */
        JoinOperation(Builder<T, U> builder) {
            this.leftSet = builder.leftSet;
            this.rightSet = builder.rightSet;
            this.condition = builder.condition;
        }

        /**
         * 联表类型
         * 
         * @param joinType
         * @return
         */
        public JoinOperation joinType(JoinType joinType) {
            this.joinType = joinType;
            return this;
        }

        /**
         * 联表描述的builder对象
         * 
         * @param <T>
         * @param <U>
         */
        public static class Builder<T, U> {
            /**
             * 左结合对象及其别名
             */
            Pair<String, List<T>> leftSet;
            /**
             * 右集合对象及其别名
             */
            Pair<String, List<U>> rightSet;
            /**
             * 联表条件对象
             */
            BiPredicate<T, U> condition;
            /**
             * 服务类后缀
             */
            private static String suffix = "ServiceImpl";

            /**
             * 设置左集合
             * 
             * @param leftList
             * @return
             */
            public Builder<T, U> left(List<T> leftList) {
                if (CollUtil.isEmpty(leftList)) {
                    leftSet = new Pair<>(StrUtil.UNDERLINE, leftList);
                    return this;
                }
                leftSet = new Pair<>(leftList.get(0).getClass().getSimpleName(), leftList);
                return this;
            }

            /**
             * 设置左集合
             *
             * @param leftAlias 左集合别名
             * @return
             */
            public Builder<T, U> left(String leftAlias) {
                leftSet = new Pair<>(leftAlias, null);
                return this;
            }

            /**
             * 设置左集合
             *
             * @param leftClass 左集合类型
             * @return
             */
            public Builder<T, U> left(Class<T> leftClass) {
                Assert.notNull(leftClass, () -> new IllegalArgumentException("左集合别名不能为空"));
                leftSet = new Pair<>(leftClass.getSimpleName(), null);
                return this;
            }

            /**
             * 设置右集合
             *
             * @param rightAlias 右集合别名
             * @return
             */
            public Builder<T, U> right(String rightAlias) {
                rightSet = new Pair<>(rightAlias, null);
                return this;
            }

            /**
             * 设置右集合
             *
             * @param rightClass 右集合类型
             * @return
             */
            public Builder<T, U> right(Class<T> rightClass) {
                Assert.notNull(rightClass, () -> new IllegalArgumentException("右集合别名不能为空"));
                leftSet = new Pair<>(rightClass.getSimpleName(), null);
                return this;
            }

            /**
             * 设置左集合及其别名
             * 
             * @param leftList
             * @return
             */
            public Builder<T, U> left(List<T> leftList, String leftAlias) {
                leftSet = new Pair<>(leftAlias, leftList);
                return this;
            }

            /**
             * 设置右集合
             * 
             * @param rightList
             * @return
             */
            public Builder<T, U> right(List<U> rightList) {
                if (CollUtil.isEmpty(rightList)) {
                    rightSet = new Pair<>(StrUtil.UNDERLINE, rightList);
                    return this;
                }
                rightSet = new Pair<>(rightList.get(0).getClass().getSimpleName(), rightList);
                return this;
            }

            /**
             * 设置右集合及其别名
             * 
             * @param rightList
             * @param rightAlias
             * @return
             */
            public Builder<T, U> right(List<U> rightList, String rightAlias) {
                rightSet = new Pair<>(rightAlias, rightList);
                return this;
            }

            /**
             * 设置关联条件
             * 
             * @param condition
             * @return
             */
            public Builder<T, U> on(BiPredicate<T, U> condition) {
                this.condition = condition;
                return this;
            }

            /**
             * 构建联表描述对象
             */
            public JoinOperation<T, U> build() {
                Assert.notNull(leftSet, () -> new IllegalArgumentException("leftList不能为空"));
                Assert.notNull(rightSet, () -> new IllegalArgumentException("rightList不能为空"));
                Assert.notNull(condition, () -> new IllegalArgumentException("集合关联on条件不能为空"));
                return new JoinOperation<>(this);
            }

        }
    }

    /**
     * 存储单个连接结果的类。包含一个映射（Map），键为别名，值为对应的对象
     */
    @Data
    @NoArgsConstructor
    private static class MultiJoinResult {
        /**
         * 键为别名，值为对应的对象
         */
        private Map<String, Object> column = new HashMap<>();

        /**
         * 复制老集合对象属性创建新集合对象
         * 
         * @param result 老结果对象
         */
        public MultiJoinResult(MultiJoinResult result) {
            this.column = new HashMap<>(result.column);
        }

        /**
         * 把结果对象根据别名存入map
         * 
         * @param alias 别名对象
         * @param item 结果对象
         */
        public void put(String alias, Object item) {
            column.put(alias, item);
        }

        /**
         * 根据别名获取结果对象并转为指的类型
         * 
         * @param alias 别名
         */
        public <T> T get(String alias) {
            Object object = column.get(alias);
            if (object == null) {
                return null;
            }
            return (T)object;
        }

        /**
         * 结果对象
         */
        public Map<String, Object> getColumns() {
            return column;
        }
    }

}
