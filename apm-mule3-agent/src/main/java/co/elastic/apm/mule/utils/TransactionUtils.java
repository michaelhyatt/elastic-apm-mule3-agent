package co.elastic.apm.mule.utils;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.context.notification.PipelineMessageNotification;
import org.mule.module.http.internal.ParameterMap;
import org.springframework.beans.factory.annotation.Autowired;

import co.elastic.apm.api.ElasticApm;
import co.elastic.apm.api.Transaction;

/**
 * @author michaelhyatt
 * 
 *         Handling of transaction starts and ends. This implementation only
 *         starts a transaction for the very first flow invocation for a
 *         particular MuleMessage. It ignores other nested flows, if a
 *         transaction was already created.
 *
 */
public class TransactionUtils {

	@Autowired
	private SpanStore txMap;

	/**
	 * Starts {@link co.elastic.apm.api.Transaction}, if none exists for a given
	 * rootMessageId
	 * 
	 * @param notification
	 */
	public void startTransactionIfNone(PipelineMessageNotification notification) {

		// Only start transaction for the first encountered flow
		MuleMessage muleMessage = getMuleMessage(notification);
		String messageId = muleMessage.getMessageRootId();

		if (txMap.depth(messageId) > 0)
			return;

		// Start transaction with propagation with remote parentId to support
		// distributed tracing
		Transaction transaction = ElasticApm.startTransactionWithRemoteParent(x -> muleMessage.getInboundProperty(x));

		String name = AnnotatedObjectUtils.getFlowName(notification);
		transaction.setName(name);
		transaction.setType(Transaction.TYPE_REQUEST);

		if (PropertyUtils.isInputPropertyCaptureEnabled())
			PropertyUtils.getInputProperties(muleMessage).forEach(pair -> updateProperties(pair, transaction, "in"));

		transaction.addLabel("messageId", messageId);

		txMap.storeTransactionOrSpan(messageId, notification, transaction);

	}

	/**
	 * Only ends the very last {@link co.elastic.apm.api.Transaction} in the Stack,
	 * ignoring the rest.
	 * 
	 * @param notification
	 */
	public void endTransactionIfNeeded(PipelineMessageNotification notification) {
		MuleMessage muleMessage = getMuleMessage(notification);
		String messageId = muleMessage.getMessageRootId();

		// Only terminate the last top level flow transaction
		if (txMap.depth(messageId) == 1) {
			Transaction transaction = (Transaction) txMap.getTransactionOrSpan(messageId, notification);

			if (PropertyUtils.isOutputPropertyCaptureEnabled())
				PropertyUtils.getOutputProperties(muleMessage)
						.forEach((pair) -> updateProperties(pair, transaction, "out"));

			transaction.end();
		}
	}

	private MuleMessage getMuleMessage(PipelineMessageNotification notification) {
		return ((MuleEvent) notification.getSource()).getMessage();
	}

	private void updateProperties(ImmutablePair<String, Object> pair, Transaction transaction, String prefix) {

		String key = pair.getLeft();

		Object value = pair.getRight();
		String stringValue;

		if (value instanceof String) {
			stringValue = (String) value;
			transaction.addLabel(prefix + ":" + key, stringValue);

		} else if (value instanceof ParameterMap) {
			ParameterMap map = (ParameterMap) value;

			map.keySet().stream().forEach((key2) -> transaction.addLabel(prefix + ":" + key + ":" + key2, map.get(key2)));

		} else {
			stringValue = "???";
			transaction.addLabel(prefix + ":" + key, stringValue);
		}

	}

}
