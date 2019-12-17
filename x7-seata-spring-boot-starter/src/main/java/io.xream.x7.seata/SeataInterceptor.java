package io.xream.x7.seata;

import io.seata.core.context.RootContext;
import io.xream.x7.reyc.api.HeaderInterceptor;
import io.xream.x7.reyc.api.SimpleRestTemplate;
import x7.core.bean.KV;

public class SeataInterceptor implements HeaderInterceptor {
    @Override
    public KV apply(SimpleRestTemplate template) {

        String xid = RootContext.getXID();
        if (xid != null && !xid.trim().equals("")) {
            return template.header(RootContext.KEY_XID, xid);
        }
        return null;
    }
}
