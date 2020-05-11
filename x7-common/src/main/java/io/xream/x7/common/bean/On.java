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

public class On {
    private String key;
    private String andOr;
    private String op;
    private JoinFrom joinFrom;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAndOr() {
        return andOr;
    }

    public void setAndOr(String andOr) {
        this.andOr = andOr;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public JoinFrom getJoinFrom() {
        return joinFrom;
    }

    public void setJoinTarget(JoinFrom joinFrom) {
        this.joinFrom = joinFrom;
    }

    @Override
    public String toString() {
        return "On{" +
                "key='" + key + '\'' +
                ", andOr='" + andOr + '\'' +
                ", op='" + op + '\'' +
                ", joinFrom=" + joinFrom +
                '}';
    }

    public static enum  Op {
        EQ ("="),
        NE( "!="),
        GT(">"),
        LT ("<"),
        GTE (">="),
        LTE ("<="),
        LIKE ("LIKE");

        private Op(String str){
            op = str;
        }
        private String op;
        public String sql(){
            return op;
        }
    }
}
