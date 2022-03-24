/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.xream.x7.base.util;

import io.xream.internal.util.StringUtil;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

/**
 * @author Sim
 */
public class SpringHeplerUtil {

    public static String parseSPEL(String condition, Method method, Object[] args) {
        if (StringUtil.isNullOrEmpty(condition))
            return "";
        if (!condition.contains("#") || (args == null || args.length==0))
            return condition;
        ExpressionParser parser = new SpelExpressionParser();
        LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();
        String[] names = discoverer.getParameterNames(method);
        EvaluationContext ctx = new StandardEvaluationContext();
        int length = names.length;
        for (int i = 0; i < length; i++) {
            ctx.setVariable(names[i], args[i]);
        }
        return parser.parseExpression(condition).getValue(ctx, String.class);
    }
}
