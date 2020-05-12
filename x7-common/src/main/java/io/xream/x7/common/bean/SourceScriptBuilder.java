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

import java.util.*;

public interface SourceScriptBuilder {
    SourceScriptBuilder source(String source);

    SourceScriptBuilder alia(String alia);

    SourceScriptBuilder joinType(JoinType joinType);

    SourceScriptBuilder on(String key, On.Op op, JoinFrom joinTarget);

    SourceScriptBuilder onOr(String key, On.Op op, JoinFrom joinTarget);

    static List<SourceScript> parse(List<String> sourceScriptsSplittedList) {


        List<SourceScript> list = new ArrayList<>();

        SourceScript sourceScript = null;
        int size = sourceScriptsSplittedList.size();
        for (int i = 0; i < size; i++) {
            String str = sourceScriptsSplittedList.get(i);
            String strUpper = str.toUpperCase();

            if ("from".equals(str.toLowerCase()))
                continue;

            switch (strUpper) {
                case "INNER":
                    sourceScript = createAndGet(list);
                    sourceScript.setJoinType(JoinType.INNER_JOIN);
                    i++;
                    break;
                case "LEFT":
                    sourceScript = createAndGet(list);
                    sourceScript.setJoinType(JoinType.LEFT_JOIN);
                    i++;
                    break;
                case "RIGHT":
                    sourceScript = createAndGet(list);
                    sourceScript.setJoinType(JoinType.RIGHT_JOIN);
                    i++;
                    break;
                case "OUTER":
                    sourceScript = createAndGet(list);
                    sourceScript.setJoinType(JoinType.OUTER_JOIN);
                    i++;
                    break;
                case "FULL":
                    sourceScript = createAndGet(list);
                    sourceScript.setJoinType(JoinType.JOIN);
                    i++;
                    break;
                case "JOIN":
                    sourceScript = createAndGet(list);
                    sourceScript.setJoinType(JoinType.JOIN);
                    break;
                case ",":
                    sourceScript = createAndGet(list);
                    sourceScript.setJoinType(JoinType.COMMA);
                    break;
                case "ON":
                    String andOr = "ON";
                    boolean flag = true;
                    while (flag) {
                        String selfKey = sourceScriptsSplittedList.get(++i);
                        String op = sourceScriptsSplittedList.get(++i);// op
                        String targetKey = sourceScriptsSplittedList.get(++i);
                        if (targetKey.startsWith(sourceScript.getSource()) || (sourceScript.getAlia() != null && targetKey.startsWith(sourceScript.getAlia()))) {
                            String temp = selfKey;
                            selfKey = targetKey;
                            targetKey = temp;
                        }

                        int selfIndex = selfKey.indexOf(".");
                        int targetIndex = targetKey.indexOf(".");

                        JoinFrom joinFrom = new JoinFrom();
                        joinFrom.setAlia(targetKey.substring(0, targetIndex));
                        joinFrom.setKey(targetKey.substring(targetIndex + 1));
                        On on = new On();
                        on.setKey(selfKey.substring(selfIndex + 1));
                        on.setOp(op);
                        on.setAndOr(andOr);
                        on.setJoinTarget(joinFrom);
                        sourceScript.getOnList().add(on);

                        flag = false;
                        if (i + 1 == size)
                            return list;
                        andOr = sourceScriptsSplittedList.get(i + 1).toUpperCase();
                        if (andOr.equals("AND") || andOr.equals("OR")){
                            flag = true;
                            i++;
                        }
                    }
                    break;

                default:
                    if (sourceScript == null){
                        sourceScript = createAndGet(list);
                    }
                    sourceScript.setSource(str);
                    if (i < size -1){
                        String tryAlia = sourceScriptsSplittedList.get(i+1);
                        if (!SqlScript.SOURCE_SCRIPT.contains(tryAlia.toUpperCase())){
                            sourceScript.setAlia(tryAlia);
                            i++;
                        }
                    }

            }
        }

        return list;
    }



    static Map<String, String> parseAlia(List<String> sourceScriptsSplittedArr) {

        Map<String, String> map = new HashMap<>();

        List<String> list = new ArrayList<>();
        Set<String> tryAliaSet = new HashSet<>();

        if (sourceScriptsSplittedArr.size() == 1) {
            map.put(sourceScriptsSplittedArr.get(0), sourceScriptsSplittedArr.get(0));
            return map;
        }
        for (String str : sourceScriptsSplittedArr) {
            boolean isKeyWord = false;

            for (String kw : SqlScript.SOURCE_SCRIPT) {
                if (kw.equals(str.toLowerCase())) {
                    isKeyWord = true;
                    break;
                }
            }

            if (!isKeyWord) {
                if (str.contains(".")) {
                    str = str.substring(0, str.indexOf("."));
                    tryAliaSet.add(str);
                } else {
                    if (StringUtil.isNotNull(str)) {
                        list.add(str);
                    }
                }
            }
        }

        int size = list.size();
        for (int i = 0; i < size; i++) {
            String str = list.get(i);
            if (tryAliaSet.contains(str)) {
                map.put(str, str);
            } else {
                if (i + 1 < size) {
                    String alia = list.get(i + 1);
                    if (tryAliaSet.contains(alia)) ;
                    map.put(alia, str);
                    i++;
                }
            }
        }

        return map;
    }

    static List<String> split(String sourceScript) {
        String[] opArrTwo = {"!=","<>","<=",">="};
        String[] opArrTwoTemp = {"&ne","&ne","&lte","&gte"};
        String[] opArrOne = {"=","<",">"};

        boolean flag = false;
        for (int i=0; i<4; i++){
            if (sourceScript.contains(opArrTwo[i])) {
                flag = true;
                sourceScript = sourceScript.replace(opArrTwo[i], opArrTwoTemp[i]);
            }
        }


        for (String op : opArrOne ){
            if (sourceScript.contains(op))
                sourceScript = sourceScript.replace(op," "+op + " ");
        }

        if (flag){
            for (int i=0; i<4; i++){
                if (sourceScript.contains(opArrTwoTemp[i]))
                    sourceScript = sourceScript.replace(opArrTwoTemp[i], " " + opArrTwo[i] + " ");
            }
        }

        if (sourceScript.contains(",")){
            sourceScript = sourceScript.replace(","," , ");
        }
        String[] arr = sourceScript.split(" ");
        List<String> list = new ArrayList<>();
        for (String str : arr) {
            if (StringUtil.isNotNull(str))
                list.add(str);
        }
        return list;
    }

    static SourceScript createAndGet(List<SourceScript> list) {
        SourceScript sourceScript = new SourceScript();
        list.add(sourceScript);
        return sourceScript;
    }

}
