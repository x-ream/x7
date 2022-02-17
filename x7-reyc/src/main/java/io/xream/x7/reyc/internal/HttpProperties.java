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
package io.xream.x7.reyc.internal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Sim
 */
@Configuration
public class HttpProperties {

    @Value("${http.connect-timeout:20000}")
    private int connectTimeout = 20000;
    @Value("${http.socket-timeout:15000}")
    private int socketTimeout = 15000;
    @Value("${http.read-timeout:15000}")
    private int readTimeout = 15000;
    @Value("${http.write-timeout:15000}")
    private int writeTimeout = 15000;
    @Value("${http.max-idle-connections:100}")
    private int maxIdleConnections = 100;
    @Value("${http.keep-alive-duration:5000}")
    private int keepAliveDuration = 5000;

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public int getMaxIdleConnections() {
        return maxIdleConnections;
    }

    public void setMaxIdleConnections(int maxIdleConnections) {
        this.maxIdleConnections = maxIdleConnections;
    }

    public int getKeepAliveDuration() {
        return keepAliveDuration;
    }

    public void setKeepAliveDuration(int keepAliveDuration) {
        this.keepAliveDuration = keepAliveDuration;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(int writeTimeout) {
        this.writeTimeout = writeTimeout;
    }
}
