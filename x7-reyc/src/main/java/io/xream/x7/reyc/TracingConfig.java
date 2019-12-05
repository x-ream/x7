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
package io.xream.x7.reyc;


import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.EmptySpanCollectorMetricsHandler;
import com.github.kristofa.brave.Sampler;
import com.github.kristofa.brave.SpanCollector;
import com.github.kristofa.brave.http.DefaultSpanNameProvider;
import com.github.kristofa.brave.http.HttpSpanCollector;
import com.github.kristofa.brave.httpclient.BraveHttpRequestInterceptor;
import com.github.kristofa.brave.httpclient.BraveHttpResponseInterceptor;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import x7.core.util.StringUtil;

@Import(TracingProperties.class)
public class TracingConfig {

    private static Logger logger = LoggerFactory.getLogger(TracingConfig.class);

    private TracingProperties properties;

    public TracingConfig(TracingProperties p){
        this.properties = p;
        System.out.println("\n");
        logger.info("Prefix: tracing.zipkin   ;   " + properties.toString());
    }

    @ConditionalOnMissingBean(Brave.class)
    @ConditionalOnProperty(
            value = {"tracing.zipkin.url"})
    @Bean
    public SpanCollector spanCollector() {
        HttpSpanCollector.Config config = HttpSpanCollector.Config.builder().compressionEnabled(properties.isCompressionEnabled()).connectTimeout(properties.getConnectTimeout())
                .flushInterval(properties.getFlushInterval()).readTimeout(properties.getReadTimeout()).build();
        return HttpSpanCollector.create(properties.getUrl(), config, new EmptySpanCollectorMetricsHandler());
    }

    @ConditionalOnMissingBean(Brave.class)
    @ConditionalOnBean(SpanCollector.class)
    @Bean
    public Brave brave(SpanCollector spanCollector, Environment env) {
        String applicationName = env.getProperty("spring.application.name");
        if (StringUtil.isNullOrEmpty(applicationName))
            throw new RuntimeException("spring.application.name=null, config it or #tracing.zipkin.url=");
        Brave.Builder builder = new Brave.Builder(applicationName);
        builder.spanCollector(spanCollector);
        builder.traceSampler(Sampler.create(properties.getSampleRate()));
        logger.info("Tracing(ZipKin): Brave instance created, default add tracing to ReyClient" );
        logger.info("Config Zipkin Servlet Tracing by: @EnableTracingServlet");
        logger.info("create more tracing filter or interceptor for spring boot project, by parameter (Brave brave), like code as follows: ");
        logger.info("       @ConditionalOnMissingBean(BraveServletFilter.class)");
        logger.info("       @ConditionalOnBean(Brave.class)");
        logger.info("       @Bean");
        logger.info("       public BraveServletFilter braveServletFilter(Brave brave) {");

        return builder.build();
    }


    @ConditionalOnMissingBean(BraveHttpRequestInterceptor.class)
    @ConditionalOnBean(Brave.class)
    @Bean
    public BraveHttpRequestInterceptor requestInterceptor(Brave brave) {
        return new BraveHttpRequestInterceptor(brave.clientRequestInterceptor(),
                new DefaultSpanNameProvider());
    }

    @ConditionalOnMissingBean(BraveHttpResponseInterceptor.class)
    @ConditionalOnBean(Brave.class)
    @Bean
    public BraveHttpResponseInterceptor responseInterceptor(Brave brave){
        return new BraveHttpResponseInterceptor(brave.clientResponseInterceptor());
    }


    public static CloseableHttpClient httpClient(BraveHttpRequestInterceptor requestInterceptor,
                                                 BraveHttpResponseInterceptor responseInterceptor) {
        CloseableHttpClient httpclient = HttpClients.custom()
                .addInterceptorFirst(requestInterceptor)
                .addInterceptorFirst(responseInterceptor).build();
        return httpclient;
    }
}
