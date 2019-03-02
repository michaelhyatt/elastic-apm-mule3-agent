package co.elastic.apm.mule.utils;

import org.mule.api.MuleMessage;
import org.mule.api.transport.PropertyScope;
import org.mule.context.notification.MessageProcessorNotification;
import org.springframework.beans.factory.annotation.Autowired;

import co.elastic.apm.api.Span;

/**
 * @author michaelhyatt
 * 
 *         Handling of flow step starts and ends.
 *
 */
public class SpanUtils {

	@Autowired
	private SpanStore txMap;

	public void startSpan(MessageProcessorNotification notification) {
		
		//System.out.println("Starting " + notification.getProcessor());

		MuleMessage message = getMuleMessage(notification);
		String messageId = getMessageId(message);
		
		Span parentSpan = txMap.getTopLevelTransaction(messageId);
		Span span = parentSpan.startSpan("ext", AnnotatedObjectUtils.getProcessorType(notification), "mule");
		span.setName(AnnotatedObjectUtils.getProcessorName(notification));

		txMap.storeTransactionOrSpan(messageId, notification, span);

		// Update MuleMessage with distributed tracing properties set into outboundProperty
		span.injectTraceHeaders(
				(headerName, headerValue) -> message.setProperty(headerName, headerValue, PropertyScope.OUTBOUND));

	}

	public void endSpan(MessageProcessorNotification notification) {

		//System.out.println("Finishing " + notification.getProcessor());
		
		MuleMessage message = getMuleMessage(notification);
		String messageId = getMessageId(message);
		
		Span span = txMap.getTransactionOrSpan(messageId, notification);

		span.end();
	}

	private String getMessageId(MuleMessage message) {
		return message.getMessageRootId();
	}

	private MuleMessage getMuleMessage(MessageProcessorNotification notification) {
		return notification.getSource().getMessage();
	}

}
