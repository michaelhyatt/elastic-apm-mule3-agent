package listeners;

import org.mule.api.context.notification.MessageProcessorNotificationListener;
import org.mule.context.notification.MessageProcessorNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TraceMessageProcessorNotificationListener implements MessageProcessorNotificationListener<MessageProcessorNotification> {

	@Override
	public void onNotification(MessageProcessorNotification notification) {
		logger.debug(notification.getActionName());
	}

	private Logger logger = LoggerFactory.getLogger(TraceMessageProcessorNotificationListener.class);

}
