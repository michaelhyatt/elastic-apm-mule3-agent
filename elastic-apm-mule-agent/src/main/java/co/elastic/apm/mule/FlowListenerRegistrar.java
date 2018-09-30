package co.elastic.apm.mule;

import javax.annotation.PostConstruct;

import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.context.notification.PipelineMessageNotificationListener;
import org.mule.context.notification.NotificationException;
import org.mule.context.notification.PipelineMessageNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlowListenerRegistrar {

	@PostConstruct
	private void registerListeners() throws NotificationException {
		registerPipelineListeners();
	}

	private void registerPipelineListeners() {
		PipelineMessageNotificationListener<PipelineMessageNotification> listener = new PipelineMessageNotificationListener<PipelineMessageNotification>() {

			public void onNotification(PipelineMessageNotification notification) {

				String id = ((MuleEvent) notification.getSource()).getMessage().getMessageRootId();

				logger.debug(String.format("%s: %s", id, notification.getActionName().toUpperCase().replace(' ', '_')));

				switch (notification.getAction()) {
				case PipelineMessageNotification.PROCESS_START:
					debug(" - PROCESS_START", id, notification);
					break;

				case PipelineMessageNotification.PROCESS_END:
					debug(" + PROCESS_END", id, notification);
					break;

				case PipelineMessageNotification.PROCESS_COMPLETE:
					debug(" + PROCESS_COMPLETE", id, notification);
					break;
				}
			}

		};

		try {
			muleContext.registerListener(listener);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void debug(String message, String id, PipelineMessageNotification notification) {
		if (logger.isDebugEnabled()) {
			String flowName = AnnotatedObjectUtils.getFlowName(notification);
			logger.debug(String.format("%s: %s => flow: %s", id, message, flowName));
		}
	}

	@Autowired
	private MuleContext muleContext;

	private Logger logger = LoggerFactory.getLogger(FlowListenerRegistrar.class);

}
