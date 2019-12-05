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
package io.xream.x7.reyc;

import io.xream.x7.reyc.internal.ClientParser;
import io.xream.x7.reyc.internal.HttpClientProxy;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import x7.core.util.ClassFileReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class ReyClientBeanRegistrar implements EnvironmentAware,ImportBeanDefinitionRegistrar  {


    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {

        String startClassName = annotationMetadata.getClassName();
        String basePackage = startClassName.substring(0, startClassName.lastIndexOf("."));

        Set<Class<?>> set = ClassFileReader.getClasses(basePackage);

        List<String> beanNameList = new ArrayList<>();

        for (Class clz : set) {
            ReyClient annotation = (ReyClient)clz.getAnnotation(ReyClient.class);
            if (annotation == null)
                continue;

            ClientParser.parse(clz,environment);

            String beanName = clz.getName();
            beanNameList.add(beanName);

            String backend = annotation.circuitBreaker();
            if (backend.equals(" ")){
                backend = null;
            }

            boolean retry = annotation.retry();

            if (!registry.containsBeanDefinition(beanName)) {

                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clz);
                GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
                definition.getPropertyValues().add("objectType", clz);
                definition.getPropertyValues().add("backend",backend);
                definition.getPropertyValues().add("retry",retry);
                definition.setBeanClass(HttpClientProxy.class);
                definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);

                registry.registerBeanDefinition(beanName, definition);

            }
        }
    }


    private Environment environment;
    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
