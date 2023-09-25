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

import io.xream.internal.util.ClassFileReader;
import io.xream.sqli.api.BaseRepository;
import io.xream.sqli.api.RepositoryX;
import io.xream.sqli.filter.BaseTypeFilter;
import io.xream.x7.sqli.repository.proxy.RepositoryProxyX;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Repository;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


class RepositoryBeanRegistrar implements ImportBeanDefinitionRegistrar {


    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {

        String startClassName = annotationMetadata.getClassName();
        String basePackage = startClassName.substring(0, startClassName.lastIndexOf("."));

        Set<Class<?>> set = ClassFileReader.getClasses(basePackage);

        Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(EnableX7Repository.class.getName());

        Object supported = attributes.get("baseTypeSupported");
        BaseTypeFilter.baseTypeSupported = Boolean.valueOf(String.valueOf(supported));

        Object obj = attributes.get("basePackages");
        if (obj != null){
            String[] strs = (String[]) obj;
            for (String str : strs){
                Set<Class<?>> set1 = ClassFileReader.getClasses(str);
                set.addAll(set1);
            }
        }

        List<String> beanNameList = new ArrayList<>();

        List<Class> list = new ArrayList<>();
        for (Class clz : set) {
            Annotation annotation = clz.getAnnotation(Repository.class);
            if (annotation == null)
                continue;

            Type[] types = clz.getGenericInterfaces();
            if (types.length == 0)
                continue;
            if (! ((types[0].getTypeName().startsWith(BaseRepository.class.getName()))
                    || (types[0].getTypeName().startsWith(RepositoryX.class.getName()))))
                continue;

            list.add(clz);
        }

        for (Class clz : list) {

            Type[] types = clz.getGenericInterfaces();

            Class clazz = Void.class;
            try {
                ParameterizedType parameterized = (ParameterizedType) types[0];
                clazz = (Class) parameterized.getActualTypeArguments()[0];
            }catch (Exception e){

            }

            String beanName = clz.getName();
            beanNameList.add(beanName);

            if (!registry.containsBeanDefinition(beanName)) {

                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clz);
                GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
                definition.getPropertyValues().add("objectType", clz);
                definition.getPropertyValues().add("clz", clazz);
                definition.setBeanClass(RepositoryProxyX.class);
                definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);

                registry.registerBeanDefinition(beanName, definition);

            }
        }
    }

}
