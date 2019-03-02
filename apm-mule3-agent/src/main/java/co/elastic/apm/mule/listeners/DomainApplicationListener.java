package co.elastic.apm.mule.listeners;

import org.mule.api.MuleContext;
import org.mule.api.context.notification.ExceptionNotificationListener;
import org.mule.api.context.notification.MessageProcessorNotificationListener;
import org.mule.api.context.notification.PipelineMessageNotificationListener;
import org.mule.config.spring.MuleArtifactContext;
import org.mule.context.notification.ExceptionNotification;
import org.mule.context.notification.MessageProcessorNotification;
import org.mule.context.notification.PipelineMessageNotification;
import org.mule.context.notification.ServerNotificationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import co.elastic.apm.mule.utils.ExceptionUtils;
import co.elastic.apm.mule.utils.SpanUtils;
import co.elastic.apm.mule.utils.TransactionUtils;

public class DomainApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private TransactionUtils transactionUtils;

	@Autowired
	private ExceptionUtils exceptionUtils;

	@Autowired
	private SpanUtils spanUtils;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {

		Object source = event.getSource();

		if (!(source instanceof MuleArtifactContext))
			return;

		MuleContext muleContext = ((MuleArtifactContext) source).getMuleContext();

		ServerNotificationManager notificationManager = enableEventNotifications(muleContext);

		subscribeToMessageProcessorNotifications(notificationManager);

		subscribeToPipelineMessageNotifications(notificationManager);

		subscribeToExceptionNotifications(notificationManager);

	}

	private void subscribeToExceptionNotifications(ServerNotificationManager notificationManager) {
		notificationManager.addListener(new ExceptionNotificationListener<ExceptionNotification>() {

			@Override
			public void onNotification(ExceptionNotification notification) {
				exceptionUtils.captureException(notification);
			}

		});
	}

	private void subscribeToPipelineMessageNotifications(ServerNotificationManager notificationManager) {
		notificationManager.addListener(new PipelineMessageNotificationListener<PipelineMessageNotification>() {

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
					// Ignored, as it is skipped when flow exception is thrown
					break;
				}
			}
		});
	}

	private void subscribeToMessageProcessorNotifications(ServerNotificationManager notificationManager) {
		notificationManager.addListener(new MessageProcessorNotificationListener<MessageProcessorNotification>() {

			@Override
			public void onNotification(MessageProcessorNotification notification) {

				switch (notification.getAction()) {
				case MessageProcessorNotification.MESSAGE_PROCESSOR_PRE_INVOKE:
					break;

				case MessageProcessorNotification.MESSAGE_PROCESSOR_PRE_INVOKE_ORIGINAL_EVENT:
					spanUtils.startSpan(notification);
					break;

				case MessageProcessorNotification.MESSAGE_PROCESSOR_POST_INVOKE:
					spanUtils.endSpan(notification);
					break;
				}
			}

		});
	}

	private ServerNotificationManager enableEventNotifications(MuleContext muleContext) {
		ServerNotificationManager notificationManager = muleContext.getNotificationManager();

		notificationManager.addInterfaceToType(MessageProcessorNotificationListener.class,
				MessageProcessorNotification.class);
		notificationManager.addInterfaceToType(PipelineMessageNotificationListener.class,
				PipelineMessageNotification.class);
		notificationManager.addInterfaceToType(ExceptionNotificationListener.class, ExceptionNotification.class);
		return notificationManager;
	}

}
