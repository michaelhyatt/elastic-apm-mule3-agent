package co.elastic.apm.mule.utils;

import org.mule.context.notification.ExceptionNotification;

import co.elastic.apm.api.ElasticApm;

/**
 * @author michaelhyatt
 * 
 *         Mule flow exception handling
 *
 */
public class ExceptionUtils {

	public static void captureException(ExceptionNotification notification) {
		ElasticApm.currentTransaction().captureException(notification.getException().getCause());
	}

}
