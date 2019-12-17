package io.xream.x7.reyc.api;

import x7.core.bean.KV;

import java.util.List;

public interface SimpleRestTemplate {


    void add(HeaderInterceptor headerInterceptor);

    KV header(String key, String value);

    String post(String url, Object request, List<KV> headerList);

    String get(String url, List<KV> headerList);
}
