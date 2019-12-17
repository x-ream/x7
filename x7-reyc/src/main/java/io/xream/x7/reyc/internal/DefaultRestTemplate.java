package io.xream.x7.reyc.internal;

import com.github.kristofa.brave.httpclient.BraveHttpRequestInterceptor;
import com.github.kristofa.brave.httpclient.BraveHttpResponseInterceptor;
import io.xream.x7.reyc.TracingConfig;
import io.xream.x7.reyc.api.HeaderInterceptor;
import io.xream.x7.reyc.api.SimpleRestTemplate;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import x7.core.bean.KV;
import x7.core.util.HttpClientUtil;

import java.util.ArrayList;
import java.util.List;

public class DefaultRestTemplate implements SimpleRestTemplate {

    private BraveHttpRequestInterceptor requestInterceptor;
    private BraveHttpResponseInterceptor responseInterceptor;
    private HttpClientProperties properties;

    private List<HeaderInterceptor> headerInterceptorList = new ArrayList<>();
    public void add(HeaderInterceptor headerInterceptor) {
        this.headerInterceptorList.add(headerInterceptor);
    }

    public DefaultRestTemplate(){
    }

    public DefaultRestTemplate(
            HttpClientProperties properties,
            BraveHttpRequestInterceptor requestInterceptor,
            BraveHttpResponseInterceptor responseInterceptor){
        this.properties = properties;
        this.requestInterceptor = requestInterceptor;
        this.responseInterceptor = responseInterceptor;
    }


    public BraveHttpRequestInterceptor getRequestInterceptor() {
        return requestInterceptor;
    }

    public void setRequestInterceptor(BraveHttpRequestInterceptor requestInterceptor) {
        this.requestInterceptor = requestInterceptor;
    }

    public BraveHttpResponseInterceptor getResponseInterceptor() {
        return responseInterceptor;
    }

    public void setResponseInterceptor(BraveHttpResponseInterceptor responseInterceptor) {
        this.responseInterceptor = responseInterceptor;
    }

    public KV header(String key, String value){
        return new KV(key, value);
    }

    public String post(String url, Object request, List<KV> headerList) {

        CloseableHttpClient httpclient = null;
        if (requestInterceptor != null && responseInterceptor != null) {
            httpclient = TracingConfig.httpClient(requestInterceptor, responseInterceptor);
        } else {
            httpclient = HttpClients.createDefault();
        }

        List<KV> tempHeaderList = new ArrayList<>();
        for (HeaderInterceptor headerInterceptor : headerInterceptorList){
            KV kv = headerInterceptor.apply(this);
            tempHeaderList.add(kv);
        }

        if (headerList!=null && !tempHeaderList.isEmpty()) {
            tempHeaderList.addAll(headerList);
        }
        String result = HttpClientUtil.post(url,request,tempHeaderList,properties.getConnectTimeout(),properties.getSocketTimeout(),httpclient);

        return result;
    }

    @Override
    public String get(String url, List<KV> headerList) {

        CloseableHttpClient httpclient = null;
        if (requestInterceptor != null && responseInterceptor != null) {
            httpclient = TracingConfig.httpClient(requestInterceptor, responseInterceptor);
        } else {
            httpclient = HttpClients.createDefault();
        }
        List<KV> tempHeaderList = new ArrayList<>();
        for (HeaderInterceptor headerInterceptor : headerInterceptorList){
            KV kv = headerInterceptor.apply(this);
            tempHeaderList.add(kv);
        }

        if (headerList!=null && !tempHeaderList.isEmpty()) {
            tempHeaderList.addAll(headerList);
        }
        return HttpClientUtil.get(url, tempHeaderList,properties.getConnectTimeout(),properties.getSocketTimeout(),httpclient);
    }


}
