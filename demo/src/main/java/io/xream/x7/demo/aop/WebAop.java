package io.xream.x7.demo.aop;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import x7.core.util.ExceptionUtil;
import x7.core.util.TimeUtil;
import x7.core.web.ViewEntity;
import x7.repository.dao.Tx;

@Aspect
@Configuration
public class WebAop {


	@Pointcut("execution(public * io.xream.x7.demo.controller.*.*(..))")
	public void cut() {

	}

	@Around("cut()")
	public Object around(ProceedingJoinPoint proceedingJoinPoint) {

		Object[] argArr = proceedingJoinPoint.getArgs();
//		Passport passport = null;
//		{
//			/*
//			 * isSignIn, FIXME 移到网关
//			 */
//			if (argArr != null) {
//				for (Object arg : argArr) {
//					if (arg instanceof Tokened) {
//						try {
//							passport = PassportUtilX.getPassport((Tokened) arg);
//							/*
//							 * 数据权限
//							 */
//							if (arg instanceof DataPermission) {
//								DataPermission.Chain.beforeHandle((DataPermission)arg, passport.getDataPermissionValue());
//							}
//							break;
//						} catch (Passport.PassportException e) {
//							Passport.PassportException pe = (Passport.PassportException) e;
//							return pe.getViewEntity();
//						}
//					}
//				}
//			}
//		}

		org.aspectj.lang.Signature signature = proceedingJoinPoint.getSignature();
		MethodSignature ms = ((MethodSignature) signature);


		{
			/*
			 * TX
			 */
			long startTime = TimeUtil.now();
			Tx.begin();
			try {
				Object obj = null;

				Class returnType = ms.getReturnType();
				if (returnType == void.class) {

					proceedingJoinPoint.proceed();
				} else {
					obj = proceedingJoinPoint.proceed();
				}

				Tx.commit();
				long endTime = TimeUtil.now();
				long handledTimeMillis = endTime - startTime;
				System.out.println("_______Transaction end, cost time: " + (handledTimeMillis) + "ms");
				if (obj instanceof ViewEntity){
					ViewEntity ve = (ViewEntity)obj;
					ve.setHandledTimeMillis(handledTimeMillis);
				}

				return obj;
			} catch (Throwable e) {
				e.printStackTrace();
				Tx.rollback();

//				if(e instanceof HystrixRuntimeException){
//					return ViewEntity.toast("服务繁忙, 请稍后");
//				}

				String msg = ExceptionUtil.getMessage(e);


				return ViewEntity.toast(msg);
			}
		}
	}
}
