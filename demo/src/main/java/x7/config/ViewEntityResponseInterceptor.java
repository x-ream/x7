package x7.config;

import io.xream.x7.base.api.ApiVersion;
import io.xream.x7.reyc.api.HeaderResponseInterceptor;

/**
 * @Author Sim
 */
public class ViewEntityResponseInterceptor implements HeaderResponseInterceptor {
    @Override
    public String getKey() {
        return ApiVersion.KEY;
    }
}
