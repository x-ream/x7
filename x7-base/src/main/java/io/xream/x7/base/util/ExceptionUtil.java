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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

public class ExceptionUtil {

    private ExceptionUtil(){}

    public static String getMessage(Exception e){
        String msg = e.getMessage();
        msg += "\n";
        StackTraceElement[] eleArr = e.getStackTrace();
        if (eleArr == null || eleArr.length == 0)
            return msg;
        msg += eleArr[0].toString();
        msg += "\n";
        int length = eleArr.length;
        if (eleArr != null && length > 0){
            if (length > 2){
                msg += eleArr[1].toString();
                msg += "\n";
                msg += eleArr[2].toString();
            }else if (length > 1){
                msg += eleArr[1].toString();
            }
        }

        return msg;
    }

    public static String getMessage(Throwable e){
        String msg = e.getMessage();
        msg += "\n";
        StackTraceElement[] eleArr = e.getStackTrace();
        if (eleArr == null || eleArr.length == 0)
            return msg;
        msg += eleArr[0].toString();
        msg += "\n";
        int length = eleArr.length;
        if (eleArr != null && length > 0){
            if (length > 2){
                msg += eleArr[1].toString();
                msg += "\n";
                msg += eleArr[2].toString();
            }else if (length > 1){
                msg += eleArr[1].toString();
            }
        }

        return msg;
    }

    public static Throwable unwrapThrowable(Throwable wrapped) {
        Throwable unwrapped = wrapped;
        while (true) {
            if (unwrapped instanceof InvocationTargetException) {
                unwrapped = ((InvocationTargetException) unwrapped).getTargetException();
            } else if (unwrapped instanceof UndeclaredThrowableException) {
                unwrapped = ((UndeclaredThrowableException) unwrapped).getUndeclaredThrowable();
            } else {
                return unwrapped;
            }
        }
    }
}
