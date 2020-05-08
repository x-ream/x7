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
package io.xream.x7.common.bean;

import io.xream.x7.common.util.StringUtil;

public class SourceScript {

    private String source;
    private JoinType joinType;
    private String on;
    private String alia;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public JoinType getJoinType() {
        return joinType;
    }

    public void setJoinType(JoinType joinType) {
        this.joinType = joinType;
    }

    public String getOn() {
        return on;
    }

    public void setOn(String on) {
        this.on = on;
    }

    public String getAlia() {
        return alia;
    }

    public void setAlia(String alia) {
        this.alia = alia;
    }

    public String sql(){
        if (StringUtil.isNullOrEmpty(source))
            return "";
        if (joinType == null || joinType == JoinType.MAIN)
            return source;
        StringBuilder sb = new StringBuilder();
        sb.append(joinType.sql()).append(source);
        if (StringUtil.isNotNull(alia))
            sb.append(SqlScript.SPACE).append(alia).append(SqlScript.SPACE);
        if (StringUtil.isNotNull(on)){
            if (on.toLowerCase().trim().startsWith("on"))
                sb.append(on);
            else
                sb.append(SqlScript.ON).append(on);
        }


        return sb.toString();
    }
}
