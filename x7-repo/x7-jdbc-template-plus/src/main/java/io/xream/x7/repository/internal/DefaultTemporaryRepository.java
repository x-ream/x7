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
package io.xream.x7.repository.internal;

import io.xream.x7.common.bean.Criteria;
import io.xream.x7.common.bean.Parsed;
import io.xream.x7.common.util.ExceptionUtil;
import io.xream.x7.repository.TemporaryRepository;
import io.xream.x7.repository.dao.TemporaryDao;
import io.xream.x7.repository.transform.DataTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.Callable;

public class DefaultTemporaryRepository implements TemporaryRepository {

    private Logger logger = LoggerFactory.getLogger(TemporaryRepository.class);

    @Autowired
    private TemporaryDao temporaryDao;
    @Autowired
    private TemporaryRepository.Parser temporaryRepositoryParser;

    private DataTransform dataTransform;
    public void setDataTransform(DataTransform dataTransform) {
        this.dataTransform = dataTransform;
    }


    private boolean doProxy(String logTag, Callable<Boolean> callable) {
        long startTime = System.currentTimeMillis();
        boolean flag = false;
        try {
            flag = callable.call();
        }catch (Exception e){
            logger.warn("{} exception: {}" , logTag,ExceptionUtil.getMessage(e));
            throw new RuntimeException(ExceptionUtil.getMessage(e));
        }finally {
            long endTime = System.currentTimeMillis();
            logger.info("{} result: {}, cost time: {}ms" , logTag, flag, (endTime - startTime));
        }

        return flag;
    }


    @Override
    public boolean create(Object obj) {
        return doProxy("create(Object)", () -> dataTransform.create(obj) > 0);
    }

    @Override
    public boolean createBatch(List objList) {
        return doProxy("createBatch(List)", () -> dataTransform.createBatch(objList) );
    }

    @Override
    public boolean findToCreate(Class clzz, Criteria.ResultMappedCriteria resultMappedCriteria) {

        return doProxy("findToCreate(Class, ResultMappedCriteria)", () -> {
            Parsed parsed = io.xream.x7.common.bean.Parser.get(clzz.getSimpleName());
            if (parsed == null) {
                io.xream.x7.common.bean.Parser.parse(clzz);
            }

            return temporaryDao.findToCreate(clzz, resultMappedCriteria);
        });

    }

    @Override
    public boolean createRepository(Class clzz) {

        return doProxy("createRepository(Class)", () -> {
            String sql = temporaryRepositoryParser.parseAndGetSql(clzz);
            return temporaryDao.execute(sql);
        });

    }

    @Override
    public boolean dropRepository(Class clzz) {

        return doProxy("dropRepository(Class)", () -> {
            Parsed parsed = io.xream.x7.common.bean.Parser.get(clzz.getSimpleName());
            if (parsed == null) {
                parsed = io.xream.x7.common.bean.Parser.get(clzz);
            }
            String sql = "DROP TABLE " + parsed.getTableName();
            return temporaryDao.execute(sql);
        });

    }
}
