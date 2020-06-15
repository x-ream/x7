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
import io.xream.x7.repository.TemporaryRepository;
import io.xream.x7.repository.dao.TemporaryDao;
import io.xream.x7.repository.transform.DataTransform;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DefaultTemporaryRepository implements TemporaryRepository {

    @Autowired
    private TemporaryDao temporaryDao;
    @Autowired
    private TemporaryRepository.Parser temporaryRepositoryParser;

    private DataTransform dataTransform;
    public void setDataTransform(DataTransform dataTransform) {
        this.dataTransform = dataTransform;
    }


    @Override
    public boolean create(Object obj) {
        boolean flag = this.dataTransform.create(obj) > 0;
        System.out.println(this.dataTransform.getOne(obj));
        return flag;
    }

    @Override
    public boolean createBatch(List objList) {
        return this.dataTransform.createBatch(objList);
    }

    @Override
    public boolean findToCreate(Class clzz, Criteria.ResultMappedCriteria resultMappedCriteria) {

        Parsed parsed = io.xream.x7.common.bean.Parser.get(clzz.getSimpleName());
        if (parsed == null) {
            io.xream.x7.common.bean.Parser.parse(clzz);
        }

        return this.temporaryDao.findToCreate(clzz, resultMappedCriteria);
    }

    @Override
    public boolean createRepository(Class clzz) {
        String sql = temporaryRepositoryParser.parseAndGetSql(clzz);
        return this.temporaryDao.execute(sql);
    }

    @Override
    public boolean dropRepository(Class clzz) {
        Parsed parsed = io.xream.x7.common.bean.Parser.get(clzz.getSimpleName());
        if (parsed == null) {
            parsed = io.xream.x7.common.bean.Parser.get(clzz);
        }
        String sql = "DROP TABLE " + parsed.getTableName();
        return this.temporaryDao.execute(sql);
    }
}
