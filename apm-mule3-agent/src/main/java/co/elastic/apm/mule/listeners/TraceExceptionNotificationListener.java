package co.elastic.apm.mule.listeners;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
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
		
		logger.debug("Received " + notification.getActionName());
		
		exceptionUtils.captureException(notification);
	}

	@Autowired
	private ExceptionUtils exceptionUtils;
	
	private Logger logger = LogManager.getLogger(TraceExceptionNotificationListener.class);

}