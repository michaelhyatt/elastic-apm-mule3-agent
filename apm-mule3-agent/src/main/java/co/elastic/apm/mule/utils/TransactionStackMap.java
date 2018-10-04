package co.elastic.apm.mule.utils;

import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import co.elastic.apm.api.Span;

public class TransactionStackMap {

	private Map<String, Stack<Span>> map = new ConcurrentHashMap<>();

	public void put(String key, Span span) {

		Stack<Span> stack = map.get(key);

		if (stack == null)
			stack = new Stack<Span>();

		stack.push(span);
		map.put(key, stack);
	}

	public Span get(String key) {
		return map.get(key).pop();
	}

	public int depth(String key) {
		if (map.containsKey(key))
			return map.get(key).size();

		return 0;
	}

	public Span peek(String key) {
		return map.get(key).peek();
	}

}
