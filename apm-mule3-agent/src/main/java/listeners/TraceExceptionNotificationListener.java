package listeners;

import org.mule.api.context.notification.ExceptionNotificationListener;
import org.mule.context.notification.ExceptionNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TraceExceptionNotificationListener implements ExceptionNotificationListener<ExceptionNotification>{

	@Override
	public void onNotification(ExceptionNotification notification) {
		logger.debug(notification.getActionName());
	}

	private Logger logger = LoggerFactory.getLogger(TraceExceptionNotificationListener.class);
}