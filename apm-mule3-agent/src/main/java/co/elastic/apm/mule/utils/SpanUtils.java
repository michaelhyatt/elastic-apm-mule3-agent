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
	private TransactionStackMap txMap;

	public void startSpan(MessageProcessorNotification notification) {

		MuleMessage message = getMuleMessage(notification);
		String messageId = getMessageId(message);

		Span parentSpan = txMap.peek(messageId);
		Span span = parentSpan.createSpan();
		span.setName(AnnotatedObjectUtils.getProcessorName(notification));
		span.setType(AnnotatedObjectUtils.getProcessorType(notification));

		txMap.put(messageId, span);

		// Update MuleMessage with distributed tracing properties set into outboundProperty
		span.injectTraceHeaders(
				(headerName, headerValue) -> message.setProperty(headerName, headerValue, PropertyScope.OUTBOUND));

	}

	public void endSpan(MessageProcessorNotification notification) {

		MuleMessage message = getMuleMessage(notification);
		Span span = txMap.get(getMessageId(message));

		span.end();
	}

	private String getMessageId(MuleMessage message) {
		return message.getMessageRootId();
	}

	private MuleMessage getMuleMessage(MessageProcessorNotification notification) {
		return notification.getSource().getMessage();
	}

}
