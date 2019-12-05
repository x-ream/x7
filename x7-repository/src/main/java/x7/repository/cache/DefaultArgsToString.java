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

import x7.core.util.JsonX;
import x7.core.web.Tokened;

/**
 *  x7.core.web.Tokened
 */
public class DefaultArgsToString implements ArgsToString<Tokened>{
    @Override
    public StringBuilder build(Object[] args) {

        StringBuilder sb = new StringBuilder();
        if (args != null) {

            for (Object arg : args) {
                if (arg instanceof Tokened) {
                    Tokened ro = (Tokened) arg;
                    long passportId = ro.getPassportId();
                    String token = ro.getToken();
                    String passportType = ro.getPassportType();

                    ro.setPassportId("0");
                    ro.setPassportType(null);
                    ro.setToken(null);

                    String str = JsonX.toJson(ro);
                    sb.append(str);

                    ro.setPassportId(String.valueOf(passportId));
                    ro.setPassportType(passportType);
                    ro.setToken(token);

                } else {
                    String str = JsonX.toJson(arg);
                    sb.append(str);
                }
            }
        }

        return sb;
    }
}
