package listeners;

import org.mule.api.context.notification.ExceptionNotificationListener;
import org.mule.context.notification.ExceptionNotification;

import co.elastic.apm.mule.utils.ExceptionUtils;

public class TraceExceptionNotificationListener implements ExceptionNotificationListener<ExceptionNotification>{

	@Override
	public void onNotification(ExceptionNotification notification) {
		ExceptionUtils.captureException(notification);
	}

}