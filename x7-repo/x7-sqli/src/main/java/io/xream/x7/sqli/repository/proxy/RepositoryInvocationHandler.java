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


import io.xream.sqli.api.BaseRepository;
import io.xream.sqli.api.RepositoryX;
import io.xream.sqli.dialect.DynamicDialectKeyRemovable;
import io.xream.sqli.util.SqliLoggerProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Sim
 */
public class RepositoryInvocationHandler implements InvocationHandler, DynamicDialectKeyRemovable {

    private RepositoryProxyX repository;

    public RepositoryInvocationHandler(RepositoryProxyX repository){
        this.repository = repository;
    }

    private String proxyString() {
        String str = null;
        if (BaseRepository.class.isAssignableFrom(repository.getObjectType())) {
            str = "BaseRepository<" + repository.getClzz().getSimpleName() + ">";
        }
        if (RepositoryX.class.isAssignableFrom(repository.getObjectType())) {
            if (str == null){
                str = "RepositoryX";
            }else {
                str += ", RepositoryX";
            }
        }
        return str;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try{

            String methodName = method.getName();
            if (methodName.equals("toString"))
                return proxyString();
            final Class clz = repository.getClzz() == Void.class ? repository.getObjectType() : repository.getClzz();
            repository.setRepositoryClzz(repository.getObjectType());

            SqliLoggerProxy.debug(clz, () -> {
                String argStr = (args != null && args.length > 0) ? args[0].getClass().getSimpleName() : "";
                return method.getName() + "(" +  argStr + ")";
            });

            final long startTime = SqliLoggerProxy.getTimeMills(clz);

            Object obj =  method.invoke(repository,args);

            final long endTime = SqliLoggerProxy.getTimeMills(clz);

            SqliLoggerProxy.debug(clz, () ->
                 obj +"\n________" + method.getName() + ", cost time: "+ (endTime - startTime) + "ms"
            );

            return obj;
        } catch (InvocationTargetException e){
            throw e.getTargetException();
        }finally {
            removeDialectKey();
        }
    }
}
