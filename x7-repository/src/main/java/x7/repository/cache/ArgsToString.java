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
package x7.repository.cache;

import x7.core.util.VerifyUtil;

public interface ArgsToString<T> {

    StringBuilder build(Object[] args);

    default String get(String prefix,Object[] args){
        StringBuilder sb = build(args);
        if (sb != null) {
            sb.insert(0, prefix);
            return VerifyUtil.toMD5(sb.toString());
        } else {
            return VerifyUtil.toMD5(prefix);
        }
    }
}
