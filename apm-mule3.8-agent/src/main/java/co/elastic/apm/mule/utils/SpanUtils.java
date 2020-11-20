package co.elastic.apm.mule.utils;

import org.apache.commons.lang3.tuple.ImmutablePair;
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

		// System.out.println("Starting " + notification.getProcessor());

		MuleMessage message = getMuleMessage(notification);
		String messageId = getMessageId(message);

		Span parentSpan = txMap.getTopLevelTransaction(messageId);
		Span span = parentSpan.startSpan(AnnotatedObjectUtils.getProcessorType(notification), "ext", "mule");
		String processorName = AnnotatedObjectUtils.getProcessorName(notification);
		span.setName(processorName);
		span.setStartTimestamp(notification.getTimestamp() * 1_000);

		txMap.storeTransactionOrSpan(messageId, notification, span);

		// Update MuleMessage with distributed tracing properties set into
		// outboundProperty
		span.injectTraceHeaders(
				(headerName, headerValue) -> message.setProperty(headerName, headerValue, PropertyScope.OUTBOUND));

		createFlowvarSpanTags(notification, span, processorName);

	}

	public void endSpan(MessageProcessorNotification notification) {
	
		MuleMessage message = getMuleMessage(notification);
		String messageId = getMessageId(message);
	
		Span span = txMap.getTransactionOrSpan(messageId, notification);
	
		span.end(notification.getTimestamp() * 1_000);
	}

	private void createFlowvarSpanTags(MessageProcessorNotification notification, Span span, String processorName) {
		if (FlowvarUtils.isCaptureFlowvarsEnabled())
			FlowvarUtils.getFlowvars(notification).filter(pair -> FlowvarUtils.isFlowvarConfigured(pair, processorName))
					.forEach(pair -> updateSpanTags(span, pair));
	}

	private void updateSpanTags(Span span, ImmutablePair<String, Object> pair) {
		span.setLabel("flowVar:" + pair.getLeft(), pair.getRight().toString());
	}

	private String getMessageId(MuleMessage message) {
		return message.getMessageRootId();
	}

	private MuleMessage getMuleMessage(MessageProcessorNotification notification) {
		return notification.getSource().getMessage();
	}

}
