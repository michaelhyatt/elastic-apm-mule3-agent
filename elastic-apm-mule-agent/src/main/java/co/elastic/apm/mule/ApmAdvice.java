package co.elastic.apm.mule;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.mule.api.MuleEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import co.elastic.apm.impl.transaction.AbstractSpan;
import co.elastic.apm.impl.transaction.Span;


@Configuration
@Aspect
public class ApmAdvice {

	@Around("execution(* org.mule.api.processor.MessageProcessor.process(*))")
	public Object injectAroundMessageProcessors(ProceedingJoinPoint pjp) throws Throwable {

		MuleEvent muleEvent = (MuleEvent) pjp.getArgs()[0];

		String id = muleEvent.getMessage().getMessageRootId();
		String stepName = AnnotatedObjectUtils.getStepName(pjp);
		String flowName = AnnotatedObjectUtils.getFlowName(muleEvent);

		logger.debug("BEFORE -> Flow: " + flowName + ", Step: " + stepName);

		AbstractSpan<?> parentSpan = txMap.peek(id);
		Span span = parentSpan.createSpan();
		span.setName("Flow:" + flowName + (stepName != null ? " Step:" + stepName : ""));
		span.withType(stepName != null ? "step" : "flow");
		span.addTag("id", id);

		// run the actual method
		Object retVal = null;
		try {

			retVal = pjp.proceed();

		} catch (Exception e) {
			span.captureException(e.getCause());
			throw e;
		}

		logger.debug("AFTER -> Flow: " + flowName + ", Step: " + stepName);

		span.end();

		return retVal;
	}

	@Autowired
	private TransactionStackMap txMap;

	private Logger logger = LoggerFactory.getLogger(ApmAdvice.class);

}
