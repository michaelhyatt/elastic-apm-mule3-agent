package co.elastic.apm.mule.listeners;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.mule.api.context.notification.PipelineMessageNotificationListener;
import org.mule.context.notification.PipelineMessageNotification;
import org.springframework.beans.factory.annotation.Autowired;

import co.elastic.apm.mule.ApmClient;
import co.elastic.apm.mule.utils.TransactionUtils;

/**
 * @author michaelhyatt
 *
 *         Listener for flow start and stop events.
 */
public class TracePipelineMessageNotificationListener
		implements PipelineMessageNotificationListener<PipelineMessageNotification> {

	@Override
	public void onNotification(PipelineMessageNotification notification) {

		logger.debug("Received " + notification.getActionName());

		switch (notification.getAction()) {
		case PipelineMessageNotification.PROCESS_START:

			if (!ApmClient.isInitialised()) {
				logger.debug("ApmClient wasn't initialised, doing lazy init on first PROCESS_START");
				ApmClient.initialiseElasticApm();
			}

			transactionUtils.startTransactionIfNone(notification);
			break;

		case PipelineMessageNotification.PROCESS_COMPLETE:
		case PipelineMessageNotification.PROCESS_END:
			transactionUtils.endTransactionIfNeeded(notification);
			break;

		}
	}

	@Autowired
	private TransactionUtils transactionUtils;

	private Logger logger = LogManager.getLogger(TracePipelineMessageNotificationListener.class);
}
