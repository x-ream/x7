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
package io.xream.x7;

import io.xream.internal.util.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class SpringHelper implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(SpringHelper.class);

    public SpringHelper(){
    }

    private static ApplicationContext applicationContext;

    private static Map<Method, String> mappingMap = new HashMap<Method, String>();

    @Override
    public void setApplicationContext(ApplicationContext arg0) throws BeansException {
        applicationContext = arg0;
        getMapPaths();
    }

    public static Object getObject(String beanName) {
        if (applicationContext == null)
            return null;
        Object object = null;
        try {
            object = applicationContext.getBean(beanName);
        } catch (Exception e) {
            logger.error("_________" + ExceptionUtil.getMessage(e));
        }
        return object;
    }

    public static ApplicationContext getContext() {
        return applicationContext;
    }

    public static <T> T getObject(Class<T> clazz) {
        if (applicationContext == null)
            return null;
        try {
            return applicationContext.getBean(clazz);
        } catch (Exception e) {
            logger.error(ExceptionUtil.getMessage(e));
        }
        return null;
    }

    public static String getRequestMapping(Method method) {
        return mappingMap.get(method);
    }

    private static void getMapPaths() {
        RequestMappingHandlerMapping rmhp = getObject(RequestMappingHandlerMapping.class);
        if (rmhp != null) {
            Map<RequestMappingInfo, HandlerMethod> map = rmhp.getHandlerMethods();
            for (RequestMappingInfo info : map.keySet()) {
                String mapping = info.getPatternsCondition().toString().replace("[", "").replace("]", "");
                HandlerMethod hm = map.get(info);
                mappingMap.put(hm.getMethod(), mapping);
                logger.info("{}, {}", mapping, hm.getMethod());
            }
        }
    }
}
