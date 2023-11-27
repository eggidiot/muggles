package com.muggles.fun.tools.core.spel;

import cn.hutool.core.util.ReUtil;
import lombok.experimental.UtilityClass;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * spring spel表达式特定语法工具
 */
@UtilityClass
public class SpelUtil {
    /**
     * 根据SPEL表达式从原始对象获取data信息
     *
     * @param spel  el表达式
     * @param value 原始对象
     * @return Object
     */
    public Object dataInfo(String spel, Object value) {
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext(value);
        context.setVariable(variableName(spel), value);
        return parser.parseExpression(spel).getValue(context);
    }

    /**
     * 根据正则表达式获取第一个英文单词作为context设置变量的名称
     *
     * @param spel el表达式
     * @return String
     */
    String variableName(String spel) {
        String regex = "\\w+";
        return ReUtil.getGroup0(regex, spel);
    }

    /**
     * 根据SPEL表达式将Token授权信息设置回原始对象
     *
     * @param spel   el表达式
     * @param value  el表达式对应的原值
     * @param target 授权对象的取值所在对象
     * @return Object
     */
    Object wrapperInfo(String spel, Object value, Object target) {
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext(value);
        context.setVariable(variableName(spel), value);
        parser.parseExpression(spel).setValue(context, target);
        return parser.parseExpression("#" + variableName(spel)).getValue(context);
    }
}
