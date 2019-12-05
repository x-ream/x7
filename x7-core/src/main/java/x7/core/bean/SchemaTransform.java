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
package x7.core.bean;

import x7.core.repository.X;

import java.io.Serializable;

public class SchemaTransform implements TransformConfigurable, Serializable {
    private static final long serialVersionUID = -7325036922933379064L;

    @X.Key
    private String id;
    private String alia;
    private String originTable;
    private String targetTable;
    private String originColumn;
    private String targetColumn;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlia() {
        return alia;
    }

    @Override
    public void setAlia(String alia) {
        this.alia = alia;
    }

    public String getOriginTable() {
        return originTable;
    }

    public void setOriginTable(String originTable) {
        this.originTable = originTable;
    }

    public String getTargetTable() {
        return targetTable;
    }

    public void setTargetTable(String targetTable) {
        this.targetTable = targetTable;
    }

    public String getOriginColumn() {
        return originColumn;
    }

    public void setOriginColumn(String originColumn) {
        this.originColumn = originColumn;
    }

    public String getTargetColumn() {
        return targetColumn;
    }

    public void setTargetColumn(String targetColumn) {
        this.targetColumn = targetColumn;
    }

    @Override
    public String toString() {
        return "SchemaTransform{" +
                "id='" + id + '\'' +
                ", alia='" + alia + '\'' +
                ", originTable='" + originTable + '\'' +
                ", targetTable='" + targetTable + '\'' +
                ", originColumn='" + originColumn + '\'' +
                ", targetColumn='" + targetColumn + '\'' +
                '}';
    }
}
