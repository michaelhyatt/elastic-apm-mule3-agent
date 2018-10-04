package listeners;

import org.mule.api.context.notification.PipelineMessageNotificationListener;
import org.mule.context.notification.PipelineMessageNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TracePipelineMessageNotificationListener implements PipelineMessageNotificationListener <PipelineMessageNotification>{

	@Override
	public void onNotification(PipelineMessageNotification notification) {
		logger.debug(notification.getActionName());
	}

	private Logger logger = LoggerFactory.getLogger(TracePipelineMessageNotificationListener.class);
}
