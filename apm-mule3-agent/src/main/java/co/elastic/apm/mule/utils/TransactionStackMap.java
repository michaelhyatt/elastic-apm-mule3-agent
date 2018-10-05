package co.elastic.apm.mule.utils;

import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import co.elastic.apm.api.Span;

/**
 * @author michaelhyatt
 * 
 *         This is a thread-safe map store of transaction and span stacks (LIFO)
 *         to keep ensure asynchronous callbacks can find transactions by Mule
 *         rootMessageId to finish them.
 *
 */
public class TransactionStackMap {

	private Map<String, Stack<Span>> map = new ConcurrentHashMap<>();

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
	public void put(String key, Span span) {

		Stack<Span> stack = map.get(key);

		if (stack == null)
			stack = new Stack<Span>();

		stack.push(span);
		map.put(key, stack);
	}

	/**
	 * Retrieve and remove the last inserted {@link co.elastic.apm.api.Span} or a
	 * {@link co.elastic.apm.api.Transaction} from the Stack stored for a particular
	 * message
	 * 
	 * @param key
	 * @return
	 */
	public Span get(String key) {
		return map.get(key).pop();
	}

	/**
	 * Returns the size of the stack of {@link co.elastic.apm.api.Span} or
	 * {@link co.elastic.apm.api.Transaction} objects stored for a key. This method
	 * can throw NullPointerException, if called on an empty Stack.
	 * 
	 * @param key
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
	 * @param key
	 * @return
	 */
	public Span peek(String key) {
		return map.get(key).peek();
	}

}
