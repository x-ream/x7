package io.xream.x7.reyc.api;

import x7.core.bean.KV;

public interface HeaderInterceptor {

    KV apply(SimpleRestTemplate template);

}
