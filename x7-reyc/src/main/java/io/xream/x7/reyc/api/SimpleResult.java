package io.xream.x7.reyc.api;

import io.xream.x7.base.KV;

import java.util.List;
import java.util.Map;

/**
 * @Author Sim
 */
public class SimpleResult {
    private String body;
    private Map<String,String> headerMap;

    public SimpleResult(String body, Map<String,String> headerMap) {
        this.body = body;
        this.headerMap = headerMap;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public void setHeaderMap(Map<String, String> headerMap) {
        this.headerMap = headerMap;
    }

    @Override
    public String toString() {
        return "SimpleResult{" +
                "body='" + body + '\'' +
                ", headerMap=" + headerMap +
                '}';
    }
}