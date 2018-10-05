package co.elastic.apm.mule.listeners;

import org.mule.api.context.notification.MessageProcessorNotificationListener;
import org.mule.context.notification.MessageProcessorNotification;
import org.springframework.beans.factory.annotation.Autowired;

import co.elastic.apm.mule.utils.SpanUtils;

public class TraceMessageProcessorNotificationListener
		implements MessageProcessorNotificationListener<MessageProcessorNotification> {

	@Override
	public void onNotification(MessageProcessorNotification notification) {

		switch (notification.getAction()) {
		case MessageProcessorNotification.MESSAGE_PROCESSOR_PRE_INVOKE:
			spanUtils.startSpan(notification);
			break;

		case MessageProcessorNotification.MESSAGE_PROCESSOR_PRE_INVOKE_ORIGINAL_EVENT:
			break;

		case MessageProcessorNotification.MESSAGE_PROCESSOR_POST_INVOKE:
			spanUtils.endSpan(notification);
			break;
		}
	}
	
	@Autowired
	private SpanUtils spanUtils;

}
