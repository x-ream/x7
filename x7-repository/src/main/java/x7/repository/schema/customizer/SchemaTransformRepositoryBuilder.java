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
package x7.repository.schema.customizer;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import x7.core.bean.SchemaTransform;
import x7.core.bean.Transformed;
import x7.repository.BaseRepository;
import x7.repository.HealthChecker;
import x7.repository.internal.RepositoryProxy;
import x7.repository.schema.SchemaTransformRepository;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class SchemaTransformRepositoryBuilder {

    public static BeanDefinitionRegistry registry = null;

    public Class<? extends BaseRepository> build(Class<? extends BaseRepository> clzz){

        String beanName = SchemaTransformRepository.class.getName();
        registry.removeBeanDefinition(beanName);
        for (BaseRepository baseRepository : HealthChecker.getRepositoryList()){
            if (baseRepository.getClz() == SchemaTransform.class){
                HealthChecker.getRepositoryList().remove(baseRepository);
                break;
            }
        }

        beanName = clzz.getName();

        Type[] types = clzz.getGenericInterfaces();

        ParameterizedType parameterized = (ParameterizedType) types[0];
        Class clazz = (Class) parameterized.getActualTypeArguments()[0];

        {
            Class[] clzArr = clazz.getInterfaces();
            boolean flag = false;
            for (Class clz : clzArr){
                if (clz == Transformed.class){
                    flag = true;
                    break;
                }
            }
            if (!flag)
                throw new RuntimeException("SchemaTransform Customized Class has to implements x7.core.bean.Transformed");
        }

        if (registry.containsBeanDefinition(beanName))
            return clzz;

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clzz);
        GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
        definition.getPropertyValues().add("objectType", clzz);
        definition.getPropertyValues().add("clz", clazz);
        definition.setBeanClass(RepositoryProxy.class);
        definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);

        registry.registerBeanDefinition(beanName, definition);

        return clzz;
    }

}
