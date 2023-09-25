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
package io.xream.x7.sqli.repository.proxy;

import io.xream.sqli.core.Repository;
import io.xream.sqli.repository.internal.DefaultRepositoryX;
import io.xream.sqli.spi.IdGeneratorProxy;
import io.xream.sqli.util.SqliLoggerProxy;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

/**
 * Biz Repository extends DefaultRepository
 *
 * @param <T>
 * @author Sim
 */
public class RepositoryProxyX<T> extends DefaultRepositoryX<T> implements FactoryBean {

    @Override
    public Class<T> getClzz() {
        return super.getClzz();
    }

    public void setClz(Class<T> clz) {
        super.setClz(clz);
        super.hook();
        SqliLoggerProxy.put(objectType, LoggerFactory.getLogger(objectType));
        if (clz != Void.class) {
            SqliLoggerProxy.put(clz, LoggerFactory.getLogger(objectType));
        }
    }

    public RepositoryProxyX(){
    }


    public void setIdGenerator(IdGeneratorProxy idGeneratorService){
        super.setIdGenerator(idGeneratorService);
    }
    @Override
    public void setRepository(Repository dataRepository){
        super.setRepository(dataRepository);
    }

    private Class<?> objectType;
    public void setObjectType(Class<?> objectType) {
        this.objectType = objectType;
    }

    @Override
    public Object getObject() throws Exception {
        return Proxy.newProxyInstance(objectType.getClassLoader(), new Class[]{objectType},new RepositoryInvocationHandler(this));
    }

    @Override
    public Class<?> getObjectType() {
        return this.objectType;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setRepositoryClzz(Class clzz) {
        super.setRepositoryClzz(clzz);
    }

}
