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
package io.xream.x7.repository.dao;

import io.xream.x7.common.util.ExceptionUtil;
import io.xream.x7.repository.exception.PersistenceException;
import io.xream.x7.repository.exception.QueryException;
import org.slf4j.Logger;

public class DaoExceptionTranslator {

    private DaoExceptionTranslator(){}

    public static  PersistenceException onRollback(Object obj, Exception e, Logger logger) {
        Throwable t = ExceptionUtil.unwrapThrowable(e);
        String msg = ExceptionUtil.getMessage(t);
        String objStr = obj == null ? "": obj.toString();
        String logStr =  objStr + ", Exception: " + msg;
        String eStr =  objStr + ", \nException: " + msg;
        logger.error(logStr);
        return new PersistenceException(eStr);
    }

    public static QueryException onQuery(Exception e, Logger logger) {
        Throwable t = ExceptionUtil.unwrapThrowable(e);
        String msg = ExceptionUtil.getMessage(t);
        logger.error(msg);
        return new QueryException(msg);
    }
}
