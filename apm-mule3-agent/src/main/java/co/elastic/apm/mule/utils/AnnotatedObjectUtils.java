package co.elastic.apm.mule.utils;

import javax.xml.namespace.QName;

import org.mule.api.AnnotatedObject;
import org.mule.api.MuleEvent;
import org.mule.api.context.notification.ServerNotification;
import org.mule.context.notification.MessageProcessorNotification;

public class AnnotatedObjectUtils {

	private static final String SOURCE_ELEMENT = "sourceElement";
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

	public static String getProcessorType(MessageProcessorNotification notification) {
		AnnotatedObject annotObj = (AnnotatedObject) notification.getProcessor();
		QName qName = new QName(HTTP_WWW_MULESOFT_ORG_SCHEMA_MULE_DOCUMENTATION, SOURCE_ELEMENT);
		String step = (String) annotObj.getAnnotation(qName);
		String value = step.split("[ <]")[1];
		return ("".equals(value) ? "step" : value);
	}

}
