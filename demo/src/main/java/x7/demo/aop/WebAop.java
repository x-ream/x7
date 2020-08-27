package x7.demo.aop;


import io.xream.x7.base.util.ExceptionUtil;
import io.xream.x7.base.web.ViewEntity;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;


@Aspect
//@Configuration
@Order(-1)
public class WebAop {


	private final static Logger logger = LoggerFactory.getLogger(WebAop.class);

	@Pointcut("execution(public * x7.demo.controller.*.*(..))")
	public void cut() {

	}

	@Around("cut()")
	public Object around(ProceedingJoinPoint proceedingJoinPoint) {

		org.aspectj.lang.Signature signature = proceedingJoinPoint.getSignature();
		MethodSignature ms = ((MethodSignature) signature);


		{

			long startTime = System.currentTimeMillis();

			try {
				Object obj = null;

				Class returnType = ms.getReturnType();
				if (returnType == void.class) {

					proceedingJoinPoint.proceed();
				} else {
					obj = proceedingJoinPoint.proceed();
				}


				long endTime = System.currentTimeMillis();
				long handledTimeMillis = endTime - startTime;
				System.out.println("________Transaction end, cost time: " + (handledTimeMillis) + "ms");
				if (obj instanceof ViewEntity){
					ViewEntity ve = (ViewEntity)obj;
					ve.setHandledTimeMillis(handledTimeMillis);
				}

				return obj;
			} catch (Throwable e) {

				System.out.println("________Transaction rollback:" + ExceptionUtil.getMessage(e));


//				if(e instanceof HystrixRuntimeException){
//					return ViewEntity.toast("服务繁忙, 请稍后");
//				}

				String msg = ExceptionUtil.getMessage(e);


				return ViewEntity.toast(msg);
			}
		}
	}
}
