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
package io.xream.x7.config;

import io.xream.internal.util.StringUtil;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class CorsFilter implements Filter {

    @Value("${access.domain}")
    private String origins = "*";

    public void setOrigins(String str){
        if (str == null)
            return;
        this.origins = str;
    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse response = (HttpServletResponse) res;

        if (StringUtil.isNullOrEmpty(origins)){
            origins = "*";
        }

        if (origins.contains(",")){
            HttpServletRequest request = (HttpServletRequest)req;
            String origin = request.getHeader("Origin");
            if (origin == null) {
                response.setHeader("Access-Control-Allow-Origin", origins);
            }else {
                String[] arr = origins.split(",");
                for (String o : arr) {
                    if (o.contains(origin)){
                        response.setHeader("Access-Control-Allow-Origin", origin);
                        break;
                    }
                }
            }
        }else{
            response.setHeader("Access-Control-Allow-Origin", origins);
        }

        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With, Customizer");
        response.setHeader("Access-Control-Max-Age","1800");
        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }

}
