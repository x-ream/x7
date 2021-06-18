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
import com.github.kristofa.brave.Sampler;
import com.github.kristofa.brave.http.DefaultSpanNameProvider;
import com.github.kristofa.brave.http.SpanNameProvider;
import com.github.kristofa.brave.spring.BraveClientHttpRequestInterceptor;
import com.github.kristofa.brave.spring.ServletHandlerInterceptor;
import io.xream.x7.base.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import zipkin.Span;
import zipkin.reporter.AsyncReporter;
import zipkin.reporter.Reporter;
import zipkin.reporter.Sender;
import zipkin.reporter.okhttp3.OkHttpSender;

/**
 * @author Rolyer Luo
 */
@Import({TracingProperties.class, BraveClientHttpRequestInterceptor.class, ServletHandlerInterceptor.class})
public class TracingConfig {

    private static Logger logger = LoggerFactory.getLogger(TracingConfig.class);

    private TracingProperties properties;

    public TracingConfig(TracingProperties p){
        this.properties = p;
        System.out.println("\n");
        logger.info("Prefix: tracing.zipkin   ;   " + properties.toString());
    }

    /**
     * 发送器配置
     * @return
     */
    @ConditionalOnProperty(value = {"tracing.zipkin.url"})
    @Bean
    Sender sender() {
        return OkHttpSender.create(properties.getUrl()+"/api/v1/spans");
    }

    /**
     * 用什么方式显示span信息
     * @param sender
     * @return
     */
    @Bean
    Reporter<Span> reporter(Sender sender) {
        //取消注释,日志打印span信息
        //return new LoggingReporter(); // 打印日志本地，通过日志收集到ES
        return AsyncReporter.builder(sender).build();
    }

    @ConditionalOnMissingBean(Brave.class)
    @Bean
    public Brave brave(Reporter<Span> reporter, Environment env) {
        String applicationName = env.getProperty("spring.application.name");
        if (StringUtil.isNullOrEmpty(applicationName)){
            throw new RuntimeException("spring.application.name=null, config it or #tracing.zipkin.url=");
        }
        Brave.Builder builder = new Brave.Builder(applicationName).reporter(reporter);
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

    /**
     * span命名提供者，默认为http方法
     * @return
     */
    @Bean
    SpanNameProvider spanNameProvider() {
        return new DefaultSpanNameProvider();
    }
}
