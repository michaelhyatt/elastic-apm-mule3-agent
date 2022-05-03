package co.elastic.apm.mule.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

import org.mule.api.context.notification.ServerNotification;
import org.mule.api.processor.MessageProcessor;
import org.mule.context.notification.MessageProcessorNotification;

import co.elastic.apm.api.Span;

/**
 * @author michaelhyatt
 * 
 *         This is a thread-safe map store of transaction and span maps (LIFO)
 *         to keep ensure asynchronous callbacks can find transactions by Mule
 *         rootMessageId to finish them.
 *
 */
public class SpanStore {

	private Map<String, Map<Optional<MessageProcessor>, Span>> map = Collections.synchronizedMap(new WeakHashMap<>());

	/**
	 * Store a {@link co.elastic.apm.api.Span} or a
	 * {@link co.elastic.apm.api.Transaction} in a {@link java.util.Map} using a
	 * {@link String} key
	 * 
	 * @param key
	 *            rootMessageId from MuleMessage
	 * @param span
	 *            Span or Transaction object to store
	 */
	public void storeTransactionOrSpan(String key, ServerNotification notification, Span span) {

		Optional<MessageProcessor> key2 = getKey2(notification);

		Map<Optional<MessageProcessor>, Span> innerMap = map.get(key);

		if (innerMap == null)
			innerMap = new HashMap<>();

		innerMap.put(key2, span);
		map.put(key, innerMap);
	}

	/**
	 * Retrieve and remove the last inserted {@link co.elastic.apm.api.Span} or a
	 * {@link co.elastic.apm.api.Transaction} from the map stored for a particular
	 * message
	 * 
	 * @param notification
	 * @return
	 */
	public Span getTransactionOrSpan(String key, ServerNotification notification) {

		Optional<MessageProcessor> key2 = getKey2(notification);

		Map<Optional<MessageProcessor>, Span> stack = map.get(key);
		Span span = stack.remove(key2);
		
		if (stack.size() == 0)
			map.remove(key);
		
		return span;
	}

	/**
	 * Returns the size of the map of {@link co.elastic.apm.api.Span} or
	 * {@link co.elastic.apm.api.Transaction} objects stored for a key. This method
	 * can throw NullPointerException, if called on an empty map.
	 * 
	 * @param notification
	 *            typically, rootMessageId from MuleMessage
	 * @return
	 */
	public int depth(String key) {
		if (map.containsKey(key))
			return map.get(key).size();

		return 0;
	}

	/**
	 * Retrieve and remove {@link co.elastic.apm.api.Span} or a
	 * {@link co.elastic.apm.api.Transaction}
	 * 
	 * @param notification
	 * @return
	 */
	public Span getTopLevelTransaction(String key) {

		return map.get(key).get(Optional.empty());
	}

	private Optional<MessageProcessor> getKey2(ServerNotification notification) {

		Optional<MessageProcessor> key2 = Optional.empty();
		
		if (notification instanceof MessageProcessorNotification) {
			MessageProcessor processor = ((MessageProcessorNotification) notification).getProcessor();
			key2 = Optional.of(processor);
		}

		return key2;
	}

}
