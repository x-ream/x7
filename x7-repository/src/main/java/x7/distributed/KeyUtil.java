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
package x7.distributed;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import x7.core.util.VerifyUtil;

public class KeyUtil {

    public static String makeKey(String prefix, String suffix, String condition, Object[] args) {

        if (args != null && args.length > 0 && condition.contains("#") && condition.contains(".")) {

            int start = condition.indexOf("#") + 1;
            int end = condition.indexOf(".");
            String objName = condition.substring(start,end);

            Object obj = null;
            for (Object arg : args) {
                if (arg instanceof Key) {
                    obj = arg;
                }
            }

            if (obj == null) {
                obj = args[0];
            }

            ExpressionParser parser = new SpelExpressionParser();
            EvaluationContext ctx = new StandardEvaluationContext();
            ctx.setVariable(objName, obj);

            condition = parser.parseExpression(condition).getValue(ctx,String.class);
        }

        String key = VerifyUtil.toMD5(prefix + condition) + suffix;

        return key;
    }
}
