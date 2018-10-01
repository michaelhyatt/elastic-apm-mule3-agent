package co.elastic.apm.mule;

import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Configuration;

import co.elastic.apm.impl.transaction.AbstractSpan;

@Configuration
public class TransactionStackMap {

	private Map<String, Stack<AbstractSpan<?>>> map = new ConcurrentHashMap<>();

	public void put(String key, AbstractSpan<?> span) {

		Stack<AbstractSpan<?>> stack = map.get(key);
		Stack<AbstractSpan<?>> newStack = new Stack<AbstractSpan<?>>();

		// Is there a Stack already?
		if (stack != null)
			newStack.addAll(stack);

		newStack.push(span);
		map.put(key, newStack);
	}

	public AbstractSpan<?> get(String key) {

		Stack<AbstractSpan<?>> stack = map.get(key);
		Stack<AbstractSpan<?>> newStack = new Stack<AbstractSpan<?>>();

		AbstractSpan<?> result = stack.pop();

		newStack.addAll(stack);
		map.put(key, newStack);

		return result;
	}

	public int depth(String key) {
		Stack<AbstractSpan<?>> value = map.get(key);

		if (value == null)
			return 0;

		return value.size();

	}

	public AbstractSpan<?> peek(String key) {
		return map.get(key).peek();
	}

}
