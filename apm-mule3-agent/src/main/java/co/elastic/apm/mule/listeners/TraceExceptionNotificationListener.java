package co.elastic.apm.mule.listeners;

import org.mule.api.context.notification.ExceptionNotificationListener;
import org.mule.context.notification.ExceptionNotification;
import org.springframework.beans.factory.annotation.Autowired;

import co.elastic.apm.mule.utils.ExceptionUtils;

/**
 * @author michaelhyatt
 * 
 *         Flow exception notification listener
 *
 */
public class TraceExceptionNotificationListener implements ExceptionNotificationListener<ExceptionNotification> {

	@Override
	public void onNotification(ExceptionNotification notification) {
		exceptionUtils.captureException(notification);
	}

	@Autowired
	private ExceptionUtils exceptionUtils;

}