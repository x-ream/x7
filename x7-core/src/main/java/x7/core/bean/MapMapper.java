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

import java.util.HashMap;
import java.util.Map;

public class MapMapper {
    private Map<String, String> propertyMapperMap = new HashMap<String, String>();
    private Map<String, String> mapperPropertyMap = new HashMap<String, String>();

    public Map<String, String> getPropertyMapperMap() {
        return propertyMapperMap;
    }

    public Map<String, String> getMapperPropertyMap() {
        return mapperPropertyMap;
    }

    public void put(String property, String mapper) {
        this.propertyMapperMap.put(property, mapper);
        this.mapperPropertyMap.put(mapper, property);
    }

    public String mapper(String property) {
        return this.propertyMapperMap.get(property);
    }

    public String property(String mapper) {
        return this.mapperPropertyMap.get(mapper);
    }

    @Override
    public String toString() {
        return "MapMapper [propertyMapperMap=" + propertyMapperMap + ", mapperPropertyMap=" + mapperPropertyMap
                + "]";
    }
}