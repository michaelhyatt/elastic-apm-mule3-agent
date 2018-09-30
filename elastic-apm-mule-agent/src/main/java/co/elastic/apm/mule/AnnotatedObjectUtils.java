package co.elastic.apm.mule;

import javax.xml.namespace.QName;

import org.aspectj.lang.ProceedingJoinPoint;
import org.mule.api.AnnotatedObject;
import org.mule.api.MuleEvent;
import org.mule.api.context.notification.ServerNotification;
import org.mule.context.notification.MessageProcessorNotification;

public class AnnotatedObjectUtils {

	private static final String NAME = "name";
	private static final String HTTP_WWW_MULESOFT_ORG_SCHEMA_MULE_DOCUMENTATION = "http://www.mulesoft.org/schema/mule/documentation";

	public static String getProcessorName(MessageProcessorNotification notification) {
		AnnotatedObject annotObj = (AnnotatedObject) notification.getProcessor();
		QName qName = new QName(HTTP_WWW_MULESOFT_ORG_SCHEMA_MULE_DOCUMENTATION, NAME);
		String step = (String) annotObj.getAnnotation(qName);
		return step;
	}

	public static String getFlowName(ServerNotification notification) {
		MuleEvent muleEvent = (MuleEvent) notification.getSource();
		return muleEvent.getFlowConstruct().getName();
	}

	public static String getFlowName(MuleEvent muleEvent) {
		return muleEvent.getFlowConstruct().getName();
	}

	public static String getStepName(ProceedingJoinPoint pjp) {
		AnnotatedObject annotatedObject = (AnnotatedObject) pjp.getTarget();
		QName qName = new QName(HTTP_WWW_MULESOFT_ORG_SCHEMA_MULE_DOCUMENTATION, NAME);
		String stepName = (String) annotatedObject.getAnnotation(qName);
		return stepName;
	}
}
