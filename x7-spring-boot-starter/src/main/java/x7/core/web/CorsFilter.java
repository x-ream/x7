package x7.core.web;


import org.springframework.beans.factory.annotation.Value;
import x7.core.util.StringUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CorsFilter implements Filter {

    @Value("${access.domain}")
    private String origins = "*";

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        if (StringUtil.isNullOrEmpty(origins)){
            origins = "*";
        }

        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("Access-Control-Allow-Origin", origins);//FIXME *改成域名
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");

        response.setHeader("Access-Control-Allow-Headers", "Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With, Customizer");
        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {


    }
}