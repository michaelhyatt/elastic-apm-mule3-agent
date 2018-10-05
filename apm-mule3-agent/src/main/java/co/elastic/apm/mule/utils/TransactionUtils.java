package co.elastic.apm.mule.utils;

import org.mule.api.MuleEvent;
import org.mule.context.notification.PipelineMessageNotification;
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
	private TransactionStackMap txMap;

	/**
	 * Starts {@link co.elastic.apm.api.Transaction}, if none exists for a given
	 * rootMessageId
	 * 
	 * @param notification
	 */
	public void startTransactionIfNone(PipelineMessageNotification notification) {

		// Only start transaction for the first encountered flow
		String messageId = getMessageId(notification);
		if (txMap.depth(messageId) > 0)
			return;

		Transaction transaction = ElasticApm.startTransaction();
		String name = AnnotatedObjectUtils.getFlowName(notification);
		transaction.setName(name);
		transaction.setType(Transaction.TYPE_REQUEST);
		transaction.addTag("messageId", messageId);
		txMap.put(messageId, transaction);

	}

	/**
	 * Only ends the very last {@link co.elastic.apm.api.Transaction} in the Stack,
	 * ignoring the rest.
	 * 
	 * @param notification
	 */
	public void endTransactionIfNeeded(PipelineMessageNotification notification) {
		String messageId = getMessageId(notification);

		// Only terminate the last top level flow transaction
		if (txMap.depth(messageId) == 1)
			txMap.get(messageId).end();
	}

	private String getMessageId(PipelineMessageNotification notification) {
		return ((MuleEvent) notification.getSource()).getMessage().getMessageRootId();
	}

}