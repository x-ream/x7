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
package x7.repository;

import x7.core.bean.SqlScript;

import java.util.List;

public class SqlParsed {

    private String countSql;
    private StringBuilder sql;
    private List<UnionSql> unionSqlList;
    private StringBuilder sortScript;

    public String getCountSql() {
        return countSql;
    }

    public void setCountSql(String countSql) {
        this.countSql = countSql;
    }


    public StringBuilder getSql(int page, int rows) {
        if (sql != null)
            return sql;

        StringBuilder sb = new StringBuilder();
        int size = unionSqlList.size();
        for (int i=0; i<size; i++) {
            UnionSql unionSql = unionSqlList.get(i);
            sb.append(SqlScript.LEFT_PARENTTHESIS).append(unionSql.getSql()).append(SqlScript.RIGHT_PARENTTHESIS);
            if (i < size -1) {
                sb.append(unionSql.getUnion());
            }
        }
        sb.append(sortScript);
        return sb;
    }

    public StringBuilder getSql() {
        if (sql != null)
            return sql;

        StringBuilder sb = new StringBuilder();
        int size = unionSqlList.size();
        for (int i=0; i<size; i++) {
            UnionSql unionSql = unionSqlList.get(i);
            sb.append(SqlScript.LEFT_PARENTTHESIS).append(unionSql.getSql()).append(SqlScript.RIGHT_PARENTTHESIS);
            if (i < size -1) {
                sb.append(unionSql.getUnion());
            }
        }
        sb.append(sortScript);
        return sb;
    }

    public void setSql(StringBuilder sql) {
        this.sql = sql;
    }

    public List<UnionSql> getUnionSqlList() {
        return unionSqlList;
    }

    public void setUnionSqlList(List<UnionSql> unionSqlList) {
        this.unionSqlList = unionSqlList;
    }

    public StringBuilder getSortScript() {
        return sortScript;
    }

    public void setSortScript(StringBuilder sortScript) {
        this.sortScript = sortScript;
    }


    public static class UnionSql {
        private String union;
        private StringBuilder sql;

        public String getUnion() {
            return union;
        }

        public void setUnion(String union) {
            this.union = union;
        }

        public StringBuilder getSql() {
            return sql;
        }

        public void setSql(StringBuilder sql) {
            this.sql = sql;
        }

        @Override
        public String toString() {
            return "UnionSql{" +
                    "union='" + union + '\'' +
                    ", sql=" + sql +
                    '}';
        }
    }
}
