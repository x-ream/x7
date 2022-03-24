package x7.demo.interceptor;


import io.xream.internal.util.ExceptionUtil;
import io.xream.x7.base.web.ViewEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ExceptionInterceptor {

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public ViewEntity handleException(Exception e){
        return ViewEntity.toast(ExceptionUtil.getMessage(e));
    }
}
