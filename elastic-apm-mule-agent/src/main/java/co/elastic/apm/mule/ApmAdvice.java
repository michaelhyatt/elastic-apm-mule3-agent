package co.elastic.apm.mule;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.mule.api.MuleEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import co.elastic.apm.api.Span;

@Aspect
public class ApmAdvice {

	@Around("execution(* org.mule.api.processor.MessageProcessor.process(*))")
	public Object injectAroundMessageProcessors(ProceedingJoinPoint pjp) throws Throwable {

		MuleEvent muleEvent = (MuleEvent) pjp.getArgs()[0];

		String id = muleEvent.getMessage().getMessageRootId();
		String stepName = AnnotatedObjectUtils.getStepName(pjp);
		String flowName = AnnotatedObjectUtils.getFlowName(muleEvent);

		if (logger.isDebugEnabled())
			logger.debug("BEFORE -> Flow: " + flowName + ", Step: " + stepName);

		Span parentSpan = txMap.peek(id);
		Span span = parentSpan.createSpan();
		span.setName("SIMPLE_SPAN: " + flowName + ( stepName != null ? "/" + stepName : ""));
		span.setType("step");
		span.addTag("id", id);

		// run the actual method
		Object retVal = null;
		try {

			retVal = pjp.proceed();

		} catch (Exception e) {
			span.captureException(e);
			throw e;
		}

		if (logger.isDebugEnabled())
			logger.debug("AFTER -> Flow: " + flowName + ", Step: " + stepName);
		
		span.end();

		return retVal;
	}

	@Autowired
	private TransactionStackMap txMap;

	private Logger logger = LoggerFactory.getLogger(ApmAdvice.class);

}
