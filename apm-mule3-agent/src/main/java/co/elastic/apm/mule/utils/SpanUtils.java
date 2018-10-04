package co.elastic.apm.mule.utils;

import org.mule.api.MuleEvent;
import org.mule.context.notification.MessageProcessorNotification;
import org.springframework.beans.factory.annotation.Autowired;

import co.elastic.apm.api.Span;

public class SpanUtils {

	@Autowired
	private TransactionStackMap txMap;

	public void startSpan(MessageProcessorNotification notification) {
		String messageId = getMessageId(notification);
		Span parentSpan = txMap.peek(messageId);
		Span span = parentSpan.createSpan();
		span.setName(AnnotatedObjectUtils.getProcessorName(notification));
		span.setType(AnnotatedObjectUtils.getProcessorType(notification));
		txMap.put(messageId, span);

	}

	public void endSpan(MessageProcessorNotification notification) {
		txMap.get(getMessageId(notification)).end();
	}

	private String getMessageId(MessageProcessorNotification notification) {
		return ((MuleEvent) notification.getSource()).getMessage().getMessageRootId();
	}
}
