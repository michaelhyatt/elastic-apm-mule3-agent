package co.elastic.apm.mule;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.mule.api.MuleEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class ApmAdvice {

	@Around("execution(* org.mule.api.processor.MessageProcessor.process(*))")
	public Object injectAroundMessageProcessors(ProceedingJoinPoint pjp) throws Throwable {

		MuleEvent muleEvent = (MuleEvent) pjp.getArgs()[0];

		String flowName = null;
		String stepName = null;

		if (logger.isDebugEnabled()) {
			stepName = AnnotatedObjectUtils.getStepName(pjp);
			flowName = AnnotatedObjectUtils.getFlowName(muleEvent);
			logger.debug("BEFORE -> Flow: " + flowName + ", Step: " + stepName);
		}

		// run the actual method
		Object retVal = null;
		try {

			retVal = pjp.proceed();

		} catch (Exception e) {

		}

		if (logger.isDebugEnabled())
			logger.debug("AFTER -> Flow: " + flowName + ", Step: " + stepName);

		return retVal;
	}

	private Logger logger = LoggerFactory.getLogger(ApmAdvice.class);

}
