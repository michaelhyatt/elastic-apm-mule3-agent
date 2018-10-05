package co.elastic.apm.mule.listeners;

import org.mule.api.context.notification.PipelineMessageNotificationListener;
import org.mule.context.notification.PipelineMessageNotification;
import org.springframework.beans.factory.annotation.Autowired;

import co.elastic.apm.mule.utils.TransactionUtils;

public class TracePipelineMessageNotificationListener
		implements PipelineMessageNotificationListener<PipelineMessageNotification> {

	@Override
	public void onNotification(PipelineMessageNotification notification) {

		switch (notification.getAction()) {
		case PipelineMessageNotification.PROCESS_START:
			transactionUtils.startTransactionIfNone(notification);
			break;

		case PipelineMessageNotification.PROCESS_END:
			transactionUtils.endTransactionIfNeeded(notification);
			break;

		case PipelineMessageNotification.PROCESS_COMPLETE:
			break;
		}
	}

	@Autowired
	private TransactionUtils transactionUtils;

}
