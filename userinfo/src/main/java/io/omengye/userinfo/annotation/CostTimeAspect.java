package io.omengye.userinfo.annotation;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

@Component
@Aspect
@Log4j2
public class CostTimeAspect {

	@Data
	private class MethodMap {
		private Long startTime;
		private String methodName;
	}

	private ThreadLocal<MethodMap> threadLocal = new ThreadLocal<>();
	
	@Pointcut("@annotation(costTime)")
	private void pointCut(CostTime costTime) {
		
	}
	
	@Before(value = "pointCut(costTime)")
	private void before(JoinPoint joinPoint, CostTime costTime) {
		MethodMap map = new MethodMap();
		map.setStartTime(System.currentTimeMillis());
		map.setMethodName(joinPoint.getSignature().getName());
		threadLocal.set(map);
	}
	
	@After(value = "pointCut(costTime)")
	private void after(CostTime costTime) throws Exception {
		log.info("{} - {} costtime: {} s", costTime.val(), threadLocal.get().methodName, (System.currentTimeMillis()-threadLocal.get().startTime)/1000F);
	}
	
}
