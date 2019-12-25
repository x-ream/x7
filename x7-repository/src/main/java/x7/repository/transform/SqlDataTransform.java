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
package x7.repository.transform;

import x7.core.bean.Criteria;
import x7.core.bean.condition.InCondition;
import x7.core.bean.condition.RefreshCondition;
import x7.core.web.Page;
import x7.repository.KeyOne;
import x7.repository.dao.Dao;

import java.util.List;
import java.util.Map;

public class SqlDataTransform implements DataTransform {

    private Dao dao;
    public void setDao(Dao dao) {
        this.dao = dao;
    }

    @Deprecated
    @Override
    public <T> void refreshCache(Class<T> clz) {
        throw new RuntimeException("Wrong Code");
    }

    @Override
    public long create(Object obj) {

            return this.dao.create(obj);

    }

    @Override
    public boolean createBatch(List<?> objList) {

            return this.dao.createBatch(objList);

    }


    @Override
    public <T> boolean refresh(RefreshCondition<T> refreshCondition) {

            return this.dao.refreshByCondition(refreshCondition);

    }

    @Override
    public <T> boolean remove(KeyOne<T> keyOne) {

            return this.dao.remove(keyOne);

    }


    @Override
    public <T> boolean execute(T obj, String sql) {

            return this.dao.execute(obj, sql);

    }


    @Override
    public <T> List<T> list(Object obj) {

            return this.dao.list(obj);

    }

    @Override
    public List<Map<String, Object>> list(Class clz, String sql, List<Object> conditionList) {
        return this.dao.list(clz, sql, conditionList);
    }

    @Override
    public <T> T getOne(T obj) {//带ID查询, 不需要alia; 不带ID查询,需要alia

            return this.dao.getOne(obj);

    }



    @Override
    public <T> List<T> in(InCondition inCondition) {

            return this.dao.in(inCondition);

    }


    @Override
    public <T> Page<T> find(Criteria criteria) {

            return this.dao.find(criteria);
    }

    @Override
    public Page<Map<String, Object>> find(Criteria.ResultMappedCriteria criteria) {

        return this.dao.find(criteria);
    }

    @Override
    public List<Map<String, Object>> list(Criteria.ResultMappedCriteria criteria) {

        return this.dao.list(criteria);
    }

    @Override
    public <T> List<T> list(Criteria criteria) {

            return this.dao.list(criteria);

    }

}
