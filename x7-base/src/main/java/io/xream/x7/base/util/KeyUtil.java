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

import io.xream.internal.util.VerifyUtil;

import java.lang.reflect.Method;

public class KeyUtil {

    private KeyUtil() {
    }

    public static String makeKey(String prefix, String condition,Method method,Object[] args) {
        String str = SpringHeplerUtil.parseSPEL(condition,method,args);
        return VerifyUtil.toMD5(prefix + str);
    }

    public static String makeLockKey(String clzzShortName,String prefix, String condition,Method method,Object[] args) {
        String value = SpringHeplerUtil.parseSPEL(condition,method,args);
        String md5 = VerifyUtil.toMD5(prefix).toLowerCase();
        md5 = md5.substring(0,7);
        return "/lock/" + clzzShortName + "/" + method.getName() + "/" + md5 + "/" + value;
    }
}
