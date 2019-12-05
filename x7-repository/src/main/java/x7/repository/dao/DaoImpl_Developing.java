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
package x7.repository.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import x7.core.bean.BeanElement;
import x7.core.bean.Criteria;
import x7.core.bean.condition.InCondition;
import x7.core.bean.condition.RefreshCondition;
import x7.core.web.Page;
import x7.repository.CriteriaParser;
import x7.repository.KeyOne;
import x7.repository.mapper.Mapper;
import x7.repository.util.ResultSetUtil;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

/**
 * @author Sim
 */
public class DaoImpl_Developing implements Dao {


    private final static Logger logger = LoggerFactory.getLogger(Dao.class);

    public DaoImpl_Developing() {
    }

    @Autowired
    private CriteriaParser criteriaParser;

    @Autowired
    private Mapper.Dialect dialect;

    public void setDialect(Mapper.Dialect dialect) {
        this.dialect = dialect;
    }
    public void setCriteriaParser(CriteriaParser criteriaParser){
        this.criteriaParser = criteriaParser;
    }



    private <T> void initObj(T obj, ResultSet rs, BeanElement tempEle, List<BeanElement> eles)
            throws Exception {

        ResultSetUtil.initObj(obj, rs, tempEle, eles);
    }


    @Override
    public long create(Object obj) {
        return 0;
    }

    @Override
    public boolean createBatch(List<?> objList) {
        return false;
    }

    @Override
    public <T> boolean remove(KeyOne<T> keyOne) {
        return false;
    }

    @Override
    public <T> boolean refreshByCondition(RefreshCondition<T> conditon) {
        return false;
    }

    @Override
    public <T> List<T> list(Object conditionObj) {
        return null;
    }

    @Override
    public List<Map<String, Object>> list(Class clz, String sql, List<Object> conditionList) {
        return null;
    }

    @Override
    public <T> T getOne(T conditionObj) {
        return null;
    }

    @Override
    public <T> List<T> in(InCondition inCondition) {
        return null;
    }

    @Override
    public Page<Map<String, Object>> find(Criteria.ResultMappedCriteria resultMapped) {
        return null;
    }

    @Override
    public List<Map<String, Object>> list(Criteria.ResultMappedCriteria resultMapped) {
        return null;
    }

    @Override
    public <T> Page<T> find(Criteria criteria) {
        return null;
    }

    @Override
    public <T> List<T> list(Criteria criteria) {
        return null;
    }

    @Override
    public <T> boolean execute(T obj, String sql) {
        return false;
    }
}