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
package io.xream.x7.repository.dao;

import io.xream.x7.common.bean.Criteria;
import io.xream.x7.common.bean.Parsed;
import io.xream.x7.common.bean.Parser;
import io.xream.x7.common.bean.SqlScript;
import io.xream.x7.common.util.BeanUtil;
import io.xream.x7.repository.CriteriaToSql;
import io.xream.x7.repository.SqlParsed;
import io.xream.x7.repository.mapper.Dialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class TemporaryDaoImpl implements TemporaryDao{

    @Autowired
    private CriteriaToSql criteriaToSql;
    @Autowired
    private Dialect dialect;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public boolean findToCreate(Class clzz, Criteria.ResultMappedCriteria resultMappedCriteria) {

        SqlParsed sqlParsed = SqlUtil.fromCriteria(resultMappedCriteria, criteriaToSql, dialect);
        StringBuilder fromSqlSb = sqlParsed.getSql();

        List<Object> list = resultMappedCriteria.getValueList();

        Parsed parsed = Parser.get(clzz);

        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TEMPORARY TABLE IF NOT EXISTS ").append(parsed.getTableName())
                .append(SqlScript.AS);

        if (list == null || list.isEmpty()) {
            jdbcTemplate.execute(sb.append(fromSqlSb).toString());
        } else {
            Object[] arr = dialect.toArr(list);
            String fromSql = fromSqlSb.toString();
            for (Object obj : arr) {
                if (obj instanceof String) {
                    fromSql = fromSql.replaceFirst("\\?", "'" + obj.toString() + "'");
                }else if (BeanUtil.isEnum(obj.getClass())){
                    fromSql = fromSql.replaceFirst("\\?", "'" + ((Enum)obj).name() + "'");
                }else if (obj instanceof Date ) {
                    fromSql = fromSql.replaceFirst("\\?",
                            "DATE_FORMAT(" + ((Date)obj).getTime()
                            + ", '%Y-%m-%d %h:%i:%s')"
                    );
                }else if (obj instanceof Timestamp) {
                    fromSql = fromSql.replaceFirst("\\?",
                            "DATE_FORMAT(" + ((Timestamp)obj).getTime()
                                    + ", '%Y-%m-%d %h:%i:%s')"
                    );
                }else {
                    fromSql = fromSql.replaceFirst("\\?", obj.toString());
                }
            }
            sb.append(fromSql);
            jdbcTemplate.execute(sb.toString());
        }

        return true;
    }

    @Override
    public boolean execute(String sql) {
        this.jdbcTemplate.execute(sql);
        return true;
    }
}
