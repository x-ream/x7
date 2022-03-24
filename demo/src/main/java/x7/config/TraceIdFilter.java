package x7.config;

import io.opentracing.Span;
import io.opentracing.Tracer;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Objects;


/**
 * @author Sim
 */
//@Order(0)
//@Component
//@WebFilter(filterName = "traceIdFilter", urlPatterns = "/*")
public class TraceIdFilter implements Filter {

    public static final String TRACE_ID = "traceId";

    @Resource
    private Tracer tracer;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        RedisAutoConfiguration d;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String traceId = request.getHeader(TRACE_ID);
        if (Objects.isNull(traceId)){
            Span span = tracer.scopeManager().activeSpan();
            if (span != null) {
                traceId = span.context().toTraceId();
            }
        }
        String path = request.getServletPath();
        if (path.length()>20) {
            path = path.substring(0,20);
        }
        MDC.put(TRACE_ID, traceId+"@"+path);
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        MDC.clear();
    }
}
