package co.elastic.apm.mule.utils;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.mule.api.MessagingException;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.context.notification.ExceptionNotification;
import org.springframework.beans.factory.annotation.Autowired;

import co.elastic.apm.api.ElasticApm;
import co.elastic.apm.api.Span;

/**
 * @author michaelhyatt
 * 
 *         Mule flow exception handling
 *
 */
public class ExceptionUtils {

	@Autowired
	private SpanStore txMap;

	private Logger logger = LogManager.getLogger(ExceptionUtils.class);

	public void captureException(ExceptionNotification notification) {

		MuleException source = (MuleException) notification.getSource();

		if (source instanceof MessagingException) {
			MuleMessage muleMessage = ((MessagingException) source).getEvent().getMessage();
			String messageId = muleMessage.getMessageRootId();
			Span transaction = txMap.getTransactionOrSpan(messageId, notification);
			transaction.captureException(notification.getException());
		} else {

			// Default handling that doesn't seem to work anymore
			logger.warn("Trying to use currentTransaction() to log the exception into APM. May not work...",
					notification.getException());

			ElasticApm.currentTransaction().captureException(notification.getException());
		}
	}
}
